package com.example

import com.example.data.CalendarJobAssignment
import com.example.data.ContractorAvailability
import com.example.data.FirestoreCalendarService
import com.example.data.JobEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FirestoreCalendarServiceTest {

    @Test
    fun testContractorAvailabilitySerialization() {
        val contractor = ContractorAvailability(
            contractorId = "marcus_vance",
            contractorName = "Marcus Vance",
            trade = "HVAC",
            date = "2026-09-11",
            startTime = "07:30",
            endTime = "16:30",
            status = "Booked",
            maxJobsPerDay = 3,
            notes = "Commercial HVAC rooftop specialist",
            phone = "(520) 883-9102"
        )

        val map = contractor.toFirestoreMap()
        assertNotNull(map)
        assertEquals("marcus_vance", map["contractorId"])
        assertEquals("Marcus Vance", map["contractorName"])
        assertEquals("HVAC", map["trade"])
        assertEquals("2026-09-11", map["date"])
        assertEquals("Booked", map["status"])
        assertEquals("07:30", map["startTime"])
        assertEquals("16:30", map["endTime"])
        assertEquals(3, map["maxJobsPerDay"])
        assertEquals("Commercial HVAC rooftop specialist", map["notes"])
        assertEquals("(520) 883-9102", map["phone"])

        val parsed = ContractorAvailability.fromFirestoreMap("marcus_vance", map)
        assertEquals(contractor.contractorId, parsed.contractorId)
        assertEquals(contractor.contractorName, parsed.contractorName)
        assertEquals(contractor.trade, parsed.trade)
        assertEquals(contractor.status, parsed.status)
        assertEquals(contractor.startTime, parsed.startTime)
        assertEquals(contractor.endTime, parsed.endTime)
        assertEquals(contractor.maxJobsPerDay, parsed.maxJobsPerDay)
        assertEquals(contractor.notes, parsed.notes)
        assertEquals(contractor.phone, parsed.phone)
    }

    @Test
    fun testCalendarJobAssignmentSerialization() {
        val assignment = CalendarJobAssignment(
            id = "assign_test_101",
            jobId = 42,
            title = "Dual HVAC Compressor Overhaul",
            trade = "HVAC",
            clientName = "Sarah Jenkins",
            clientPhone = "(520) 555-0199",
            address = "782 N Stone Ave, Tucson, AZ",
            contractorId = "marcus_vance",
            contractorName = "Marcus Vance",
            date = "2026-09-11",
            startHour = 14,
            startMinute = 30,
            durationHours = 2.5,
            status = "In Progress",
            notes = "Rooftop unit access required",
            colorHex = 0xFF039BE5
        )

        val map = assignment.toFirestoreMap()
        assertNotNull(map)
        assertEquals("Dual HVAC Compressor Overhaul", map["title"])
        assertEquals("HVAC", map["trade"])
        assertEquals("Sarah Jenkins", map["clientName"])
        assertEquals(14, map["startHour"])
        assertEquals(30, map["startMinute"])
        assertEquals(2.5, map["durationHours"])
        assertEquals("In Progress", map["status"])
        assertEquals(42, map["jobId"])

        val parsed = CalendarJobAssignment.fromFirestoreMap("assign_test_101", map)
        assertEquals(assignment.id, parsed.id)
        assertEquals(assignment.title, parsed.title)
        assertEquals(assignment.trade, parsed.trade)
        assertEquals(assignment.clientName, parsed.clientName)
        assertEquals(assignment.contractorId, parsed.contractorId)
        assertEquals(assignment.date, parsed.date)
        assertEquals(assignment.startHour, parsed.startHour)
        assertEquals(assignment.startMinute, parsed.startMinute)
        assertEquals(assignment.durationHours, parsed.durationHours, 0.001)
        assertEquals(assignment.status, parsed.status)
        assertEquals(assignment.jobId, parsed.jobId)
    }

    @Test
    fun testTimeSlotFormattedCalculation() {
        val morningAssignment = CalendarJobAssignment(
            id = "assign_morn",
            title = "Morning Service",
            trade = "Electrical",
            clientName = "Client A",
            clientPhone = "520-000-1111",
            address = "123 Main St",
            contractorId = "c1",
            contractorName = "Contractor 1",
            date = "2026-09-11",
            startHour = 8,
            startMinute = 0,
            durationHours = 2.0
        )
        assertEquals("8:00 AM – 10:00 AM", morningAssignment.timeSlotFormatted)

        val afternoonAssignment = CalendarJobAssignment(
            id = "assign_aft",
            title = "Afternoon Service",
            trade = "Plumbing",
            clientName = "Client B",
            clientPhone = "520-000-2222",
            address = "456 Stone Ave",
            contractorId = "c2",
            contractorName = "Contractor 2",
            date = "2026-09-11",
            startHour = 13,
            startMinute = 30,
            durationHours = 1.5
        )
        assertEquals("1:30 PM – 3:00 PM", afternoonAssignment.timeSlotFormatted)
    }

    @Test
    fun testFirestoreCalendarServiceLocalCRUD() {
        // Test updating contractor availability
        val initialList = FirestoreCalendarService.contractorAvailabilities.value
        assertTrue("Contractors should be seeded", initialList.isNotEmpty())

        val alex = initialList.first { it.contractorId == "alex_ramirez" }
        val updatedAlex = alex.copy(status = "Off Duty", notes = "Attending electrical training seminar")
        FirestoreCalendarService.updateContractorAvailability(updatedAlex)

        val currentAlex = FirestoreCalendarService.contractorAvailabilities.value.find { it.contractorId == "alex_ramirez" }
        assertNotNull(currentAlex)
        assertEquals("Off Duty", currentAlex?.status)
        assertEquals("Attending electrical training seminar", currentAlex?.notes)

        // Test creating and deleting job assignment
        val testAssignment = CalendarJobAssignment(
            id = "unit_test_assign_99",
            jobId = 99,
            title = "Emergency Water Leak Repair",
            trade = "Plumbing",
            clientName = "Robert Taylor",
            clientPhone = "(520) 220-4100",
            address = "455 E Broadway Blvd, Tucson, AZ",
            contractorId = "carlos_mendez",
            contractorName = "Carlos Mendez",
            date = "2026-09-11",
            startHour = 11,
            startMinute = 0,
            durationHours = 1.5,
            status = "Dispatched"
        )
        FirestoreCalendarService.saveJobAssignment(testAssignment)

        val assignmentsAfterAdd = FirestoreCalendarService.jobAssignments.value
        assertTrue(assignmentsAfterAdd.any { it.id == "unit_test_assign_99" })

        // Delete job assignment
        FirestoreCalendarService.deleteJobAssignment("unit_test_assign_99")
        val assignmentsAfterDelete = FirestoreCalendarService.jobAssignments.value
        assertFalse(assignmentsAfterDelete.any { it.id == "unit_test_assign_99" })
    }

    @Test
    fun testAssignJobFromRoomEntity() {
        val dummyJob = JobEntity(
            id = 77,
            title = "Kitchen Circuit Breaker Upgrade",
            trade = "Electrical",
            clientName = "Eleanor Vance",
            clientPhone = "(520) 310-8822",
            address = "1200 N 4th Ave, Tucson, AZ",
            status = "Scheduled",
            billedAmount = 0.0,
            pipelineValue = 450.0,
            scheduledDate = "Tomorrow 9:00 AM",
            assignedTech = "Alex Ramirez",
            priority = "High",
            notes = "Square D breaker 20A"
        )

        val contractor = ContractorAvailability(
            contractorId = "alex_ramirez",
            contractorName = "Alex Ramirez",
            trade = "Electrical",
            date = "2026-09-11"
        )

        FirestoreCalendarService.assignJobFromRoom(dummyJob, contractor, hour = 10, duration = 2.0)

        val assignments = FirestoreCalendarService.jobAssignments.value
        val match = assignments.find { it.jobId == 77 }
        assertNotNull("Assigned job should exist in calendar state", match)
        assertEquals("Kitchen Circuit Breaker Upgrade", match?.title)
        assertEquals("Electrical", match?.trade)
        assertEquals("Alex Ramirez", match?.contractorName)
        assertEquals(10, match?.startHour)
        assertEquals(2.0, match?.durationHours ?: 0.0, 0.001)

        // Cleanup
        if (match != null) {
            FirestoreCalendarService.deleteJobAssignment(match.id)
        }
    }
}
