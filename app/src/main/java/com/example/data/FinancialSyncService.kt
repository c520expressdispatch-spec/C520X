package com.example.data

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class QboSyncSummary(
    val syncedCount: Int,
    val totalAmount: Double,
    val syncTimestamp: String,
    val batchId: String,
    val statusMessage: String
)

data class StripeConnectAccount(
    val accountId: String = "acct_1Nzk2Q2eZvKYlo2C",
    val businessName: String = "C520X Operations",
    val status: String = "Active & Verified",
    val expressPayoutsEnabled: Boolean = true,
    val availableBalance: Double = 24850.00,
    val pendingBalance: Double = 6320.00,
    val currency: String = "USD",
    val defaultCardFee: String = "2.9% + 30¢",
    val achFee: String = "0.8% ($5 cap)"
)

class FinancialSyncService {
    val stripeAccount = StripeConnectAccount()

    fun generateStripePaymentLink(invoiceNumber: String, amount: Double, clientName: String): String {
        val slug = invoiceNumber.lowercase().replace("-", "")
        return "https://pay.stripe.com/c520x/$slug?amt=${amount.toInt()}&client=${clientName.replace(" ", "+")}"
    }

    suspend fun syncExpensesToQuickBooks(
        expenses: List<ExpenseEntity>
    ): QboSyncSummary {
        // Simulate network API call to Intuit QuickBooks Online OAuth2 endpoints
        delay(800)
        val pending = expenses.filter { !it.qboSynced }
        val now = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(Date())
        val batchId = "QBO-BATCH-${UUID.randomUUID().toString().take(6).uppercase()}"

        return QboSyncSummary(
            syncedCount = pending.size,
            totalAmount = pending.sumOf { it.amount },
            syncTimestamp = now,
            batchId = batchId,
            statusMessage = "Successfully exported ${pending.size} expenses ($${pending.sumOf { it.amount }.toInt()}) to QuickBooks Online (Company ID #9130352841)"
        )
    }

    suspend fun processStripePayment(
        invoiceNumber: String,
        clientName: String,
        amount: Double,
        method: String // "Credit Card", "Apple Pay", "ACH Bank Transfer"
    ): Pair<Boolean, String> {
        delay(600)
        val txnRef = "ch_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        return Pair(true, txnRef)
    }
}
