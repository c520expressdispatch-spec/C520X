package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Service that interfaces with Firebase Firestore to manage contractor availability
 * and job assignments in real-time, backing the Google Calendar UI.
 */
object FirestoreCalendarService {

    private const val TAG = "FirestoreCalendar"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var availabilityListener: ListenerRegistration? = null
    private var assignmentsListener: ListenerRegistration? = null

    private val _isFirestoreConnected = MutableStateFlow(false)
    val isFirestoreConnected: StateFlow<Boolean> = _isFirestoreConnected.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow("Listening to cloud updates...")
    val lastSyncTimestamp: StateFlow<String> = _lastSyncTimestamp.asStateFlow()

    private val _contractorAvailabilities = MutableStateFlow<List<ContractorAvailability>>(emptyList())
    val contractorAvailabilities: StateFlow<List<ContractorAvailability>> = _contractorAvailabilities.asStateFlow()

    private val _jobAssignments = MutableStateFlow<List<CalendarJobAssignment>>(emptyList())
    val jobAssignments: StateFlow<List<CalendarJobAssignment>> = _jobAssignments.asStateFlow()

    init {
        // Populate baseline availability and assignments so UI renders immediately
        seedBaselineData()
        startFirestoreSync()
    }

    private fun seedBaselineData() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val baselineContractors = listOf(
            ContractorAvailability(
                contractorId = "alex_ramirez",
                contractorName = "Alex Ramirez (Lead Tech)",
                trade = "Electrical",
                date = todayStr,
                startTime = "08:00 AM",
                endTime = "05:00 PM",
                status = "Available",
                maxJobsPerDay = 4,
                activeJobsCount = 2,
                notes = "Certified Master Electrician. Van #12 equipped with 200A panels.",
                phone = "(520) 441-8920"
            ),
            ContractorAvailability(
                contractorId = "marcus_vance",
                contractorName = "Marcus Vance",
                trade = "HVAC",
                date = todayStr,
                startTime = "07:30 AM",
                endTime = "04:30 PM",
                status = "Booked",
                maxJobsPerDay = 3,
                activeJobsCount = 2,
                notes = "EPA Universal Certified. Specializes in multi-zone heat pump installs.",
                phone = "(520) 773-9104"
            ),
            ContractorAvailability(
                contractorId = "carlos_mendez",
                contractorName = "Carlos Mendez",
                trade = "Plumbing",
                date = todayStr,
                startTime = "08:30 AM",
                endTime = "06:00 PM",
                status = "Available",
                maxJobsPerDay = 5,
                activeJobsCount = 1,
                notes = "Hydro-jetter & camera scope on board. Ready for slab leak calls.",
                phone = "(520) 338-1922"
            ),
            ContractorAvailability(
                contractorId = "elena_rostova",
                contractorName = "Elena Rostova",
                trade = "Carpentry",
                date = todayStr,
                startTime = "09:00 AM",
                endTime = "05:00 PM",
                status = "Emergency On-Call",
                maxJobsPerDay = 3,
                activeJobsCount = 1,
                notes = "Cabinetry & framing specialist. Available for commercial dispatch.",
                phone = "(520) 890-4421"
            )
        )
        _contractorAvailabilities.value = baselineContractors

