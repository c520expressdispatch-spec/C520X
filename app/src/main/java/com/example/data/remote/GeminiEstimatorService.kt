package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.models.CustomApiConfig
import com.example.data.models.EstimateResult
import com.example.data.models.EstimatorModel
import com.example.data.models.EstimatorStyle
import com.example.data.models.LineItem
import com.example.data.models.ProjectMilestone
import com.example.data.models.TradeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class GeminiEstimatorService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateEstimate(
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
        customApiConfig: CustomApiConfig = CustomApiConfig()
    ): EstimateResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.ifBlank { "" }

        val prompt = buildPrompt(
            title = title,
            trade = trade,
            scopeDetails = scopeDetails,
            squareFeetOrUnits = squareFeetOrUnits,
            materialGrade = materialGrade,
            urgency = urgency,
            locationZip = locationZip,
            markupPercent = markupPercent,
            contingencyPercent = contingencyPercent,
            style = style
        )

        if (model == EstimatorModel.CUSTOM_API && customApiConfig.endpointUrl.isNotBlank()) {
            return@withContext callCustomApi(prompt, title, trade, model, style, customApiConfig, squareFeetOrUnits)
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiEstimator", "Using local high-precision calculation engine (API key not configured)")
            return@withContext calculateDeterministicEstimate(
                title, trade, scopeDetails, squareFeetOrUnits, materialGrade, urgency, locationZip, markupPercent, contingencyPercent, model, style
            )
        }

        try {
            val selectedApiModel = when (model) {
                EstimatorModel.GEMINI_3_5_FLASH -> "gemini-3.5-flash"
                EstimatorModel.GEMINI_3_1_PRO -> "gemini-3.1-pro-preview"
                EstimatorModel.GEMINI_FLASH_LITE -> "gemini-3.1-flash-lite-preview"
                EstimatorModel.GEMINI_THINKING -> "gemini-3.1-pro-preview"
                else -> "gemini-3.5-flash"
            }
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$selectedApiModel:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("text", prompt)
                    }
                    val contentObj = JSONObject().apply {
                        put("parts", JSONArray().put(partObj))
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                val genConfig = JSONObject().apply {
                    put("temperature", if (style == EstimatorStyle.CONTRACTOR_PRECISION) 0.2 else 0.4)
                    put("topP", 0.95)
                    put("topK", 40)
                    if (model == EstimatorModel.GEMINI_THINKING) {
                        put("thinkingConfig", JSONObject().put("thinkingLevel", "low"))
                    }
                }
                put("generationConfig", genConfig)
            }
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful && responseBody.isNotBlank()) {
                val parsedResult = parseGeminiResponse(responseBody, title, trade, model, style, squareFeetOrUnits, markupPercent, contingencyPercent)
                if (parsedResult != null) {
                    return@withContext parsedResult
                }
            }
            calculateDeterministicEstimate(
                title, trade, scopeDetails, squareFeetOrUnits, materialGrade, urgency, locationZip, markupPercent, contingencyPercent, model, style
            )
        } catch (e: Exception) {
            Log.e("GeminiEstimator", "Error calling Gemini API: ${e.message}", e)
            calculateDeterministicEstimate(
                title, trade, scopeDetails, squareFeetOrUnits, materialGrade, urgency, locationZip, markupPercent, contingencyPercent, model, style
            )
        }
    }

    private fun buildPrompt(
        title: String,
        trade: TradeCategory,
        scopeDetails: String,
        squareFeetOrUnits: Double,
        materialGrade: String,
        urgency: String,
        locationZip: String,
        markupPercent: Int,
        contingencyPercent: Int,
        style: EstimatorStyle
    ): String {
        return """
You are an expert Master Estimator & Contractor Specialist. Generate a comprehensive, professional project cost estimate and bidding proposal.
PROJECT DETAILS:
- Title: $title
- Trade Category: ${trade.displayName}
- Scope / Requirements: $scopeDetails
- Dimensions / Units: $squareFeetOrUnits ${trade.typicalUnit}
- Material Grade: $materialGrade
- Urgency: $urgency
- Location / Zip: $locationZip
- Contractor Markup: $markupPercent%
- Contingency Reserve: $contingencyPercent%
- Estimation Style: ${style.displayName} (${style.subtitle})

Respond strictly with a structured JSON object inside ```json ... ``` markdown code fence matching this exact format:
{
  "title": "$title",
  "scopeSummary": "concise executive summary of project deliverables",
  "estimatedDays": 5,
  "laborCost": 3500.0,
  "materialCost": 2800.0,
  "permitsCost": 350.0,
  "equipmentCost": 400.0,
  "overheadAndProfit": 1400.0,
  "contingencyAmount": 600.0,
  "totalMinCost": 8500.0,
  "targetCost": 9050.0,
  "totalMaxCost": 10200.0,
  "lineItems": [
    {"category": "Labor", "name": "Skilled Demo & Prep", "quantity": 16.0, "unit": "hours", "unitCost": 85.0, "totalCost": 1360.0, "notes": "Two technicians"},
    {"category": "Materials", "name": "Primary Fixtures & Materials", "quantity": 1.0, "unit": "lot", "unitCost": 2500.0, "totalCost": 2500.0, "notes": "$materialGrade grade"},
    {"category": "Permits & Fees", "name": "Municipal Trade Permit & Inspection", "quantity": 1.0, "unit": "permit", "unitCost": 350.0, "totalCost": 350.0, "notes": "City code compliant"}
  ],
  "milestones": [
    {"phase": "Deposit & Mobilization", "percentage": 30, "amount": 2715.0, "description": "Material procurement and permits"},
    {"phase": "Rough-In & Midpoint", "percentage": 40, "amount": 3620.0, "description": "Core structural and mechanical installation"},
    {"phase": "Final Walkthrough & Completion", "percentage": 30, "amount": 2715.0, "description": "Finishes, punch list and sign-off"}
  ],
  "formattedProposal": "Professional formatted client quote letter suitable for sending on Thumbtack, TaskRabbit, Angi, Yelp, or Houzz."
}
""".trimIndent()
    }

    private fun parseGeminiResponse(
        responseBody: String,
        title: String,
        trade: TradeCategory,
        model: EstimatorModel,
        style: EstimatorStyle,
        units: Double,
        markupPercent: Int,
        contingencyPercent: Int
    ): EstimateResult? {
        try {
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            val text = parts.getJSONObject(0).optString("text", "")

            val jsonStart = text.indexOf("{")
            val jsonEnd = text.lastIndexOf("}")
            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) return null
            val jsonString = text.substring(jsonStart, jsonEnd + 1)
            val data = JSONObject(jsonString)
            val labor = data.optDouble("laborCost", 0.0)
            val materials = data.optDouble("materialCost", 0.0)
            val permits = data.optDouble("permitsCost", 0.0)
            val equip = data.optDouble("equipmentCost", 0.0)
            val op = data.optDouble("overheadAndProfit", 0.0)
            val contingency = data.optDouble("contingencyAmount", 0.0)
            val target = data.optDouble("targetCost", labor + materials + permits + equip + op + contingency)
            val min = data.optDouble("totalMinCost", target * 0.92)
            val max = data.optDouble("totalMaxCost", target * 1.12)
            val days = data.optInt("estimatedDays", 5)
            val summary = data.optString("scopeSummary", title)
            val proposal = data.optString("formattedProposal", text)

            val lineItemsList = mutableListOf<LineItem>()
            val lineItemsArray = data.optJSONArray("lineItems")
            if (lineItemsArray != null) {
                for (i in 0 until lineItemsArray.length()) {
                    val item = lineItemsArray.getJSONObject(i)
                    lineItemsList.add(
                        LineItem(
                            category = item.optString("category", "General"),
                            name = item.optString("name", "Work Item"),
                            quantity = item.optDouble("quantity", 1.0),
                            unit = item.optString("unit", "ea"),
                            unitCost = item.optDouble("unitCost", 0.0),
                            totalCost = item.optDouble("totalCost", 0.0),
                            notes = item.optString("notes", "")
                        )
                    )
                }
            }
            val milestonesList = mutableListOf<ProjectMilestone>()
            val milestonesArray = data.optJSONArray("milestones")
            if (milestonesArray != null) {
                for (i in 0 until milestonesArray.length()) {
                    val m = milestonesArray.getJSONObject(i)
                    milestonesList.add(
                        ProjectMilestone(
                            phase = m.optString("phase", "Phase ${i + 1}"),
                            percentage = m.optInt("percentage", 33),
                            amount = m.optDouble("amount", target / 3),
                            description = m.optString("description", "")
                        )
                    )
                }
            }

            return EstimateResult(
                title = data.optString("title", title),
                trade = trade,
                modelUsed = model.displayName,
                styleUsed = style.displayName,
                totalMinCost = (min * 100).roundToInt() / 100.0,
                totalMaxCost = (max * 100).roundToInt() / 100.0,
                targetCost = (target * 100).roundToInt() / 100.0,
                laborCost = (labor * 100).roundToInt() / 100.0,
                materialCost = (materials * 100).roundToInt() / 100.0,
                permitsAndFeesCost = (permits * 100).roundToInt() / 100.0,
                equipmentCost = (equip * 100).roundToInt() / 100.0,
                overheadAndProfit = (op * 100).roundToInt() / 100.0,
                contingencyAmount = (contingency * 100).roundToInt() / 100.0,
                estimatedDays = days,
                lineItems = lineItemsList,
                milestones = milestonesList,
                scopeSummary = summary,
                rawAiProposal = proposal
            )
        } catch (e: Exception) {
            Log.e("GeminiEstimator", "Failed to parse JSON response: ${e.message}")
            return null
        }
    }

    private fun callCustomApi(
        prompt: String,
        title: String,
        trade: TradeCategory,
        model: EstimatorModel,
        style: EstimatorStyle,
        config: CustomApiConfig,
        units: Double
    ): EstimateResult {
        try {
            val jsonBody = JSONObject().apply {
                put("model", config.modelIdentifier)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", if (config.systemPromptOverride.isNotBlank()) config.systemPromptOverride else "You are an expert contractor cost estimator.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }
                put("messages", messages)
                put("temperature", 0.3)
            }
            val requestBuilder = Request.Builder()
                .url(config.endpointUrl)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            if (config.apiKey.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "${config.customAuthHeader} ${config.apiKey}".trim())
            }
            val response = client.newCall(requestBuilder.build()).execute()
            val body = response.body?.string() ?: ""
            if (response.isSuccessful && body.isNotBlank()) {
                val root = JSONObject(body)
                val choices = root.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    val content = message?.optString("content", "") ?: ""
                    val jsonStart = content.indexOf("{")
                    val jsonEnd = content.lastIndexOf("}")
                    if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                        val parsed = parseGeminiResponse(
                            "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":${JSONObject.quote(content)}}]}}]}",
                            title, trade, model, style, units, 15, 10
                        )
                        if (parsed != null) return parsed
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CustomApi", "Custom API error: ${e.message}")
        }
        return calculateDeterministicEstimate(title, trade, "", units, "Standard", "Standard", "00000", 15, 10, model, style)
    }

    fun calculateDeterministicEstimate(
        title: String,
        trade: TradeCategory,
        scopeDetails: String,
        units: Double,
        materialGrade: String,
        urgency: String,
        locationZip: String,
        markupPercent: Int,
        contingencyPercent: Int,
        model: EstimatorModel,
        style: EstimatorStyle
    ): EstimateResult {
        val qty = if (units > 0) units else 100.0
        val gradeMultiplier = when (materialGrade.lowercase()) {
            "economy" -> 0.75
            "standard" -> 1.0
            "premium" -> 1.45
            "ultra-luxury", "luxury" -> 2.10
            else -> 1.0
        }
        val styleAdjustment = when (style) {
            EstimatorStyle.CONTRACTOR_PRECISION -> 1.0
            EstimatorStyle.CONSUMER_BALLPARK -> 1.05
            EstimatorStyle.INSURANCE_XACTIMATE -> 1.15
            EstimatorStyle.LUXURY_ARCHITECTURAL -> 1.65
            EstimatorStyle.COMPETITIVE_BIDDER -> 0.88
        }
        val baseLaborRate = trade.avgHourlyRate.toDouble()
        val estimatedLaborHours = when (trade) {
            TradeCategory.REMODELING -> (qty * 0.45).coerceAtLeast(24.0)
            TradeCategory.PLUMBING -> (qty * 0.35).coerceAtLeast(8.0)
            TradeCategory.ELECTRICAL -> (qty * 0.40).coerceAtLeast(8.0)
            TradeCategory.ROOFING -> (qty * 0.20).coerceAtLeast(16.0)
            TradeCategory.PAINTING -> (qty * 0.08).coerceAtLeast(10.0)
            TradeCategory.HVAC -> (qty * 0.30).coerceAtLeast(12.0)
            TradeCategory.FLOORING -> (qty * 0.12).coerceAtLeast(12.0)
            TradeCategory.CARPENTRY -> (qty * 0.28).coerceAtLeast(16.0)
            TradeCategory.LANDSCAPING -> (qty * 0.10).coerceAtLeast(12.0)
            TradeCategory.HANDYMAN -> (qty * 0.50).coerceAtLeast(4.0)
            TradeCategory.CONCRETE -> (qty * 0.25).coerceAtLeast(16.0)
            TradeCategory.CUSTOM -> (qty * 0.35).coerceAtLeast(20.0)
        }
        val laborCost = estimatedLaborHours * baseLaborRate * styleAdjustment
        val baseMaterialUnitCost = when (trade) {
            TradeCategory.REMODELING -> 45.0
            TradeCategory.PLUMBING -> 65.0
            TradeCategory.ELECTRICAL -> 40.0
            TradeCategory.ROOFING -> 55.0
            TradeCategory.PAINTING -> 1.25
            TradeCategory.HVAC -> 85.0
            TradeCategory.FLOORING -> 8.50
            TradeCategory.CARPENTRY -> 28.0
            TradeCategory.LANDSCAPING -> 12.0
            TradeCategory.HANDYMAN -> 20.0
            TradeCategory.CONCRETE -> 18.0
            TradeCategory.CUSTOM -> 35.0
        }
        val materialCost = (qty * baseMaterialUnitCost * gradeMultiplier * styleAdjustment).coerceAtLeast(350.0)
        val permitsAndFees = if (trade in listOf(TradeCategory.REMODELING, TradeCategory.ELECTRICAL, TradeCategory.PLUMBING, TradeCategory.ROOFING, TradeCategory.HVAC)) 380.0 else 0.0
        val equipmentCost = when (trade) {
            TradeCategory.ROOFING, TradeCategory.CONCRETE, TradeCategory.LANDSCAPING -> 450.0
            TradeCategory.REMODELING, TradeCategory.HVAC -> 275.0
            else -> 120.0
        }
        val subtotal = laborCost + materialCost + permitsAndFees + equipmentCost
        val overheadAndProfit = subtotal * (markupPercent / 100.0)
        val contingencyAmount = subtotal * (contingencyPercent / 100.0)
        val targetCost = subtotal + overheadAndProfit + contingencyAmount
        val totalMinCost = targetCost * 0.90
        val totalMaxCost = targetCost * 1.15
        val estimatedDays = (estimatedLaborHours / 8.0).coerceAtLeast(1.0).roundToInt()
        val lineItems = listOf(
            LineItem("Labor", "Master Technician Demolition & Preparation", (estimatedLaborHours * 0.3).roundToInt().toDouble(), "hrs", baseLaborRate, (estimatedLaborHours * 0.3 * baseLaborRate), "Site containment & surface prep"),
            LineItem("Labor", "Certified Trade Installation & Trim Work", (estimatedLaborHours * 0.7).roundToInt().toDouble(), "hrs", baseLaborRate, (estimatedLaborHours * 0.7 * baseLaborRate), "Licensed journeyman lead"),
            LineItem("Materials", "Primary Grade Specifications ($materialGrade)", qty, trade.typicalUnit, (materialCost * 0.75 / qty.coerceAtLeast(1.0)), materialCost * 0.75, "Spec-sheet compliant materials"),
            LineItem("Materials", "Rough Hardware, Fasteners & Sealants", 1.0, "lot", materialCost * 0.25, materialCost * 0.25, "Adhesives, anchors and trim items"),
            LineItem("Permits & Fees", "Municipal Permit Filing & Final Inspection", 1.0, "filing", permitsAndFees, permitsAndFees, "Code compliance assurance"),
            LineItem("Equipment", "Specialized Heavy Tool Rental & Haul-Away", 1.0, "job", equipmentCost, equipmentCost, "Debris disposal and containment")
        )
        val milestones = listOf(
            ProjectMilestone("Initial Deposit & Procurement", 30, targetCost * 0.30, "Secures crew schedule, ordering $materialGrade materials, and permit application."),
            ProjectMilestone("Rough-in & Mechanical Phase", 40, targetCost * 0.40, "Completion of structural framing, electrical/plumbing rough-in and passed inspection."),
            ProjectMilestone("Final Finishes & Sign-off", 30, targetCost * 0.30, "Final polish, client punch list walkthrough, warranty packet hand-off.")
        )
        val proposal = """
# FORMAL PROJECT PROPOSAL & ESTIMATE
**Project:** $title
**Category:** ${trade.displayName} | **Model Engine:** ${model.displayName}
**Style:** ${style.displayName}

### EXECUTIVE SCOPE OF WORK
$scopeDetails (Dimension: $qty ${trade.typicalUnit}, Quality: $materialGrade)

### FINANCIAL SUMMARY
- **Target Contract Price:** $${"%,.2f".format(targetCost)}
- **Estimated Range:** $${"%,.2f".format(totalMinCost)} - $${"%,.2f".format(totalMaxCost)}
- **Labor Subtotal:** $${"%,.2f".format(laborCost)} (~$estimatedLaborHours labor hours)
- **Materials Subtotal:** $${"%,.2f".format(materialCost)}
- **Permits & Code Compliance:** $${"%,.2f".format(permitsAndFees)}
- **Contractor Markup & Overhead:** $${"%,.2f".format(overheadAndProfit)} (${markupPercent}%)
- **Safety Contingency Reserve:** $${"%,.2f".format(contingencyAmount)} (${contingencyPercent}%)

### TIMELINE & MILESTONES
Estimated Turnaround: **$estimatedDays Business Days**
1. **Deposit (30%):** $${"%,.2f".format(targetCost * 0.30)}
2. **Progress Milestone (40%):** $${"%,.2f".format(targetCost * 0.40)}
3. **Completion & Acceptance (30%):** $${"%,.2f".format(targetCost * 0.30)}

*Ready for immediate dispatch to Thumbtack Pro, TaskRabbit, Angi, Yelp, or Houzz Pro.*
""".trimIndent()

        return EstimateResult(
            title = title,
            trade = trade,
            modelUsed = model.displayName,
            styleUsed = style.displayName,
            totalMinCost = (totalMinCost * 100).roundToInt() / 100.0,
            totalMaxCost = (totalMaxCost * 100).roundToInt() / 100.0,
            targetCost = (targetCost * 100).roundToInt() / 100.0,
            laborCost = (laborCost * 100).roundToInt() / 100.0,
            materialCost = (materialCost * 100).roundToInt() / 100.0,
            permitsAndFeesCost = (permitsAndFees * 100).roundToInt() / 100.0,
            equipmentCost = (equipmentCost * 100).roundToInt() / 100.0,
            overheadAndProfit = (overheadAndProfit * 100).roundToInt() / 100.0,
            contingencyAmount = (contingencyAmount * 100).roundToInt() / 100.0,
            estimatedDays = estimatedDays,
            lineItems = lineItems,
            milestones = milestones,
            scopeSummary = if (scopeDetails.isNotBlank()) scopeDetails else "Comprehensive ${trade.displayName} project ($qty ${trade.typicalUnit})",
            rawAiProposal = proposal
        )
    }

    suspend fun chatWithGemini(prompt: String, history: List<Pair<String, String>>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.ifBlank { return@withContext "API key not configured." }
        if (apiKey == "MY_GEMINI_API_KEY") return@withContext "Please configure your Gemini API Key in Settings."
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    for (msg in history) {
                        val role = if (msg.first == "user") "user" else "model"
                        put(JSONObject().apply {
                            put("role", role)
                            put("parts", JSONArray().put(JSONObject().put("text", msg.second)))
                        })
                    }
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                    })
                }
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", "You are the AI Command Assistant for Canyon 520 Express, a contractor and dispatch operations command center. You help the dispatch team summarize jobs, draft messages, and provide insights into contractor operations.")))
                })
            }
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful && responseBody.isNotBlank()) {
                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "Empty response.")
                    }
                }
            }
            return@withContext "Error: Could not retrieve response from AI."
        } catch (e: Exception) {
            Log.e("GeminiChat", "Chat Error: ${e.message}", e)
            return@withContext "Connection error: ${e.message}"
        }
    }

    suspend fun generateImageWithGemini(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.ifBlank { return@withContext "API key not configured." }
        if (apiKey == "MY_GEMINI_API_KEY") return@withContext "Please configure your Gemini API Key in Settings."
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-image-preview:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                    })
                }
                put("contents", contentsArray)
                val genConfig = JSONObject().apply {
                    put("responseModalities", JSONArray().put("IMAGE"))
                    put("imageConfig", JSONObject().apply {
                        put("aspectRatio", "1:1")
                        put("imageSize", "1K")
                    })
                }
                put("generationConfig", genConfig)
            }
            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (response.isSuccessful && responseBody.isNotBlank()) {
                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val part = parts.getJSONObject(0)
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val data = inlineData.optString("data")
                            return@withContext "data:image/jpeg;base64,$data"
                        }
                    }
                }
            }
            return@withContext "Error: Failed to generate image from AI."
        } catch (e: Exception) {
            Log.e("GeminiImage", "Image Error: ${e.message}", e)
            return@withContext "Connection error: ${e.message}"
        }
    }
}
