package com.example.data.model

import androidx.annotation.DrawableRes
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

enum class ProductCategory(val titleEn: String, val titleBn: String) {
    ALL("All", "সব"),
    GROCERY("Grocery Combos", "গ্রোসারি কম্বো"),
    ELECTRONICS("Electronics", "ইলেকট্রনিক্স"),
    APPLIANCES("Home Appliances", "হোম অ্যাপ্লায়েন্স"),
    KITCHEN("Kitchen & Living", "কিচেন ও লিভিং")
}

enum class InstallmentTenure(
    val months: Int,
    val markupPercent: Int,
    val downPaymentRate: Double
) {
    THREE_MONTHS(months = 3, markupPercent = 0, downPaymentRate = 0.25),
    SIX_MONTHS(months = 6, markupPercent = 4, downPaymentRate = 0.20),
    TWELVE_MONTHS(months = 12, markupPercent = 8, downPaymentRate = 0.15)
}

enum class KycStatus {
    NOT_STARTED,
    PENDING_REVIEW,
    VERIFIED,
    ACTION_REQUIRED
}

enum class InstallmentPaymentStatus {
    PAID,
    UPCOMING,
    OVERDUE
}

enum class PaymentChannel(val label: String, val walletAccount: String) {
    BKASH("bKash", "01711-009988 (Merchant)"),
    NAGAD("Nagad", "01819-009988 (Merchant)"),
    ROCKET("Rocket", "01711-009988-2"),
    BANK_TRANSFER("Bank Transfer", "Rimvo Hut Ltd • DBBL A/C 101.110.44892")
}

data class Product(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val brand: String,
    val category: ProductCategory,
    val cashPriceBdt: Int,
    val originalPriceBdt: Int,
    val rating: Double,
    val reviewCount: Int,
    @DrawableRes val imageRes: Int,
    val specsEn: List<String>,
    val specsBn: List<String>,
    val bnplEligible: Boolean = true,
    val maxTenure: InstallmentTenure = InstallmentTenure.TWELVE_MONTHS,
    val badgeEn: String = "0% 3M BNPL",
    val badgeBn: String = "০% ৩ মাসের কিস্তি"
) {
    val lowestMonthlyBdt: Int
        get() = InstallmentCalculator.calculate(cashPriceBdt, maxTenure).monthlyAmount
}

data class InstallmentSchedulePreview(
    val installmentNumber: Int,
    val dueDateLabel: String,
    val amountBdt: Int
)

data class InstallmentBreakdown(
    val tenure: InstallmentTenure,
    val cashPriceBdt: Int,
    val markupAmountBdt: Int,
    val totalPayableBdt: Int,
    val downPaymentTodayBdt: Int,
    val financedPrincipalBdt: Int,
    val monthlyAmount: Int,
    val schedule: List<InstallmentSchedulePreview>
)

object InstallmentCalculator {
    fun calculate(cashPriceBdt: Int, tenure: InstallmentTenure): InstallmentBreakdown {
        val markup = ((cashPriceBdt * tenure.markupPercent) / 100.0).roundToInt()
        val total = cashPriceBdt + markup
        val downPayment = (total * tenure.downPaymentRate).roundToInt()
        val financed = total - downPayment
        val monthly = (financed.toDouble() / tenure.months).roundToInt()

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val schedule = (1..tenure.months).map { monthOffset ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, monthOffset)
            val amount = if (monthOffset == tenure.months) {
                // Adjust final installment by any 1-2 BDT rounding difference so total matches exactly
                financed - (monthly * (tenure.months - 1))
            } else {
                monthly
            }
            InstallmentSchedulePreview(
                installmentNumber = monthOffset,
                dueDateLabel = dateFormat.format(cal.time),
                amountBdt = amount
            )
        }

        return InstallmentBreakdown(
            tenure = tenure,
            cashPriceBdt = cashPriceBdt,
            markupAmountBdt = markup,
            totalPayableBdt = total,
            downPaymentTodayBdt = downPayment,
            financedPrincipalBdt = financed,
            monthlyAmount = monthly,
            schedule = schedule
        )
    }
}

fun formatBdt(amount: Int): String {
    val nf = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "৳${nf.format(amount)}"
}

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val lineTotalBdt: Int get() = product.cashPriceBdt * quantity
}

data class KycFormState(
    val fullName: String = "",
    val nidNumber: String = "",
    val dateOfBirth: String = "",
    val presentAddress: String = "",
    val nomineeName: String = "",
    val nomineePhone: String = "",
    val nomineeRelation: String = "",
    val nidFrontUri: String? = null,
    val nidBackUri: String? = null,
    val utilityBillUri: String? = null,
    val selfieCompleted: Boolean = false,
    val status: KycStatus = KycStatus.NOT_STARTED,
    val reviewerNote: String = ""
)

data class InstallmentOrderItem(
    val orderId: String,
    val productTitle: String,
    val brand: String,
    val totalPayableBdt: Int,
    val downPaymentPaidBdt: Int,
    val financedAmountBdt: Int,
    val tenureMonths: Int,
    val createdDateLabel: String,
    val paymentChannel: String,
    val downPaymentTxId: String,
    val schedule: List<InstallmentEntry>
) {
    val paidCount: Int get() = schedule.count { it.status == InstallmentPaymentStatus.PAID }
    val isCompleted: Boolean get() = schedule.isNotEmpty() && paidCount == schedule.size
    val remainingBalanceBdt: Int
        get() = schedule.filter { it.status != InstallmentPaymentStatus.PAID }.sumOf { it.amountBdt }
    val nextDueEntry: InstallmentEntry?
        get() = schedule.firstOrNull { it.status != InstallmentPaymentStatus.PAID }
}

data class InstallmentEntry(
    val entryId: Long,
    val orderId: String,
    val installmentNumber: Int,
    val dueDateLabel: String,
    val amountBdt: Int,
    val status: InstallmentPaymentStatus,
    val paidAtLabel: String? = null,
    val receiptTxId: String? = null,
    val paidChannel: String? = null
)

data class PaymentReceipt(
    val receiptNumber: String,
    val orderId: String,
    val productTitle: String,
    val installmentNumber: Int,
    val amountPaidBdt: Int,
    val channel: String,
    val transactionId: String,
    val paidDateLabel: String
)
