package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppRepository(private val database: AppDatabase) {
    val jobs: Flow<List<JobEntity>> = database.jobDao().getAllJobs()
    val timeLogs: Flow<List<TimeLogEntity>> = database.timeLogDao().getAllTimeLogs()
    val leads: Flow<List<LeadEntity>> = database.leadDao().getAllLeads()
    val messages: Flow<List<ChatMessageEntity>> = database.chatDao().getAllMessages()
    val payments: Flow<List<PaymentEntity>> = database.paymentDao().getAllPayments()
    val reviews: Flow<List<ReviewEntity>> = database.reviewDao().getAllReviews()
    val expenses: Flow<List<ExpenseEntity>> = database.expenseDao().getAllExpenses()
    val roomScans: Flow<List<RoomScanEntity>> = database.roomScanDao().getAllRoomScans()
    val projectTasks: Flow<List<ProjectTaskEntity>> = database.projectTaskDao().getAllTasks()
    val customerBookings: Flow<List<CustomerBookingEntity>> = database.customerBookingDao().getAllBookings()
    val marketplaceBids: Flow<List<MarketplaceBidEntity>> = database.marketplaceBidDao().getAllBids()
    val freightLoads: Flow<List<FreightLoadEntity>> = database.freightLoadDao().getAllLoads()
    val projectMilestones: Flow<List<ProjectMilestoneEntity>> = database.projectMilestoneDao().getAllMilestones()

    suspend fun seedInitialDataIfEmpty() {
        val existingJobs = database.jobDao().getAllJobs().first()
        if (existingJobs.isNotEmpty()) return

        // 24 jobs exactly mirroring the C520X dispatch breakdown
        val initialJobs = listOf(
            // 4 Completed (Total Billed $7,020)
            JobEntity(
                title = "Main Electrical Service Panel Upgrade",
                trade = "Electrical",
                clientName = "Vanguard Logistics Hub",
                clientPhone = "(520) 488-9120",
                address = "4200 E Valencia Rd, Tucson, AZ",
                status = "Completed",
                billedAmount = 2850.0,
                pipelineValue = 2850.0,
                scheduledDate = "Completed Yesterday",
                assignedTech = "Alex Ramirez (Lead Tech)",
                priority = "High",
                notes = "Passed city inspection with zero notes. Dual meter base installed.",
                sortOrder = 0
            ),
            JobEntity(
                title = "Commercial Hydro Jetting & Line Clearing",
                trade = "Plumbing",
                clientName = "Catalina Vista Dining",
                clientPhone = "(520) 319-4402",
                address = "1840 N Stone Ave, Tucson, AZ",
                status = "Completed",
                billedAmount = 1820.0,
                pipelineValue = 1820.0,
                scheduledDate = "Completed Sep 05",
                assignedTech = "Carlos Mendez (Master Plumber)",
                priority = "High",
                notes = "Grease trap clean-out and 4-inch main sewer stack clear.",
                sortOrder = 1
            ),
            JobEntity(
                title = "Dual Split AC Inverter Diagnostics & Recharge",
                trade = "HVAC",
                clientName = "Saguaro Tech Campus",
                clientPhone = "(520) 795-8831",
                address = "901 W Auto Mall Dr, Tucson, AZ",
                status = "Completed",
                billedAmount = 950.0,
                pipelineValue = 950.0,
                scheduledDate = "Completed Sep 04",
                assignedTech = "Marcus Vance (HVAC Specialist)",
                priority = "Medium",
                notes = "Replaced TXV valve and recharged 6.4 lbs R410A refrigerant.",
                sortOrder = 2
            ),
            JobEntity(
                title = "Structural Subfloor Repair & Framing",
                trade = "Carpentry",
                clientName = "Desert Crest Residences",
                clientPhone = "(520) 624-7711",
                address = "3150 N Swan Rd, Tucson, AZ",
                status = "Completed",
                billedAmount = 1400.0,
                pipelineValue = 1400.0,
                scheduledDate = "Completed Sep 02",
                assignedTech = "David Chen (Carpentry Pro)",
                priority = "Normal",
                notes = "Reinforced 2x10 joists and moisture barrier subfloor sheeting.",
                sortOrder = 3
            ),

            // 3 In Progress ($5,900 Billed)
            JobEntity(
                title = "Multi-Circuit High-Bay LED Retrofit",
                trade = "Electrical",
                clientName = "Canyon Ridge Distribution",
                clientPhone = "(520) 571-3390",
                address = "6700 S Tucson Blvd, Tucson, AZ",
                status = "In Progress",
                billedAmount = 3100.0,
                pipelineValue = 4200.0,
                scheduledDate = "Today - Active",
                assignedTech = "Alex Ramirez (Lead Tech)",
                priority = "High",
                notes = "30 high-bay fixtures converted to 150W smart sensor LEDs.",
                sortOrder = 4
            ),
            JobEntity(
                title = "Commercial Kitchen Backflow Preventer Test & Install",
                trade = "Plumbing",
                clientName = "Ocotillo Gastro Pub",
                clientPhone = "(520) 882-9901",
                address = "245 E Congress St, Tucson, AZ",
                status = "In Progress",
                billedAmount = 1900.0,
                pipelineValue = 2100.0,
                scheduledDate = "Today - Active",
                assignedTech = "Carlos Mendez (Master Plumber)",
                priority = "High",
                notes = "RPZ assembly pressure tested and certified.",
                sortOrder = 5
            ),
            JobEntity(
                title = "Emergency R-32 Heat Pump Coil Replacement",
                trade = "HVAC",
                clientName = "Mountain View Dental Suites",
                clientPhone = "(520) 327-1400",
                address = "5200 E Grant Rd, Tucson, AZ",
                status = "In Progress",
                billedAmount = 900.0,
                pipelineValue = 1830.0,
                scheduledDate = "Today - Active",
                assignedTech = "Marcus Vance (HVAC Specialist)",
                priority = "High",
                notes = "Evaporator coil extracted, brazing suction lines now.",
                sortOrder = 6
            ),

            // 3 Dispatched ($3,070 Billed)
            JobEntity(
                title = "Custom Hardwood Office Millwork & Trim",
                trade = "Carpentry",
                clientName = "Apex Executive Park",
                clientPhone = "(520) 887-5520",
                address = "1200 N El Dorado Pl, Tucson, AZ",
                status = "Dispatched",
                billedAmount = 1250.0,
                pipelineValue = 2150.0,
                scheduledDate = "En Route - ETA 15m",
                assignedTech = "David Chen (Carpentry Pro)",
                priority = "Medium",
                notes = "Oak wainscoting and hidden storage cabinets.",
                sortOrder = 7
            ),
            JobEntity(
                title = "Exterior Facade Protective Elastomeric Coating",
                trade = "Painting",
                clientName = "Kino Plaza Commercial",
                clientPhone = "(520) 746-8800",
                address = "2805 E Ajo Way, Tucson, AZ",
                status = "Dispatched",
                billedAmount = 1120.0,
                pipelineValue = 1800.0,
                scheduledDate = "En Route - ETA 25m",
                assignedTech = "Ramon Cruz (Coatings Tech)",
                priority = "Normal",
                notes = "Pressure washing stage complete; primer coat prep.",
                sortOrder = 8
            ),
            JobEntity(
                title = "Facility Hardware Safety Audit & Mounting",
                trade = "Handyman",
                clientName = "Rincon Medical Care",
                clientPhone = "(520) 298-2300",
                address = "10350 E Golf Links Rd, Tucson, AZ",
                status = "Dispatched",
                billedAmount = 700.0,
                pipelineValue = 950.0,
                scheduledDate = "En Route - ETA 10m",
                assignedTech = "Tyler Brooks (Maintenance Tech)",
                priority = "Normal",
                notes = "ADA grab bars and seismic cabinet anchors.",
                sortOrder = 9
            ),

            // 6 Scheduled ($10,420 Billed)
            JobEntity(
                title = "Industrial 480V Subpanel & Conduit Run",
                trade = "Electrical",
                clientName = "Sonoran Metals Fab",
                clientPhone = "(520) 628-1199",
                address = "3820 S Palo Verde Rd, Tucson, AZ",
                status = "Scheduled",
                billedAmount = 3000.0,
                pipelineValue = 4100.0,
                scheduledDate = "Tomorrow 8:00 AM",
                assignedTech = "Alex Ramirez (Lead Tech)",
                priority = "High",
                notes = "Rigid conduit installed overhead; pulling 2/0 THHN wire.",
                sortOrder = 10
            ),
            JobEntity(
                title = "Commercial Boiler Re-circulation Loop",
                trade = "Plumbing",
                clientName = "Tucson Foothills Hotel",
                clientPhone = "(520) 299-1000",
                address = "7000 N Resort Dr, Tucson, AZ",
                status = "Scheduled",
                billedAmount = 1900.0,
                pipelineValue = 2000.0,
                scheduledDate = "Tomorrow 10:30 AM",
                assignedTech = "Carlos Mendez (Master Plumber)",
                priority = "High",
                notes = "Grundfos pump swap and balancing valves installation.",
                sortOrder = 11
            ),
            JobEntity(
                title = "Interior Warehouse Acoustic Ceiling Painting",
                trade = "Painting",
                clientName = "Express Cargo Center",
                clientPhone = "(520) 573-0022",
                address = "7300 S Kolb Rd, Tucson, AZ",
                status = "Scheduled",
                billedAmount = 2230.0,
                pipelineValue = 3450.0,
                scheduledDate = "Sep 10 7:00 AM",
                assignedTech = "Ramon Cruz (Coatings Tech)",
                priority = "Medium",
                notes = "Dryfall matte black coating across 12,000 sq ft.",
                sortOrder = 12
            ),
            JobEntity(
                title = "Heavy Commercial Door Jam & Panic Hardware",
                trade = "Carpentry",
                clientName = "Gateway Tech Park",
                clientPhone = "(520) 434-5100",
                address = "9000 S Rita Rd, Tucson, AZ",
                status = "Scheduled",
                billedAmount = 1200.0,
                pipelineValue = 2000.0,
                scheduledDate = "Sep 10 1:00 PM",
                assignedTech = "David Chen (Carpentry Pro)",
                priority = "Normal",
                notes = "Von Duprin panic bar crash bars installed on fire exits.",
                sortOrder = 13
            ),
            JobEntity(
                title = "Commercial Parapet Flashing & Patch Sealing",
                trade = "Roofing",
                clientName = "Silverlake Business Park",
                clientPhone = "(520) 792-3400",
                address = "1400 W Silverlake Rd, Tucson, AZ",
                status = "Scheduled",
                billedAmount = 1600.0,
                pipelineValue = 4000.0,
                scheduledDate = "Sep 11 8:00 AM",
                assignedTech = "Santiago Ortiz (Roofing Lead)",
                priority = "High",
                notes = "TPO membrane seam heat-weld and drip edge sealing.",
                sortOrder = 14
            ),
            JobEntity(
                title = "Tenant Finish Handyman Turnover Worklist",
                trade = "Handyman",
                clientName = "Broadway Tower Office 4B",
                clientPhone = "(520) 323-9000",
                address = "333 E Broadway Blvd, Tucson, AZ",
                status = "Scheduled",
                billedAmount = 470.0,
                pipelineValue = 770.0,
                scheduledDate = "Sep 11 2:30 PM",
                assignedTech = "Tyler Brooks (Maintenance Tech)",
                priority = "Normal",
                notes = "Drywall patch, baseboard caulk, and smart lock programming.",
                sortOrder = 15
            ),

            // 8 Pending Estimate ($0 Billed, contributing to pipeline value)
            JobEntity(
                title = "Full Rooftop Solar Disconnect & Service Wire",
                trade = "Electrical",
                clientName = "Tanque Verde Solar LLC",
                clientPhone = "(520) 749-3011",
                address = "8900 E Tanque Verde Rd, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 2850.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "High",
                notes = "Rapid shutdown switch and battery storage subfeed conduit.",
                sortOrder = 16
            ),
            JobEntity(
                title = "Underground Water Main Trenching & Poly Line",
                trade = "Plumbing",
                clientName = "Sabino Canyon Ranch",
                clientPhone = "(520) 296-6100",
                address = "5900 N Sabino Canyon Rd, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 3800.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "Medium",
                notes = "180ft trench with 1.5 inch CTS poly line.",
                sortOrder = 17
            ),
            JobEntity(
                title = "Multi-Zone Variable Flow HVAC System Spec",
                trade = "HVAC",
                clientName = "Ironwood Surgery Center",
                clientPhone = "(520) 544-2400",
                address = "6320 N La Cholla Blvd, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 4900.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "High",
                notes = "Hospital cleanroom HEPA positive pressure airflow balance.",
                sortOrder = 18
            ),
            JobEntity(
                title = "Conference Room Sound Dampening Wood Baffles",
                trade = "Carpentry",
                clientName = "Venture Labs Tucson",
                clientPhone = "(520) 621-0000",
                address = "1230 E Speedway Blvd, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 2400.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "Normal",
                notes = "Architectural acoustic slats with walnut veneer.",
                sortOrder = 19
            ),
            JobEntity(
                title = "Epoxy Floor Resin Coating for Machine Shop",
                trade = "Painting",
                clientName = "Precision Machining Corp",
                clientPhone = "(520) 748-1800",
                address = "4500 S Contractors Way, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 3500.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "Medium",
                notes = "100% solids epoxy with non-slip aluminum oxide aggregate.",
                sortOrder = 20
            ),
            JobEntity(
                title = "Facilities Modular Furniture Assembly (18 Pods)",
                trade = "Handyman",
                clientName = "BioTech Desert Incubator",
                clientPhone = "(520) 884-7000",
                address = "1601 S Pantano Rd, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 1450.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "Normal",
                notes = "Herman Miller sit-stand power troughs and cable raceways.",
                sortOrder = 21
            ),
            JobEntity(
                title = "Built-Up Asphalt Flat Roof Restoration",
                trade = "Roofing",
                clientName = "Midtown Storage Central",
                clientPhone = "(520) 326-8800",
                address = "2200 N 1st Ave, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 5200.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "High",
                notes = "Silicone elastomeric coating over 3-ply BUR roofing.",
                sortOrder = 22
            ),
            JobEntity(
                title = "Commercial Security Floodlight Electrical Runs",
                trade = "Electrical",
                clientName = "Southwest Freight Terminal",
                clientPhone = "(520) 574-9000",
                address = "8500 S Rita Rd, Tucson, AZ",
                status = "Pending Estimate",
                billedAmount = 0.0,
                pipelineValue = 1950.0,
                scheduledDate = "Needs Quote",
                assignedTech = "Unassigned",
                priority = "Medium",
                notes = "Pole mount photocell LED floods around perimeter fence.",
                sortOrder = 23
            )
        )
        database.jobDao().insertJobs(initialJobs)

        // Initial Leads from Thumbtack Pro & TaskRabbit
        val initialLeads = listOf(
            LeadEntity(
                clientName = "Helena Brooks",
                clientPhone = "(520) 405-8911",
                serviceTrade = "Electrical",
                projectDescription = "EV Charger NEMA 14-50 50A circuit install in two-car garage. Need quote fast.",
                budget = 1200.0,
                source = "Thumbtack Pro",
                status = "New Lead",
                urgency = "Immediate"
            ),
            LeadEntity(
                clientName = "Derrick Vance",
                clientPhone = "(520) 668-3329",
                serviceTrade = "Plumbing",
                projectDescription = "Tankless water heater conversion from 50gal tank with gas line check.",
                budget = 3400.0,
                source = "TaskRabbit",
                status = "Contacted",
                urgency = "This Week"
            ),
            LeadEntity(
                clientName = "Sandra Miller",
                clientPhone = "(520) 907-1144",
                serviceTrade = "HVAC",
                projectDescription = "Condenser vibrating loudly and freezing up lines. Needs inspection today.",
                budget = 950.0,
                source = "Thumbtack Pro",
                status = "Quoted",
                urgency = "Immediate"
            ),
            LeadEntity(
                clientName = "Greg Thorne",
                clientPhone = "(520) 529-7600",
                serviceTrade = "Roofing",
                projectDescription = "Monsoon tile blow-off repair and underlayment felt inspection.",
                budget = 2100.0,
                source = "Direct Call",
                status = "Booked",
                urgency = "Flexible"
            )
        )
        database.leadDao().insertLeads(initialLeads)

        // Initial Payments ($7,020 collected settled payments)
        val initialPayments = listOf(
            PaymentEntity(
                invoiceNumber = "INV-520-1044",
                clientName = "Vanguard Logistics Hub",
                trade = "Electrical",
                amount = 2850.0,
                status = "Paid",
                dateFormatted = "Yesterday, 4:15 PM",
                paymentMethod = "Stripe Gateway",
                transactionRef = "ch_3Nxp28Lkd009aX"
            ),
            PaymentEntity(
                invoiceNumber = "INV-520-1043",
                clientName = "Catalina Vista Dining",
                trade = "Plumbing",
                amount = 1820.0,
                status = "Paid",
                dateFormatted = "Sep 05, 11:30 AM",
                paymentMethod = "Direct ACH",
                transactionRef = "ach_982402B89"
            ),
            PaymentEntity(
                invoiceNumber = "INV-520-1042",
                clientName = "Desert Crest Residences",
                trade = "Carpentry",
                amount = 1400.0,
                status = "Paid",
                dateFormatted = "Sep 02, 2:45 PM",
                paymentMethod = "Square Reader",
                transactionRef = "sq_tx_554199"
            ),
            PaymentEntity(
                invoiceNumber = "INV-520-1041",
                clientName = "Saguaro Tech Campus",
                trade = "HVAC",
                amount = 950.0,
                status = "Paid",
                dateFormatted = "Sep 04, 5:00 PM",
                paymentMethod = "Apple Pay / Stripe",
                transactionRef = "ch_3Ny88Lq99201"
            ),
            PaymentEntity(
                invoiceNumber = "INV-520-1045",
                clientName = "Canyon Ridge Distribution",
                trade = "Electrical",
                amount = 3100.0,
                status = "Pending",
                dateFormatted = "Due in 3 days",
                paymentMethod = "Commercial Net-30",
                transactionRef = "pending_invoice"
            ),
            PaymentEntity(
                invoiceNumber = "INV-520-1046",
                clientName = "Ocotillo Gastro Pub",
                trade = "Plumbing",
                amount = 1900.0,
                status = "Processing",
                dateFormatted = "Initiated Today",
                paymentMethod = "Stripe ACH Transfer",
                transactionRef = "ach_in_flight"
            )
        )
        database.paymentDao().insertPayments(initialPayments)

        // Initial Reviews (Yelp-style 5-star feedback)
        val initialReviews = listOf(
            ReviewEntity(
                clientName = "Brandon S.",
                trade = "Electrical",
                techName = "Alex Ramirez",
                rating = 5.0f,
                reviewText = "Alex upgraded our warehouse 480V service in record time. Zero downtime on our shipping bays. Highly recommend C520X!",
                date = "2 days ago",
                verifiedCustomer = true,
                companyResponse = "Thank you Brandon! Alex is one of our top master electricians. Glad we kept your shipping lines rolling."
            ),
            ReviewEntity(
                clientName = "Elena Martinez",
                trade = "Plumbing",
                techName = "Carlos Mendez",
                rating = 5.0f,
                reviewText = "Carlos saved our restaurant from closing during dinner rush. Cleared grease stack and inspected backflow with video cam.",
                date = "4 days ago",
                verifiedCustomer = true,
                companyResponse = "Always happy to support our local Tucson restaurants! We keep dispatch on call 24/7."
            ),
            ReviewEntity(
                clientName = "David K.",
                trade = "HVAC",
                techName = "Marcus Vance",
                rating = 4.8f,
                reviewText = "Fast response in 105° heat. Replaced TXV valve and got cooling restored in under two hours. Very transparent pricing.",
                date = "Last week",
                verifiedCustomer = true,
                companyResponse = "Our team knows summer cooling emergencies can't wait. Thank you for trusting C520X!"
            )
        )
        database.reviewDao().insertReviews(initialReviews)

        // Initial Chat Messages
        val initialMessages = listOf(
            ChatMessageEntity(
                senderRole = "Dispatch",
                senderName = "C520X Dispatch",
                message = "Welcome to C520X Dispatch Command. Lead Techs are synced for live tracking.",
                timestamp = System.currentTimeMillis() - 3600000 * 4,
                isRead = true
            ),
            ChatMessageEntity(
                senderRole = "Client",
                senderName = "Helena Brooks (Thumbtack)",
                message = "Hi! Can someone confirm if you can install a 50A EV charger by this Thursday afternoon?",
                timestamp = System.currentTimeMillis() - 3600000 * 2,
                isRead = true
            ),
            ChatMessageEntity(
                senderRole = "Tech",
                senderName = "Alex Ramirez (Lead Tech)",
                message = "Yes Helena! I reviewed your panel photos. We have the 50A breaker and 6 AWG wire in truck inventory. Estimate ready.",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true,
                attachmentType = "invoice_quote",
                attachmentName = "C520X_Estimate_EV_Charger.pdf"
            )
        )
        database.chatDao().insertMessages(initialMessages)

        // Initial Time Logs
        val initialTimeLogs = listOf(
            TimeLogEntity(
                jobId = 1,
                jobTitle = "Main Electrical Service Panel Upgrade",
                trade = "Electrical",
                techName = "Alex Ramirez",
                startTimeFormatted = "Yesterday 08:00 AM - 01:30 PM",
                durationSeconds = 19800,
                hourlyRate = 125.0,
                billableAmount = 687.50,
                notes = "Completed dual meter base install, grounded to copper cold water & rebar."
            ),
            TimeLogEntity(
                jobId = 2,
                jobTitle = "Commercial Hydro Jetting & Line Clearing",
                trade = "Plumbing",
                techName = "Carlos Mendez",
                startTimeFormatted = "Sep 05 09:15 AM - 01:45 PM",
                durationSeconds = 16200,
                hourlyRate = 135.0,
                billableAmount = 607.50,
                notes = "Ran 3000 PSI nozzle 120ft through kitchen drain branch."
            )
        )
        initialTimeLogs.forEach { database.timeLogDao().insertTimeLog(it) }

        // Initial Expenses (for QuickBooks Online Sync & Job Costing)
        val initialExpenses = listOf(
            ExpenseEntity(
                jobId = 1,
                jobTitle = "Main Electrical Service Panel Upgrade",
                vendor = "The Home Depot Pro Desk",
                category = "Job Materials",
                amount = 890.00,
                dateFormatted = "Yesterday",
                receiptName = "HD_Pro_Receipt_7749.pdf",
                billableToClient = true,
                qboSynced = true,
                qboTxnId = "QBO-EXP-88401",
                qboSyncTimestamp = System.currentTimeMillis() - 86400000
            ),
            ExpenseEntity(
                jobId = 2,
                jobTitle = "Commercial Hydro Jetting & Line Clearing",
                vendor = "Ferguson Plumbing Supply",
                category = "Job Materials",
                amount = 345.50,
                dateFormatted = "Sep 05",
                receiptName = "Ferguson_Inv_0029.pdf",
                billableToClient = true,
                qboSynced = true,
                qboTxnId = "QBO-EXP-88402",
                qboSyncTimestamp = System.currentTimeMillis() - 86400000 * 2
            ),
            ExpenseEntity(
                jobId = 4,
                jobTitle = "Structural Subfloor Repair & Framing",
                vendor = "Lumber City Building Materials",
                category = "Job Materials",
                amount = 1420.00,
                dateFormatted = "Today 07:45 AM",
                receiptName = "LumberCity_Subfloor_Timber.jpg",
                billableToClient = true,
                qboSynced = false
            ),
            ExpenseEntity(
                jobId = 4,
                jobTitle = "Structural Subfloor Repair & Framing",
                vendor = "Sunbelt Rentals",
                category = "Equipment Rental",
                amount = 450.00,
                dateFormatted = "Today 08:30 AM",
                receiptName = "Sunbelt_Floor_Jack_Rental.pdf",
                billableToClient = true,
                qboSynced = false
            ),
            ExpenseEntity(
                jobId = 5,
                jobTitle = "Gourmet Kitchen Remodel",
                vendor = "City Development Services Dept",
                category = "Permits & Fees",
                amount = 650.00,
                dateFormatted = "Yesterday",
                receiptName = "City_Permit_Receipt_5521.pdf",
                billableToClient = true,
                qboSynced = false
            ),
            ExpenseEntity(
                jobId = 5,
                jobTitle = "Gourmet Kitchen Remodel",
                vendor = "Arizona Tile & Stone Center",
                category = "Job Materials",
                amount = 2850.00,
                dateFormatted = "Sep 06",
                receiptName = "Quartz_Slab_Delivery.pdf",
                billableToClient = true,
                qboSynced = false
            )
        )
        database.expenseDao().insertExpenses(initialExpenses)

        // Initial Room Scans (3D Floor Plans & Photorealistic Renders)
        val initialRoomScans = listOf(
            RoomScanEntity(
                jobId = 5,
                roomName = "Gourmet Kitchen & Dining Expansion",
                clientName = "Eleanor & David Vance",
                lengthFeet = 18.5,
                widthFeet = 14.0,
                ceilingHeightFeet = 9.5,
                areaSqFt = 259.0,
                wallCount = 4,
                doorCount = 2,
                windowCount = 2,
                snappedPhotos = "North Plumbing Rough-in, Island Center Load Bearing Post, East Window Header, Electrical Subpanel Drop",
                photoCount = 4,
                scanDate = "Today 08:30 AM",
                renderStyle = "Transitional Luxury",
                tradeType = "Remodeler",
                status = "Render Ready"
            ),
            RoomScanEntity(
                jobId = 6,
                roomName = "Luxury Master Bathroom Suite & Wet Room",
                clientName = "Marcus Holloway",
                lengthFeet = 12.0,
                widthFeet = 10.5,
                ceilingHeightFeet = 9.0,
                areaSqFt = 126.0,
                wallCount = 4,
                doorCount = 1,
                windowCount = 1,
                snappedPhotos = "Curbless Shower Pan Center, Free Tub Supply Stub-out, Double Vanity Stud Wall",
                photoCount = 3,
                scanDate = "Yesterday 03:15 PM",
                renderStyle = "Modern Minimalist",
                tradeType = "Builder",
                status = "Render Ready"
            ),
            RoomScanEntity(
                jobId = 7,
                roomName = "Executive Great Room & Architectural Millwork",
                clientName = "Catalina Vista Properties",
                lengthFeet = 24.0,
                widthFeet = 18.0,
                ceilingHeightFeet = 11.0,
                areaSqFt = 432.0,
                wallCount = 4,
                doorCount = 2,
                windowCount = 4,
                snappedPhotos = "Fireplace Flue Header, Coffered Ceiling Joist Layout, French Door Jambs, Low-Voltage Audio Run",
                photoCount = 4,
                scanDate = "Sep 05",
                renderStyle = "Contemporary Farmhouse",
                tradeType = "Interior Designer",
                status = "Render Ready"
            )
        )
        database.roomScanDao().insertRoomScans(initialRoomScans)

        // Seed Initial Project Tasks for Drag-and-Drop Kanban Board
        val initialProjectTasks = listOf(
            // Backlog
            ProjectTaskEntity(
                projectId = 1,
                projectName = "Foothills Luxury Kitchen Remodel",
                title = "Procure Custom 42-inch Rift White Oak Cabinets",
                description = "Verify shop drawings with architect. Ensure soft-close European hinges and dovetail drawer boxes.",
                state = "Backlog",
                priority = "High",
                assignedTech = "Marcus Vance",
                dueDate = "Sep 18",
                estimatedHours = 6.0,
                trade = "Carpentry",
                sortOrder = 0,
                checklistDone = 0,
                checklistTotal = 3
            ),
            ProjectTaskEntity(
                projectId = 2,
                projectName = "Catalina Ridge Solar & EV Upgrade",
                title = "Order Dual Subpanel Surge Protection Units",
                description = "Type 2 SPD 50kA per phase for main distribution panel and EV feeder.",
                state = "Backlog",
                priority = "Medium",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Sep 20",
                estimatedHours = 2.0,
                trade = "Electrical",
                sortOrder = 1,
                checklistDone = 1,
                checklistTotal = 2
            ),
            ProjectTaskEntity(
                projectId = 3,
                projectName = "Tucson Foothills Bath Suite Remodel",
                title = "Permit Filing for Structural Header Modification",
                description = "Submit stamped engineering calculations to Pima County Development Services for load-bearing header.",
                state = "Backlog",
                priority = "Urgent",
                assignedTech = "Dispatch Desk",
                dueDate = "Sep 14",
                estimatedHours = 3.5,
                trade = "Permits",
                sortOrder = 2,
                checklistDone = 2,
                checklistTotal = 4
            ),
            ProjectTaskEntity(
                projectId = 3,
                projectName = "Tucson Foothills Bath Suite Remodel",
                title = "Procure Delta Champagne Bronze Shower Rough Valves",
                description = "TempAssure thermostatic rough-in valve with multi-function diverter.",
                state = "Backlog",
                priority = "Low",
                assignedTech = "Carlos Mendez",
                dueDate = "Sep 22",
                estimatedHours = 1.5,
                trade = "Plumbing",
                sortOrder = 3,
                checklistDone = 1,
                checklistTotal = 1
            ),
            // To Do
            ProjectTaskEntity(
                projectId = 1,
                projectName = "Foothills Luxury Kitchen Remodel",
                title = "Core Drill 4\" Slab Penetration for Island Drain",
                description = "Locate post-tension cables via GPR radar scan before core drill penetrations.",
                state = "To Do",
                priority = "Urgent",
                assignedTech = "Carlos Mendez",
                dueDate = "Tomorrow",
                estimatedHours = 3.5,
                trade = "Plumbing",
                sortOrder = 0,
                checklistDone = 1,
                checklistTotal = 3
            ),
            ProjectTaskEntity(
                projectId = 2,
                projectName = "Catalina Ridge Solar & EV Upgrade",
                title = "Pull 6 AWG Copper Wire for 50A EV Charger",
                description = "Pull through 1\" EMT conduit from basement subpanel to west garage bay.",
                state = "To Do",
                priority = "High",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Sep 12",
                estimatedHours = 4.0,
                trade = "Electrical",
                sortOrder = 1,
                checklistDone = 0,
                checklistTotal = 2
            ),
            ProjectTaskEntity(
                projectId = 3,
                projectName = "Tucson Foothills Bath Suite Remodel",
                title = "Install Moisture-Resistant Cement Backerboard",
                description = "Install 1/2\" HardieBacker on shower wet walls using alkali-resistant screws and mesh tape.",
                state = "To Do",
                priority = "Medium",
                assignedTech = "Sarah Lin",
                dueDate = "Sep 13",
                estimatedHours = 5.0,
                trade = "Tile",
                sortOrder = 2,
                checklistDone = 2,
                checklistTotal = 4
            ),
            ProjectTaskEntity(
                projectId = 4,
                projectName = "Mercado Commercial Plaza Service",
                title = "Inspect Load-Bearing Post Footing Dimensions",
                description = "Verify 24x24x12\" concrete pad footing depth before steel base plate anchoring.",
                state = "To Do",
                priority = "Low",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Sep 15",
                estimatedHours = 2.5,
                trade = "Framing",
                sortOrder = 3,
                checklistDone = 0,
                checklistTotal = 2
            ),
            // In Progress
            ProjectTaskEntity(
                projectId = 3,
                projectName = "Tucson Foothills Bath Suite Remodel",
                title = "Rough-in Master Bath PEX Hot/Cold Supplies",
                description = "Run 3/4\" Uponor ProPEX expansion loop with dedicated shutoff manifold.",
                state = "In Progress",
                priority = "Urgent",
                assignedTech = "Carlos Mendez",
                dueDate = "Today 3:00 PM",
                estimatedHours = 6.0,
                trade = "Plumbing",
                sortOrder = 0,
                checklistDone = 3,
                checklistTotal = 5
            ),
            ProjectTaskEntity(
                projectId = 1,
                projectName = "Foothills Luxury Kitchen Remodel",
                title = "Run Dedicated 20A Circuits for Kitchen Counter GFCIs",
                description = "Home run 12/2 Romex to 200A panel, split between island, appliance garage, and microwave.",
                state = "In Progress",
                priority = "High",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Today 5:00 PM",
                estimatedHours = 4.5,
                trade = "Electrical",
                sortOrder = 1,
                checklistDone = 2,
                checklistTotal = 3
            ),
            ProjectTaskEntity(
                projectId = 2,
                projectName = "Catalina Ridge Solar & EV Upgrade",
                title = "Mount Inverter Disconnect & Rigid Conduit Runs",
                description = "Securing Sol-Ark hybrid inverter disconnect switch to exterior masonry wall with Tapcons.",
                state = "In Progress",
                priority = "High",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Today",
                estimatedHours = 5.0,
                trade = "Electrical",
                sortOrder = 2,
                checklistDone = 1,
                checklistTotal = 3
            ),
            // In Review
            ProjectTaskEntity(
                projectId = 3,
                projectName = "Tucson Foothills Bath Suite Remodel",
                title = "Rough Plumbing Hydrostatic Pressure Test (80 PSI)",
                description = "Hold 80 PSI test pressure gauge for 24 hours prior to county plumbing inspector sign-off.",
                state = "In Review",
                priority = "Urgent",
                assignedTech = "Carlos Mendez",
                dueDate = "Inspection Tomorrow",
                estimatedHours = 2.0,
                trade = "Plumbing",
                sortOrder = 0,
                checklistDone = 4,
                checklistTotal = 4
            ),
            ProjectTaskEntity(
                projectId = 2,
                projectName = "Catalina Ridge Solar & EV Upgrade",
                title = "City Electrical Inspector Rough Wire Walkthrough",
                description = "Check box fill calculations, arc-fault breakers, and solar rapid shutdown labeling.",
                state = "In Review",
                priority = "Urgent",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Today 2:30 PM",
                estimatedHours = 1.5,
                trade = "Electrical",
                sortOrder = 1,
                checklistDone = 3,
                checklistTotal = 3
            ),
            ProjectTaskEntity(
                projectId = 1,
                projectName = "Foothills Luxury Kitchen Remodel",
                title = "Kitchen Drywall Finish Level 4 Inspection",
                description = "Inspect tape joints, screw dimples, and corners with directional halogen work lights.",
                state = "In Review",
                priority = "Medium",
                assignedTech = "Sarah Lin",
                dueDate = "Tomorrow",
                estimatedHours = 2.0,
                trade = "Drywall",
                sortOrder = 2,
                checklistDone = 2,
                checklistTotal = 2
            ),
            // Done
            ProjectTaskEntity(
                projectId = 3,
                projectName = "Tucson Foothills Bath Suite Remodel",
                title = "Demolition of Existing Tile & Fixture Haul-off",
                description = "Safely capped water supplies, removed fiberglass tub insert, and disposed in roll-off dumpster.",
                state = "Done",
                priority = "High",
                assignedTech = "Sarah Lin",
                dueDate = "Sep 06",
                estimatedHours = 8.0,
                trade = "Demolition",
                sortOrder = 0,
                checklistDone = 4,
                checklistTotal = 4
            ),
            ProjectTaskEntity(
                projectId = 2,
                projectName = "Catalina Ridge Solar & EV Upgrade",
                title = "Trenching & Conduit Lay for Detached Garage Service",
                description = "Excavated 18\" trench, laid 2\" schedule 40 PVC with warning tape at 12\". Backfilled and compacted.",
                state = "Done",
                priority = "Medium",
                assignedTech = "Alex Ramirez (Lead)",
                dueDate = "Sep 07",
                estimatedHours = 7.0,
                trade = "Electrical",
                sortOrder = 1,
                checklistDone = 3,
                checklistTotal = 3
            ),
            ProjectTaskEntity(
                projectId = 4,
                projectName = "Mercado Commercial Plaza Service",
                title = "Main Water Shutoff Valve Replacement to 1\" Ball Valve",
                description = "Cut out seized gate valve and soldered Apollo lead-free brass ball valve with test tee.",
                state = "Done",
                priority = "Urgent",
                assignedTech = "Carlos Mendez",
                dueDate = "Sep 05",
                estimatedHours = 3.0,
                trade = "Plumbing",
                sortOrder = 2,
                checklistDone = 2,
                checklistTotal = 2
            )
        )
        database.projectTaskDao().insertTasks(initialProjectTasks)

        // Seed Customer Bookings (Thumbtack / TaskRabbit / Angie / Yelp style)
        val initialBookings = listOf(
            CustomerBookingEntity(
                id = 1,
                customerName = "Elena Rostova",
                customerPhone = "(520) 840-2291",
                customerEmail = "elena.r@desertliving.com",
                address = "7820 E Broadway Blvd, Tucson, AZ",
                category = "Plumbing",
                projectTitle = "Emergency Main Sewer Line Hydro-Jetting",
                description = "Severe drainage backup in commercial building main restrooms. Water pooling. Need immediate root cutting and 4000 PSI hydro-jetting.",
                budget = 1200.0,
                preferredDate = "Today - Immediate",
                urgency = "Emergency Today",
                status = "Open For Bids",
                bidsCount = 2,
                serviceType = "Commercial"
            ),
            CustomerBookingEntity(
                id = 2,
                customerName = "Marcus Sterling",
                customerPhone = "(520) 612-9934",
                customerEmail = "msterling@greentech.org",
                address = "3410 N Campbell Ave, Tucson, AZ",
                category = "Electrical",
                projectTitle = "200A Smart Subpanel & Dual Level-2 EV Chargers",
                description = "Looking to install a 200A Square D smart panel and two 48-amp EV wallbox chargers in commercial fleet garage bay. Conduit run approx 45ft.",
                budget = 3200.0,
                preferredDate = "Within 48 Hours",
                urgency = "Within 48 Hours",
                status = "Open For Bids",
                bidsCount = 3,
                serviceType = "Commercial"
            ),
            CustomerBookingEntity(
                id = 3,
                customerName = "Sarah Jenkins",
                customerPhone = "(520) 441-8902",
                customerEmail = "s.jenkins@foothillsres.net",
                address = "5520 N Oracle Rd, Tucson, AZ",
                category = "Remodeling",
                projectTitle = "Master Bath Porcelain Tile & Floating Vanity",
                description = "Full remodel of 120 sq ft bath. Install Schluter waterproof system, 24x48 porcelain tile, curbless walk-in shower with linear drain.",
                budget = 4500.0,
                preferredDate = "Monday Next Week",
                urgency = "Within 48 Hours",
                status = "Booked",
                assignedProName = "Apex Craft Builders",
                assignedProRating = 4.95f,
                assignedProBadge = "Thumbtack Top Pro • Yelp 5★",
                acceptedQuoteAmount = 4250.0,
                bidsCount = 3,
                serviceType = "Residential"
            ),
            CustomerBookingEntity(
                id = 4,
                customerName = "Desert Vista Medical",
                customerPhone = "(520) 325-7780",
                customerEmail = "facilities@desertvistamed.com",
                address = "1920 E River Rd, Tucson, AZ",
                category = "HVAC",
                projectTitle = "Rooftop Commercial HVAC Condenser Motor Replacement",
                description = "10-ton Trane package unit fan motor seized. Urgent repair needed before afternoon temperatures spike.",
                budget = 1850.0,
                preferredDate = "Today 1:00 PM",
                urgency = "Emergency Today",
                status = "In Progress",
                assignedProName = "CoolBreeze Thermal Pros",
                assignedProRating = 4.9f,
                assignedProBadge = "Angie Certified Pro",
                acceptedQuoteAmount = 1650.0,
                bidsCount = 2,
                serviceType = "Commercial"
            ),
            CustomerBookingEntity(
                id = 5,
                customerName = "Innovate Tech Hub",
                customerPhone = "(520) 577-4431",
                customerEmail = "operations@innovateth.io",
                address = "4400 E Broadway Blvd, Tucson, AZ",
                category = "Moving & Freight",
                projectTitle = "Same-Day Commercial Office Furniture Assembly & Logistics",
                description = "12 standing executive desks, 24 ergonomic mesh chairs, and 1 boardroom conference table delivered from freight dock to 3rd floor. Assembly and packaging removal required.",
                budget = 850.0,
                preferredDate = "Today 2:00 PM",
                urgency = "Emergency Today",
                status = "Open For Bids",
                bidsCount = 2,
                serviceType = "Commercial"
            ),
            CustomerBookingEntity(
                id = 6,
                customerName = "David & Lisa Miller",
                customerPhone = "(520) 791-3310",
                customerEmail = "dmiller@desertview.net",
                address = "8910 E Tanque Verde Rd, Tucson, AZ",
                category = "HVAC",
                projectTitle = "Whole-Home Dual-Zone Heat Pump Conversion",
                description = "Replace 15-year-old gas furnace with high-efficiency 18 SEER Bosch inverter heat pump. Includes Honeywell smart touch thermostats and duct sealing.",
                budget = 6800.0,
                preferredDate = "Within 48 Hours",
                urgency = "Within 48 Hours",
                status = "Open For Bids",
                bidsCount = 1,
                serviceType = "Residential"
            ),
            CustomerBookingEntity(
                id = 7,
                customerName = "Katherine Vance",
                customerPhone = "(520) 529-6744",
                customerEmail = "kvance@catalinares.org",
                address = "6420 N Skyline Dr, Tucson, AZ",
                category = "Plumbing",
                projectTitle = "Tankless Water Heater Installation & Copper Repipe",
                description = "Existing 50-gallon tank leaking. Install Navien NPE-240A2 condensing tankless unit with dedicated recirculation loop and gas line upgrade.",
                budget = 3600.0,
                preferredDate = "Today - Immediate",
                urgency = "Emergency Today",
                status = "Open For Bids",
                bidsCount = 1,
                serviceType = "Residential"
            ),
            CustomerBookingEntity(
                id = 8,
                customerName = "Oak Creek Culinary Bistro",
                customerPhone = "(520) 624-8833",
                customerEmail = "chef@oakcreekbistro.com",
                address = "122 E Congress St, Tucson, AZ",
                category = "Refrigeration",
                projectTitle = "Commercial Walk-In Freezer R-404A Compressor Overhaul",
                description = "Walk-in freezer temperature rising to 28°F. Need licensed commercial refrigeration tech to diagnose TXV valve, vacuum evacuation, and recharge.",
                budget = 3900.0,
                preferredDate = "Today - Immediate",
                urgency = "Emergency Today",
                status = "Open For Bids",
                bidsCount = 2,
                serviceType = "Commercial"
            ),
            CustomerBookingEntity(
                id = 9,
                customerName = "Robert & Carmen Chen",
                customerPhone = "(520) 331-4091",
                customerEmail = "robert.chen@arizonatech.edu",
                address = "7310 E Sunrise Dr, Tucson, AZ",
                category = "Carpentry",
                projectTitle = "Custom Redwood Pergola & Paver Outdoor Kitchen",
                description = "Build 16x20 heavy timber cedar/redwood shade pergola over Belgard concrete paver patio with built-in stainless BBQ island framing.",
                budget = 7800.0,
                preferredDate = "Flexible Next Week",
                urgency = "Flexible This Week",
                status = "Open For Bids",
                bidsCount = 0,
                serviceType = "Residential"
            ),
            CustomerBookingEntity(
                id = 10,
                customerName = "Plaza Del Sol Commercial Center",
                customerPhone = "(520) 887-2100",
                customerEmail = "maintenance@plazadelsol.biz",
                address = "3850 W Ina Rd, Tucson, AZ",
                category = "Electrical",
                projectTitle = "Commercial Facility High-Bay LED Conversion & 3-Phase Panel",
                description = "Retrofit 48 warehouse metal-halide high-bay fixtures to 150W Cree LED with occupancy daylight sensors, plus 3-phase disconnect testing.",
                budget = 5600.0,
                preferredDate = "Within 48 Hours",
                urgency = "Within 48 Hours",
                status = "Open For Bids",
                bidsCount = 1,
                serviceType = "Commercial"
            ),
            CustomerBookingEntity(
                id = 11,
                customerName = "Melissa Alvarez",
                customerPhone = "(520) 299-1845",
                customerEmail = "malvarez@swlifestyle.com",
                address = "4105 N Sabino Canyon Rd, Tucson, AZ",
                category = "Painting",
                projectTitle = "Whole-Home Interior Repaint (Sherwin-Williams Emerald)",
                description = "2,600 sq ft 4-bed home interior. Caulking, drywall patching, 2 coats SW Emerald Satin on walls and Semi-Gloss on baseboards and doors.",
                budget = 3800.0,
                preferredDate = "Flexible Next Week",
                urgency = "Flexible This Week",
                status = "Open For Bids",
                bidsCount = 1,
                serviceType = "Residential"
            ),
            CustomerBookingEntity(
                id = 12,
                customerName = "Apex Logistics Terminal",
                customerPhone = "(520) 574-8800",
                customerEmail = "ops@apexterminal.logistics",
                address = "6800 S Country Club Rd, Tucson, AZ",
                category = "Facility Maintenance",
                projectTitle = "Hydraulic Loading Dock Leveler Repair & Roll-up Door Spring",
                description = "Dock bay #4 hydraulic cylinder seized. Dock bay #6 commercial overhead roll-up door torsion spring snapped. Freight trucks delayed.",
                budget = 4200.0,
                preferredDate = "Today - Immediate",
                urgency = "Emergency Today",
                status = "Open For Bids",
                bidsCount = 2,
                serviceType = "Commercial"
            )
        )
        database.customerBookingDao().insertBookings(initialBookings)

        // Seed Marketplace Bids (Thumbtack / Angie / TaskRabbit / Yelp pros)
        val initialBids = listOf(
            MarketplaceBidEntity(
                bookingRequestId = 1,
                proName = "Carlos Mendez (Master Plumber)",
                proCompany = "Mendez Hydro Dynamics",
                proRating = 4.95f,
                reviewCount = 84,
                badge = "Thumbtack Top Pro",
                bidAmount = 850.0,
                estimatedHours = 2.5,
                earliestAvailability = "Today 1:00 PM",
                proposalNote = "Includes fiber optic camera inspection before and after jetting. 1-year clog-free guarantee with C520X priority dispatch."
            ),
            MarketplaceBidEntity(
                bookingRequestId = 1,
                proName = "Rapid Rooter 24/7",
                proCompany = "Tucson Sewer Pros",
                proRating = 4.8f,
                reviewCount = 47,
                badge = "Angie Certified",
                bidAmount = 950.0,
                estimatedHours = 3.0,
                earliestAvailability = "Today 2:30 PM",
                proposalNote = "Fully equipped with trailer-mounted 4000 PSI hydrojetter and bio-clean enzyme treatment."
            ),
            MarketplaceBidEntity(
                bookingRequestId = 2,
                proName = "Alex Ramirez",
                proCompany = "VoltCraft Electrical Solutions",
                proRating = 4.98f,
                reviewCount = 112,
                badge = "Thumbtack Top Pro",
                bidAmount = 2850.0,
                estimatedHours = 6.0,
                earliestAvailability = "Tomorrow 8:00 AM",
                proposalNote = "Square D QO 200A panel with built-in surge protection, city inspection coordination, and dual 48A EV wallbox installations."
            ),
            MarketplaceBidEntity(
                bookingRequestId = 2,
                proName = "Precision Amp Tech",
                proCompany = "Desert Current Electric",
                proRating = 4.85f,
                reviewCount = 63,
                badge = "Yelp Recommended",
                bidAmount = 3100.0,
                estimatedHours = 7.0,
                earliestAvailability = "Tomorrow 10:00 AM",
                proposalNote = "Licensed ROC electrical contractor. Includes 10-year warranty on craftsmanship and panel components."
            ),
            MarketplaceBidEntity(
                bookingRequestId = 2,
                proName = "GreenGrid Energy",
                proCompany = "Solar & EV Masters",
                proRating = 4.7f,
                reviewCount = 29,
                badge = "TaskRabbit Elite",
                bidAmount = 2600.0,
                estimatedHours = 5.5,
                earliestAvailability = "Friday 9:00 AM",
                proposalNote = "EV charger specialist, certified installer for ChargePoint, Wallbox, and Tesla Universal Wall Connectors."
            ),
            MarketplaceBidEntity(
                bookingRequestId = 3,
                proName = "Apex Craft Builders",
                proCompany = "Apex Luxury Remodeling",
                proRating = 4.95f,
                reviewCount = 96,
                badge = "Thumbtack Top Pro • Yelp 5★",
                bidAmount = 4250.0,
                estimatedHours = 24.0,
                earliestAvailability = "Monday 8:00 AM",
                proposalNote = "Schluter-Kerdi waterproof membrane, large format porcelain tile precision leveling, floating vanity install.",
                isAccepted = true
            ),
            MarketplaceBidEntity(
                bookingRequestId = 5,
                proName = "Express Assembly & Logistics",
                proCompany = "C520X Delivery & Task Pro",
                proRating = 4.92f,
                reviewCount = 58,
                badge = "TaskRabbit Elite",
                bidAmount = 650.0,
                estimatedHours = 4.0,
                earliestAvailability = "Today 2:30 PM",
                proposalNote = "2-man certified assembly crew with liftgate box truck, power assembly tools, blanket wraps, and complete packaging haul-off."
            ),
            MarketplaceBidEntity(
                bookingRequestId = 5,
                proName = "QuickMove Handyman",
                proCompany = "QuickMove Assembly",
                proRating = 4.75f,
                reviewCount = 33,
                badge = "Angie Certified",
                bidAmount = 740.0,
                estimatedHours = 4.5,
                earliestAvailability = "Today 4:00 PM",
                proposalNote = "Assembled 200+ commercial standing desks and conference suites. Fast, professional, insured."
            )
        )
        database.marketplaceBidDao().insertBids(initialBids)

        // Seed DAT One Style Freight & Delivery Loads
        val initialFreight = listOf(
            FreightLoadEntity(
                id = 1,
                loadNumber = "DAT-98102",
                originCity = "Phoenix, AZ",
                destinationCity = "Los Angeles, CA",
                deadheadMiles = 18,
                tripMiles = 372,
                equipmentType = "Dry Van",
                weightLbs = 38500,
                commodity = "Commercial Solar Inverters & Batteries",
                rateTotal = 1450.0,
                ratePerMile = 3.89,
                brokerName = "C520X Freight Express",
                brokerPhone = "(520) 880-9200",
                brokerCreditScore = 98,
                daysToPay = "QuickPay 24hr",
                pickupDate = "Sep 10 08:00",
                deliveryDate = "Sep 10 19:00",
                status = "Available",
                isSaved = true
            ),
            FreightLoadEntity(
                id = 2,
                loadNumber = "DAT-94218",
                originCity = "Dallas, TX",
                destinationCity = "Atlanta, GA",
                deadheadMiles = 25,
                tripMiles = 781,
                equipmentType = "Reefer",
                weightLbs = 42000,
                commodity = "Chilled Organic Berries & Produce (34°F)",
                rateTotal = 2950.0,
                ratePerMile = 3.78,
                brokerName = "CH Robinson Express",
                brokerPhone = "(800) 323-7308",
                brokerCreditScore = 96,
                daysToPay = "QuickPay 24hr",
                pickupDate = "Sep 11 06:00",
                deliveryDate = "Sep 12 14:00",
                status = "Available"
            ),
            FreightLoadEntity(
                id = 3,
                loadNumber = "DAT-89104",
                originCity = "Chicago, IL",
                destinationCity = "Columbus, OH",
                deadheadMiles = 12,
                tripMiles = 355,
                equipmentType = "Flatbed",
                weightLbs = 44000,
                commodity = "Structural Steel I-Beams & Pipe Spools",
                rateTotal = 1650.0,
                ratePerMile = 4.65,
                brokerName = "TQL Freight Logistics",
                brokerPhone = "(800) 580-3101",
                brokerCreditScore = 94,
                daysToPay = "7 Days ACH",
                pickupDate = "Sep 10 10:00",
                deliveryDate = "Sep 11 08:00",
                status = "Available"
            ),
            FreightLoadEntity(
                id = 4,
                loadNumber = "DAT-77291",
                originCity = "Tucson, AZ",
                destinationCity = "San Diego, CA",
                deadheadMiles = 8,
                tripMiles = 407,
                equipmentType = "Box Truck 26ft",
                weightLbs = 12500,
                commodity = "Architectural Millwork & Luxury Cabinets",
                rateTotal = 1550.0,
                ratePerMile = 3.81,
                brokerName = "Apex Freight Dispatch",
                brokerPhone = "(520) 791-4400",
                brokerCreditScore = 99,
                daysToPay = "QuickPay 24hr",
                pickupDate = "Today 14:00",
                deliveryDate = "Tomorrow 09:00",
                status = "In Transit",
                driverAssigned = "Dave Miller (Unit #104)",
                currentGpsLocation = "I-8 West near Yuma, AZ (Mile 62)",
                rateConSigned = true
            ),
            FreightLoadEntity(
                id = 5,
                loadNumber = "DAT-65103",
                originCity = "Denver, CO",
                destinationCity = "Salt Lake City, UT",
                deadheadMiles = 30,
                tripMiles = 518,
                equipmentType = "Hotshot",
                weightLbs = 16800,
                commodity = "Excavator Hydraulic Attachments & Tracks",
                rateTotal = 2100.0,
                ratePerMile = 4.05,
                brokerName = "Landstar Ranger Dispatch",
                brokerPhone = "(800) 872-9400",
                brokerCreditScore = 95,
                daysToPay = "QuickPay 24hr",
                pickupDate = "Sep 11 07:00",
                deliveryDate = "Sep 12 11:00",
                status = "Available"
            ),
            FreightLoadEntity(
                id = 6,
                loadNumber = "DAT-55209",
                originCity = "Houston, TX",
                destinationCity = "Memphis, TN",
                deadheadMiles = 14,
                tripMiles = 566,
                equipmentType = "Cargo Van / Sprinter",
                weightLbs = 3200,
                commodity = "Expedited Aircraft Avionics Components",
                rateTotal = 1380.0,
                ratePerMile = 2.44,
                brokerName = "Expedite Now Worldwide",
                brokerPhone = "(888) 450-2020",
                brokerCreditScore = 97,
                daysToPay = "QuickPay 24hr",
                pickupDate = "Today 16:00",
                deliveryDate = "Tomorrow 06:00",
                status = "Dispatched",
                driverAssigned = "Tony Morales (Sprinter #12)",
                rateConSigned = true
            ),
            FreightLoadEntity(
                id = 7,
                loadNumber = "DAT-44102",
                originCity = "Tucson, AZ",
                destinationCity = "El Paso, TX",
                deadheadMiles = 10,
                tripMiles = 318,
                equipmentType = "Dry Van",
                weightLbs = 26000,
                commodity = "Contractor Building Supplies & Copper Spools",
                rateTotal = 1280.0,
                ratePerMile = 4.02,
                brokerName = "C520X Freight Express",
                brokerPhone = "(520) 880-9200",
                brokerCreditScore = 98,
                daysToPay = "QuickPay 24hr",
                pickupDate = "Yesterday",
                deliveryDate = "Today 08:00",
                status = "Delivered",
                driverAssigned = "Alex Ramirez",
                rateConSigned = true,
                bolNumber = "BOL-C520X-99812"
            )
        )
        database.freightLoadDao().insertLoads(initialFreight)

        // Seed Project Milestones (Contractor Workflow & Progress Draws)
        val initialMilestones = listOf(
            // Booking #3: Sarah Jenkins - Master Bath Remodel ($4,500)
            ProjectMilestoneEntity(
                bookingId = 3,
                title = "Phase 1: Permit Pull, Demolition & Subfloor Prep",
                description = "Secure city plumbing/building permits, demo tile surround, haul off drywall & salvage subfloor.",
                phaseNumber = 1,
                targetDate = "Completed Sep 5",
                drawAmount = 1000.0,
                status = "Completed",
                completedTimestamp = System.currentTimeMillis() - 86400000L * 3,
                verificationNotes = "Permit #2026-P912 approved. Clean subfloor inspection passed."
            ),
            ProjectMilestoneEntity(
                bookingId = 3,
                title = "Phase 2: Rough-In Plumbing & Schluter Waterproofing",
                description = "Install 2\" linear drain trap, rough-in PEX-A hot/cold lines, apply Schluter Kerdi waterproofing membrane.",
                phaseNumber = 2,
                targetDate = "Completed Sep 7",
                drawAmount = 1200.0,
                status = "Completed",
                completedTimestamp = System.currentTimeMillis() - 86400000L,
                verificationNotes = "24-hour flood test verified zero leaks. Kerdi-band joints sealed."
            ),
            ProjectMilestoneEntity(
                bookingId = 3,
                title = "Phase 3: 24x48 Large-Format Porcelain Tile Setting",
                description = "Laser layout, polymer-modified thinset, precision leveling clips for zero lippage, curbless transition.",
                phaseNumber = 3,
                targetDate = "Today 4:00 PM",
                drawAmount = 1100.0,
                status = "In Progress",
                completedTimestamp = 0L,
                verificationNotes = "70% tile set, wall leveling spacers engaged."
            ),
            ProjectMilestoneEntity(
                bookingId = 3,
                title = "Phase 4: Floating Double Vanity & Matte Black Trim",
                description = "Mount 72\" teak floating vanity, dual under-mount sinks, matte black thermostatic shower valve trim.",
                phaseNumber = 4,
                targetDate = "Tomorrow 2:00 PM",
                drawAmount = 700.0,
                status = "Pending",
                completedTimestamp = 0L,
                verificationNotes = "Fixtures received on site, ready for mount."
            ),
            ProjectMilestoneEntity(
                bookingId = 3,
                title = "Phase 5: Client Final Walkthrough & 5-Star Sign-Off",
                description = "Silicone color-match caulking, glass enclosure seal, client final punch list and QBO invoice transmission.",
                phaseNumber = 5,
                targetDate = "Sep 12 11:00 AM",
                drawAmount = 250.0,
                status = "Pending",
                completedTimestamp = 0L,
                verificationNotes = "Final invoice prepared for one-touch send."
            ),

            // Booking #4: Desert Vista Medical - Rooftop Commercial HVAC ($1,850)
            ProjectMilestoneEntity(
                bookingId = 4,
                title = "Phase 1: Lockout-Tagout Safety & Crane Lift Rigging",
                description = "OSHA compliant 480V 3-phase electrical disconnect lockout, fall arrest rigging, roof harness perimeter.",
                phaseNumber = 1,
                targetDate = "Completed Today 9:00 AM",
                drawAmount = 400.0,
                status = "Completed",
                completedTimestamp = System.currentTimeMillis() - 14400000L,
                verificationNotes = "High-voltage circuits zeroed with Fluke multimeter."
            ),
            ProjectMilestoneEntity(
                bookingId = 4,
                title = "Phase 2: Seized Motor Extraction & Dynamic Blade Balancing",
                description = "Pull locked 10-ton condenser fan motor, balance aluminum fan blades, inspect vibration isolators.",
                phaseNumber = 2,
                targetDate = "In Progress",
                drawAmount = 650.0,
                status = "In Progress",
                completedTimestamp = 0L,
                verificationNotes = "Motor unbolted; shaft alignment checked."
            ),
            ProjectMilestoneEntity(
                bookingId = 4,
                title = "Phase 3: Trane 10-Ton Motor Installation & Contactor Wire",
                description = "Install OEM Trane condenser fan motor, replace 3-pole 40A contactor and dual run capacitor.",
                phaseNumber = 3,
                targetDate = "Today 2:30 PM",
                drawAmount = 500.0,
                status = "Pending",
                completedTimestamp = 0L,
                verificationNotes = "OEM motor staged on roof deck."
            ),
            ProjectMilestoneEntity(
                bookingId = 4,
                title = "Phase 4: CFM Velocity Test, Thermal Run & QBO Billing",
                description = "Measure amp draw under load, verify CFM air velocity, log delta-T, customer digital sign-off.",
                phaseNumber = 4,
                targetDate = "Today 4:30 PM",
                drawAmount = 300.0,
                status = "Pending",
                completedTimestamp = 0L,
                verificationNotes = "Digital service ticket ready."
            )
        )
        database.projectMilestoneDao().insertMilestones(initialMilestones)
    }

    suspend fun updateJobStatus(id: Int, status: String) {
        database.jobDao().updateJobStatus(id, status)
    }

    suspend fun insertJob(job: JobEntity) = database.jobDao().insertJob(job)
    suspend fun insertJobs(jobs: List<JobEntity>) = database.jobDao().insertJobs(jobs)
    suspend fun deleteJob(id: Int) = database.jobDao().deleteJob(id)
    suspend fun updateJobSortOrder(id: Int, order: Int) = database.jobDao().updateJobSortOrder(id, order)

    suspend fun insertTimeLog(log: TimeLogEntity) = database.timeLogDao().insertTimeLog(log)
    suspend fun deleteTimeLog(id: Int) = database.timeLogDao().deleteTimeLog(id)

    suspend fun insertLead(lead: LeadEntity) = database.leadDao().insertLead(lead)
    suspend fun updateLeadStatus(id: Int, status: String) = database.leadDao().updateLeadStatus(id, status)

    suspend fun sendMessage(msg: ChatMessageEntity) = database.chatDao().insertMessage(msg)
    suspend fun updateChatMessage(msg: ChatMessageEntity) = database.chatDao().updateMessage(msg)
    suspend fun updateChatMessageText(id: Int, newText: String) = database.chatDao().updateMessageText(id, newText)
    suspend fun deleteChatMessage(id: Int) = database.chatDao().deleteMessage(id)

    suspend fun updatePaymentStatus(id: Int, status: String) = database.paymentDao().updatePaymentStatus(id, status)
    suspend fun insertPayment(payment: PaymentEntity) = database.paymentDao().insertPayment(payment)

    suspend fun insertReview(review: ReviewEntity) = database.reviewDao().insertReview(review)

    // Expenses & QBO
    suspend fun insertExpense(expense: ExpenseEntity) = database.expenseDao().insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = database.expenseDao().updateExpense(expense)
    suspend fun markExpenseQboSynced(id: Int, txnId: String, timestamp: Long) = database.expenseDao().markExpenseQboSynced(id, txnId, timestamp)
    suspend fun syncAllPendingExpenses(txnId: String, timestamp: Long) = database.expenseDao().syncAllPendingExpenses(txnId, timestamp)
    suspend fun deleteExpense(id: Int) = database.expenseDao().deleteExpense(id)

    // Room Scans & 3D
    suspend fun insertRoomScan(scan: RoomScanEntity) = database.roomScanDao().insertRoomScan(scan)
    suspend fun updateRoomScan(scan: RoomScanEntity) = database.roomScanDao().updateRoomScan(scan)
    suspend fun deleteRoomScan(id: Int) = database.roomScanDao().deleteRoomScan(id)

    // Project Tasks & Drag-and-Drop Kanban Persistence
    suspend fun insertProjectTask(task: ProjectTaskEntity) = database.projectTaskDao().insertTask(task)
    suspend fun updateProjectTask(task: ProjectTaskEntity) = database.projectTaskDao().updateTask(task)
    suspend fun updateProjectTaskState(id: Int, state: String) = database.projectTaskDao().updateTaskState(id, state)
    suspend fun updateProjectTaskPriority(id: Int, priority: String) = database.projectTaskDao().updateTaskPriority(id, priority)
    suspend fun updateProjectTaskStateAndOrder(id: Int, state: String, sortOrder: Int) = database.projectTaskDao().updateTaskStateAndOrder(id, state, sortOrder)
    suspend fun updateProjectTaskSortOrder(id: Int, sortOrder: Int) = database.projectTaskDao().updateTaskSortOrder(id, sortOrder)
    suspend fun deleteProjectTask(id: Int) = database.projectTaskDao().deleteTask(id)

    // Customer Bookings (Thumbtack / TaskRabbit / Angie / Yelp)
    suspend fun insertCustomerBooking(booking: CustomerBookingEntity) = database.customerBookingDao().insertBooking(booking)
    suspend fun updateCustomerBooking(booking: CustomerBookingEntity) = database.customerBookingDao().updateBooking(booking)
    suspend fun updateCustomerBookingStatus(id: Int, status: String) = database.customerBookingDao().updateBookingStatus(id, status)
    suspend fun assignProAndAcceptQuote(id: Int, proName: String, quoteAmount: Double, status: String = "Booked") =
        database.customerBookingDao().assignProAndAccept(id, proName, quoteAmount, status)
    suspend fun deleteCustomerBooking(id: Int) = database.customerBookingDao().deleteBooking(id)

    // Contractor Bids
    suspend fun insertMarketplaceBid(bid: MarketplaceBidEntity): Long {
        val bidId = database.marketplaceBidDao().insertBid(bid)
        database.customerBookingDao().incrementBidsCount(bid.bookingRequestId)
        return bidId
    }
    suspend fun markBidAccepted(bidId: Int) = database.marketplaceBidDao().markBidAccepted(bidId)
    suspend fun deleteMarketplaceBid(id: Int) = database.marketplaceBidDao().deleteBid(id)

    // Freight & Logistics Loads (DAT One Style)
    suspend fun insertFreightLoad(load: FreightLoadEntity) = database.freightLoadDao().insertLoad(load)
    suspend fun insertFreightLoads(loads: List<FreightLoadEntity>) = database.freightLoadDao().insertLoads(loads)
    suspend fun updateFreightLoad(load: FreightLoadEntity) = database.freightLoadDao().updateLoad(load)
    suspend fun updateFreightLoadStatus(id: Int, status: String) = database.freightLoadDao().updateLoadStatus(id, status)
    suspend fun bookFreightLoad(id: Int, driverName: String) = database.freightLoadDao().bookLoad(id, driverName)
    suspend fun updateFreightLoadTransit(id: Int, status: String, gps: String) = database.freightLoadDao().updateLoadTransit(id, status, gps)
    suspend fun deliverFreightLoad(id: Int, bol: String) = database.freightLoadDao().deliverLoad(id, bol)
    suspend fun toggleSaveFreightLoad(id: Int) = database.freightLoadDao().toggleSaveLoad(id)
    suspend fun deleteFreightLoad(id: Int) = database.freightLoadDao().deleteLoad(id)

    // Project Milestones (Contractor Workflow)
    fun getMilestonesForBooking(bookingId: Int): Flow<List<ProjectMilestoneEntity>> = database.projectMilestoneDao().getMilestonesForBooking(bookingId)
    suspend fun insertProjectMilestone(milestone: ProjectMilestoneEntity) = database.projectMilestoneDao().insertMilestone(milestone)
    suspend fun updateProjectMilestone(milestone: ProjectMilestoneEntity) = database.projectMilestoneDao().updateMilestone(milestone)
    suspend fun updateProjectMilestoneStatus(id: Int, status: String, timestamp: Long = System.currentTimeMillis()) =
        database.projectMilestoneDao().updateMilestoneStatus(id, status, timestamp)
    suspend fun deleteProjectMilestone(id: Int) = database.projectMilestoneDao().deleteMilestone(id)
}

