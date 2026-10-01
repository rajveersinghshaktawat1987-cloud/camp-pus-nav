package com.example.data.local

import com.example.data.model.BuildingCategory
import com.example.data.model.BuildingFloorPlan
import com.example.data.model.CampusBuilding
import com.example.data.model.FloorRoom
import com.example.data.model.LocationCategory
import com.example.data.model.RoomType
import com.example.data.model.SavedLocation

object CampusSeedData {

    val buildings = listOf(
        CampusBuilding(
            id = "b_sci",
            code = "SCI-100",
            name = "Gates Science Complex",
            category = BuildingCategory.ACADEMIC,
            description = "Premier center for Computer Science, AI Labs, robotics, and interactive lecture halls.",
            numFloors = 4,
            isAccessible = true,
            openHours = "7:00 AM – 11:00 PM (24/7 keycard for majors)",
            amenities = listOf("Elevator", "Restrooms", "Water Refill", "Cafe", "Printers", "Bike Rack", "Study Tables"),
            campusX = 420f,
            campusY = 110f,
            width = 160f,
            height = 100f,
            latitude = 37.4300,
            longitude = -122.1700,
            highlights = listOf("Room 101 Turing Auditorium", "Room 204 AI Robotics Lab", "Room 310 Cloud Computing Center", "Byte Cafe Level 1")
        ),
        CampusBuilding(
            id = "b_lib",
            code = "LIB-200",
            name = "Langdell Memorial Library",
            category = BuildingCategory.LIBRARY,
            description = "Main campus research library with collaborative study floors, silent reading rooms, and archives.",
            numFloors = 4,
            isAccessible = true,
            openHours = "24 Hours (Staffed 8:00 AM – Midnight)",
            amenities = listOf("Elevator", "Restrooms", "Water Refill", "Silent Study", "Printers", "Group Study Rooms", "Book Return", "Cafe"),
            campusX = 470f,
            campusY = 290f,
            width = 170f,
            height = 120f,
            latitude = 37.4280,
            longitude = -122.1690,
            highlights = listOf("Main Floor Circulation Desk", "Floor 2 Silent Study Gallery", "Floor 3 Digital Media Commons", "Basement Special Collections")
        ),
        CampusBuilding(
            id = "b_stu",
            code = "STU-300",
            name = "Tresidder Student Union",
            category = BuildingCategory.STUDENT_CENTER,
            description = "Hub of campus student life, student organizations, bookstore, coffee roasters, and event halls.",
            numFloors = 2,
            isAccessible = true,
            openHours = "6:30 AM – Midnight Daily",
            amenities = listOf("Elevator", "Restrooms", "ATM", "Coffee Roaster", "Campus Store", "Microwaves", "Lockers", "Lounge"),
            campusX = 200f,
            campusY = 480f,
            width = 150f,
            height = 110f,
            latitude = 37.4255,
            longitude = -122.1740,
            highlights = listOf("Level 1 Campus Bookstore", "Level 1 Coffee House & Eatery", "Level 2 Student Activities Center", "Ballroom Oak Hall")
        ),
        CampusBuilding(
            id = "b_din",
            code = "DIN-400",
            name = "Wilbur Dining Commons",
            category = BuildingCategory.DINING,
            description = "All-you-care-to-eat dining pavilion featuring international stations, salad bar, grill, and bakery.",
            numFloors = 1,
            isAccessible = true,
            openHours = "Breakfast 7–10 AM, Lunch 11 AM–2:30 PM, Dinner 5–9 PM",
            amenities = listOf("Restrooms", "Water Refill", "Outdoor Patio", "Gluten-Free Station", "Halal & Vegan Bar"),
            campusX = 720f,
            campusY = 480f,
            width = 150f,
            height = 100f,
            latitude = 37.4255,
            longitude = -122.1650,
            highlights = listOf("Main Servery", "Fireside Lounge Dining", "Express Grab-and-Go Kiosk")
        ),
        CampusBuilding(
            id = "b_quad",
            code = "ADM-001",
            name = "Old Main Quad & Memorial Chapel",
            category = BuildingCategory.ADMINISTRATION,
            description = "Historic heart of the university featuring red sandstone arcades, administrative offices, and iconic clock tower.",
            numFloors = 2,
            isAccessible = true,
            openHours = "8:00 AM – 6:00 PM (Chapel open for quiet reflection)",
            amenities = listOf("Restrooms", "Elevator", "Historical Tours", "Visitor Information", "Drinking Fountains"),
            campusX = 460f,
            campusY = 470f,
            width = 180f,
            height = 130f,
            latitude = 37.4260,
            longitude = -122.1695,
            highlights = listOf("President's Office Suite", "Memorial Chapel Nave", "Bursar & Registrar Services", "Historic Clock Archway")
        ),
        CampusBuilding(
            id = "b_eng",
            code = "ENG-150",
            name = "Engineering Quad & Maker Hall",
            category = BuildingCategory.ACADEMIC,
            description = "Mechanical, Civil and Electrical Engineering design studios, 3D printing labs, and fabrication shops.",
            numFloors = 3,
            isAccessible = true,
            openHours = "7:30 AM – 10:00 PM",
            amenities = listOf("Elevator", "Restrooms", "3D Printers", "Laser Cutters", "Water Refill", "Safety Showers"),
            campusX = 220f,
            campusY = 280f,
            width = 130f,
            height = 95f,
            latitude = 37.4285,
            longitude = -122.1735,
            highlights = listOf("Room 110 Maker Space", "Room 205 CAD & Circuit Design Lab", "Auditorium E-100")
        ),
        CampusBuilding(
            id = "b_phy",
            code = "PHY-180",
            name = "Varian Physics & Nano Research",
            category = BuildingCategory.ACADEMIC,
            description = "Quantum research, optics laboratories, particle astrophysics departments, and clean rooms.",
            numFloors = 3,
            isAccessible = true,
            openHours = "8:00 AM – 8:00 PM",
            amenities = listOf("Elevator", "Restrooms", "Water Refill", "Bike Lockers", "Printers"),
            campusX = 700f,
            campusY = 120f,
            width = 130f,
            height = 90f,
            latitude = 37.4298,
            longitude = -122.1655,
            highlights = listOf("Room 102 Quantum Optics Lab", "Floor 2 Astrophysics Seminar Room", "Basement Vibration-Isolated Labs")
        ),
        CampusBuilding(
            id = "b_res",
            code = "RES-500",
            name = "Graduate Research & Innovation Hub",
            category = BuildingCategory.ACADEMIC,
            description = "Interdisciplinary center hosting bio-engineering incubators, patent office, and faculty fellowship lounges.",
            numFloors = 3,
            isAccessible = true,
            openHours = "7:00 AM – 9:00 PM",
            amenities = listOf("Elevator", "Restrooms", "Conference Rooms", "Espresso Bar", "High-Speed WiFi"),
            campusX = 720f,
            campusY = 290f,
            width = 140f,
            height = 95f,
            latitude = 37.4280,
            longitude = -122.1650,
            highlights = listOf("Room 150 Innovation Forum", "Room 240 Patent & Tech Transfer", "Floor 3 Faculty Study")
        ),
        CampusBuilding(
            id = "b_gym",
            code = "REC-600",
            name = "Arrillaga Recreation & Gym",
            category = BuildingCategory.SPORTS,
            description = "Olympic indoor pool, basketball courts, weight training suites, climbing wall, and group fitness studios.",
            numFloors = 2,
            isAccessible = true,
            openHours = "6:00 AM – 11:00 PM Daily",
            amenities = listOf("Elevator", "Locker Rooms", "Showers", "Towel Service", "Water Refill", "Equipment Rental", "Pro Shop"),
            campusX = 180f,
            campusY = 690f,
            width = 160f,
            height = 120f,
            latitude = 37.4230,
            longitude = -122.1745,
            highlights = listOf("Weight Room Level 1", "Indoor Lap Pool", "Climbing Wall Annex", "Cardio Deck Level 2")
        ),
        CampusBuilding(
            id = "b_dorm",
            code = "DORM-700",
            name = "Roble Residence Village",
            category = BuildingCategory.DORMITORY,
            description = "Undergraduate student residential hall featuring study lounges, community kitchens, courtyards, and laundry.",
            numFloors = 3,
            isAccessible = true,
            openHours = "Residents Only (24/7 Security Entry)",
            amenities = listOf("Elevator", "Laundry Room", "Kitchen", "Quiet Study Lounge", "Bike Racks", "Package Locker"),
            campusX = 430f,
            campusY = 700f,
            width = 160f,
            height = 110f,
            latitude = 37.4232,
            longitude = -122.1700,
            highlights = listOf("A-Wing & B-Wing Common Lounges", "Central Courtyard Garden", "Package Hub")
        ),
        CampusBuilding(
            id = "b_hlth",
            code = "HLTH-800",
            name = "Student Health & Wellness",
            category = BuildingCategory.HEALTH,
            description = "Primary urgent care, pharmacy, physical therapy, mental wellness counseling, and immunization clinic.",
            numFloors = 2,
            isAccessible = true,
            openHours = "Mon–Fri 8:30 AM – 5:00 PM (24/7 Nurse Advice Line)",
            amenities = listOf("Elevator", "Restrooms", "Pharmacy", "Counseling Wing", "Wheelchair Ramp"),
            campusX = 710f,
            campusY = 690f,
            width = 130f,
            height = 95f,
            latitude = 37.4230,
            longitude = -122.1652,
            highlights = listOf("Level 1 Urgent Care & Pharmacy", "Level 2 Mental Health & Wellness Services")
        ),
        CampusBuilding(
            id = "b_prk_n",
            code = "PKG-N",
            name = "North Parking Garage & EV Charging",
            category = BuildingCategory.PARKING,
            description = "Multi-level covered parking structure with 800 spaces, 24 Level-2 EV charging bays, and automated pay kiosks.",
            numFloors = 4,
            isAccessible = true,
            openHours = "24/7 Open Access (Permit required Mon-Fri 6AM-4PM)",
            amenities = listOf("Elevator", "EV Charging", "Emergency Call Boxes", "Pay Stations", "Covered Parking"),
            campusX = 200f,
            campusY = 120f,
            width = 110f,
            height = 80f,
            latitude = 37.4310,
            longitude = -122.1740,
            highlights = listOf("Level 1 Visitor Hourly Parking", "Level 1 EV Charging Bays", "Level 2-4 Permit Parking")
        ),
        CampusBuilding(
            id = "b_transit",
            code = "TRN-E",
            name = "East Transit Hub & Shuttle Plaza",
            category = BuildingCategory.PARKING,
            description = "Central interchange for campus Marguerite shuttles, city transit buses, rideshare pickup, and bike share dock.",
            numFloors = 1,
            isAccessible = true,
            openHours = "24/7 (Buses run 6:00 AM – Midnight)",
            amenities = listOf("Shuttle Stop", "Bus Shelter", "Digital Arrival Boards", "Bike Share Dock", "Rideshare Zone", "Benches"),
            campusX = 760f,
            campusY = 840f,
            width = 140f,
            height = 80f,
            latitude = 37.4210,
            longitude = -122.1640,
            highlights = listOf("Line A Express to Caltrain", "Line B Medical Center Shuttle", "Bikeshare Bay 4")
        )
    )

