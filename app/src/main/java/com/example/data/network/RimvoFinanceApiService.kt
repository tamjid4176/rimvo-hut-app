package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class OtpRequestDto(
    @Json(name = "phone_number") val phoneNumber: String
)

@JsonClass(generateAdapter = true)
data class OtpVerifyDto(
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "otp_code") val otpCode: String
)

@JsonClass(generateAdapter = true)
data class AuthTokenResponseDto(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String = "Bearer",
    @Json(name = "expires_in") val expiresIn: Long
)

@JsonClass(generateAdapter = true)
data class KycSubmitRequestDto(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "nid_number") val nidNumber: String,
    @Json(name = "date_of_birth") val dateOfBirth: String,
    @Json(name = "present_address") val presentAddress: String,
    @Json(name = "nominee_name") val nomineeName: String,
    @Json(name = "nominee_phone") val nomineePhone: String,
    @Json(name = "nominee_relation") val nomineeRelation: String,
    @Json(name = "nid_front_ref") val nidFrontRef: String,
    @Json(name = "nid_back_ref") val nidBackRef: String,
    @Json(name = "utility_bill_ref") val utilityBillRef: String,
    @Json(name = "selfie_verified") val selfieVerified: Boolean
)

@JsonClass(generateAdapter = true)
data class KycStatusResponseDto(
    @Json(name = "kyc_status") val kycStatus: String,
    @Json(name = "reviewer_note") val reviewerNote: String? = null
)

@JsonClass(generateAdapter = true)
data class CreditLimitResponseDto(
    @Json(name = "total_limit_bdt") val totalLimitBdt: Int,
    @Json(name = "used_limit_bdt") val usedLimitBdt: Int,
    @Json(name = "available_limit_bdt") val availableLimitBdt: Int,
    @Json(name = "kyc_verified") val kycVerified: Boolean
)

@JsonClass(generateAdapter = true)
data class CreateInstallmentPlanRequestDto(
    @Json(name = "woo_order_id") val wooOrderId: String,
    @Json(name = "tenure_months") val tenureMonths: Int,
    @Json(name = "cash_price_bdt") val cashPriceBdt: Int,
    @Json(name = "down_payment_bdt") val downPaymentBdt: Int,
    @Json(name = "payment_channel") val paymentChannel: String,
    @Json(name = "down_payment_tx_id") val downPaymentTxId: String
)

@JsonClass(generateAdapter = true)
data class InstallmentPlanDto(
    @Json(name = "plan_id") val planId: String,
    @Json(name = "order_id") val orderId: String,
    @Json(name = "status") val status: String,
    @Json(name = "remaining_bdt") val remainingBdt: Int
)

@JsonClass(generateAdapter = true)
data class RepaymentRequestDto(
    @Json(name = "plan_id") val planId: String,
    @Json(name = "installment_number") val installmentNumber: Int,
    @Json(name = "amount_bdt") val amountBdt: Int,
    @Json(name = "payment_channel") val paymentChannel: String,
    @Json(name = "transaction_id") val transactionId: String
)

@JsonClass(generateAdapter = true)
data class RepaymentReceiptDto(
    @Json(name = "receipt_no") val receiptNo: String,
    @Json(name = "paid_at") val paidAt: String,
    @Json(name = "status") val status: String
)

/**
 * Custom Rimvo Finance BNPL REST API endpoints.
 */
interface RimvoFinanceApiService {
    @POST("api/v1/auth/otp/request")
    suspend fun requestOtp(@Body body: OtpRequestDto): Response<Unit>

    @POST("api/v1/auth/otp/verify")
    suspend fun verifyOtp(@Body body: OtpVerifyDto): Response<AuthTokenResponseDto>

    @POST("api/v1/kyc")
    suspend fun submitKyc(@Body body: KycSubmitRequestDto): Response<KycStatusResponseDto>

    @GET("api/v1/kyc")
    suspend fun getKycStatus(): Response<KycStatusResponseDto>

    @GET("api/v1/credit-limit")
    suspend fun getCreditLimit(): Response<CreditLimitResponseDto>

    @GET("api/v1/installment-plans")
    suspend fun getInstallmentPlans(
        @Query("status") status: String? = null
    ): Response<List<InstallmentPlanDto>>

    @POST("api/v1/installment-plans")
    suspend fun createInstallmentPlan(
        @Body body: CreateInstallmentPlanRequestDto
    ): Response<InstallmentPlanDto>

    @POST("api/v1/repayments")
    suspend fun submitRepayment(
        @Body body: RepaymentRequestDto
    ): Response<RepaymentReceiptDto>
}
