package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        EstimateEntity::class,
        LeadEntity::class,
        WebhookEntity::class,
        WebhookLogEntity::class,
        PartnerConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun estimateDao(): EstimateDao
    abstract fun leadDao(): LeadDao
    abstract fun webhookDao(): WebhookDao
    abstract fun webhookLogDao(): WebhookLogDao
    abstract fun partnerConfigDao(): PartnerConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "protrade_database"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val partners = listOf(
                PartnerConfigEntity(
                    partnerKey = "THUMBTACK",
                    isConnected = true,
                    apiKey = "tb_live_pk_9942a781b01c",
                    webhookUrl = "https://hooks.thumbtack.com/v1/pro/leads",
                    rating = 4.9,
                    reviewsCount = 142,
                    activeLeadsCount = 4,
                    customNotes = "Top Pro Badge Active. High conversion on Kitchen & Bath Remodels."
                ),
                PartnerConfigEntity(
                    partnerKey = "TASKRABBIT",
                    isConnected = true,
                    apiKey = "tr_oauth_tok_7720bc4e",
                    webhookUrl = "https://api.taskrabbit.com/v2/tasker/dispatch",
                    rating = 4.8,
                    reviewsCount = 98,
                    activeLeadsCount = 6,
                    customNotes = "Elite Tasker Level 3. Best for urgent repairs, electrical & mounting."
                ),
                PartnerConfigEntity(
                    partnerKey = "ANGI",
                    isConnected = true,
                    apiKey = "angi_pro_sec_330198ad",
                    webhookUrl = "https://leadconnector.angi.com/webhooks/pro",
                    rating = 4.9,
                    reviewsCount = 215,
                    activeLeadsCount = 3,
                    customNotes = "Angi Certified Contractor. High ticket roofing & full additions."
                ),
                PartnerConfigEntity(
                    partnerKey = "YELP",
                    isConnected = true,
                    apiKey = "yelp_biz_api_884920fc",
                    webhookUrl = "https://api.yelp.com/v3/biz/messaging/webhook",
                    rating = 4.7,
                    reviewsCount = 84,
                    activeLeadsCount = 5,
                    customNotes = "Verified License Badge. Quick response time: avg 4 mins."
                ),
                PartnerConfigEntity(
                    partnerKey = "HOUZZ",
                    isConnected = true,
                    apiKey = "houzz_pro_jwt_554109ea",
                    webhookUrl = "https://pro.houzz.com/api/v1/inbound_leads",
                    rating = 5.0,
                    reviewsCount = 47,
                    activeLeadsCount = 2,
                    customNotes = "Houzz Pro Ultimate. Architectural design, 3D proposals & luxury clients."
                )
            )
            database.partnerConfigDao().insertAllConfigs(partners)

            val webhooks = listOf(
                WebhookEntity(
                    name = "Zapier - Contractor CRM Sync",
                    url = "https://hooks.zapier.com/hooks/catch/9128374/trade_pro_sync",
                    triggerEvent = "ALL_EVENTS",
                    httpMethod = "POST",
                    authHeader = "Bearer zap_live_test_key_884",
                    customHeadersJson = "{\"Content-Type\":\"application/json\", \"X-Source\":\"ProTrade-AI\"}",
                    isEnabled = true,
                    lastStatusCode = 200,
                    lastLatencyMs = 142,
                    lastTriggeredAt = System.currentTimeMillis() - 1000 * 60 * 15
                ),
                WebhookEntity(
                    name = "Make.com (Integromat) Invoice Dispatcher",
                    url = "https://hook.eu1.make.com/kxm4920s8174nqp0198",
                    triggerEvent = "ON_ESTIMATE_CREATED",
                    httpMethod = "POST",
                    authHeader = "",
                    customHeadersJson = "{\"Content-Type\":\"application/json\"}",
                    isEnabled = true,
                    lastStatusCode = 200,
                    lastLatencyMs = 188,
                    lastTriggeredAt = System.currentTimeMillis() - 1000 * 60 * 45
                ),
                WebhookEntity(
                    name = "n8n Self-Hosted Quote Automation",
                    url = "https://n8n.internal-trades.io/webhook/quote-dispatcher",
                    triggerEvent = "ON_QUOTE_DISPATCHED",
                    httpMethod = "POST",
                    authHeader = "X-N8N-TOKEN n8n_sec_5501",
                    customHeadersJson = "{\"Content-Type\":\"application/json\", \"X-Agent\":\"ProTradeMaster\"}",
                    isEnabled = true,
                    lastStatusCode = 200,
                    lastLatencyMs = 95,
                    lastTriggeredAt = System.currentTimeMillis() - 1000 * 60 * 120
                ),
                WebhookEntity(
                    name = "Slack / Discord Project Alerts",
                    url = "https://hooks.slack.com/services/T00/B00/X00EXAMPLE",
                    triggerEvent = "ON_LEAD_RECEIVED",
                    httpMethod = "POST",
                    authHeader = "",
                    customHeadersJson = "{\"Content-Type\":\"application/json\"}",
                    isEnabled = false,
                    lastStatusCode = null,
                    lastLatencyMs = null,
                    lastTriggeredAt = null
                )
            )
            database.webhookDao().insertWebhooks(webhooks)

            val leads = listOf(
                LeadEntity(
                    clientName = "Sarah Jenkins",
                    clientLocation = "Austin, TX (78704)",
                    partnerType = "THUMBTACK",
                    tradeName = "Kitchen & Bath Remodel",
                    projectTitle = "Full Master Bathroom Remodel + Walk-in Shower",
                    projectDescription = "Need demolition of existing 1990s tub, install custom frameless glass walk-in shower with subway tiles, double vanity with quartz top, and heated tile floor (120 sq ft).",
                    customerBudget = "$15,000 - $22,000",
                    urgency = "Within 2 Weeks",
                    status = "New",
                    quoteSentAmount = null,
                    receivedAt = System.currentTimeMillis() - 1000 * 60 * 12
                ),
                LeadEntity(
                    clientName = "Marcus Vance",
                    clientLocation = "Denver, CO (80202)",
                    partnerType = "TASKRABBIT",
                    tradeName = "Electrical & EV / Panels",
                    projectTitle = "Tesla Level 2 EV Wall Connector + 60A Subpanel Run",
                    projectDescription = "Need a 240V 60-amp circuit installed from main panel in basement to attached garage (approx 45 feet conduit run) and hardwire wall connector.",
                    customerBudget = "$850 - $1,400",
                    urgency = "Immediate / Today",
                    status = "New",
                    quoteSentAmount = null,
                    receivedAt = System.currentTimeMillis() - 1000 * 60 * 28
                ),
                LeadEntity(
                    clientName = "Eleanor & David Brooks",
                    clientLocation = "Seattle, WA (98103)",
                    partnerType = "ANGI",
                    tradeName = "Roofing & Gutters",
                    projectTitle = "Architectural Shingle Roof Replacement (28 Squares)",
                    projectDescription = "Total tear-off of 25-year-old cedar shake/shingle roof. Install GAF Timberline HDZ architectural shingles, ice/water barrier, ridge vents, and new seamless aluminum gutters.",
                    customerBudget = "$18,000 - $26,000",
                    urgency = "Within 1 Month",
                    status = "Quoted",
                    quoteSentAmount = 21450.0,
                    receivedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3
                ),
                LeadEntity(
                    clientName = "Brian Gallagher",
                    clientLocation = "Chicago, IL (60614)",
                    partnerType = "YELP",
                    tradeName = "Plumbing & Piping",
                    projectTitle = "Tankless Water Heater Conversion + Whole-Home Softener",
                    projectDescription = "Replace old 50-gallon tank heater with Navien NPE-240A2 condensing tankless unit. Route new gas line intake and PVC direct vent. Add sediment pre-filter.",
                    customerBudget = "$4,000 - $6,500",
                    urgency = "Within 1 Week",
                    status = "New",
                    quoteSentAmount = null,
                    receivedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 5
                ),
                LeadEntity(
                    clientName = "Victoria Sterling",
                    clientLocation = "Scottsdale, AZ (85255)",
                    partnerType = "HOUZZ",
                    tradeName = "Kitchen & Bath Remodel",
                    projectTitle = "Luxury Chef's Kitchen Renovation (380 sq ft)",
                    projectDescription = "Full bespoke remodel: Custom walnut cabinetry, Calacatta Gold marble waterfall island (10ft), Sub-Zero/Wolf appliance suite installation, custom brass pot filler, and recessed LED channel lighting.",
                    customerBudget = "$75,000 - $110,000",
                    urgency = "Flexible / Design Phase",
                    status = "Quoted",
                    quoteSentAmount = 88500.0,
                    receivedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24
                )
            )
            database.leadDao().insertLeads(leads)
        }
    }
}
