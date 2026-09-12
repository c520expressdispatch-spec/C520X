package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("C520X", appName)
    }

    @Test
    fun `verify room database seeding and jobs`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val repo = AppRepository(db)

        repo.seedInitialDataIfEmpty()
        val jobs = repo.jobs.first()

        assertEquals(24, jobs.size)
        assertTrue(jobs.any { it.clientName.contains("Vanguard Logistics") })
        assertTrue(jobs.any { it.trade == "Electrical" })
        assertTrue(jobs.any { it.trade == "Plumbing" })
        db.close()
    }

    @Test
    fun `verify ai predictive analytics baseline generation`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val repo = AppRepository(db)
        repo.seedInitialDataIfEmpty()

        val jobs = repo.jobs.first()
        val timeLogs = repo.timeLogs.first()
        val leads = repo.leads.first()
        val payments = repo.payments.first()

        val aiService = com.example.data.AiPredictiveService()
        val report = aiService.buildStatisticalBaseline(jobs, timeLogs, leads, payments)

        assertTrue("On-time probability should be valid", report.overallOnTimeProbability in 50..100)
        assertTrue("Should produce timeline predictions", report.timelinePredictions.isNotEmpty())
        assertTrue("Should detect risk factors", report.riskFactors.isNotEmpty())
        assertTrue("Should generate resource allocation strategies", report.resourceStrategies.isNotEmpty())
        assertTrue("Should calculate trade velocity indices", report.tradeVelocityIndices.containsKey("Electrical"))
        assertTrue("Should forecast overrun savings", report.overrunForecast.preventedSavings > 0)
        db.close()
    }

    @Test
    fun `verify kanban board room persistence and state transitions`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val repo = AppRepository(db)
        repo.seedInitialDataIfEmpty()

        val initialTasks = repo.projectTasks.first()
        assertTrue("Kanban board tasks should be seeded in Room", initialTasks.isNotEmpty())

        val task = initialTasks.first()
        val originalState = task.state
        val targetState = if (originalState == "Done") "To Do" else "Done"

        // Perform state transition (drag-and-drop simulation)
        repo.updateProjectTaskStateAndOrder(task.id, targetState, 0)
        val updatedTasksAfterMove = repo.projectTasks.first()
        val movedTask = updatedTasksAfterMove.find { it.id == task.id }
        assertEquals("Task state should be updated and persisted in Room", targetState, movedTask?.state)

        // Perform priority update
        repo.updateProjectTaskPriority(task.id, "Urgent")
        val updatedTasksAfterPriority = repo.projectTasks.first()
        val prioritizedTask = updatedTasksAfterPriority.find { it.id == task.id }
        assertEquals("Task priority should be updated and persisted in Room", "Urgent", prioritizedTask?.priority)

        // Insert new task
        val newTask = com.example.data.ProjectTaskEntity(
            title = "Rough-in Master Bath Jacuzzi lines",
            projectName = "Foothills Luxury Kitchen Remodel",
            description = "Pressure test copper lines to 80 PSI",
            state = "In Progress",
            priority = "High",
            assignedTech = "Marcus Vance",
            dueDate = "Tomorrow",
            estimatedHours = 4.5,
            trade = "Plumbing",
            sortOrder = 99
        )
        repo.insertProjectTask(newTask)
        val allTasksWithNew = repo.projectTasks.first()
        assertTrue("New task should be queryable from Room Flow", allTasksWithNew.any { it.title.contains("Master Bath Jacuzzi") })

        db.close()
    }
}