    val defaultSavedLocations = listOf(
        SavedLocation(
            id = 1,
            name = "CS 106B Lecture Hall",
            buildingId = "b_sci",
            buildingName = "Gates Science Complex",
            floorNumber = 1,
            roomNumber = "Auditorium 101",
            category = LocationCategory.CLASSROOM,
            tagColorHex = "#3B82F6",
            notes = "Sit near center row with power outlets. Prof office hours next door.",
            campusX = 500f,
            campusY = 160f,
            latitude = 37.4300,
            longitude = -122.1700,
            isFavorite = true,
            collectionName = "My Classes"
        ),
        SavedLocation(
            id = 2,
            name = "Silent Study Corner (3rd Floor)",
            buildingId = "b_lib",
            buildingName = "Langdell Memorial Library",
            floorNumber = 3,
            roomNumber = "Alcove 340",
            category = LocationCategory.STUDY_SPOT,
            tagColorHex = "#10B981",
            notes = "Quiet corner with natural light, comfortable armchairs, and fast WiFi.",
            campusX = 555f,
            campusY = 350f,
            latitude = 37.4280,
            longitude = -122.1690,
            isFavorite = true,
            collectionName = "Study Nooks"
        ),
        SavedLocation(
            id = 3,
            name = "Coffee House Patio",
            buildingId = "b_stu",
            buildingName = "Tresidder Student Union",
            floorNumber = 1,
            roomNumber = "CoHo Patio",
            category = LocationCategory.FOOD_COFFEE,
            tagColorHex = "#F59E0B",
            notes = "Best iced matcha latte on campus. Live acoustic music on Thursdays.",
            campusX = 275f,
            campusY = 535f,
            latitude = 37.4255,
            longitude = -122.1740,
            isFavorite = true,
            collectionName = "Favorite Eats"
        ),
        SavedLocation(
            id = 4,
            name = "My Bike Rack (Science Hall East)",
            buildingId = "b_sci",
            buildingName = "Gates Science Complex",
            floorNumber = 1,
            roomNumber = "East Rack #12",
            category = LocationCategory.PARKING_BIKE,
            tagColorHex = "#64748B",
            notes = "Secured with U-Lock under the covered awning.",
            campusX = 580f,
            campusY = 160f,
            latitude = 37.4300,
            longitude = -122.1695,
            isFavorite = false,
            collectionName = "Parking & Transit"
        )
    )

