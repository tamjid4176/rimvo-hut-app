package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int
)

@Entity(tableName = "installment_orders")
data class InstallmentOrderEntity(
    @PrimaryKey val orderId: String,
    val productTitle: String,
    val brand: String,
    val totalPayableBdt: Int,
    val downPaymentPaidBdt: Int,
    val financedAmountBdt: Int,
    val tenureMonths: Int,
    val createdDateLabel: String,
    val paymentChannel: String,
    val downPaymentTxId: String,
    val createdTimestamp: Long
)

@Entity(tableName = "installment_entries")
data class InstallmentEntryEntity(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val orderId: String,
    val installmentNumber: Int,
    val dueDateLabel: String,
    val amountBdt: Int,
    val status: String,
    val paidAtLabel: String? = null,
    val receiptTxId: String? = null,
    val paidChannel: String? = null
)

@Entity(tableName = "kyc_submissions")
data class KycSubmissionEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val nidNumber: String,
    val dateOfBirth: String,
    val presentAddress: String,
    val nomineeName: String,
    val nomineePhone: String,
    val nomineeRelation: String,
    val nidFrontUri: String?,
    val nidBackUri: String?,
    val utilityBillUri: String?,
    val selfieCompleted: Boolean,
    val status: String,
    val reviewerNote: String
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun observeCart(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteById(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface InstallmentDao {
    @Query("SELECT * FROM installment_orders ORDER BY createdTimestamp DESC")
    fun observeOrders(): Flow<List<InstallmentOrderEntity>>

    @Query("SELECT * FROM installment_entries ORDER BY orderId, installmentNumber ASC")
    fun observeAllEntries(): Flow<List<InstallmentEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: InstallmentOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<InstallmentEntryEntity>)

    @Query(
        "UPDATE installment_entries SET status = :status, paidAtLabel = :paidAt, receiptTxId = :txId, paidChannel = :channel WHERE entryId = :entryId"
    )
    suspend fun markEntryPaid(
        entryId: Long,
        status: String,
        paidAt: String,
        txId: String,
        channel: String
    )

    @Query("SELECT COUNT(*) FROM installment_orders")
    suspend fun countOrders(): Int
}

@Dao
interface KycDao {
    @Query("SELECT * FROM kyc_submissions WHERE id = 1")
    fun observeKyc(): Flow<KycSubmissionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertKyc(kyc: KycSubmissionEntity)
}

@Database(
    entities = [
        CartItemEntity::class,
        InstallmentOrderEntity::class,
        InstallmentEntryEntity::class,
        KycSubmissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RimvoDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun installmentDao(): InstallmentDao
    abstract fun kycDao(): KycDao

    companion object {
        @Volatile
        private var INSTANCE: RimvoDatabase? = null

        fun getInstance(context: Context): RimvoDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    RimvoDatabase::class.java,
                    "rimvo_hut.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
