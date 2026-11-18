package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class WooCategoryDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "count") val count: Int
)

@JsonClass(generateAdapter = true)
data class WooProductImageDto(
    @Json(name = "id") val id: Long,
    @Json(name = "src") val src: String
)

@JsonClass(generateAdapter = true)
data class WooProductDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: String,
    @Json(name = "regular_price") val regularPrice: String?,
    @Json(name = "short_description") val shortDescription: String?,
    @Json(name = "average_rating") val averageRating: String?,
    @Json(name = "rating_count") val ratingCount: Int?,
    @Json(name = "images") val images: List<WooProductImageDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WooLineItemRequest(
    @Json(name = "product_id") val productId: Long,
    @Json(name = "quantity") val quantity: Int
)

@JsonClass(generateAdapter = true)
data class WooBillingAddress(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "address_1") val address1: String,
    @Json(name = "city") val city: String,
    @Json(name = "phone") val phone: String
)

@JsonClass(generateAdapter = true)
data class WooCreateOrderRequest(
    @Json(name = "payment_method") val paymentMethod: String,
    @Json(name = "payment_method_title") val paymentMethodTitle: String,
    @Json(name = "set_paid") val setPaid: Boolean,
    @Json(name = "billing") val billing: WooBillingAddress,
    @Json(name = "line_items") val lineItems: List<WooLineItemRequest>,
    @Json(name = "customer_note") val customerNote: String? = null
)

@JsonClass(generateAdapter = true)
data class WooOrderResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "number") val number: String,
    @Json(name = "status") val status: String,
    @Json(name = "total") val total: String
)

/**
 * WooCommerce REST API v3 client for rimvohut.com
 */
interface WooCommerceApiService {
    @GET("wp-json/wc/v3/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): Response<List<WooProductDto>>

    @GET("wp-json/wc/v3/products/categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 30
    ): Response<List<WooCategoryDto>>

    @POST("wp-json/wc/v3/orders")
    suspend fun createOrder(
        @Body request: WooCreateOrderRequest
    ): Response<WooOrderResponse>
}