    val floorPlans = listOf(
        // Gates Science Complex (b_sci)
        BuildingFloorPlan(
            buildingId = "b_sci",
            floorNumber = 1,
            floorName = "Floor 1 - Main Concourse & Auditorium",
            rooms = listOf(
                FloorRoom("sci_101", "Room 101 Turing Auditorium", RoomType.CLASSROOM, 1, 0.25f, 0.35f, true, 250),
                FloorRoom("sci_cafe", "Byte Cafe & Lounge", RoomType.CAFE, 1, 0.75f, 0.35f, true, 40),
                FloorRoom("sci_el1", "Central Elevator", RoomType.ELEVATOR, 1, 0.50f, 0.50f, true),
                FloorRoom("sci_wc1", "Main Restrooms", RoomType.RESTROOM, 1, 0.50f, 0.20f, true),
                FloorRoom("sci_st1", "Stairwell A", RoomType.STAIRS, 1, 0.10f, 0.50f, false)
            )
        ),
        BuildingFloorPlan(
            buildingId = "b_sci",
            floorNumber = 2,
            floorName = "Floor 2 - AI & Robotics Labs",
            rooms = listOf(
                FloorRoom("sci_201", "Room 201 Computer Vision Lab", RoomType.LAB, 2, 0.25f, 0.30f, true, 30),
                FloorRoom("sci_204", "Room 204 AI Robotics Studio", RoomType.LAB, 2, 0.70f, 0.30f, true, 35),
                FloorRoom("sci_210", "Collaborative Study Pods", RoomType.STUDY_AREA, 2, 0.45f, 0.75f, true, 20),
                FloorRoom("sci_el2", "Central Elevator", RoomType.ELEVATOR, 2, 0.50f, 0.50f, true),
                FloorRoom("sci_wc2", "Restrooms", RoomType.RESTROOM, 2, 0.50f, 0.20f, true)
            )
        ),
        BuildingFloorPlan(
            buildingId = "b_sci",
            floorNumber = 3,
            floorName = "Floor 3 - Faculty Offices & Cloud Center",
            rooms = listOf(
                FloorRoom("sci_301", "Faculty Office Suite", RoomType.OFFICE, 3, 0.25f, 0.40f, true),
                FloorRoom("sci_310", "Cloud Computing Center", RoomType.LAB, 3, 0.75f, 0.40f, true, 45),
                FloorRoom("sci_el3", "Central Elevator", RoomType.ELEVATOR, 3, 0.50f, 0.50f, true),
                FloorRoom("sci_wc3", "Restrooms", RoomType.RESTROOM, 3, 0.50f, 0.20f, true)
            )
        ),
        // Langdell Library (b_lib)
        BuildingFloorPlan(
            buildingId = "b_lib",
            floorNumber = 1,
            floorName = "Floor 1 - Information Commons & Circulation",
            rooms = listOf(
                FloorRoom("lib_circ", "Circulation & Reference Desk", RoomType.OFFICE, 1, 0.50f, 0.30f, true),
                FloorRoom("lib_print", "Printing & Tech Station", RoomType.STUDY_AREA, 1, 0.25f, 0.50f, true),
                FloorRoom("lib_cafe", "Library Books & Beans", RoomType.CAFE, 1, 0.75f, 0.50f, true),
                FloorRoom("lib_el1", "North Elevator", RoomType.ELEVATOR, 1, 0.45f, 0.70f, true),
                FloorRoom("lib_wc1", "Ground Restrooms", RoomType.RESTROOM, 1, 0.15f, 0.25f, true)
            )
        ),
        BuildingFloorPlan(
            buildingId = "b_lib",
            floorNumber = 2,
            floorName = "Floor 2 - Silent Study & Research Stacks",
            rooms = listOf(
                FloorRoom("lib_silent", "Silent Reading Hall", RoomType.STUDY_AREA, 2, 0.35f, 0.40f, true, 120),
                FloorRoom("lib_stacks", "Humanities & Law Stacks", RoomType.CLASSROOM, 2, 0.75f, 0.40f, true),
                FloorRoom("lib_el2", "North Elevator", RoomType.ELEVATOR, 2, 0.45f, 0.70f, true),
                FloorRoom("lib_wc2", "Restrooms", RoomType.RESTROOM, 2, 0.15f, 0.25f, true)
            )
        ),
        BuildingFloorPlan(
            buildingId = "b_lib",
            floorNumber = 3,
            floorName = "Floor 3 - Digital Media Commons & Group Rooms",
            rooms = listOf(
                FloorRoom("lib_grp1", "Group Study Rooms 301-308", RoomType.CLASSROOM, 3, 0.25f, 0.35f, true, 48),
                FloorRoom("lib_alcove", "Alcove 340 Study Spot", RoomType.STUDY_AREA, 3, 0.75f, 0.35f, true, 16),
                FloorRoom("lib_el3", "North Elevator", RoomType.ELEVATOR, 3, 0.45f, 0.70f, true),
                FloorRoom("lib_wc3", "Restrooms", RoomType.RESTROOM, 3, 0.15f, 0.25f, true)
            )
        ),
        // Student Union (b_stu)
        BuildingFloorPlan(
            buildingId = "b_stu",
            floorNumber = 1,
            floorName = "Level 1 - Dining, Market & Campus Store",
            rooms = listOf(
                FloorRoom("stu_store", "Campus Bookstore & Merch", RoomType.OFFICE, 1, 0.30f, 0.40f, true),
                FloorRoom("stu_coho", "Coffee House & Dining Terrace", RoomType.CAFE, 1, 0.70f, 0.40f, true, 80),
                FloorRoom("stu_el1", "Passenger Elevator", RoomType.ELEVATOR, 1, 0.50f, 0.65f, true),
                FloorRoom("stu_wc1", "Restrooms & Lockers", RoomType.RESTROOM, 1, 0.20f, 0.70f, true)
            )
        ),
        BuildingFloorPlan(
            buildingId = "b_stu",
            floorNumber = 2,
            floorName = "Level 2 - Student Clubs & Oak Ballroom",
            rooms = listOf(
                FloorRoom("stu_ballroom", "Oak Memorial Ballroom", RoomType.CLASSROOM, 2, 0.35f, 0.40f, true, 300),
                FloorRoom("stu_orgs", "Student Leadership Council", RoomType.OFFICE, 2, 0.75f, 0.40f, true),
                FloorRoom("stu_el2", "Passenger Elevator", RoomType.ELEVATOR, 2, 0.50f, 0.65f, true),
                FloorRoom("stu_wc2", "Restrooms", RoomType.RESTROOM, 2, 0.20f, 0.70f, true)
            )
        )
    )

