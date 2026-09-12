package com.example.data

import java.util.UUID

/**
 * Contractor availability model synchronized with Firebase Firestore `contractor_availability` collection.
 */
data class ContractorAvailability(
    val contractorId: String,
    val contractorName: String,
    val trade: String,
    val date: String, // e.g. "2026-09-11"
    val startTime: String = "08:00 AM",
    val endTime: String = "05:00 PM",
    val status: String = "Available", // "Available", "Booked", "Emergency On-Call", "Off Duty"
    val maxJobsPerDay: Int = 4,
    val activeJobsCount: Int = 1,
    val notes: String = "",
    val phone: String = "(520) 555-0199",
    val syncedWithFirestore: Boolean = true
) {
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "contractorId" to contractorId,
            "contractorName" to contractorName,
            "trade" to trade,
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
            "status" to status,
            "maxJobsPerDay" to maxJobsPerDay,
            "activeJobsCount" to activeJobsCount,
            "notes" to notes,
            "phone" to phone,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    companion object {
        fun fromFirestoreMap(id: String, map: Map<String, Any>): ContractorAvailability {
            return ContractorAvailability(
                contractorId = map["contractorId"] as? String ?: id,
                contractorName = map["contractorName"] as? String ?: "Contractor",
                trade = map["trade"] as? String ?: "General",
                date = map["date"] as? String ?: "2026-09-11",
                startTime = map["startTime"] as? String ?: "08:00 AM",
                endTime = map["endTime"] as? String ?: "05:00 PM",
                status = map["status"] as? String ?: "Available",
                maxJobsPerDay = (map["maxJobsPerDay"] as? Number)?.toInt() ?: 4,
                activeJobsCount = (map["activeJobsCount"] as? Number)?.toInt() ?: 0,
                notes = map["notes"] as? String ?: "",
                phone = map["phone"] as? String ?: "(520) 555-0199",
                syncedWithFirestore = true
            )
        }
    }
}

/**
 * Calendar Job Assignment model synchronized with Firebase Firestore `calendar_job_assignments` collection.
 */
data class CalendarJobAssignment(
    val id: String = UUID.randomUUID().toString(),
    val jobId: Int = 0,
    val title: String,
    val trade: String,
    val clientName: String,
    val clientPhone: String,
    val address: String,
    val contractorId: String,
    val contractorName: String,
    val date: String, // e.g. "2026-09-11"
    val startHour: Int = 9, // 0 - 23
    val startMinute: Int = 0, // 0 or 30
    val durationHours: Double = 2.0, // e.g. 1.5, 2.0, 3.0
    val status: String = "Scheduled", // "Scheduled", "Dispatched", "In Progress", "Completed"
    val priority: String = "Normal", // "Normal", "High", "Emergency"
    val colorHex: Long = 0xFF1A73E8, // Google Calendar color
    val notes: String = "",
    val syncedWithFirestore: Boolean = true
) {
    val timeSlotFormatted: String
        get() {
            val endTotalMinutes = (startHour * 60 + startMinute + (durationHours * 60)).toInt()
            val endH = (endTotalMinutes / 60) % 24
            val endM = endTotalMinutes % 60
            return "${formatHourMinute(startHour, startMinute)} – ${formatHourMinute(endH, endM)}"
        }

    private fun formatHourMinute(hour: Int, min: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when (hour) {
            0 -> 12
            in 1..12 -> hour
            else -> hour - 12
        }
        return "%d:%02d %s".format(displayHour, min, amPm)
    }

    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "jobId" to jobId,
            "title" to title,
            "trade" to trade,
            "clientName" to clientName,
            "clientPhone" to clientPhone,
            "address" to address,
            "contractorId" to contractorId,
            "contractorName" to contractorName,
            "date" to date,
            "startHour" to startHour,
            "startMinute" to startMinute,
            "durationHours" to durationHours,
            "status" to status,
            "priority" to priority,
            "colorHex" to colorHex,
            "notes" to notes,
            "updatedAt" to System.currentTimeMillis()
        )
    }

    companion object {
        fun fromFirestoreMap(id: String, map: Map<String, Any>): CalendarJobAssignment {
            return CalendarJobAssignment(
                id = map["id"] as? String ?: id,
                jobId = (map["jobId"] as? Number)?.toInt() ?: 0,
                title = map["title"] as? String ?: "Job Assignment",
                trade = map["trade"] as? String ?: "Electrical",
                clientName = map["clientName"] as? String ?: "Customer",
                clientPhone = map["clientPhone"] as? String ?: "(520) 555-0188",
                address = map["address"] as? String ?: "Tucson, AZ",
                contractorId = map["contractorId"] as? String ?: "alex_ramirez",
                contractorName = map["contractorName"] as? String ?: "Alex Ramirez",
                date = map["date"] as? String ?: "2026-09-11",
                startHour = (map["startHour"] as? Number)?.toInt() ?: 9,
                startMinute = (map["startMinute"] as? Number)?.toInt() ?: 0,
                durationHours = (map["durationHours"] as? Number)?.toDouble() ?: 2.0,
                status = map["status"] as? String ?: "Scheduled",
                priority = map["priority"] as? String ?: "Normal",
                colorHex = (map["colorHex"] as? Number)?.toLong() ?: 0xFF1A73E8,
                notes = map["notes"] as? String ?: "",
                syncedWithFirestore = true
            )
        }
    }
}
