package com.example.data

data class EstimateLineItem(
    val id: String,
    val category: String, // Labor, Materials, Permits, Subcontractor, Contingency
    val description: String,
    val quantity: Double,
    val unit: String, // hrs, sq ft, linear ft, units, lump sum
    val unitPrice: Double,
    val totalPrice: Double
)

data class ProjectPhase(
    val phaseNumber: Int,
    val name: String,
    val durationDays: Int,
    val tasks: List<String>,
    val milestoneDeliverable: String
)

data class AiGeneratedEstimate(
    val id: String,
    val title: String,
    val clientName: String,
    val location: String,
    val tradePersona: String, // Builder, Remodeler, Interior Designer, Contractor & Design-Build
    val projectSummary: String,
    val squareFootage: Double,
    val lineItems: List<EstimateLineItem>,
    val subtotal: Double,
    val markupPercent: Double,
    val markupAmount: Double,
    val permitCost: Double,
    val totalCost: Double,
    val estimatedDurationWeeks: Int,
    val projectPhases: List<ProjectPhase>,
    val localPricingBasis: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TradePersona(val label: String, val tagline: String, val defaultMarkup: Double) {
    BUILDER("Builder", "New Construction, Framing & Turnkey Builds", 20.0),
    REMODELER("Remodeler", "Kitchens, Baths, Additions & Renovations", 22.5),
    INTERIOR_DESIGNER("Interior Designer", "Space Planning, 3D Finishes & Furnishings", 25.0),
    DESIGN_BUILD("Contractor & Design-Build", "Integrated Architecture & Full General Contracting", 20.0)
}