    // Campus Pathway Nodes for Dijkstra Pathfinding
    // Points represent crossroads and entrances
    data class PathNode(val id: String, val x: Float, val y: Float, val label: String)
    data class PathEdge(val fromId: String, val toId: String, val hasStairs: Boolean = false)

    val pathNodes = listOf(
        PathNode("n_prk_n", 255f, 200f, "North Garage Exit"),
        PathNode("n_sci_ent", 500f, 210f, "Gates Science Entrance"),
        PathNode("n_phy_ent", 765f, 210f, "Physics Lab Entrance"),
        PathNode("n_cross_n", 500f, 250f, "North Quad Intersection"),
        PathNode("n_eng_ent", 285f, 375f, "Engineering Quad Entrance"),
        PathNode("n_lib_ent", 555f, 410f, "Library Front Plaza"),
        PathNode("n_res_ent", 790f, 385f, "Research Hub Entrance"),
        PathNode("n_west_walk", 285f, 440f, "West Walkway"),
        PathNode("n_quad_center", 550f, 440f, "Main Quad Memorial Fountain"),
        PathNode("n_east_walk", 790f, 440f, "East Walkway"),
        PathNode("n_stu_ent", 275f, 590f, "Student Union Plaza"),
        PathNode("n_quad_south", 550f, 600f, "South Quad Arch"),
        PathNode("n_din_ent", 795f, 580f, "Dining Commons Entrance"),
        PathNode("n_gym_ent", 260f, 810f, "Gym & Pool Plaza"),
        PathNode("n_dorm_ent", 510f, 810f, "Roble Village Courtyard"),
        PathNode("n_hlth_ent", 775f, 785f, "Health Center Entry"),
        PathNode("n_south_cross", 510f, 850f, "South Campus Boulevard"),
        PathNode("n_transit_ent", 830f, 920f, "Transit Hub Terminal")
    )

