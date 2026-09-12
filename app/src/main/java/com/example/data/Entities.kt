package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val trade: String, // Plumbing, Electrical, HVAC, Carpentry, Painting, Handyman, Roofing
    val clientName: String,
    val clientPhone: String,
    val address: String,
    val status: String, // Pending Estimate, Scheduled, Dispatched, In Progress, Completed
    val billedAmount: Double,
    val pipelineValue: Double,
    val scheduledDate: String,
    val assignedTech: String,
    val priority: String = "Normal", // High, Medium, Normal
    val notes: String = "",
    val sortOrder: Int = 0
)

@Entity(tableName = "time_logs")
data class TimeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobId: Int,
    val jobTitle: String,
    val trade: String,
    val techName: String,
    val startTimeFormatted: String,
    val durationSeconds: Long,
    val hourlyRate: Double,
    val billableAmount: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val clientPhone: String,
    val serviceTrade: String,
    val projectDescription: String,
    val budget: Double,
    val source: String, // Thumbtack Pro, TaskRabbit, Direct Call, Yelp
    val status: String, // New Lead, Contacted, Quoted, Booked
    val urgency: String = "This Week",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: String = "general",
    val senderRole: String, // Dispatch, Client, Tech
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val attachmentType: String = "none", // none, image, invoice_quote, pdf
    val attachmentName: String = ""
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceNumber: String,
    val clientName: String,
    val trade: String,
    val amount: Double,
    val status: String, // Paid, Pending, Processing
    val dateFormatted: String,
    val paymentMethod: String, // Stripe Gateway, Square, Direct ACH, Apple Pay
    val transactionRef: String = ""
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val trade: String,
    val techName: String,
    val rating: Float, // 1.0 to 5.0
    val reviewText: String,
    val date: String,
    val verifiedCustomer: Boolean = true,
    val companyResponse: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobId: Int = 0,
    val jobTitle: String,
    val vendor: String,
    val category: String, // Job Materials, Subcontractor Labor, Equipment Rental, Permits & Fees, Jobsite Supplies
    val amount: Double,
    val dateFormatted: String,
    val receiptName: String = "receipt_doc.pdf",
    val billableToClient: Boolean = true,
    val qboSynced: Boolean = false,
    val qboTxnId: String = "",
    val qboSyncTimestamp: Long = 0L,
    val taxDeductible: Boolean = true
)

@Entity(tableName = "room_scans")
data class RoomScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jobId: Int = 0,
    val roomName: String,
    val clientName: String,
    val lengthFeet: Double,
    val widthFeet: Double,
    val ceilingHeightFeet: Double,
    val areaSqFt: Double,
    val wallCount: Int = 4,
    val doorCount: Int = 1,
    val windowCount: Int = 2,
    val snappedPhotos: String = "North Plumbing Rough-in, Load-bearing Post, Countertop Bay, Ceiling Can Light Layout",
    val photoCount: Int = 4,
    val scanDate: String,
    val renderStyle: String = "Modern Minimalist",
    val tradeType: String = "Remodeler",
    val status: String = "Render Ready"
)

@Entity(tableName = "project_tasks")
data class ProjectTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int = 1,
    val projectName: String,
    val title: String,
    val description: String = "",
    val state: String, // Backlog, To Do, In Progress, In Review, Done
    val priority: String, // Urgent, High, Medium, Low
    val assignedTech: String = "Alex Ramirez (Lead)",
    val dueDate: String = "Today",
    val estimatedHours: Double = 4.0,
    val trade: String = "Carpentry",
    val sortOrder: Int = 0,
    val checklistDone: Int = 0,
    val checklistTotal: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_bookings")
data class CustomerBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val address: String,
    val category: String, // Plumbing, Electrical, HVAC, Remodeling, Roofing, Handyman, Carpentry, Painting, Cleaning, Moving & Freight
    val projectTitle: String,
    val description: String,
    val budget: Double,
    val preferredDate: String,
    val urgency: String = "Within 48 Hours", // Emergency Today, Within 48 Hours, Flexible This Week
    val status: String = "Open For Bids", // Open For Bids, Booked, Dispatched, In Progress, Completed, Cancelled
    val assignedProName: String = "",
    val assignedProRating: Float = 4.9f,
    val assignedProBadge: String = "Thumbtack Top Pro • Yelp 5★",
    val acceptedQuoteAmount: Double = 0.0,
    val bidsCount: Int = 0,
    val completionNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val serviceType: String = "Residential" // "Residential" or "Commercial"
)

@Entity(tableName = "project_milestones")
data class ProjectMilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val title: String,
    val description: String = "",
    val phaseNumber: Int = 1,
    val targetDate: String = "In 3 Days",
    val drawAmount: Double = 0.0,
    val status: String = "Pending", // "Pending", "In Progress", "Completed"
    val completedTimestamp: Long = 0L,
    val verificationNotes: String = ""
)

@Entity(tableName = "marketplace_bids")
data class MarketplaceBidEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingRequestId: Int,
    val proName: String,
    val proCompany: String,
    val proRating: Float, // 4.8, 4.9, 5.0
    val reviewCount: Int,
    val badge: String, // Thumbtack Top Pro, Angie Certified, TaskRabbit Elite, Yelp Recommended
    val bidAmount: Double,
    val estimatedHours: Double,
    val earliestAvailability: String,
    val proposalNote: String,
    val isAccepted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "freight_loads")
data class FreightLoadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val loadNumber: String, // e.g. DAT-90214
    val originCity: String, // e.g. Phoenix, AZ
    val destinationCity: String, // e.g. Los Angeles, CA
    val deadheadMiles: Int,
    val tripMiles: Int,
    val equipmentType: String, // Dry Van, Reefer, Flatbed, Box Truck 26ft, Hotshot, Power Only, Cargo Van / Sprinter
    val weightLbs: Int,
    val commodity: String,
    val rateTotal: Double,
    val ratePerMile: Double,
    val brokerName: String,
    val brokerPhone: String = "(520) 880-9200",
    val brokerCreditScore: Int = 98,
    val daysToPay: String = "QuickPay 24hr",
    val pickupDate: String,
    val deliveryDate: String,
    val status: String = "Available", // Available, Bidded, Booked, Dispatched, In Transit, Delivered, Factored
    val isSaved: Boolean = false,
    val rateConSigned: Boolean = false,
    val bolNumber: String = "",
    val driverAssigned: String = "",
    val currentGpsLocation: String = "",
    val timestamp: Long = System.currentTimeMillis()
)


