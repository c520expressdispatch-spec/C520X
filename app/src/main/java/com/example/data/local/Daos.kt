package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {
    @Query("SELECT * FROM estimates ORDER BY createdAt DESC")
    fun getAllEstimates(): Flow<List<EstimateEntity>>

    @Query("SELECT * FROM estimates WHERE id = :id LIMIT 1")
    suspend fun getEstimateById(id: Long): EstimateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: EstimateEntity): Long

    @Query("DELETE FROM estimates WHERE id = :id")
    suspend fun deleteEstimateById(id: Long)

    @Query("DELETE FROM estimates")
    suspend fun deleteAllEstimates()
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY receivedAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE partnerType = :partner ORDER BY receivedAt DESC")
    fun getLeadsByPartner(partner: String): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("UPDATE leads SET status = :status, quoteSentAmount = :quoteAmount WHERE id = :id")
    suspend fun updateLeadStatus(id: Long, status: String, quoteAmount: Double?)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Long)
}

@Dao
interface WebhookDao {
    @Query("SELECT * FROM webhooks ORDER BY id ASC")
    fun getAllWebhooks(): Flow<List<WebhookEntity>>

    @Query("SELECT * FROM webhooks WHERE isEnabled = 1")
    suspend fun getEnabledWebhooks(): List<WebhookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebhook(webhook: WebhookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebhooks(webhooks: List<WebhookEntity>)

    @Update
    suspend fun updateWebhook(webhook: WebhookEntity)

    @Query("DELETE FROM webhooks WHERE id = :id")
    suspend fun deleteWebhookById(id: Long)
}

@Dao
interface WebhookLogDao {
    @Query("SELECT * FROM webhook_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<WebhookLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WebhookLogEntity): Long

    @Query("DELETE FROM webhook_logs")
    suspend fun clearAllLogs()
}

@Dao
interface PartnerConfigDao {
    @Query("SELECT * FROM partner_configs")
    fun getAllPartnerConfigs(): Flow<List<PartnerConfigEntity>>

    @Query("SELECT * FROM partner_configs WHERE partnerKey = :partnerKey LIMIT 1")
    suspend fun getConfigForPartner(partnerKey: String): PartnerConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: PartnerConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllConfigs(configs: List<PartnerConfigEntity>)
}
