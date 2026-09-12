package com.example.data.models

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AngiRed
import com.example.ui.theme.MintGreen
import com.example.ui.theme.SafetyGold
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TaskRabbitGreen
import com.example.ui.theme.ThumbtackBlue
import com.example.ui.theme.YelpRed

enum class CanvasViewMode {
    SPREADSHEET_GRID,
    PIPELINE_CARDS
}

data class CanvasLeadEntry(
    val id: String,
    val leadDate: String,
    val customerName: String,
    val serviceRequested: String,
    val city: String,
    val zipCode: String,
    val projectScope: String,
    val status: String, // "Quoted", "New", "Closed Won", "In Pipeline"
    val estBudget: String,
    val quoteAmount: String,
    val nextFollowUp: String,
    val notes: String,
    val sourceChannel: String, // "Thumbtack", "Angi", "Yelp", "TaskRabbit", "PPW", "SMS / Text", "Email Inquiries"
    val contactDetail: String = "",
    val isSpam: Boolean = false
) {
    val statusBadgeColor: Color
        get() = when (status.lowercase()) {
            "quoted" -> AccentBlue
            "new" -> StatusAmber
            "closed won", "won" -> StatusGreen
            "in pipeline", "in progress" -> AccentCyan
            else -> Color.Gray
        }

    val channelColor: Color
        get() = when {
            sourceChannel.contains("Thumbtack", ignoreCase = true) -> ThumbtackBlue
            sourceChannel.contains("TaskRabbit", ignoreCase = true) -> TaskRabbitGreen
            sourceChannel.contains("Angi", ignoreCase = true) -> AngiRed
            sourceChannel.contains("Yelp", ignoreCase = true) -> YelpRed
            sourceChannel.contains("PPW", ignoreCase = true) -> MintGreen
            sourceChannel.contains("SMS", ignoreCase = true) -> SafetyGold
            sourceChannel.contains("Email", ignoreCase = true) -> AccentCyan
            else -> AccentBlue
        }
}