        val baselineAssignments = listOf(
            CalendarJobAssignment(
                id = "assign_elec_101",
                jobId = 101,
                title = "200A Main Service Panel Upgrade",
                trade = "Electrical",
                clientName = "Sarah Jenkins",
                clientPhone = "(520) 882-3901",
                address = "4820 E Sunrise Dr, Tucson, AZ",
                contractorId = "alex_ramirez",
                contractorName = "Alex Ramirez (Lead Tech)",
                date = todayStr,
                startHour = 8,
                startMinute = 30,
                durationHours = 3.0,
                status = "In Progress",
                priority = "High",
                colorHex = 0xFF1A73E8, // Google Blue
                notes = "Utility permit inspected. TEP coordination complete."
            ),
            CalendarJobAssignment(
                id = "assign_hvac_102",
                jobId = 102,
                title = "Emergency AC Compressor Failure",
                trade = "HVAC",
                clientName = "Desert Ridge Medical Clinic",
                clientPhone = "(520) 624-8119",
                address = "1200 N Campbell Ave, Tucson, AZ",
                contractorId = "marcus_vance",
                contractorName = "Marcus Vance",
                date = todayStr,
                startHour = 10,
                startMinute = 0,
                durationHours = 2.5,
                status = "Dispatched",
                priority = "Emergency",
                colorHex = 0xFFD50000, // Google Tomato Red
                notes = "Patient rooms above 82°F. Expedite R410A charge and contactor swap."
            ),
            CalendarJobAssignment(
                id = "assign_plumb_103",
                jobId = 103,
                title = "Commercial Water Heater Replacement",
                trade = "Plumbing",
                clientName = "Robert Chen (Bistro 520)",
                clientPhone = "(520) 791-4500",
                address = "254 E Congress St, Tucson, AZ",
                contractorId = "carlos_mendez",
                contractorName = "Carlos Mendez",
                date = todayStr,
                startHour = 13,
                startMinute = 0,
                durationHours = 2.0,
                status = "Scheduled",
                priority = "Normal",
                colorHex = 0xFF0B8043, // Google Sage Green
                notes = "75-gallon commercial unit staging on site. Shut off valve verified."
            ),
            CalendarJobAssignment(
                id = "assign_carp_104",
                jobId = 104,
                title = "Structural Subfloor & Framing Repair",
                trade = "Carpentry",
                clientName = "Foothills HOA Clubhouse",
                clientPhone = "(520) 577-8022",
                address = "6400 N Swan Rd, Tucson, AZ",
                contractorId = "elena_rostova",
                contractorName = "Elena Rostova",
                date = todayStr,
                startHour = 15,
                startMinute = 30,
                durationHours = 2.0,
                status = "Scheduled",
                priority = "Normal",
                colorHex = 0xFFF4511E, // Google Tangerine Orange
                notes = "Termite damage replacement near entry sill plate."
            )
        )
        _jobAssignments.value = baselineAssignments
    }

    private fun getFirestoreInstance(): FirebaseFirestore? {
        return try {
            val app = com.google.firebase.FirebaseApp.getInstance()
            FirebaseFirestore.getInstance(app)
        } catch (e: Exception) {
            null
        }
    }

    fun startFirestoreSync() {
        val firestore = getFirestoreInstance()
        if (firestore == null) {
            _isFirestoreConnected.value = false
            _lastSyncTimestamp.value = "Offline cache active (Local DB synced)"
            return
        }
        try {
            // 1. Listen to Contractor Availability in real-time
            availabilityListener?.remove()
            availabilityListener = firestore.collection("contractor_availability")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore availability listener error: ${error.message}")
                        _isFirestoreConnected.value = false
                        return@addSnapshotListener
                    }

                    _isFirestoreConnected.value = true
                    updateSyncTime()

                    if (snapshot != null && !snapshot.isEmpty) {
                        val cloudItems = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { ContractorAvailability.fromFirestoreMap(doc.id, it) }
                        }
                        if (cloudItems.isNotEmpty()) {
                            _contractorAvailabilities.value = cloudItems
                        }
                    } else if (snapshot != null && snapshot.isEmpty) {
                        // Push baseline to cloud to initialize collection
                        pushBaselineAvailabilityToCloud()
                    }
                }

            // 2. Listen to Job Assignments in real-time
            assignmentsListener?.remove()
            assignmentsListener = firestore.collection("calendar_job_assignments")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore assignments listener error: ${error.message}")
                        _isFirestoreConnected.value = false
                        return@addSnapshotListener
                    }

                    _isFirestoreConnected.value = true
                    updateSyncTime()

                    if (snapshot != null && !snapshot.isEmpty) {
                        val cloudAssignments = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { CalendarJobAssignment.fromFirestoreMap(doc.id, it) }
                        }
                        if (cloudAssignments.isNotEmpty()) {
                            _jobAssignments.value = cloudAssignments
                        }
                    } else if (snapshot != null && snapshot.isEmpty) {
                        pushBaselineAssignmentsToCloud()
                    }
                }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firestore listener: ${e.message}")
            _isFirestoreConnected.value = false
            _lastSyncTimestamp.value = "Offline cache active (Local DB synced)"
        }
    }

    private fun updateSyncTime() {
        val timeFormatted = SimpleDateFormat("h:mm:ss a", Locale.US).format(Date())
        _lastSyncTimestamp.value = "Firestore synced live at $timeFormatted"
    }

    private fun pushBaselineAvailabilityToCloud() {
        serviceScope.launch {
            try {
                val firestore = getFirestoreInstance() ?: return@launch
                for (avail in _contractorAvailabilities.value) {
                    firestore.collection("contractor_availability")
                        .document(avail.contractorId)
                        .set(avail.toFirestoreMap(), SetOptions.merge())
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not push baseline availability: ${e.message}")
            }
        }
    }

    private fun pushBaselineAssignmentsToCloud() {
        serviceScope.launch {
            try {
                val firestore = getFirestoreInstance() ?: return@launch
                for (assign in _jobAssignments.value) {
                    firestore.collection("calendar_job_assignments")
                        .document(assign.id)
                        .set(assign.toFirestoreMap(), SetOptions.merge())
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not push baseline assignments: ${e.message}")
            }
        }
    }

    /**
     * Save or update a job assignment to both local state and Firestore.
     */
    fun saveJobAssignment(assignment: CalendarJobAssignment) {
        val currentList = _jobAssignments.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == assignment.id }
        if (index >= 0) {
            currentList[index] = assignment
        } else {
            currentList.add(assignment)
        }
        _jobAssignments.value = currentList

        // Push to Firestore
        serviceScope.launch {
            try {
                val firestore = getFirestoreInstance() ?: return@launch
                firestore.collection("calendar_job_assignments")
                    .document(assignment.id)
                    .set(assignment.toFirestoreMap(), SetOptions.merge())
                updateSyncTime()
            } catch (e: Exception) {
                Log.w(TAG, "Error writing assignment to Firestore: ${e.message}")
            }
        }
    }

    /**
     * Delete a job assignment from both local state and Firestore.
     */
    fun deleteJobAssignment(id: String) {
        _jobAssignments.value = _jobAssignments.value.filter { it.id != id }

        serviceScope.launch {
            try {
                val firestore = getFirestoreInstance() ?: return@launch
                firestore.collection("calendar_job_assignments")
                    .document(id)
                    .delete()
                updateSyncTime()
            } catch (e: Exception) {
                Log.w(TAG, "Error deleting assignment from Firestore: ${e.message}")
            }
        }
    }

    /**
     * Update a contractor's availability in both local state and Firestore.
     */
    fun updateContractorAvailability(availability: ContractorAvailability) {
        val currentList = _contractorAvailabilities.value.toMutableList()
        val index = currentList.indexOfFirst { it.contractorId == availability.contractorId }
        if (index >= 0) {
            currentList[index] = availability
        } else {
            currentList.add(availability)
        }
        _contractorAvailabilities.value = currentList

        serviceScope.launch {
            try {
                val firestore = getFirestoreInstance() ?: return@launch
                firestore.collection("contractor_availability")
                    .document(availability.contractorId)
                    .set(availability.toFirestoreMap(), SetOptions.merge())
                updateSyncTime()
            } catch (e: Exception) {
                Log.w(TAG, "Error writing availability to Firestore: ${e.message}")
            }
        }
    }

    /**
     * Sync an existing JobEntity from Room into a CalendarJobAssignment.
     */
    fun assignJobFromRoom(job: JobEntity, contractor: ContractorAvailability, hour: Int, duration: Double) {
        val color = when (job.trade) {
            "Electrical" -> 0xFF1A73E8 // Blue
            "HVAC" -> 0xFF039BE5 // Peacock Cyan
            "Plumbing" -> 0xFF0B8043 // Sage Green
            "Carpentry" -> 0xFFF4511E // Tangerine
            "Roofing" -> 0xFF8E24AA // Grape
            else -> 0xFFF6BF26 // Banana Yellow
        }

        val assignment = CalendarJobAssignment(
            id = "assign_room_${job.id}_${System.currentTimeMillis() % 10000}",
            jobId = job.id,
            title = job.title,
            trade = job.trade,
            clientName = job.clientName,
            clientPhone = job.clientPhone,
            address = job.address,
            contractorId = contractor.contractorId,
            contractorName = contractor.contractorName,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
            startHour = hour,
            startMinute = 0,
            durationHours = duration,
            status = if (job.status.isNotEmpty()) job.status else "Scheduled",
            priority = job.priority,
            colorHex = color,
            notes = job.notes
        )
        saveJobAssignment(assignment)
    }
}
