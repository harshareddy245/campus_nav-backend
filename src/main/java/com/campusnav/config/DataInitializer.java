package com.campusnav.config;

import com.campusnav.entity.*;
import com.campusnav.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Seeds initial campus infrastructure, buildings, classrooms, labs,
 * facilities, pathway connections, and test user accounts on startup.
 */
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BuildingRepository buildingRepository;
    private final FacilityRepository facilityRepository;
    private final CampusLocationRepository locationRepository;
    private final RouteConnectionRepository connectionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           BuildingRepository buildingRepository,
                           FacilityRepository facilityRepository,
                           CampusLocationRepository locationRepository,
                           RouteConnectionRepository connectionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.buildingRepository = buildingRepository;
        this.facilityRepository = facilityRepository;
        this.locationRepository = locationRepository;
        this.connectionRepository = connectionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data load.");
            return;
        }

        log.info("Seeding initial CampusNav data...");

        // 1. Seed Users
        User admin = new User("admin", "admin@campusnav.edu", passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_FACULTY, Role.ROLE_STUDENT));
        admin.setProfile(new UserProfile("System Administrator", "ADM-001", "Campus IT Services", "+1-555-0100"));
        userRepository.save(admin);

        User student = new User("student", "student@campusnav.edu", passwordEncoder.encode("student123"));
        student.setRoles(Set.of(Role.ROLE_STUDENT));
        student.setProfile(new UserProfile("Alex Morgan", "22CS045", "Computer Science", "+1-555-0145"));
        userRepository.save(student);

        User faculty = new User("faculty", "faculty@campusnav.edu", passwordEncoder.encode("faculty123"));
        faculty.setRoles(Set.of(Role.ROLE_FACULTY));
        faculty.setProfile(new UserProfile("Dr. Robert Chen", "FAC-102", "Computer Science", "+1-555-0199"));
        userRepository.save(faculty);

        // 2. Seed Facilities
        Facility water = facilityRepository.save(new Facility("Drinking Water Purifier", "WATER", "Droplet"));
        Facility restroom = facilityRepository.save(new Facility("Restroom", "RESTROOM", "Bath"));
        Facility elevator = facilityRepository.save(new Facility("Elevator / Lift", "ACCESSIBILITY", "ArrowUpDown"));
        Facility ramp = facilityRepository.save(new Facility("Wheelchair Ramp", "ACCESSIBILITY", "Accessibility"));
        Facility wifi = facilityRepository.save(new Facility("High-Speed WiFi Zone", "SERVICES", "Wifi"));
        Facility cafe = facilityRepository.save(new Facility("Snack Kiosk", "FOOD", "Coffee"));
        Facility firstAid = facilityRepository.save(new Facility("First Aid Post", "SERVICES", "Cross"));
        Facility atm = facilityRepository.save(new Facility("Campus ATM", "SERVICES", "CreditCard"));

        // 3. Seed Buildings
        Building blkA = buildingRepository.save(new Building(
                "Block A - Computer Science & Engineering", "BLK-A",
                "Home to CSE, IT, software research labs and main lecture halls.", 4,
                240, 200, 160, 240
        ));

        Building blkB = buildingRepository.save(new Building(
                "Block B - Sciences & Robotics", "BLK-B",
                "Electronics, Physics, Robotics and IoT innovation labs.", 4,
                620, 140, 160, 220
        ));

        Building lib = buildingRepository.save(new Building(
                "Central Library & Resource Hub", "LIB-MAIN",
                "Four-tier academic repository, journals, digital catalog, quiet study.", 3,
                550, 360, 150, 180
        ));

        Building adminBlock = buildingRepository.save(new Building(
                "Administrative Headquarters", "ADM-HQ",
                "Principal's office, Admissions, Academic Registrar, Accounts.", 3,
                100, 320, 140, 180
        ));

        Building foodCourt = buildingRepository.save(new Building(
                "Campus Food Court & Dining", "CAFE-MAIN",
                "Central dining facility, coffee lounge, multi-cuisine food stalls.", 2,
                380, 440, 140, 120
        ));

        Building sac = buildingRepository.save(new Building(
                "Student Activity Center", "SAC",
                "Auditorium, indoor badminton court, gym, clubs and societies.", 2,
                380, 80, 150, 130
        ));

        // 4. Seed Campus Locations
        CampusLocation gate = new CampusLocation(
                "Main Campus Entrance", "GATE-01", "GATE", 0, 100, 560,
                "Main entry checkpoint with security booth and visitor register.",
                "24/7 Open", "Campus Security Post", true, adminBlock
        );
        gate.setFacilities(Set.of(ramp, atm));
        gate = locationRepository.save(gate);

        CampusLocation adminFoyer = new CampusLocation(
                "Administrative Foyer & Helpdesk", "ADM-101", "OFFICE", 0, 140, 420,
                "General visitor inquiries, fee counter, transcripts, and ID cards.",
                "8:30 AM - 5:00 PM", "Ms. Sarah Jenkins", true, adminBlock
        );
        adminFoyer.setFacilities(Set.of(water, restroom, ramp, wifi));
        adminFoyer = locationRepository.save(adminFoyer);

        CampusLocation deanOffice = new CampusLocation(
                "Dean of Academic Affairs", "ADM-201", "OFFICE", 1, 140, 340,
                "Office for academic curriculum, permissions, and scholarships.",
                "10:00 AM - 4:00 PM", "Prof. Eleanor Vance", true, adminBlock
        );
        deanOffice.setFacilities(Set.of(elevator, restroom));
        deanOffice = locationRepository.save(deanOffice);

        CampusLocation blkAEntrance = new CampusLocation(
                "Block A Main Foyer", "BLK-A-IN", "CLASSROOM", 0, 300, 410,
                "Central entrance for Computer Science and Information Technology.",
                "7:30 AM - 8:00 PM", "Block A Security Desk", true, blkA
        );
        blkAEntrance.setFacilities(Set.of(water, restroom, ramp, wifi));
        blkAEntrance = locationRepository.save(blkAEntrance);

        CampusLocation lh101 = new CampusLocation(
                "Lecture Hall 101", "LH-101", "CLASSROOM", 0, 270, 340,
                "Smart classroom with multimedia projector, 90 seating capacity.",
                "8:00 AM - 6:00 PM", "Dept Coordinator", true, blkA
        );
        lh101.setFacilities(Set.of(wifi));
        lh101 = locationRepository.save(lh101);

        CampusLocation javaLab = new CampusLocation(
                "Advanced Java & Cloud Computing Lab", "LAB-301", "LAB", 2, 270, 240,
                "High-performance workstations for Spring Boot, Docker, Java 17 and Cloud.",
                "8:30 AM - 6:30 PM", "Prof. Alan Vance", true, blkA
        );
        javaLab.setFacilities(Set.of(wifi, elevator, water));
        javaLab = locationRepository.save(javaLab);

        CampusLocation aiLab = new CampusLocation(
                "AI & Machine Learning Research Lab", "LAB-302", "LAB", 2, 350, 240,
                "Equipped with GPU clusters for deep learning, NLP, and computer vision.",
                "9:00 AM - 7:00 PM", "Dr. Priya Sharma", true, blkA
        );
        aiLab.setFacilities(Set.of(wifi, elevator));
        aiLab = locationRepository.save(aiLab);

        CampusLocation hodCse = new CampusLocation(
                "HOD Computer Science Office", "HOD-CSE", "OFFICE", 1, 350, 340,
                "Office of the Head of Department of Computer Science & Engineering.",
                "9:30 AM - 4:30 PM", "Dr. Robert Chen", true, blkA
        );
        hodCse.setFacilities(Set.of(wifi, water));
        hodCse = locationRepository.save(hodCse);

        CampusLocation centralCourtyard = new CampusLocation(
                "Central Academic Quadrangle", "COURT-01", "FACILITY", 0, 450, 340,
                "Open paved garden square connecting all major departmental buildings.",
                "Always Open", "Campus Facilities", true, foodCourt
        );
        centralCourtyard.setFacilities(Set.of(ramp, wifi));
        centralCourtyard = locationRepository.save(centralCourtyard);

        CampusLocation cafeteria = new CampusLocation(
                "Main Cafeteria & Food Court", "CAF-01", "CAFETERIA", 0, 450, 480,
                "Multi-counter food court serving breakfast, meals, coffee, and snacks.",
                "8:00 AM - 8:30 PM", "Food Court Manager", true, foodCourt
        );
        cafeteria.setFacilities(Set.of(water, restroom, cafe, ramp, wifi));
        cafeteria = locationRepository.save(cafeteria);

        CampusLocation libraryEntrance = new CampusLocation(
                "Central Library - Reading Hall", "LIB-G01", "LIBRARY", 0, 600, 420,
                "Main library check-in desk, reference books, print stations, and study carrels.",
                "8:00 AM - 9:00 PM", "Chief Librarian", true, lib
        );
        libraryEntrance.setFacilities(Set.of(water, restroom, ramp, wifi, elevator));
        libraryEntrance = locationRepository.save(libraryEntrance);

        CampusLocation digitalLibrary = new CampusLocation(
                "Digital E-Resource Center", "LIB-F01", "LIBRARY", 1, 600, 340,
                "Access to IEEE Xplore, ACM Digital Library, Springer, and research computers.",
                "8:30 AM - 8:00 PM", "Digital Services Desk", true, lib
        );
        digitalLibrary.setFacilities(Set.of(wifi, elevator));
        digitalLibrary = locationRepository.save(digitalLibrary);

        CampusLocation blkBEntrance = new CampusLocation(
                "Block B Robotics Foyer", "BLK-B-IN", "LAB", 0, 660, 240,
                "Ground floor lobby for Electronics, Mechatronics, and Robotics.",
                "8:00 AM - 7:00 PM", "Block B Security Desk", true, blkB
        );
        blkBEntrance.setFacilities(Set.of(water, restroom, ramp, elevator));
        blkBEntrance = locationRepository.save(blkBEntrance);

        CampusLocation iotLab = new CampusLocation(
                "IoT & Embedded Systems Lab", "LAB-IOT", "LAB", 1, 720, 200,
                "Development boards (ESP32, Raspberry Pi), sensor racks, and solder benches.",
                "9:00 AM - 6:00 PM", "Prof. Maya Lin", true, blkB
        );
        iotLab.setFacilities(Set.of(wifi, elevator));
        iotLab = locationRepository.save(iotLab);

        CampusLocation sacAuditorium = new CampusLocation(
                "University Grand Auditorium", "SAC-01", "AUDITORIUM", 0, 450, 130,
                "800-seat multi-purpose hall for symposiums, cultural fests, and guest lectures.",
                "9:00 AM - 6:00 PM", "SAC Events Office", true, sac
        );
        sacAuditorium.setFacilities(Set.of(water, restroom, ramp, wifi));
        sacAuditorium = locationRepository.save(sacAuditorium);

        CampusLocation clinic = new CampusLocation(
                "Campus Health Center & Pharmacy", "CLINIC-01", "FACILITY", 0, 160, 500,
                "Immediate medical care, first aid, nurse on duty, and ambulance dispatch.",
                "24/7 Service", "Dr. Marcus Reed", true, adminBlock
        );
        clinic.setFacilities(Set.of(firstAid, ramp, water, restroom));
        clinic = locationRepository.save(clinic);

        // 5. Seed Interconnected Route Connections (Edges with step directions)
        addBidirectional(gate, adminFoyer, 60, 45, "WALKWAY", true, "Walk straight north along the palm-lined main avenue towards Administration");
        addBidirectional(gate, clinic, 40, 30, "WALKWAY", true, "Turn slight right after main security booth to enter Campus Health Center");
        addBidirectional(clinic, adminFoyer, 50, 38, "WALKWAY", true, "Proceed north along covered pathway past the clinic lawn");

        addBidirectional(adminFoyer, deanOffice, 25, 20, "ELEVATOR", true, "Take the central elevator or stairs from ground floor to Floor 1");
        addBidirectional(adminFoyer, blkAEntrance, 90, 70, "WALKWAY", true, "Head east along Academic Boulevard towards Block A");
        addBidirectional(adminFoyer, cafeteria, 130, 100, "WALKWAY", true, "Follow south-east diagonal promenade towards Food Court");

        addBidirectional(blkAEntrance, lh101, 35, 25, "CORRIDOR", true, "Enter Block A ground floor and take the left corridor to LH-101");
        addBidirectional(blkAEntrance, hodCse, 45, 35, "CORRIDOR", true, "Take elevator or stairs to Floor 1, turn right to HOD CSE office");
        addBidirectional(lh101, javaLab, 40, 30, "ELEVATOR", true, "Take Block A North Elevator to Floor 2, room LAB-301 is opposite");
        addBidirectional(hodCse, javaLab, 30, 22, "CORRIDOR", true, "Take stairs to Floor 2 and turn left towards Advanced Java Lab");
        addBidirectional(javaLab, aiLab, 20, 15, "CORRIDOR", true, "Cross the 2nd floor research bridge connecting Java Lab to AI Lab");

        addBidirectional(blkAEntrance, centralCourtyard, 65, 50, "WALKWAY", true, "Exit Block A east doors into the Central Academic Quadrangle");
        addBidirectional(centralCourtyard, cafeteria, 70, 55, "WALKWAY", true, "Walk south across the garden paving into Main Cafeteria entrance");
        addBidirectional(centralCourtyard, libraryEntrance, 80, 60, "WALKWAY", true, "Head east towards the glass front facade of the Central Library");
        addBidirectional(centralCourtyard, sacAuditorium, 110, 85, "WALKWAY", true, "Walk north along central plaza towards University Auditorium");

        addBidirectional(libraryEntrance, digitalLibrary, 30, 25, "ELEVATOR", true, "Use Central Library glass elevator to access Digital Library on 1st Floor");
        addBidirectional(libraryEntrance, cafeteria, 85, 65, "WALKWAY", true, "Walk south-west along Library lawn straight into Cafeteria");

        addBidirectional(centralCourtyard, blkBEntrance, 120, 95, "WALKWAY", true, "Walk north-east towards the Robotics & Science Block B");
        addBidirectional(blkBEntrance, iotLab, 35, 25, "ELEVATOR", true, "Take Block B main lift to Floor 1, enter IoT & Embedded Systems Lab");
        addBidirectional(blkBEntrance, sacAuditorium, 95, 75, "WALKWAY", true, "Head north-west across recreation lane into Student Activity Center");
        addBidirectional(blkAEntrance, sacAuditorium, 130, 100, "WALKWAY", true, "Walk north along Block A perimeter path towards Auditorium");

        log.info("CampusNav initialization complete! Seeded 6 buildings, 16 locations, 20 pathways, and 3 user accounts.");
    }

    private void addBidirectional(CampusLocation a, CampusLocation b, double distance, int seconds,
                                  String type, boolean accessible, String instruction) {
        connectionRepository.save(new RouteConnection(a, b, distance, seconds, type, accessible, instruction));
        connectionRepository.save(new RouteConnection(b, a, distance, seconds, type, accessible,
                "Return journey: follow " + type.toLowerCase() + " back from " + b.getName() + " to " + a.getName()));
    }
}