object CanvasLeadDataSource {
    val initialLeads: List<CanvasLeadEntry> = listOf(
        CanvasLeadEntry(
            id = "THUMB-001",
            leadDate = "2026-09-10",
            customerName = "K. C.",
            serviceRequested = "Kitchen Remodel",
            city = "Tucson, AZ",
            zipCode = "85737",
            projectScope = "Full kitchen remodel: cabinets, countertops, sink",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$14,850.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $14,850 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 5-7 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-002",
            leadDate = "2026-09-08",
            customerName = "F. R.",
            serviceRequested = "Bathroom Remodel",
            city = "Green Valley, AZ",
            zipCode = "85614",
            projectScope = "Master bath remodel: sink, mirror, fixtures, lighting",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$7,400.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $7,400 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-003",
            leadDate = "2026-09-08",
            customerName = "T. U.",
            serviceRequested = "Bathroom Remodel",
            city = "Tucson, AZ",
            zipCode = "85750",
            projectScope = "Half bath: remove existing and install new sink",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$7,400.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $7,400 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-004",
            leadDate = "2026-09-06",
            customerName = "B. P.",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85711",
            projectScope = "Remove 12ft load-bearing wall, install support beam",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$4,850.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,850 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-005",
            leadDate = "2026-09-04",
            customerName = "A. B.",
            serviceRequested = "Kitchen Remodel",
            city = "Tucson, AZ",
            zipCode = "85746",
            projectScope = "Remove old cabinets, build & install new cabinets",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$14,850.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $14,850 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 5-7 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-006",
            leadDate = "2026-09-03",
            customerName = "M. H.",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85705",
            projectScope = "Roof tile, house siding & porch ceiling repair",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$3,400.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $3,400 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-007",
            leadDate = "2026-09-02",
            customerName = "A. S.",
            serviceRequested = "Tile Installation and Replacement",
            city = "Tucson, AZ",
            zipCode = "85704",
            projectScope = "Demo shower pan and install new tile",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$3,400.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $3,400 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-008",
            leadDate = "2026-09-01",
            customerName = "I. S.",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85705",
            projectScope = "Remove bedroom wall and install new door",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$4,850.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,850 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-009",
            leadDate = "2026-09-01",
            customerName = "N. M.",
            serviceRequested = "Tile Installation and Replacement",
            city = "Tucson, AZ",
            zipCode = "85711",
            projectScope = "30 sq ft kitchen backsplash on rough cement wall",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$14,850.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $14,850 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 5-7 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "THUMB-010",
            leadDate = "2026-09-01",
            customerName = "B. L.",
            serviceRequested = "Tile Installation and Replacement",
            city = "Marana, AZ",
            zipCode = "85658",
            projectScope = "Floor tile 401-500 sq ft in 3 bedrooms & closet",
            status = "Quoted",
            estBudget = "",
            quoteAmount = "$3,400.00",
            nextFollowUp = "2026-09-13",
            notes = "[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $3,400 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack"
        ),
        CanvasLeadEntry(
            id = "INBOUND-011",
            leadDate = "2026-09-12",
            customerName = "C. Torres",
            serviceRequested = "Bathroom Remodel",
            city = "Tucson, AZ",
            zipCode = "85743",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Email Inquiries [Source: Email Inquiries] [Contact: homeowner47@gmail.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Email Inquiries",
            contactDetail = "homeowner47@gmail.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-012",
            leadDate = "2026-09-12",
            customerName = "SEO Agency Bot",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85706",
            projectScope = "Guest bathroom walk-in shower remodel with subway tiles",
            status = "Quoted",
            estBudget = "$8,500.00",
            quoteAmount = "$7,650.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via SMS / Text [Source: SMS / Text] [Contact: (520) 555-5215] [Spam: true]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $7,650 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "SMS / Text",
            contactDetail = "(520) 555-5215",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-013",
            leadDate = "2026-09-12",
            customerName = "T. Henderson",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85722",
            projectScope = "Guest bathroom walk-in shower remodel with subway tiles",
            status = "Quoted",
            estBudget = "$8,500.00",
            quoteAmount = "$7,650.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Angi (Angie's List) [Source: Angi (Angie's List)] [Contact: lead-match@angi.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $7,650 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Angi",
            contactDetail = "lead-match@angi.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-014",
            leadDate = "2026-09-12",
            customerName = "L. Chen",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85730",
            projectScope = "Complete kitchen overhaul: quartz countertops and shaker cabinetry",
            status = "Quoted",
            estBudget = "$22,000.00",
            quoteAmount = "$19,800.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via TaskRabbit [Source: TaskRabbit] [Contact: app-user@taskrabbit.net]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $19,800 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 5-7 days. Projected profit margin: 21%.",
            sourceChannel = "TaskRabbit",
            contactDetail = "app-user@taskrabbit.net"
        ),
        CanvasLeadEntry(
            id = "INBOUND-015",
            leadDate = "2026-09-12",
            customerName = "Crypto Mining Deals",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85741",
            projectScope = "Master bathroom vanity replacement, plumbing rough-in and mirrors",
            status = "New",
            estBudget = "$7,300.00",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via SMS / Text [Source: SMS / Text] [Contact: (520) 555-7617] [Spam: true]",
            sourceChannel = "SMS / Text",
            contactDetail = "(520) 555-7617",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-016",
            leadDate = "2026-09-12",
            customerName = "K. Lopez",
            serviceRequested = "Tile Installation and Replacement",
            city = "Tucson, AZ",
            zipCode = "85744",
            projectScope = "Complete kitchen overhaul: quartz countertops and shaker cabinetry",
            status = "Quoted",
            estBudget = "$22,000.00",
            quoteAmount = "$19,800.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Yelp [Source: Yelp] [Contact: yelp-lead@biz.yelp.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $19,800 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 5-7 days. Projected profit margin: 21%.",
            sourceChannel = "Yelp",
            contactDetail = "yelp-lead@biz.yelp.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-017",
            leadDate = "2026-09-12",
            customerName = "G. Patel",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85735",
            projectScope = "Urgent loan approved click here for crypto bitcoin investment telegram",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Thumbtack (mlmshuteye31@gmail.com) for recipient mlmshuteye31@gmail.com [Source: Thumbtack (mlmshuteye31@gmail.com)] [Contact: mlmshuteye31@gmail.com] [Spam: true]",
            sourceChannel = "Thumbtack",
            contactDetail = "mlmshuteye31@gmail.com",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-018",
            leadDate = "2026-09-12",
            customerName = "K. Lopez",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85715",
            projectScope = "We guarantee 1st page Google rank high and cheap backlinks seo",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via PPW (Pro Referral) [Source: PPW (Pro Referral)] [Contact: pro-inquiry@ppw-network.org] [Spam: true]",
            sourceChannel = "PPW",
            contactDetail = "pro-inquiry@ppw-network.org",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-019",
            leadDate = "2026-09-12",
            customerName = "D. K.",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85703",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Yelp [Source: Yelp] [Contact: yelp-lead@biz.yelp.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Yelp",
            contactDetail = "yelp-lead@biz.yelp.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-020",
            leadDate = "2026-09-12",
            customerName = "P. Adams",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85731",
            projectScope = "We guarantee 1st page Google rank high and cheap backlinks seo",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via SMS / Text [Source: SMS / Text] [Contact: (520) 555-1359] [Spam: true]",
            sourceChannel = "SMS / Text",
            contactDetail = "(520) 555-1359",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-021",
            leadDate = "2026-09-12",
            customerName = "Crypto Mining Deals",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85710",
            projectScope = "Demo existing floor tile in living room & hallway (~450 sq ft)",
            status = "New",
            estBudget = "$6,200.00",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Angi (Angie's List) [Source: Angi (Angie's List)] [Contact: lead-match@angi.com] [Spam: true]",
            sourceChannel = "Angi",
            contactDetail = "lead-match@angi.com",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-022",
            leadDate = "2026-09-12",
            customerName = "P. Adams",
            serviceRequested = "Kitchen Remodel",
            city = "Tucson, AZ",
            zipCode = "85749",
            projectScope = "Backsplash tile installation behind stove and under cabinets",
            status = "Quoted",
            estBudget = "$1,800.00",
            quoteAmount = "$1,600.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via PPW (Pro Referral) [Source: PPW (Pro Referral)] [Contact: pro-inquiry@ppw-network.org]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $1,600 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "PPW",
            contactDetail = "pro-inquiry@ppw-network.org"
        ),
        CanvasLeadEntry(
            id = "INBOUND-023",
            leadDate = "2026-09-12",
            customerName = "E. Jenkins",
            serviceRequested = "Kitchen Remodel",
            city = "Tucson, AZ",
            zipCode = "85703",
            projectScope = "Demo existing floor tile in living room & hallway (~450 sq ft)",
            status = "Quoted",
            estBudget = "$6,200.00",
            quoteAmount = "$5,600.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Thumbtack (mlmshuteye31@gmail.com) for recipient mlmshuteye31@gmail.com [Source: Thumbtack (mlmshuteye31@gmail.com)] [Contact: mlmshuteye31@gmail.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $5,600 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack",
            contactDetail = "mlmshuteye31@gmail.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-024",
            leadDate = "2026-09-12",
            customerName = "G. Patel",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85749",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Thumbtack (mlmshuteye31@gmail.com) for recipient mlmshuteye31@gmail.com [Source: Thumbtack (mlmshuteye31@gmail.com)] [Contact: mlmshuteye31@gmail.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack",
            contactDetail = "mlmshuteye31@gmail.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-025",
            leadDate = "2026-09-12",
            customerName = "D. K.",
            serviceRequested = "Bathroom Remodel",
            city = "Tucson, AZ",
            zipCode = "85724",
            projectScope = "Demo existing floor tile in living room & hallway (~450 sq ft)",
            status = "Quoted",
            estBudget = "$6,200.00",
            quoteAmount = "$5,600.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via PPW (Pro Referral) [Source: PPW (Pro Referral)] [Contact: pro-inquiry@ppw-network.org]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $5,600 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "PPW",
            contactDetail = "pro-inquiry@ppw-network.org"
        ),
        CanvasLeadEntry(
            id = "INBOUND-026",
            leadDate = "2026-09-12",
            customerName = "S. Miller",
            serviceRequested = "Kitchen Remodel",
            city = "Tucson, AZ",
            zipCode = "85717",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Yelp [Source: Yelp] [Contact: yelp-lead@biz.yelp.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Yelp",
            contactDetail = "yelp-lead@biz.yelp.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-027",
            leadDate = "2026-09-12",
            customerName = "G. Patel",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85748",
            projectScope = "Demo existing floor tile in living room & hallway (~450 sq ft)",
            status = "Quoted",
            estBudget = "$6,200.00",
            quoteAmount = "$5,600.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via SMS / Text [Source: SMS / Text] [Contact: (520) 555-2920]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $5,600 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "SMS / Text",
            contactDetail = "(520) 555-2920"
        ),
        CanvasLeadEntry(
            id = "INBOUND-028",
            leadDate = "2026-09-12",
            customerName = "C. Torres",
            serviceRequested = "Kitchen Remodel",
            city = "Tucson, AZ",
            zipCode = "85747",
            projectScope = "Guest bathroom walk-in shower remodel with subway tiles",
            status = "Quoted",
            estBudget = "$8,500.00",
            quoteAmount = "$7,650.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Thumbtack (mlmshuteye31@gmail.com) for recipient mlmshuteye31@gmail.com [Source: Thumbtack (mlmshuteye31@gmail.com)] [Contact: mlmshuteye31@gmail.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $7,650 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack",
            contactDetail = "mlmshuteye31@gmail.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-029",
            leadDate = "2026-09-12",
            customerName = "T. Henderson",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85733",
            projectScope = "We guarantee 1st page Google rank high and cheap backlinks seo",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via SMS / Text [Source: SMS / Text] [Contact: (520) 555-2156] [Spam: true]",
            sourceChannel = "SMS / Text",
            contactDetail = "(520) 555-2156",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-030",
            leadDate = "2026-09-12",
            customerName = "G. Patel",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85749",
            projectScope = "Master bathroom vanity replacement, plumbing rough-in and mirrors",
            status = "Quoted",
            estBudget = "$7,300.00",
            quoteAmount = "$6,550.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Thumbtack (mlmshuteye31@gmail.com) for recipient mlmshuteye31@gmail.com [Source: Thumbtack (mlmshuteye31@gmail.com)] [Contact: mlmshuteye31@gmail.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $6,550 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Thumbtack",
            contactDetail = "mlmshuteye31@gmail.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-031",
            leadDate = "2026-09-12",
            customerName = "T. Henderson",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85746",
            projectScope = "We guarantee 1st page Google rank high and cheap backlinks seo",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Yelp [Source: Yelp] [Contact: yelp-lead@biz.yelp.com] [Spam: true]",
            sourceChannel = "Yelp",
            contactDetail = "yelp-lead@biz.yelp.com",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-032",
            leadDate = "2026-09-12",
            customerName = "D. K.",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85747",
            projectScope = "Master bathroom vanity replacement, plumbing rough-in and mirrors",
            status = "Quoted",
            estBudget = "$7,300.00",
            quoteAmount = "$6,550.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via TaskRabbit [Source: TaskRabbit] [Contact: app-user@taskrabbit.net]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $6,550 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "TaskRabbit",
            contactDetail = "app-user@taskrabbit.net"
        ),
        CanvasLeadEntry(
            id = "INBOUND-033",
            leadDate = "2026-09-12",
            customerName = "D. K.",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85720",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Angi (Angie's List) [Source: Angi (Angie's List)] [Contact: lead-match@angi.com]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "Angi",
            contactDetail = "lead-match@angi.com"
        ),
        CanvasLeadEntry(
            id = "INBOUND-034",
            leadDate = "2026-09-12",
            customerName = "L. Chen",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85727",
            projectScope = "We guarantee 1st page Google rank high and cheap backlinks seo",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Angi (Angie's List) [Source: Angi (Angie's List)] [Contact: lead-match@angi.com] [Spam: true]",
            sourceChannel = "Angi",
            contactDetail = "lead-match@angi.com",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-035",
            leadDate = "2026-09-12",
            customerName = "K. Lopez",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85706",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via TaskRabbit [Source: TaskRabbit] [Contact: app-user@taskrabbit.net]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "TaskRabbit",
            contactDetail = "app-user@taskrabbit.net"
        ),
        CanvasLeadEntry(
            id = "INBOUND-036",
            leadDate = "2026-09-12",
            customerName = "R. Vance",
            serviceRequested = "Home Remodeling",
            city = "Tucson, AZ",
            zipCode = "85738",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via TaskRabbit [Source: TaskRabbit] [Contact: app-user@taskrabbit.net]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "TaskRabbit",
            contactDetail = "app-user@taskrabbit.net"
        ),
        CanvasLeadEntry(
            id = "INBOUND-037",
            leadDate = "2026-09-12",
            customerName = "SEO Agency Bot",
            serviceRequested = "Construction Services",
            city = "Tucson, AZ",
            zipCode = "85713",
            projectScope = "Guest bathroom walk-in shower remodel with subway tiles",
            status = "New",
            estBudget = "$8,500.00",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via TaskRabbit [Source: TaskRabbit] [Contact: app-user@taskrabbit.net] [Spam: true]",
            sourceChannel = "TaskRabbit",
            contactDetail = "app-user@taskrabbit.net",
            isSpam = true
        ),
        CanvasLeadEntry(
            id = "INBOUND-038",
            leadDate = "2026-09-12",
            customerName = "D. K.",
            serviceRequested = "Bathroom Remodel",
            city = "Tucson, AZ",
            zipCode = "85743",
            projectScope = "Remove interior wall between dining room and kitchen",
            status = "Quoted",
            estBudget = "$4,800.00",
            quoteAmount = "$4,300.00",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via TaskRabbit [Source: TaskRabbit] [Contact: app-user@taskrabbit.net]\n[Gemini 1.5 Pro Agent Autonomous Bid]: Proposed $4,300 (Price-to-Win Aggressive). Includes all turnkey materials, labor, debris haul-off, C520X 10-Yr Guarantee, and licensed dispatch guarantee. Start date availability: Next Monday. Est completion: 2-4 days. Projected profit margin: 21%.",
            sourceChannel = "TaskRabbit",
            contactDetail = "app-user@taskrabbit.net"
        ),
        CanvasLeadEntry(
            id = "INBOUND-039",
            leadDate = "2026-09-12",
            customerName = "P. Adams",
            serviceRequested = "General Inquiries",
            city = "Tucson, AZ",
            zipCode = "85728",
            projectScope = "We guarantee 1st page Google rank high and cheap backlinks seo",
            status = "New",
            estBudget = "",
            quoteAmount = "",
            nextFollowUp = "2026-09-13",
            notes = "Inbound webhook captured via Email Inquiries [Source: Email Inquiries] [Contact: homeowner36@gmail.com] [Spam: true]",
            sourceChannel = "Email Inquiries",
            contactDetail = "homeowner36@gmail.com",
            isSpam = true
        )
    )

