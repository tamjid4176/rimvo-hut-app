package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.i18n.AppLanguage
import com.example.data.local.UserPrefsSnapshot
import com.example.data.model.CartItem
import com.example.data.model.InstallmentEntry
import com.example.data.model.InstallmentOrderItem
import com.example.data.model.InstallmentTenure
import com.example.data.model.KycFormState
import com.example.data.model.KycStatus
import com.example.data.model.PaymentChannel
import com.example.data.model.PaymentReceipt
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.repository.RimvoCatalog
import com.example.data.repository.RimvoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RimvoUiState(
    val prefs: UserPrefsSnapshot = UserPrefsSnapshot(),
    val products: List<Product> = RimvoCatalog.products,
    val searchQuery: String = "",
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val selectedBrand: String? = null,
    val cartItems: List<CartItem> = emptyList(),
    val kycState: KycFormState = KycFormState(),
    val installmentOrders: List<InstallmentOrderItem> = emptyList(),
    val isPinUnlocked: Boolean = true,
    val activeReceipt: PaymentReceipt? = null,
    val statusMessage: String? = null,
    val lastConfirmedOrderId: String? = null
) {
    val totalCreditLimitBdt: Int
        get() = if (kycState.status == KycStatus.VERIFIED) prefs.creditLimitTotalBdt else 0

    val usedCreditBdt: Int
        get() = installmentOrders.sumOf { it.remainingBalanceBdt }

    val availableCreditLimitBdt: Int
        get() = (totalCreditLimitBdt - usedCreditBdt).coerceAtLeast(0)

    val cartTotalBdt: Int
        get() = cartItems.sumOf { it.lineTotalBdt }

    val cartCount: Int
        get() = cartItems.sumOf { it.quantity }

    val filteredProducts: List<Product>
        get() = products.filter { p ->
            val matchesCategory =
                selectedCategory == ProductCategory.ALL || p.category == selectedCategory
            val matchesBrand = selectedBrand == null || p.brand.equals(selectedBrand, ignoreCase = true)
            val q = searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                p.nameEn.contains(q, ignoreCase = true) ||
                p.nameBn.contains(q, ignoreCase = true) ||
                p.brand.contains(q, ignoreCase = true)
            matchesCategory && matchesBrand && matchesQuery
        }

    val nextDueInstallment: Pair<InstallmentOrderItem, InstallmentEntry>?
        get() = installmentOrders
            .mapNotNull { order -> order.nextDueEntry?.let { entry -> order to entry } }
            .firstOrNull()
}

private data class TransientUiState(
    val searchQuery: String = "",
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val selectedBrand: String? = null,
    val isPinUnlocked: Boolean = false,
    val pinLockInitialized: Boolean = false,
    val activeReceipt: PaymentReceipt? = null,
    val statusMessage: String? = null,
    val lastConfirmedOrderId: String? = null
)

class RimvoViewModel(private val repository: RimvoRepository) : ViewModel() {

    private val transientState = MutableStateFlow(TransientUiState())