    val pathEdges = listOf(
        // North connections
        PathEdge("n_prk_n", "n_cross_n", false),
        PathEdge("n_sci_ent", "n_cross_n", false),
        PathEdge("n_phy_ent", "n_cross_n", false),
        // North to Library & Quad
        PathEdge("n_cross_n", "n_eng_ent", false),
        PathEdge("n_cross_n", "n_lib_ent", false),
        PathEdge("n_cross_n", "n_res_ent", false),
        // Central Library & Quad connections
        PathEdge("n_eng_ent", "n_west_walk", false),
        PathEdge("n_lib_ent", "n_quad_center", false),
        PathEdge("n_res_ent", "n_east_walk", false),
        PathEdge("n_west_walk", "n_quad_center", false),
        PathEdge("n_quad_center", "n_east_walk", false),
        // Quad to Student Union, Chapel, Dining
        PathEdge("n_west_walk", "n_stu_ent", false),
        PathEdge("n_quad_center", "n_quad_south", true), // Scenic stone steps through arcade
        PathEdge("n_quad_center", "n_stu_ent", false),   // Level paved ramp bypass
        PathEdge("n_quad_center", "n_din_ent", false),
        PathEdge("n_east_walk", "n_din_ent", false),
        // South connections to Housing, Gym, Health
        PathEdge("n_stu_ent", "n_gym_ent", false),
        PathEdge("n_quad_south", "n_dorm_ent", false),
        PathEdge("n_din_ent", "n_hlth_ent", false),
        PathEdge("n_gym_ent", "n_dorm_ent", false),
        PathEdge("n_dorm_ent", "n_south_cross", false),
        PathEdge("n_hlth_ent", "n_transit_ent", false),
        PathEdge("n_south_cross", "n_transit_ent", false)
    )
}
