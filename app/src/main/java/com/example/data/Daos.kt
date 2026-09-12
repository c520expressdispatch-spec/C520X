package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY sortOrder ASC, id DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: Int): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

    @Update
    suspend fun updateJob(job: JobEntity)

    @Query("UPDATE jobs SET status = :status WHERE id = :id")
    suspend fun updateJobStatus(id: Int, status: String)

    @Query("UPDATE jobs SET sortOrder = :newOrder WHERE id = :id")
    suspend fun updateJobSortOrder(id: Int, newOrder: Int)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJob(id: Int)
}

@Dao
interface TimeLogDao {
    @Query("SELECT * FROM time_logs ORDER BY timestamp DESC")
    fun getAllTimeLogs(): Flow<List<TimeLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeLog(log: TimeLogEntity): Long

    @Query("DELETE FROM time_logs WHERE id = :id")
    suspend fun deleteTimeLog(id: Int)
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("UPDATE leads SET status = :status WHERE id = :id")
    suspend fun updateLeadStatus(id: Int, status: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(msgs: List<ChatMessageEntity>)

    @Update
    suspend fun updateMessage(msg: ChatMessageEntity)

    @Query("UPDATE chat_messages SET message = :newMessage WHERE id = :id")
    suspend fun updateMessageText(id: Int, newMessage: String)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY id DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("UPDATE payments SET status = :status WHERE id = :id")
    suspend fun updatePaymentStatus(id: Int, status: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("UPDATE expenses SET qboSynced = 1, qboTxnId = :txnId, qboSyncTimestamp = :timestamp WHERE id = :id")
    suspend fun markExpenseQboSynced(id: Int, txnId: String, timestamp: Long)

    @Query("UPDATE expenses SET qboSynced = 1, qboTxnId = :txnId, qboSyncTimestamp = :timestamp WHERE qboSynced = 0")
    suspend fun syncAllPendingExpenses(txnId: String, timestamp: Long)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Int)
}

@Dao
interface RoomScanDao {
    @Query("SELECT * FROM room_scans ORDER BY id DESC")
    fun getAllRoomScans(): Flow<List<RoomScanEntity>>

    @Query("SELECT * FROM room_scans WHERE id = :id")
    suspend fun getRoomScanById(id: Int): RoomScanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomScan(scan: RoomScanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoomScans(scans: List<RoomScanEntity>)

    @Update
    suspend fun updateRoomScan(scan: RoomScanEntity)

    @Query("DELETE FROM room_scans WHERE id = :id")
    suspend fun deleteRoomScan(id: Int)
}

@Dao
interface ProjectTaskDao {
    @Query("SELECT * FROM project_tasks ORDER BY sortOrder ASC, id ASC")
    fun getAllTasks(): Flow<List<ProjectTaskEntity>>

    @Query("SELECT * FROM project_tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): ProjectTaskEntity?

    @Query("SELECT * FROM project_tasks WHERE state = :state ORDER BY sortOrder ASC, id ASC")
    fun getTasksByState(state: String): Flow<List<ProjectTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ProjectTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<ProjectTaskEntity>)

    @Update
    suspend fun updateTask(task: ProjectTaskEntity)

    @Query("UPDATE project_tasks SET state = :state, sortOrder = :sortOrder, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskStateAndOrder(id: Int, state: String, sortOrder: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE project_tasks SET state = :state, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskState(id: Int, state: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE project_tasks SET priority = :priority, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTaskPriority(id: Int, priority: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE project_tasks SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateTaskSortOrder(id: Int, sortOrder: Int)

    @Query("DELETE FROM project_tasks WHERE id = :id")
    suspend fun deleteTask(id: Int)
}

@Dao
interface CustomerBookingDao {
    @Query("SELECT * FROM customer_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<CustomerBookingEntity>>

    @Query("SELECT * FROM customer_bookings WHERE id = :id")
    suspend fun getBookingById(id: Int): CustomerBookingEntity?

    @Query("SELECT * FROM customer_bookings WHERE status = 'Open For Bids' ORDER BY id DESC")
    fun getOpenMarketplaceBookings(): Flow<List<CustomerBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: CustomerBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<CustomerBookingEntity>)

