package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        JobEntity::class,
        TimeLogEntity::class,
        LeadEntity::class,
        ChatMessageEntity::class,
        PaymentEntity::class,
        ReviewEntity::class,
        ExpenseEntity::class,
        RoomScanEntity::class,
        ProjectTaskEntity::class,
        CustomerBookingEntity::class,
        MarketplaceBidEntity::class,
        FreightLoadEntity::class,
        ProjectMilestoneEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobDao(): JobDao
    abstract fun timeLogDao(): TimeLogDao
    abstract fun leadDao(): LeadDao
    abstract fun chatDao(): ChatDao
    abstract fun paymentDao(): PaymentDao
    abstract fun reviewDao(): ReviewDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun roomScanDao(): RoomScanDao
    abstract fun projectTaskDao(): ProjectTaskDao
    abstract fun customerBookingDao(): CustomerBookingDao
    abstract fun marketplaceBidDao(): MarketplaceBidDao
    abstract fun freightLoadDao(): FreightLoadDao
    abstract fun projectMilestoneDao(): ProjectMilestoneDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "c520x.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
