package com.example.data.repository

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.EstimateEntity
import com.example.data.local.JsonHelper
import com.example.data.local.LeadEntity
import com.example.data.local.PartnerConfigEntity
import com.example.data.local.WebhookEntity
import com.example.data.local.WebhookLogEntity
import com.example.data.models.CustomApiConfig
import com.example.data.models.EstimateResult
import com.example.data.models.EstimatorModel
import com.example.data.models.EstimatorStyle
import com.example.data.models.LeadItem
import com.example.data.models.LineItem
import com.example.data.models.PartnerType
import com.example.data.models.ProjectMilestone
import com.example.data.models.TradeCategory
import com.example.data.models.WebhookConfig
import com.example.data.models.WebhookDeliveryLog
import com.example.data.remote.GeminiEstimatorService
import com.example.data.remote.WebhookDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ProTradeRepository(
    private val database: AppDatabase,
    private val estimatorService: GeminiEstimatorService = GeminiEstimatorService(),
    private val webhookDispatcher: WebhookDispatcher = WebhookDispatcher()
) {
    val allSavedEstimates: Flow<List<EstimateEntity>> = database.estimateDao().getAllEstimates()

    suspend fun generateAndSaveEstimate(
        title: String,
        trade: TradeCategory,
        scopeDetails: String,
        squareFeetOrUnits: Double,
        materialGrade: String,
        urgency: String,
        locationZip: String,
        markupPercent: Int,
        contingencyPercent: Int,
        model: EstimatorModel,
        style: EstimatorStyle,
        customApiConfig: CustomApiConfig = CustomApiConfig(),
        autoSave: Boolean = true
    ): EstimateResult {
        val result = estimatorService.generateEstimate(
            title = title,
            trade = trade,
            scopeDetails = scopeDetails,
            squareFeetOrUnits = squareFeetOrUnits,
            materialGrade = materialGrade,
            urgency = urgency,
            locationZip = locationZip,
            markupPercent = markupPercent,
            contingencyPercent = contingencyPercent,
            model = model,
            style = style,
            customApiConfig = customApiConfig
        )
        if (autoSave) {
            saveEstimateResult(result)
        }
        triggerWebhooksForEvent("ON_ESTIMATE_CREATED", result)
        return result
    }

    suspend fun saveEstimateResult(estimate: EstimateResult): Long {
        val lineItemsJson = JSONArray().apply {
            estimate.lineItems.forEach { item ->
                put(JSONObject().apply {
                    put("category", item.category)
                    put("name", item.name)
                    put("quantity", item.quantity)
                    put("unit", item.unit)
                    put("unitCost", item.unitCost)
                    put("totalCost", item.totalCost)
                    put("notes", item.notes)
                })
            }
        }.toString()

        val milestonesJson = JSONArray().apply {
            estimate.milestones.forEach { m ->
                put(JSONObject().apply {
                    put("phase", m.phase)
                    put("percentage", m.percentage)
                    put("amount", m.amount)
                    put("description", m.description)
                })
            }
        }.toString()

        val entity = EstimateEntity(
            title = estimate.title,
            tradeName = estimate.trade.name,
            modelUsed = estimate.modelUsed,
            styleUsed = estimate.styleUsed,
            totalMinCost = estimate.totalMinCost,
            totalMaxCost = estimate.totalMaxCost,
            targetCost = estimate.targetCost,
            laborCost = estimate.laborCost,
            materialCost = estimate.materialCost,
            permitsCost = estimate.permitsAndFeesCost,
            equipmentCost = estimate.equipmentCost,
            overheadAndProfit = estimate.overheadAndProfit,
            contingencyAmount = estimate.contingencyAmount,
            estimatedDays = estimate.estimatedDays,
            scopeSummary = estimate.scopeSummary,
            lineItemsJson = lineItemsJson,
            milestonesJson = milestonesJson,
            rawProposal = estimate.rawAiProposal
        )
        return database.estimateDao().insertEstimate(entity)
    }

    suspend fun deleteEstimate(id: Long) {
        database.estimateDao().deleteEstimateById(id)
    }

    val allLeads: Flow<List<LeadItem>> = database.leadDao().getAllLeads().map { list ->
        list.map { entity ->
            val partner = try { PartnerType.valueOf(entity.partnerType) } catch (e: Exception) { PartnerType.THUMBTACK }
            val trade = TradeCategory.entries.find { it.displayName == entity.tradeName } ?: TradeCategory.REMODELING
            LeadItem(
                id = entity.id,
                clientName = entity.clientName,
                clientLocation = entity.clientLocation,
                partner = partner,
                trade = trade,
                projectTitle = entity.projectTitle,
                projectDescription = entity.projectDescription,
                customerBudget = entity.customerBudget,
                urgency = entity.urgency,
                status = entity.status,
                quoteSentAmount = entity.quoteSentAmount,
                receivedAt = entity.receivedAt
            )
        }
    }

    suspend fun createNewLead(lead: LeadItem): Long {
        val entity = LeadEntity(
            clientName = lead.clientName,
            clientLocation = lead.clientLocation,
            partnerType = lead.partner.name,
            tradeName = lead.trade.displayName,
            projectTitle = lead.projectTitle,
            projectDescription = lead.projectDescription,
            customerBudget = lead.customerBudget,
            urgency = lead.urgency,
            status = lead.status,
            quoteSentAmount = lead.quoteSentAmount,
            receivedAt = System.currentTimeMillis()
        )
        val id = database.leadDao().insertLead(entity)
        triggerLeadWebhook(lead)
        return id
    }

    suspend fun updateLeadStatus(id: Long, status: String, quoteAmount: Double? = null) {
        database.leadDao().updateLeadStatus(id, status, quoteAmount)
    }

    suspend fun deleteLead(id: Long) {
        database.leadDao().deleteLeadById(id)
    }

    suspend fun generateAiLeadResponseAndQuote(
        lead: LeadItem,
        model: EstimatorModel = EstimatorModel.GEMINI_3_5_FLASH,
        style: EstimatorStyle = when (lead.partner) {
            PartnerType.THUMBTACK, PartnerType.TASKRABBIT -> EstimatorStyle.COMPETITIVE_BIDDER
            PartnerType.HOUZZ -> EstimatorStyle.LUXURY_ARCHITECTURAL
            PartnerType.ANGI -> EstimatorStyle.CONTRACTOR_PRECISION
            PartnerType.YELP -> EstimatorStyle.CONSUMER_BALLPARK
        }
    ): EstimateResult {
        return generateAndSaveEstimate(
            title = lead.projectTitle,
            trade = lead.trade,
            scopeDetails = "${lead.projectDescription} (Client: ${lead.clientName}, Budget: ${lead.customerBudget}, Platform: ${lead.partner.displayName})",
            squareFeetOrUnits = 100.0,
            materialGrade = if (lead.partner == PartnerType.HOUZZ) "Premium" else "Standard",
            urgency = lead.urgency,
            locationZip = lead.clientLocation,
            markupPercent = 15,
            contingencyPercent = 10,
            model = model,
            style = style,
            autoSave = true
        )
    }

    suspend fun dispatchQuoteForLead(
        lead: LeadItem,
        estimate: EstimateResult
    ): WebhookDeliveryLog {
        val partnerConfig = database.partnerConfigDao().getConfigForPartner(lead.partner.name)
        val partnerWebhookUrl = partnerConfig?.webhookUrl ?: ""
        val log = webhookDispatcher.dispatchQuoteToPartner(
            partner = lead.partner,
            clientName = lead.clientName,
            projectTitle = lead.projectTitle,
            quoteAmount = estimate.targetCost,
            proposalText = estimate.rawAiProposal,
            partnerWebhookUrl = partnerWebhookUrl
        )
        saveWebhookLog(log)
        updateLeadStatus(lead.id, "Quoted", estimate.targetCost)
        return log
    }

    val allWebhooks: Flow<List<WebhookConfig>> = database.webhookDao().getAllWebhooks().map { list ->
        list.map { entity ->
            WebhookConfig(
                id = entity.id,
                name = entity.name,
                url = entity.url,
                triggerEvent = entity.triggerEvent,
                httpMethod = entity.httpMethod,
                authHeader = entity.authHeader,
                customHeadersJson = entity.customHeadersJson,
                isEnabled = entity.isEnabled,
                lastStatusCode = entity.lastStatusCode,
                lastLatencyMs = entity.lastLatencyMs,
                lastTriggeredAt = entity.lastTriggeredAt
            )
        }
    }

    val recentWebhookLogs: Flow<List<WebhookDeliveryLog>> = database.webhookLogDao().getRecentLogs().map { list ->
        list.map { entity ->
            WebhookDeliveryLog(
                id = entity.id,
                webhookName = entity.webhookName,
                url = entity.url,
                event = entity.event,
                payloadJson = entity.payloadJson,
                statusCode = entity.statusCode,
                responseBody = entity.responseBody,
                latencyMs = entity.latencyMs,
                isSuccess = entity.isSuccess,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun saveWebhookConfig(config: WebhookConfig): Long {
        val entity = WebhookEntity(
            id = config.id,
            name = config.name,
            url = config.url,
            triggerEvent = config.triggerEvent,
            httpMethod = config.httpMethod,
            authHeader = config.authHeader,
            customHeadersJson = config.customHeadersJson,
            isEnabled = config.isEnabled,
            lastStatusCode = config.lastStatusCode,
            lastLatencyMs = config.lastLatencyMs,
            lastTriggeredAt = config.lastTriggeredAt
        )
        return if (config.id == 0L) {
            database.webhookDao().insertWebhook(entity)
        } else {
            database.webhookDao().updateWebhook(entity)
            config.id
        }
    }

    suspend fun deleteWebhook(id: Long) {
        database.webhookDao().deleteWebhookById(id)
    }

    suspend fun testWebhookExecution(
        webhookName: String,
        url: String,
        httpMethod: String,
        authHeader: String,
        customHeadersJson: String,
        payloadJson: String
    ): WebhookDeliveryLog {
        val log = webhookDispatcher.testWebhook(
            webhookName = webhookName,
            url = url,
            httpMethod = httpMethod,
            authHeader = authHeader,
            customHeadersJson = customHeadersJson,
            payloadJson = payloadJson
        )
        saveWebhookLog(log)
        return log
    }

    suspend fun saveWebhookLog(log: WebhookDeliveryLog) {
        val entity = WebhookLogEntity(
            webhookName = log.webhookName,
            url = log.url,
            event = log.event,
            payloadJson = log.payloadJson,
            statusCode = log.statusCode,
            responseBody = log.responseBody,
            latencyMs = log.latencyMs,
            isSuccess = log.isSuccess,
            timestamp = log.timestamp
        )
        database.webhookLogDao().insertLog(entity)
    }

    suspend fun clearWebhookLogs() {
        database.webhookLogDao().clearAllLogs()
    }

    private suspend fun triggerWebhooksForEvent(event: String, estimate: EstimateResult) {
        val enabledWebhooks = database.webhookDao().getEnabledWebhooks()
        for (hook in enabledWebhooks) {
            if (hook.triggerEvent == event || hook.triggerEvent == "ALL_EVENTS") {
                val webhookConfig = WebhookConfig(
                    id = hook.id,
                    name = hook.name,
                    url = hook.url,
                    triggerEvent = hook.triggerEvent,
                    httpMethod = hook.httpMethod,
                    authHeader = hook.authHeader,
                    customHeadersJson = hook.customHeadersJson,
                    isEnabled = hook.isEnabled
                )
                val log = webhookDispatcher.dispatchEstimateWebhook(webhookConfig, estimate)
                saveWebhookLog(log)
                val updated = hook.copy(
                    lastStatusCode = log.statusCode,
                    lastLatencyMs = log.latencyMs,
                    lastTriggeredAt = log.timestamp
                )
                database.webhookDao().updateWebhook(updated)
            }
        }
    }

    private suspend fun triggerLeadWebhook(lead: LeadItem) {
        val enabledWebhooks = database.webhookDao().getEnabledWebhooks()
        val payload = JSONObject().apply {
            put("event", "LEAD_RECEIVED")
            put("partner", lead.partner.name)
            put("client", lead.clientName)
            put("trade", lead.trade.displayName)
            put("budget", lead.customerBudget)
            put("urgency", lead.urgency)
            put("description", lead.projectDescription)
            put("timestamp", lead.receivedAt)
        }.toString(2)
        for (hook in enabledWebhooks) {
            if (hook.triggerEvent == "ON_LEAD_RECEIVED" || hook.triggerEvent == "ALL_EVENTS") {
                val log = webhookDispatcher.testWebhook(
                    webhookName = hook.name,
                    url = hook.url,
                    httpMethod = hook.httpMethod,
                    authHeader = hook.authHeader,
                    customHeadersJson = hook.customHeadersJson,
                    payloadJson = payload
                )
                saveWebhookLog(log)
            }
        }
    }

    val allPartnerConfigs: Flow<List<PartnerConfigEntity>> = database.partnerConfigDao().getAllPartnerConfigs()

    suspend fun updatePartnerConfig(config: PartnerConfigEntity) {
        database.partnerConfigDao().insertOrUpdateConfig(config)
    }

    suspend fun chatWithAi(prompt: String, history: List<Pair<String, String>>): String {
        return estimatorService.chatWithGemini(prompt, history)
    }

    suspend fun generateAiImage(prompt: String): String {
        return estimatorService.generateImageWithGemini(prompt)
    }
}