    @Update
    suspend fun updateBooking(booking: CustomerBookingEntity)

    @Query("UPDATE customer_bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Query("UPDATE customer_bookings SET assignedProName = :proName, acceptedQuoteAmount = :quoteAmount, status = :status WHERE id = :id")
    suspend fun assignProAndAccept(id: Int, proName: String, quoteAmount: Double, status: String = "Booked")

    @Query("UPDATE customer_bookings SET bidsCount = bidsCount + 1 WHERE id = :id")
    suspend fun incrementBidsCount(id: Int)

    @Query("DELETE FROM customer_bookings WHERE id = :id")
    suspend fun deleteBooking(id: Int)
}

@Dao
interface MarketplaceBidDao {
    @Query("SELECT * FROM marketplace_bids WHERE bookingRequestId = :requestId ORDER BY bidAmount ASC")
    fun getBidsForBooking(requestId: Int): Flow<List<MarketplaceBidEntity>>

    @Query("SELECT * FROM marketplace_bids ORDER BY id DESC")
    fun getAllBids(): Flow<List<MarketplaceBidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBid(bid: MarketplaceBidEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBids(bids: List<MarketplaceBidEntity>)

    @Query("UPDATE marketplace_bids SET isAccepted = 1 WHERE id = :bidId")
    suspend fun markBidAccepted(bidId: Int)

    @Query("DELETE FROM marketplace_bids WHERE id = :id")
    suspend fun deleteBid(id: Int)
}

@Dao
interface FreightLoadDao {
    @Query("SELECT * FROM freight_loads ORDER BY id DESC")
    fun getAllLoads(): Flow<List<FreightLoadEntity>>

    @Query("SELECT * FROM freight_loads WHERE id = :id")
    suspend fun getLoadById(id: Int): FreightLoadEntity?

    @Query("SELECT * FROM freight_loads WHERE status = 'Available' ORDER BY ratePerMile DESC")
    fun getAvailableLoads(): Flow<List<FreightLoadEntity>>

    @Query("SELECT * FROM freight_loads WHERE status IN ('Booked', 'Dispatched', 'In Transit', 'Delivered') ORDER BY id DESC")
    fun getActiveHauls(): Flow<List<FreightLoadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoad(load: FreightLoadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoads(loads: List<FreightLoadEntity>)

    @Update
    suspend fun updateLoad(load: FreightLoadEntity)

    @Query("UPDATE freight_loads SET status = :status WHERE id = :id")
    suspend fun updateLoadStatus(id: Int, status: String)

    @Query("UPDATE freight_loads SET status = 'Booked', driverAssigned = :driverName, rateConSigned = 1 WHERE id = :id")
    suspend fun bookLoad(id: Int, driverName: String)

    @Query("UPDATE freight_loads SET status = :status, currentGpsLocation = :gps WHERE id = :id")
    suspend fun updateLoadTransit(id: Int, status: String, gps: String)

    @Query("UPDATE freight_loads SET status = 'Delivered', bolNumber = :bol WHERE id = :id")
    suspend fun deliverLoad(id: Int, bol: String)

    @Query("UPDATE freight_loads SET isSaved = CASE WHEN isSaved = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleSaveLoad(id: Int)

    @Query("DELETE FROM freight_loads WHERE id = :id")
    suspend fun deleteLoad(id: Int)
}

@Dao
interface ProjectMilestoneDao {
    @Query("SELECT * FROM project_milestones WHERE bookingId = :bookingId ORDER BY phaseNumber ASC")
    fun getMilestonesForBooking(bookingId: Int): Flow<List<ProjectMilestoneEntity>>

    @Query("SELECT * FROM project_milestones ORDER BY phaseNumber ASC")
    fun getAllMilestones(): Flow<List<ProjectMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: ProjectMilestoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<ProjectMilestoneEntity>)

    @Update
    suspend fun updateMilestone(milestone: ProjectMilestoneEntity)

    @Query("UPDATE project_milestones SET status = :status, completedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateMilestoneStatus(id: Int, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM project_milestones WHERE id = :id")
    suspend fun deleteMilestone(id: Int)
}


