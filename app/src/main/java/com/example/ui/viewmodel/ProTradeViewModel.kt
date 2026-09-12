package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.EstimateEntity
import com.example.data.local.PartnerConfigEntity
import com.example.data.models.CustomApiConfig
import com.example.data.models.DispatchJobItem
import com.example.data.models.DispatchJobStatus
import com.example.data.models.DispatchStatusOverview
import com.example.data.models.NotificationPreferences
import com.example.data.models.PlatformNotificationSetting
import com.example.data.models.OperationsDashboardMetrics
import com.example.data.models.ServiceCategoryPerformance
import com.example.data.models.EstimateResult
import com.example.data.models.EstimatorModel
import com.example.data.models.EstimatorStyle
import com.example.data.models.LeadItem
import com.example.data.models.PartnerType
import com.example.data.models.TradeCategory
import com.example.data.models.WebhookConfig
import com.example.data.models.WebhookDeliveryLog
import com.example.data.repository.ProTradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String, val icon: String) {
    DASHBOARD("Overview", "Dashboard"),
    ESTIMATOR("AI Estimator", "Calculate"),
    LEADS("Leads Hub", "Inbox"),
    INTEGRATIONS("Webhooks", "Webhook"),
    AI_ASSISTANT("AI Assistant", "AutoAwesome")
}

class ProTradeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProTradeRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = ProTradeRepository(db)
    }

    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    val savedEstimates: StateFlow<List<EstimateEntity>> = repository.allSavedEstimates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leads: StateFlow<List<LeadItem>> = repository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val webhooks: StateFlow<List<WebhookConfig>> = repository.allWebhooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val webhookLogs: StateFlow<List<WebhookDeliveryLog>> = repository.recentWebhookLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val partnerConfigs: StateFlow<List<PartnerConfigEntity>> = repository.allPartnerConfigs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectTitle = MutableStateFlow("Master Bathroom & Walk-in Shower Renovation")
    val selectedTrade = MutableStateFlow(TradeCategory.REMODELING)
    val scopeDetails = MutableStateFlow("Tear out old tub, install curbless frameless glass walk-in shower with porcelain subway tiles, double vanity with quartz top, upgraded Moen fixtures, heated tile floor.")
    val dimensionsOrUnits = MutableStateFlow("120")
    val materialGrade = MutableStateFlow("Premium")
    val urgency = MutableStateFlow("Within 2 Weeks")
    val locationZip = MutableStateFlow("78704")
    val markupPercent = MutableStateFlow(18)
    val contingencyPercent = MutableStateFlow(10)
    val selectedModel = MutableStateFlow(EstimatorModel.GEMINI_3_5_FLASH)
    val selectedStyle = MutableStateFlow(EstimatorStyle.CONTRACTOR_PRECISION)

    val customApiConfig = MutableStateFlow(
        CustomApiConfig(
            endpointUrl = "https://api.openai.com/v1/chat/completions",
            apiKey = "",
            modelIdentifier = "gpt-4o-mini",
            customAuthHeader = "Bearer"
        )
    )

    private val _isEstimating = MutableStateFlow(false)
    val isEstimating: StateFlow<Boolean> = _isEstimating.asStateFlow()

    private val _currentEstimateResult = MutableStateFlow<EstimateResult?>(null)
    val currentEstimateResult: StateFlow<EstimateResult?> = _currentEstimateResult.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun runAiEstimate() {
        val title = projectTitle.value.ifBlank { "${selectedTrade.value.displayName} Project" }
        val units = dimensionsOrUnits.value.toDoubleOrNull() ?: 100.0
        viewModelScope.launch {
            _isEstimating.value = true
            try {
                val result = repository.generateAndSaveEstimate(
                    title = title,
                    trade = selectedTrade.value,
                    scopeDetails = scopeDetails.value,
                    squareFeetOrUnits = units,
                    materialGrade = materialGrade.value,
                    urgency = urgency.value,
                    locationZip = locationZip.value,
                    markupPercent = markupPercent.value,
                    contingencyPercent = contingencyPercent.value,
                    model = selectedModel.value,
                    style = selectedStyle.value,
                    customApiConfig = customApiConfig.value,
                    autoSave = true
                )
                _currentEstimateResult.value = result
                showMessage("Estimate generated successfully (${result.modelUsed} / ${result.styleUsed})")
            } catch (e: Exception) {
                showMessage("Error generating estimate: ${e.message}")
            } finally {
                _isEstimating.value = false
            }
        }
    }

    fun setEstimateResult(result: EstimateResult) {
        _currentEstimateResult.value = result
    }

    fun deleteEstimate(id: Long) {
        viewModelScope.launch {
            repository.deleteEstimate(id)
            showMessage("Estimate deleted")
        }
    }

    val selectedPartnerFilter = MutableStateFlow<PartnerType?>(null)

    fun filterLeadsByPartner(partner: PartnerType?) {
        selectedPartnerFilter.value = partner
    }

    fun updateLeadStatus(id: Long, status: String, quoteAmount: Double? = null) {
        viewModelScope.launch {
            repository.updateLeadStatus(id, status, quoteAmount)
            showMessage("Lead status updated to $status")
        }
    }

    fun generateAiEstimateForLead(lead: LeadItem) {
        viewModelScope.launch {
            _isEstimating.value = true
            try {
                val result = repository.generateAiLeadResponseAndQuote(lead, selectedModel.value)
                _currentEstimateResult.value = result
                _currentTab.value = AppNavTab.ESTIMATOR
                showMessage("AI Auto-Estimate created for ${lead.clientName} on ${lead.partner.displayName}")
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                _isEstimating.value = false
            }
        }
    }

    fun dispatchQuoteForLead(lead: LeadItem, estimate: EstimateResult) {
        viewModelScope.launch {
            try {
                val log = repository.dispatchQuoteForLead(lead, estimate)
                if (log.isSuccess) {
                    showMessage("Quote $${"%,.2f".format(estimate.targetCost)} dispatched to ${lead.partner.displayName}!")
                } else {
                    showMessage("Quote logged (Simulated partner dispatch: ${log.statusCode})")
                }
            } catch (e: Exception) {
                showMessage("Dispatch failed: ${e.message}")
            }
        }
    }

    fun addNewManualLead(
        clientName: String,
        location: String,
        partner: PartnerType,
        trade: TradeCategory,
        title: String,
        desc: String,
        budget: String,
        urgency: String
    ) {
        viewModelScope.launch {
            val lead = LeadItem(
                clientName = clientName,
                clientLocation = location,
                partner = partner,
                trade = trade,
                projectTitle = title,
                projectDescription = desc,
                customerBudget = budget,
                urgency = urgency,
                status = "New"
            )
            repository.createNewLead(lead)
            showMessage("New lead added from ${partner.displayName}")
        }
    }

    val testWebhookUrl = MutableStateFlow("https://hooks.zapier.com/hooks/catch/sample/trade-estimate")
    val testWebhookMethod = MutableStateFlow("POST")
    val testWebhookAuth = MutableStateFlow("")
    val testWebhookHeaders = MutableStateFlow("{\"Content-Type\":\"application/json\", \"X-Agent\":\"ProTrade-Master\"}")
    val testWebhookPayload = MutableStateFlow("{\n  \"event\": \"PROJECT_ESTIMATE_DISPATCH\",\n  \"trade\": \"Electrical & EV\",\n  \"targetAmount\": 1850.00,\n  \"partner\": \"THUMBTACK\",\n  \"status\": \"APPROVED\"\n}")

    private val _isTestingWebhook = MutableStateFlow(false)
    val isTestingWebhook: StateFlow<Boolean> = _isTestingWebhook.asStateFlow()

    private val _lastTestResult = MutableStateFlow<WebhookDeliveryLog?>(null)
    val lastTestResult: StateFlow<WebhookDeliveryLog?> = _lastTestResult.asStateFlow()

    fun runLiveWebhookTest() {
        viewModelScope.launch {
            _isTestingWebhook.value = true
            try {
                val result = repository.testWebhookExecution(
                    webhookName = "Live Test Runner",
                    url = testWebhookUrl.value,
                    httpMethod = testWebhookMethod.value,
                    authHeader = testWebhookAuth.value,
                    customHeadersJson = testWebhookHeaders.value,
                    payloadJson = testWebhookPayload.value
                )
                _lastTestResult.value = result
                showMessage(if (result.isSuccess) "Webhook Delivered (HTTP ${result.statusCode} in ${result.latencyMs}ms)" else "Webhook sent (Status ${result.statusCode}): ${result.responseBody}")
            } catch (e: Exception) {
                showMessage("Webhook error: ${e.message}")
            } finally {
                _isTestingWebhook.value = false
            }
        }
    }

    fun saveWebhook(
        name: String,
        url: String,
        event: String,
        method: String,
        auth: String,
        headers: String
    ) {
        viewModelScope.launch {
            val config = WebhookConfig(
                name = name,
                url = url,
                triggerEvent = event,
                httpMethod = method,
                authHeader = auth,
                customHeadersJson = headers
            )
            repository.saveWebhookConfig(config)
            showMessage("Webhook endpoint saved!")
        }
    }

    fun deleteWebhook(id: Long) {
        viewModelScope.launch {
            repository.deleteWebhook(id)
            showMessage("Webhook removed")
        }
    }

    fun clearAllWebhookLogs() {
        viewModelScope.launch {
            repository.clearWebhookLogs()
            showMessage("Webhook delivery logs cleared")
        }
    }

    fun updatePartnerDetails(partnerKey: String, apiKey: String, webhookUrl: String, notes: String) {
        viewModelScope.launch {
            val existing = partnerConfigs.value.find { it.partnerKey == partnerKey }
            val updated = existing?.copy(
                apiKey = apiKey,
                webhookUrl = webhookUrl,
                customNotes = notes
            ) ?: PartnerConfigEntity(
                partnerKey = partnerKey,
                isConnected = true,
                apiKey = apiKey,
                webhookUrl = webhookUrl,
                rating = 4.9,
                reviewsCount = 50,
                activeLeadsCount = 2,
                customNotes = notes
            )
            repository.updatePartnerConfig(updated)
            showMessage("Partner settings updated for $partnerKey")
        }
    }

    private val _operationsMetrics = MutableStateFlow(
        OperationsDashboardMetrics(
            activeJobsCount = 12,
            pipelineValue = 36370.0,
            billedRevenue = 26410.0,
            collectedPayments = 7020.0,
            totalTrackedJobs = 24,
            completionRatePercent = 16.7
        )
    )
    val operationsMetrics: StateFlow<OperationsDashboardMetrics> = _operationsMetrics.asStateFlow()

    private val _serviceCategories = MutableStateFlow(
        listOf(
            ServiceCategoryPerformance("Plumbing", 4, 3, 1, 5620.0, 5920.0),
            ServiceCategoryPerformance("Electrical", 4, 3, 1, 8950.0, 11150.0),
            ServiceCategoryPerformance("HVAC", 4, 3, 1, 1870.0, 2780.0),
            ServiceCategoryPerformance("Carpentry", 3, 2, 1, 3850.0, 5550.0),
            ServiceCategoryPerformance("Painting", 3, 3, 0, 3350.0, 5250.0),
            ServiceCategoryPerformance("Handyman", 3, 3, 0, 1170.0, 1720.0),
            ServiceCategoryPerformance("Roofing", 3, 3, 0, 1600.0, 4000.0)
        )
    )
    val serviceCategories: StateFlow<List<ServiceCategoryPerformance>> = _serviceCategories.asStateFlow()

    private val _dispatchStatuses = MutableStateFlow(
        listOf(
            DispatchStatusOverview(DispatchJobStatus.COMPLETED, 4, 7020.0),
            DispatchStatusOverview(DispatchJobStatus.IN_PROGRESS, 3, 5900.0),
            DispatchStatusOverview(DispatchJobStatus.DISPATCHED, 3, 3070.0),
            DispatchStatusOverview(DispatchJobStatus.SCHEDULED, 6, 10420.0),
            DispatchStatusOverview(DispatchJobStatus.PENDING_ESTIMATE, 8, 0.0)
        )
    )
    val dispatchStatuses: StateFlow<List<DispatchStatusOverview>> = _dispatchStatuses.asStateFlow()

    private val _dispatchJobs = MutableStateFlow(
        listOf(
            DispatchJobItem("C520X-PL-101", "Plumbing", "Marcus Brody", "742 Evergreen Terr, Canyon", DispatchJobStatus.COMPLETED, 2800.0, 2800.0, "Tech Dave Miller", "Completed Today", PartnerType.THUMBTACK, "Whole-house water heater replacement & copper re-pipe"),
            DispatchJobItem("C520X-PL-102", "Plumbing", "Sarah Jenkins", "1208 Oak Ridge Way, Mesa", DispatchJobStatus.IN_PROGRESS, 1820.0, 1820.0, "Tech Dave Miller", "In Progress (Arrived 9:30 AM)", PartnerType.ANGI, "Main sewer line hydro-jetting and cleanout valve replacement"),
            DispatchJobItem("C520X-PL-103", "Plumbing", "David Chen", "405 Desert Vista, Scottsdale", DispatchJobStatus.DISPATCHED, 1000.0, 1300.0, "Crew Express #2", "En Route - ETA 20m", PartnerType.TASKRABBIT, "Emergency slab leak detection and manifold isolation"),
            DispatchJobItem("C520X-PL-104", "Plumbing", "Elena Vasquez", "8901 Sunset Blvd, Phoenix", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 1600.0, "Pending Dispatch", "Awaiting Quote", PartnerType.YELP, "Commercial kitchen grease trap & backflow certification"),
            DispatchJobItem("C520X-EL-201", "Electrical", "Robert Langdon", "3342 S 520 Express Hwy, Tucson", DispatchJobStatus.COMPLETED, 4220.0, 4220.0, "Master Electrician Ray", "Completed Yesterday", PartnerType.HOUZZ, "200A Smart Main Panel Upgrade & Level 2 Dual EV Charger"),
            DispatchJobItem("C520X-EL-202", "Electrical", "Karen Miller", "5512 Desert Sage, Tempe", DispatchJobStatus.IN_PROGRESS, 2350.0, 2950.0, "Tech Alex Rivera", "In Progress (90% done)", PartnerType.THUMBTACK, "Whole-home recessed LED retrofit & Lutron smart dimmers"),
            DispatchJobItem("C520X-EL-203", "Electrical", "Tom Walker", "1104 Pine Valley Rd, Chandler", DispatchJobStatus.SCHEDULED, 2380.0, 3980.0, "Crew Alpha Electrics", "Scheduled Tomorrow 8:00 AM", PartnerType.ANGI, "Tesla Powerwall backup sub-panel installation and trenching"),
            DispatchJobItem("C520X-EL-204", "Electrical", "Rachel Adams", "2910 Canyon Creek, Glendale", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 2400.0, "Pending Dispatch", "Awaiting AI Review", PartnerType.YELP, "Commercial 3-phase motor wiring and disconnect installation"),
            DispatchJobItem("C520X-HV-301", "HVAC", "James Vance", "7701 E Camelback, Scottsdale", DispatchJobStatus.COMPLETED, 1870.0, 1870.0, "HVAC Lead Frank", "Completed Today", PartnerType.THUMBTACK, "High-efficiency heat pump tune-up & dual zone damper repair"),
            DispatchJobItem("C520X-HV-302", "HVAC", "Patricia Moore", "4092 Mountain View, Peoria", DispatchJobStatus.SCHEDULED, 0.0, 910.0, "Tech Frank V.", "Scheduled Thursday 1:00 PM", PartnerType.TASKRABBIT, "Duct sanitization and smart ecobee thermostat balancing"),
            DispatchJobItem("C520X-HV-303", "HVAC", "Arthur King", "602 Red Rock Dr, Sedona", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 3400.0, "Pending Dispatch", "Awaiting Inspection", PartnerType.ANGI, "5-Ton Trane Variable Inverter rooftop AC replacement"),
            DispatchJobItem("C520X-HV-304", "HVAC", "Linda Chavez", "1820 Adobe Wells, Chandler", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 1200.0, "Pending Dispatch", "Awaiting Quote", PartnerType.HOUZZ, "Mini-split installation for newly built home gym casita"),
            DispatchJobItem("C520X-CP-401", "Carpentry", "Brian Cooper", "2150 Canyon Pass, Gilbert", DispatchJobStatus.COMPLETED, 3850.0, 3850.0, "Master Carpenter Joe", "Completed 2 Days Ago", PartnerType.HOUZZ, "Custom built-in library cabinetry with walnut finish"),
            DispatchJobItem("C520X-CP-402", "Carpentry", "Grace Hall", "831 Cactus Wren, Surprise", DispatchJobStatus.SCHEDULED, 0.0, 1700.0, "Carpenter Joe & Crew", "Scheduled Friday 9:00 AM", PartnerType.THUMBTACK, "Redwood pergola reinforcement & composite deck stairs"),
            DispatchJobItem("C520X-CP-403", "Carpentry", "George Martinez", "410 S 520 Expressway, Tucson", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 2200.0, "Pending Dispatch", "Awaiting Measurement", PartnerType.ANGI, "Custom cedar fencing & motorized sliding driveway gate"),
            DispatchJobItem("C520X-PT-501", "Painting", "Emily Foster", "904 Whispering Palms, Phoenix", DispatchJobStatus.IN_PROGRESS, 1730.0, 2100.0, "Paint Crew Bravo", "In Progress (2nd Coat)", PartnerType.ANGI, "Exterior stucco paint prep, elastomeric coating & trim"),
            DispatchJobItem("C520X-PT-502", "Painting", "Walter Scott", "1420 Sunburst Dr, Mesa", DispatchJobStatus.DISPATCHED, 1620.0, 1950.0, "Paint Crew Charlie", "En Route - ETA 15m", PartnerType.THUMBTACK, "Interior 3-bedroom walls, vaulted ceilings & satin doors"),
            DispatchJobItem("C520X-PT-503", "Painting", "Hannah White", "662 Ironwood Trail, Scottsdale", DispatchJobStatus.SCHEDULED, 0.0, 1200.0, "Crew Bravo", "Scheduled Monday 7:30 AM", PartnerType.YELP, "Cabinet spray lacquering & epoxy kitchen island finish"),
            DispatchJobItem("C520X-HM-601", "Handyman", "Samuel Green", "501 Apache Blvd, Tempe", DispatchJobStatus.DISPATCHED, 450.0, 450.0, "Tech Leo Vance", "On-Site / Dispatched", PartnerType.TASKRABBIT, "Drywall patch, 85-inch OLED TV wall mounting & fixture swaps"),
            DispatchJobItem("C520X-HM-602", "Handyman", "Chloe Bennett", "1214 Canyon Ridge, Gilbert", DispatchJobStatus.SCHEDULED, 720.0, 720.0, "Tech Leo Vance", "Scheduled Wednesday 2:00 PM", PartnerType.THUMBTACK, "Weather-stripping, sliding glass door track rebuild & deadbolts"),
            DispatchJobItem("C520X-HM-603", "Handyman", "Steven Clark", "309 Ocotillo Way, Phoenix", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 550.0, "Pending Dispatch", "Awaiting Quote", PartnerType.YELP, "Attic ladder installation, ceiling fan replacements"),
            DispatchJobItem("C520X-RF-701", "Roofing", "Carlos Diaz", "7110 Desert Ridge, Scottsdale", DispatchJobStatus.SCHEDULED, 1600.0, 2400.0, "Roofing Crew Alpha", "Scheduled Thursday 6:30 AM", PartnerType.ANGI, "Tile roof underlayment repair, valley flashing & ridge caps"),
            DispatchJobItem("C520X-RF-702", "Roofing", "Donna Peterson", "820 Canyon Vista, Mesa", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 1600.0, "Pending Dispatch", "Awaiting Drone Scan", PartnerType.HOUZZ, "Flat roof silicone elastomeric commercial coating"),
            DispatchJobItem("C520X-RF-703", "Roofing", "Paul Hughes", "4412 S Sun Valley, Tucson", DispatchJobStatus.PENDING_ESTIMATE, 0.0, 2800.0, "Pending Dispatch", "Awaiting Quote", PartnerType.THUMBTACK, "Monsoon storm gutter replacement and fascia board replacement")
        )
    )
    val dispatchJobs: StateFlow<List<DispatchJobItem>> = _dispatchJobs.asStateFlow()

    val selectedTradeFilter = MutableStateFlow<String?>(null)
    val selectedStatusFilter = MutableStateFlow<DispatchJobStatus?>(null)

    fun filterByTrade(trade: String?) {
        selectedTradeFilter.value = trade
    }

    fun filterByStatus(status: DispatchJobStatus?) {
        selectedStatusFilter.value = status
    }

    fun updateJobDispatchStatus(jobId: String, newStatus: DispatchJobStatus) {
        val updatedList = _dispatchJobs.value.map { job ->
            if (job.id == jobId) {
                val newBilled = when (newStatus) {
                    DispatchJobStatus.COMPLETED -> job.estimatedPipelineValue
                    DispatchJobStatus.IN_PROGRESS -> job.estimatedPipelineValue * 0.7
                    DispatchJobStatus.DISPATCHED -> job.estimatedPipelineValue * 0.4
                    DispatchJobStatus.SCHEDULED -> job.billedAmount
                    DispatchJobStatus.PENDING_ESTIMATE -> 0.0
                }
                job.copy(status = newStatus, billedAmount = newBilled)
            } else {
                job
            }
        }
        _dispatchJobs.value = updatedList
        recalculateOperationsMetrics(updatedList)
        showMessage("Job $jobId status changed to ${newStatus.displayName}")
    }

    fun assignTechToJob(jobId: String, techName: String) {
        val updatedList = _dispatchJobs.value.map { job ->
            if (job.id == jobId) job.copy(assignedTech = techName) else job
        }
        _dispatchJobs.value = updatedList
        showMessage("Assigned $techName to $jobId")
    }

    private fun recalculateOperationsMetrics(jobs: List<DispatchJobItem>) {
        val activeCount = jobs.count { it.status == DispatchJobStatus.SCHEDULED || it.status == DispatchJobStatus.IN_PROGRESS || it.status == DispatchJobStatus.DISPATCHED }
        val pipeline = jobs.sumOf { it.estimatedPipelineValue }
        val billed = jobs.sumOf { it.billedAmount }
        val completed = jobs.count { it.status == DispatchJobStatus.COMPLETED }
        val collected = jobs.filter { it.status == DispatchJobStatus.COMPLETED }.sumOf { it.billedAmount }
        val completionRate = if (jobs.isNotEmpty()) (completed.toDouble() / jobs.size) * 100 else 0.0
        _operationsMetrics.value = OperationsDashboardMetrics(
            activeJobsCount = activeCount,
            pipelineValue = pipeline,
            billedRevenue = billed,
            collectedPayments = collected,
            totalTrackedJobs = jobs.size,
            completionRatePercent = String.format(java.util.Locale.US, "%.1f", completionRate).toDoubleOrNull() ?: 16.7
        )
    }

    private val _incomingWebhooks = MutableStateFlow(
        listOf(
            com.example.data.models.IncomingWebhookEvent(
                id = "WH-IN-104",
                partner = PartnerType.THUMBTACK,
                eventType = "New Direct Lead",
                details = "Water heater replacement request in Mesa",
                status = com.example.data.models.IncomingWebhookStatus.PROCESSED,
                timestampMs = System.currentTimeMillis() - 1000 * 60 * 5
            ),
            com.example.data.models.IncomingWebhookEvent(
                id = "WH-IN-103",
                partner = PartnerType.TASKRABBIT,
                eventType = "Booking Confirmed",
                details = "Mount 85-inch TV and install soundbar",
                status = com.example.data.models.IncomingWebhookStatus.PROCESSED,
                timestampMs = System.currentTimeMillis() - 1000 * 60 * 25
            ),
            com.example.data.models.IncomingWebhookEvent(
                id = "WH-IN-102",
                partner = PartnerType.YELP,
                eventType = "Message Received",
                details = "Requesting quote for commercial kitchen grease trap",
                status = com.example.data.models.IncomingWebhookStatus.PENDING,
                timestampMs = System.currentTimeMillis() - 1000 * 60 * 65
            ),
            com.example.data.models.IncomingWebhookEvent(
                id = "WH-IN-101",
                partner = PartnerType.ANGI,
                eventType = "Lead Matched",
                details = "Emergency slab leak detection needed",
                status = com.example.data.models.IncomingWebhookStatus.FAILED,
                timestampMs = System.currentTimeMillis() - 1000 * 60 * 120
            )
        )
    )
    val incomingWebhooks: StateFlow<List<com.example.data.models.IncomingWebhookEvent>> = _incomingWebhooks.asStateFlow()

    val webhookSearchQuery = MutableStateFlow("")
    val selectedWebhookPartner = MutableStateFlow<PartnerType?>(null)
    val selectedWebhookStatus = MutableStateFlow<com.example.data.models.IncomingWebhookStatus?>(null)
    val webhookOnlyImportant = MutableStateFlow(false)

    fun updateWebhookSearchQuery(query: String) {
        webhookSearchQuery.value = query
    }

    fun filterWebhooksByPartner(partner: PartnerType?) {
        selectedWebhookPartner.value = partner
    }

    fun filterWebhooksByStatus(status: com.example.data.models.IncomingWebhookStatus?) {
        selectedWebhookStatus.value = status
    }

    fun toggleWebhookOnlyImportant() {
        webhookOnlyImportant.value = !webhookOnlyImportant.value
    }

    val selectedWebhookIds = MutableStateFlow<Set<String>>(emptySet())
    val isWebhookSelectionMode = MutableStateFlow(false)

    fun toggleWebhookSelection(id: String) {
        val current = selectedWebhookIds.value
        selectedWebhookIds.value = if (current.contains(id)) {
            current - id
        } else {
            current + id
        }
        isWebhookSelectionMode.value = selectedWebhookIds.value.isNotEmpty()
    }

    fun clearWebhookSelections() {
        selectedWebhookIds.value = emptySet()
        isWebhookSelectionMode.value = false
    }

    fun archiveWebhook(id: String) {
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id == id) it.copy(status = com.example.data.models.IncomingWebhookStatus.ARCHIVED) else it
        }
    }

    fun restoreWebhook(id: String) {
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id == id) it.copy(status = com.example.data.models.IncomingWebhookStatus.PENDING) else it
        }
    }

    fun toggleWebhookImportant(id: String) {
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id == id) it.copy(isImportant = !it.isImportant) else it
        }
    }

    fun markSelectedWebhooksAsProcessed() {
        val selected = selectedWebhookIds.value
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id in selected) it.copy(status = com.example.data.models.IncomingWebhookStatus.PROCESSED) else it
        }
        clearWebhookSelections()
    }

    fun archiveSelectedWebhooks() {
        val selected = selectedWebhookIds.value
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id in selected) it.copy(status = com.example.data.models.IncomingWebhookStatus.ARCHIVED) else it
        }
        clearWebhookSelections()
    }

    fun restoreSelectedWebhooks() {
        val selected = selectedWebhookIds.value
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id in selected) it.copy(status = com.example.data.models.IncomingWebhookStatus.PENDING) else it
        }
        clearWebhookSelections()
    }

    fun toggleImportantSelectedWebhooks() {
        val selected = selectedWebhookIds.value
        val allImportant = _incomingWebhooks.value.filter { it.id in selected }.all { it.isImportant }
        _incomingWebhooks.value = _incomingWebhooks.value.map {
            if (it.id in selected) it.copy(isImportant = !allImportant) else it
        }
        clearWebhookSelections()
    }

    val notificationPreferences = MutableStateFlow(NotificationPreferences())
    val isNotificationSettingsOpen = MutableStateFlow(false)

    fun openNotificationSettings() {
        isNotificationSettingsOpen.value = true
    }

    fun closeNotificationSettings() {
        isNotificationSettingsOpen.value = false
    }

    fun toggleMasterPush(enabled: Boolean) {
        notificationPreferences.value = notificationPreferences.value.copy(masterPushEnabled = enabled)
    }

    fun togglePlatformPush(partner: PartnerType, enabled: Boolean) {
        val current = notificationPreferences.value
        val currentSetting = current.platformSettings[partner] ?: PlatformNotificationSetting(partner)
        val updatedMap = current.platformSettings.toMutableMap()
        updatedMap[partner] = currentSetting.copy(enabled = enabled)
        notificationPreferences.value = current.copy(platformSettings = updatedMap)
    }

    fun toggleImmediatePush(partner: PartnerType, immediate: Boolean) {
        val current = notificationPreferences.value
        val currentSetting = current.platformSettings[partner] ?: PlatformNotificationSetting(partner)
        val updatedMap = current.platformSettings.toMutableMap()
        updatedMap[partner] = currentSetting.copy(immediatePush = immediate)
        notificationPreferences.value = current.copy(platformSettings = updatedMap)
    }

    fun toggleQuietHours(enabled: Boolean) {
        notificationPreferences.value = notificationPreferences.value.copy(quietHoursEnabled = enabled)
    }

    fun updateQuietHours(start: String, end: String) {
        notificationPreferences.value = notificationPreferences.value.copy(quietHoursStart = start, quietHoursEnd = end)
    }

    fun toggleEmergencyBypass(bypass: Boolean) {
        notificationPreferences.value = notificationPreferences.value.copy(emergencyBypassQuietHours = bypass)
    }

    fun updateAlertTone(tone: String) {
        notificationPreferences.value = notificationPreferences.value.copy(alertTone = tone)
        showMessage("Alert tone set to: $tone")
    }

    fun toggleVibration(enabled: Boolean) {
        notificationPreferences.value = notificationPreferences.value.copy(vibrationEnabled = enabled)
    }

    fun updateMinLeadValueThreshold(threshold: Int) {
        notificationPreferences.value = notificationPreferences.value.copy(minLeadValueThreshold = threshold)
        val label = if (threshold == 0) "All Leads ($0+)" else "$$threshold+"
        showMessage("Lead alert filter: $label")
    }

    fun clearAlertHistory() {
        notificationPreferences.value = notificationPreferences.value.copy(alertHistory = emptyList())
        showMessage("Alert audit log cleared")
    }

    fun sendTestNotification(partner: PartnerType) {
        val setting = notificationPreferences.value.platformSettings[partner]
        val isImmediate = setting?.immediatePush == true
        val priorityTag = if (isImmediate) "HIGH PRIORITY" else "STANDARD"

        val newEntry = com.example.data.models.NotificationHistoryItem(
            partner = partner,
            title = "${partner.displayName} Lead Alert",
            message = "New customer dispatch lead via ${partner.brandTag} ($priorityTag)",
            isHighPriority = isImmediate,
            timestampMs = System.currentTimeMillis()
        )
        val updatedHistory = listOf(newEntry) + notificationPreferences.value.alertHistory
        notificationPreferences.value = notificationPreferences.value.copy(alertHistory = updatedHistory)

        showMessage("[${partner.brandTag} ALERT $priorityTag] Push test received: Immediate notification dispatched for new leads!")
    }

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                role = "model",
                text = "Hello! I'm the Canyon 520 Express Command AI. I can help answer questions about operations, write dispatch messages, or generate job site visualizations. What do you need?",
                isImage = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val isChatLoading = MutableStateFlow(false)

    fun sendChatMessage(prompt: String, generateImage: Boolean = false) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(role = "user", text = prompt)
        _chatMessages.value = _chatMessages.value + userMsg
        isChatLoading.value = true

        viewModelScope.launch {
            try {
                if (generateImage) {
                    val result = repository.generateAiImage(prompt)
                    _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", text = result, isImage = true)
                } else {
                    val history = _chatMessages.value.map { Pair(it.role, it.text) }
                    val response = repository.chatWithAi(prompt, history)
                    if (response.startsWith("Error:") || response.startsWith("Connection error") || response.startsWith("Please configure")) {
                        val fallback = generateSmartDispatchResponse(prompt)
                        _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", text = fallback)
                    } else {
                        _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", text = response)
                    }
                }
            } catch (e: Exception) {
                val fallback = generateSmartDispatchResponse(prompt)
                _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", text = fallback)
            } finally {
                isChatLoading.value = false
            }
        }
    }

    private fun generateSmartDispatchResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("delayed tech") || lower.contains("draft a message") -> {
                """
Suggested Client Notification (Delayed Technician):

"Hi [Customer Name], this is Canyon 520 Express Dispatch. Our lead technician, Marcus, is currently wrapping up an emergency job nearby and is running approximately 20-30 minutes behind schedule.

We apologize for the slight delay — he is en route with all specialized parts to service your home. Live tracker: https://c520x.link/track-8401. Thank you for your patience!"

Tip: Tap and hold or copy this text to dispatch directly via SMS.
""".trimIndent()
            }
            lower.contains("slab leak") || lower.contains("estimate for a slab") -> {
                """
Preliminary AI Estimate: Emergency Slab Leak Detection & Reroute

Acoustic & Thermal Leak Pinpointing: $450.00
Concrete Penetration & Slab Access: $650.00
PEX-A Copper Bypass Line (20ft): $1,250.00
Hydraulic Cement Seal & Moisture Barrier: $350.00
---------------------------------------------------
Total Base Cost: $2,700.00
Recommended Contingency (10%): $270.00
Client Quote: $2,970.00 | Est. Turnaround: 1.5 Days

Ready to dispatch or export to the Estimator tool.
""".trimIndent()
            }
            lower.contains("active jobs") || lower.contains("show me today's active") -> {
                val activeList = _dispatchJobs.value.filter { it.status != DispatchJobStatus.COMPLETED }
                val jobsSummary = activeList.take(4).joinToString("\n") { job ->
                    "• #${job.id}: ${job.serviceTrade} for ${job.clientName} | Tech: ${job.assignedTech} | Status: ${job.status.displayName}"
                }
                """
Current Active Dispatch Jobs (${activeList.size} Total):

$jobsSummary

Team Status: Technicians actively assigned in field. On-time dispatch rate is 94.2%.
""".trimIndent()
            }
            lower.contains("webhook") || lower.contains("summarize today's webhook") -> {
                val count = _incomingWebhooks.value.size
                val pending = _incomingWebhooks.value.count { it.status == com.example.data.models.IncomingWebhookStatus.PENDING }
                """
Webhook Events Summary:

Total Partner Events Received: $count
Pending Dispatch Review: $pending
Top Sources: Yelp for Business, Thumbtack Pro, Angi
High Priority Flags: Urgent leads requiring immediate technician dispatch.
""".trimIndent()
            }
            lower.contains("completion rate") || lower.contains("analyze") -> {
                val metrics = _operationsMetrics.value
                """
Operations Performance Analysis:

Tracked Jobs: ${metrics.totalTrackedJobs}
Active Dispatch Pipeline: ${metrics.activeJobsCount} Jobs ($${"%,.0f".format(metrics.pipelineValue)})
Completion Rate: ${metrics.completionRatePercent}% on first-visit resolution
Billed Revenue: $${"%,.0f".format(metrics.billedRevenue)} gross billable.
""".trimIndent()
            }
            else -> {
                "C520X Operations Command is standing by. All systems operational. Dispatch queue: ${_dispatchJobs.value.count { it.status == DispatchJobStatus.IN_PROGRESS }} jobs currently in progress."
            }
        }
    }
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String,
    val text: String,
    val isImage: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis()
)
