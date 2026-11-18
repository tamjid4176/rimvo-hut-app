package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.LocalStrings
import com.example.data.model.InstallmentCalculator
import com.example.data.model.InstallmentTenure
import com.example.data.model.KycStatus
import com.example.data.model.PaymentChannel
import com.example.data.model.formatBdt
import com.example.ui.theme.BkashPink
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.MonoNumberStyle
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.OutlineHairline
import com.example.ui.theme.OverdueRed
import com.example.ui.theme.OverdueRedSoft
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenSoft
import com.example.ui.viewmodel.RimvoUiState

@Composable
fun CartCheckoutScreen(
    uiState: RimvoUiState,
    initialInstallmentMode: Boolean,
    initialTenure: InstallmentTenure,
    onUpdateQuantity: (String, Int) -> Unit,
    onBrowseStore: () -> Unit,
    onOpenKyc: () -> Unit,
    onPlaceOrder: (
        address: String,
        useInstallments: Boolean,
        tenure: InstallmentTenure,
        channel: PaymentChannel,
        txId: String
    ) -> Unit,
    onViewInstallments: () -> Unit,
    onClearConfirmedOrder: () -> Unit
) {
    val strings = LocalStrings.current
    val isBangla = uiState.prefs.language == AppLanguage.BN

    var deliveryAddress by remember(uiState.prefs.deliveryAddress) {
        mutableStateOf(uiState.prefs.deliveryAddress)
    }
    var useInstallments by remember(initialInstallmentMode) {
        mutableStateOf(initialInstallmentMode)
    }
    var selectedTenure by remember(initialTenure) {
        mutableStateOf(initialTenure)
    }
    var selectedChannel by remember { mutableStateOf(PaymentChannel.BKASH) }
    var txId by remember { mutableStateOf("BKH94A72X1") }
    var receiptUri by remember { mutableStateOf<Uri?>(null) }

    val receiptPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) receiptUri = uri
    }

    if (uiState.lastConfirmedOrderId != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = VerifiedGreenSoft,
                modifier = Modifier.size(84.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = VerifiedGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings.orderPlacedTitle,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Order #${uiState.lastConfirmedOrderId} synced with Rimvo Hut & Rimvo Finance.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    onClearConfirmedOrder()
                    onViewInstallments()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("order_success_view_plans"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(strings.navInstallments)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    onClearConfirmedOrder()
                    onBrowseStore()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(strings.shopNow)
            }
        }
        return
    }

    if (uiState.cartItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = EmeraldSoft,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings.emptyCartTitle,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = strings.emptyCartDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onBrowseStore,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("empty_cart_shop_btn")
            ) {
                Text(strings.shopNow)
            }
        }
        return
    }

    val totalCash = uiState.cartTotalBdt
    val breakdown = remember(totalCash, selectedTenure) {
        InstallmentCalculator.calculate(totalCash, selectedTenure)
    }
    val isKycVerified = uiState.kycState.status == KycStatus.VERIFIED
    val creditEligible = isKycVerified && breakdown.financedPrincipalBdt <= uiState.availableCreditLimitBdt
    val amountDueNow = if (useInstallments) breakdown.downPaymentTodayBdt else totalCash

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 108.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = strings.cartTitle,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Cart Items
        items(uiState.cartItems, key = { it.product.id }) { cartItem ->
            val name = if (isBangla) cartItem.product.nameBn else cartItem.product.nameEn
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = cartItem.product.imageRes),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatBdt(cartItem.lineTotalBdt),
                            style = MaterialTheme.typography.titleMedium.merge(MonoNumberStyle),
                            color = EmeraldDark
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity - 1) },
                            modifier = Modifier.testTag("cart_dec_${cartItem.product.id}")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "${cartItem.quantity}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(
                            onClick = { onUpdateQuantity(cartItem.product.id, cartItem.quantity + 1) },
                            modifier = Modifier.testTag("cart_inc_${cartItem.product.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }
        }

        // Delivery Address Selector
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary)
                        Text(strings.deliveryAddressTitle, style = MaterialTheme.typography.titleMedium)
                    }
                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        singleLine = false,
                        maxLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_address_input")
                    )
                }
            }
        }

        // Payment Option Toggle: Full vs Rimvo Finance Installments
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(strings.paymentOptionTitle, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { useInstallments = false }
                            .background(if (!useInstallments) EmeraldSoft else Color.Transparent)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !useInstallments,
                            onClick = { useInstallments = false },
                            modifier = Modifier.testTag("payment_mode_full")
                        )
                        Column {
                            Text(strings.fullPaymentOption, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Pay ${formatBdt(totalCash)} in full now",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { useInstallments = true }
                            .background(if (useInstallments) EmeraldSoft else Color.Transparent)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = useInstallments,
                            onClick = { useInstallments = true },
                            modifier = Modifier.testTag("payment_mode_installment")
                        )
                        Column {
                            Text(strings.installmentOption, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Split into 3, 6, or 12 transparent payments",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldDark
                            )
                        }
                    }

                    if (useInstallments) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InstallmentTenure.entries.forEach { tenure ->
                                FilterChip(
                                    selected = selectedTenure == tenure,
                                    onClick = { selectedTenure = tenure },
                                    label = { Text("${tenure.months}M (${tenure.markupPercent}%)") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EmeraldPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("checkout_tenure_${tenure.months}m")
                                )
                            }
                        }

                        // Credit Limit Validation Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (creditEligible) VerifiedGreenSoft else OverdueRedSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (creditEligible) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (creditEligible) VerifiedGreen else OverdueRed
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (creditEligible) strings.creditCheckPassed else strings.creditCheckFailed,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (creditEligible) EmeraldDark else OverdueRed
                                    )
                                    Text(
                                        text = "Financed: ${formatBdt(breakdown.financedPrincipalBdt)} • Available Limit: ${formatBdt(uiState.availableCreditLimitBdt)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                if (!isKycVerified) {
                                    OutlinedButton(onClick = onOpenKyc) {
                                        Text(strings.verifyNow)
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = OutlineHairline)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strings.downPaymentTodayLabel, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = formatBdt(breakdown.downPaymentTodayBdt),
                                style = MaterialTheme.typography.titleSmall.merge(MonoNumberStyle),
                                color = EmeraldDark
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${strings.monthlyInstallmentLabel} (${selectedTenure.months}x)", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = formatBdt(breakdown.monthlyAmount),
                                style = MaterialTheme.typography.titleSmall.merge(MonoNumberStyle),
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }

        // Wallet / Bank Transfer & Transaction ID Submission
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(strings.walletPaymentTitle, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentChannel.entries.forEach { channel ->
                            val selected = channel == selectedChannel
                            val accent = when (channel) {
                                PaymentChannel.BKASH -> BkashPink
                                PaymentChannel.NAGAD -> NagadOrange
                                PaymentChannel.ROCKET -> RocketPurple
                                PaymentChannel.BANK_TRANSFER -> EmeraldPrimary
                            }
                            Surface(
                                onClick = { selectedChannel = channel },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selected) accent else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("channel_${channel.name}")
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (channel == PaymentChannel.BANK_TRANSFER) "Bank" else channel.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Send ${formatBdt(amountDueNow)} to ${selectedChannel.walletAccount}",
                            style = MaterialTheme.typography.labelLarge,
                            color = EmeraldDark,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = txId,
                        onValueChange = { txId = it },
                        label = { Text(strings.txIdLabel) },
                        placeholder = { Text(strings.txIdHint) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_txid_input")
                    )

                    OutlinedButton(
                        onClick = {
                            receiptPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_upload_receipt")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (receiptUri != null) strings.uploadedLabel else strings.uploadReceiptBtn
                        )
                    }

                    Button(
                        onClick = {
                            onPlaceOrder(
                                deliveryAddress,
                                useInstallments,
                                selectedTenure,
                                selectedChannel,
                                txId
                            )
                        },
                        enabled = (!useInstallments || creditEligible) && deliveryAddress.isNotBlank() && txId.isNotBlank(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("checkout_place_order_btn")
                    ) {
                        Text(
                            text = "${strings.placeOrderBtn} • ${formatBdt(amountDueNow)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