    fun exportToCsv(leads: List<CanvasLeadEntry>): String {
        val sb = StringBuilder()
        sb.append(",Thumbtack Lead Log & Pipeline,,,,,,,,,,\n")
        sb.append(",\"Real-time tracker for incoming customer inquiries, job estimates, and pipeline status\",,,,,,,,,,\n")
        val total = leads.size
        val newCount = leads.count { it.status.equals("New", ignoreCase = true) }
        val pipelineCount = leads.count { it.status.equals("Quoted", ignoreCase = true) || it.status.equals("In Pipeline", ignoreCase = true) }
        val wonCount = leads.count { it.status.equals("Closed Won", ignoreCase = true) || it.status.equals("Won", ignoreCase = true) }
        sb.append(",TOTAL LEADS,,NEW LEADS,,IN PIPELINE,,WON JOBS,,,,\n")
        sb.append(",📋 Inquiries,$total,🆕 Needs Action,$newCount,💬 In Progress,$pipelineCount,⭐ Closed Won,$wonCount,,,\n")
        sb.append(",All recorded leads,,Awaiting first response,,Quoted or in discussion,,Successfully won jobs,,,,\n")
        sb.append(",,,,,,,,,,,\n")
        sb.append(",Lead Date,Customer Name,Service Requested,City,Zip Code,Project Scope,Status,Est. Budget,Quote Amount,Next Follow-Up,Notes\n")
        leads.forEach { lead ->
            val scopeEsc = if (lead.projectScope.contains(",") || lead.projectScope.contains("\"")) "\"${lead.projectScope.replace("\"", "\"\"")}\"" else lead.projectScope
            val notesEsc = if (lead.notes.contains(",") || lead.notes.contains("\n") || lead.notes.contains("\"")) "\"${lead.notes.replace("\"", "\"\"")}\"" else lead.notes
            val cityEsc = "\"${lead.city}\""
            sb.append(",${lead.leadDate},${lead.customerName},${lead.serviceRequested},$cityEsc,${lead.zipCode},$scopeEsc,${lead.status},\"${lead.estBudget}\",\"${lead.quoteAmount}\",${lead.nextFollowUp},$notesEsc\n")
        }
        return sb.toString()
    }
}