    val uiState: StateFlow<RimvoUiState> = combine(
        repository.prefsFlow,
        repository.cartItemsFlow,
        repository.kycFlow,
        repository.installmentOrdersFlow,
        transientState
    ) { prefs, cart, kyc, orders, transient ->
        val unlocked = if (!prefs.pinLockEnabled) {
            true
        } else if (!transient.pinLockInitialized) {
            false
        } else {
            transient.isPinUnlocked
        }
        RimvoUiState(
            prefs = prefs,
            cartItems = cart,
            kycState = kyc,
            installmentOrders = orders,
            searchQuery = transient.searchQuery,
            selectedCategory = transient.selectedCategory,
            selectedBrand = transient.selectedBrand,
            isPinUnlocked = unlocked,
            activeReceipt = transient.activeReceipt,
            statusMessage = transient.statusMessage,
            lastConfirmedOrderId = transient.lastConfirmedOrderId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RimvoUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeededData()
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val next = if (uiState.value.prefs.language == AppLanguage.EN) {
                AppLanguage.BN
            } else {
                AppLanguage.EN
            }
            repository.setLanguage(next)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { repository.setLanguage(language) }
    }

    fun completeOnboardingAsGuest() {
        viewModelScope.launch { repository.completeOnboarding() }
    }

    fun requestOtp(phone: String) {
        viewModelScope.launch {
            repository.requestOtp(phone)
        }
    }

    fun verifyOtp(phone: String, otp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.verifyOtp(phone, otp)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "OTP verification failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { repository.signOut() }
    }

    fun updateSearch(query: String) {
        transientState.value = transientState.value.copy(searchQuery = query)
    }

    fun selectCategory(category: ProductCategory) {
        transientState.value = transientState.value.copy(selectedCategory = category)
    }

    fun selectBrand(brand: String?) {
        val current = transientState.value.selectedBrand
        val next = if (current == brand) null else brand
        transientState.value = transientState.value.copy(selectedBrand = next)
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
            val msg = if (uiState.value.prefs.language == AppLanguage.BN) {
                "কার্টে যোগ করা হয়েছে"
            } else {
                "Added to cart"
            }
            transientState.value = transientState.value.copy(statusMessage = msg)
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch { repository.updateCartQuantity(productId, quantity) }
    }

    fun submitKyc(form: KycFormState, statusOverride: KycStatus = KycStatus.PENDING_REVIEW) {
        viewModelScope.launch {
            repository.submitKyc(form, statusOverride)
        }
    }

    fun simulateKycStatus(status: KycStatus) {
        viewModelScope.launch {
            repository.updateKycStatus(status)
        }
    }

    fun placeOrder(
        deliveryAddress: String,
        useInstallments: Boolean,
        tenure: InstallmentTenure,
        channel: PaymentChannel,
        txId: String,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            val items = uiState.value.cartItems
            if (items.isEmpty()) return@launch
            val orderId = repository.placeOrder(
                items = items,
                deliveryAddress = deliveryAddress,
                useInstallments = useInstallments,
                tenure = tenure,
                channel = channel,
                txId = txId
            )
            transientState.value = transientState.value.copy(lastConfirmedOrderId = orderId)
            onComplete(orderId)
        }
    }

    fun clearConfirmedOrder() {
        transientState.value = transientState.value.copy(lastConfirmedOrderId = null)
    }

    fun payInstallment(
        order: InstallmentOrderItem,
        entry: InstallmentEntry,
        channel: PaymentChannel,
        txId: String
    ) {
        viewModelScope.launch {
            repository.payInstallment(order.orderId, entry, channel, txId)
            val msg = if (uiState.value.prefs.language == AppLanguage.BN) {
                "কিস্তি পরিশোধ সফল হয়েছে!"
            } else {
                "Installment paid successfully!"
            }
            transientState.value = transientState.value.copy(statusMessage = msg)
        }
    }

    fun showReceipt(order: InstallmentOrderItem, entry: InstallmentEntry) {
        transientState.value = transientState.value.copy(
            activeReceipt = PaymentReceipt(
                receiptNumber = "RCP-${order.orderId.takeLast(4)}-${entry.installmentNumber}",
                orderId = order.orderId,
                productTitle = order.productTitle,
                installmentNumber = entry.installmentNumber,
                amountPaidBdt = entry.amountBdt,
                channel = entry.paidChannel ?: order.paymentChannel,
                transactionId = entry.receiptTxId ?: "VERIFIED",
                paidDateLabel = entry.paidAtLabel ?: entry.dueDateLabel
            )
        )
    }

    fun dismissReceipt() {
        transientState.value = transientState.value.copy(activeReceipt = null)
    }

    fun clearStatusMessage() {
        transientState.value = transientState.value.copy(statusMessage = null)
    }

    fun setWhatsappReminders(enabled: Boolean) {
        viewModelScope.launch { repository.setWhatsappReminders(enabled) }
    }

    fun setPushReminders(enabled: Boolean) {
        viewModelScope.launch { repository.setPushReminders(enabled) }
    }

    fun setPinLock(enabled: Boolean, pin: String) {
        viewModelScope.launch {
            repository.setPinLock(enabled, pin)
            transientState.value = transientState.value.copy(
                isPinUnlocked = true,
                pinLockInitialized = true
            )
        }
    }

    fun verifyAppPin(enteredPin: String): Boolean {
        val correct = uiState.value.prefs.appPin
        return if (enteredPin == correct) {
            transientState.value = transientState.value.copy(
                isPinUnlocked = true,
                pinLockInitialized = true
            )
            true
        } else {
            false
        }
    }

    fun setDeliveryAddress(address: String) {
        viewModelScope.launch { repository.setDeliveryAddress(address) }
    }
}

class RimvoViewModelFactory(private val repository: RimvoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RimvoViewModel::class.java)) {
            return RimvoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
