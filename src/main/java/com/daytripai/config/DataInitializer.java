package com.daytripai.config;

import com.daytripai.entity.*;
import com.daytripai.enums.DestinationCategory;
import com.daytripai.enums.ExpenseCategory;
import com.daytripai.enums.RoleType;
import com.daytripai.enums.TripStatus;
import com.daytripai.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Database Initialization & Seed Data Runner.
 * Automatically populates sample users, diverse tourist destinations,
 * and a sample trip upon first startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;
    private final TripRepository tripRepository;
    private final ItineraryRepository itineraryRepository;
    private final ExpenseRepository expenseRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DestinationRepository destinationRepository,
                           TripRepository tripRepository,
                           ItineraryRepository itineraryRepository,
                           ExpenseRepository expenseRepository,
                           FavoriteRepository favoriteRepository,
                           ReviewRepository reviewRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.destinationRepository = destinationRepository;
        this.tripRepository = tripRepository;
        this.itineraryRepository = itineraryRepository;
        this.expenseRepository = expenseRepository;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking database initialization status...");

        // 1. Seed Users
        User travelerUser = seedUsers();

        // 2. Seed Destinations
        List<Destination> seededDestinations = seedDestinations();

        // 3. Seed Sample Trip & Expenses
        seedSampleTrip(travelerUser, seededDestinations);

        log.info("Database initialization completed successfully!");
    }

    private User seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already exist in database. Skipping user seeding.");
            return userRepository.findByEmail("traveler@daytripai.com").orElse(null);
        }

        log.info("Seeding default application users...");
        User admin = new User(
                "admin@daytripai.com",
                passwordEncoder.encode("Admin@123"),
                "System Administrator",
                RoleType.ROLE_ADMIN
        );
        admin.setPhone("+91 9876543210");
        userRepository.save(admin);

        User traveler = new User(
                "traveler@daytripai.com",
                passwordEncoder.encode("Traveler@123"),
                "Alex Traveler",
                RoleType.ROLE_USER
        );
        traveler.setPhone("+91 9123456780");
        userRepository.save(traveler);

        log.info("Seeded 2 users: admin@daytripai.com and traveler@daytripai.com");
        return traveler;
    }

    private List<Destination> seedDestinations() {
        if (destinationRepository.count() >= 60) {
            log.info("Destinations already fully seeded ({} records). Skipping destination seeding.", destinationRepository.count());
            return destinationRepository.findAll();
        }

        log.info("Seeding curated tourist destinations across all major Indian cities...");

        List<Destination> destinations = Arrays.asList(
                // 1. Hyderabad (Telangana)
                new Destination(
                        "Charminar & Laad Bazaar",
                        "The iconic 16th-century four-minaret monument surrounded by Hyderabad's bustling traditional bangle and perfume bazaars.",
                        "Hyderabad", "Telangana", DestinationCategory.HERITAGE,
                        50.0, LocalTime.of(9, 0), LocalTime.of(17, 30), 1.5,
                        17.3616, 78.4747, 4.7, 1240,
                        "https://images.unsplash.com/photo-1572445271230-a78b5944a659?auto=format&fit=crop&w=1000&q=80",
                        "Char Kaman, Ghansi Bazaar, Hyderabad", true
                ),
                new Destination(
                        "Golconda Fort",
                        "Massive medieval hilltop fortress famed for its acoustic marvels, diamond trade vaults, and panoramic sunset views.",
                        "Hyderabad", "Telangana", DestinationCategory.HERITAGE,
                        80.0, LocalTime.of(9, 0), LocalTime.of(18, 0), 2.5,
                        17.3833, 78.4011, 4.8, 2150,
                        "https://images.unsplash.com/photo-1605649487212-47bdab064df7?auto=format&fit=crop&w=1000&q=80",
                        "Ibrahim Bagh, Hyderabad", true
                ),
                new Destination(
                        "Salar Jung Museum",
                        "One of the largest individual art collections in the world, featuring the famous 19th-century musical clock and Veiled Rebecca.",
                        "Hyderabad", "Telangana", DestinationCategory.ART_CULTURE,
                        100.0, LocalTime.of(10, 0), LocalTime.of(17, 0), 2.0,
                        17.3713, 78.4804, 4.6, 980,
                        "https://images.unsplash.com/photo-1566127444979-b3d2b654e3d7?auto=format&fit=crop&w=1000&q=80",
                        "Darulshifa, Hyderabad", false
                ),
                new Destination(
                        "Hussain Sagar Lake & Buddha Statue",
                        "Scenic urban lake featuring a monolithic 18-meter granite Buddha statue accessed via speedboats and catamarans.",
                        "Hyderabad", "Telangana", DestinationCategory.NATURE,
                        150.0, LocalTime.of(8, 0), LocalTime.of(21, 0), 1.5,
                        17.4239, 78.4738, 4.5, 1420,
                        "https://images.unsplash.com/photo-1626014303757-6467329b35ca?auto=format&fit=crop&w=1000&q=80",
                        "Tank Bund Road, Hyderabad", true
                ),
                new Destination(
                        "Ramoji Film City",
                        "World's largest integrated film studio complex offering film sets, stunt shows, theme parks, and studio tram tours.",
                        "Hyderabad", "Telangana", DestinationCategory.ENTERTAINMENT,
                        1350.0, LocalTime.of(9, 0), LocalTime.of(17, 30), 6.0,
                        17.2543, 78.6808, 4.7, 3800,
                        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1000&q=80",
                        "Anaspur Village, Hayathnagar, Hyderabad", true
                ),
                new Destination(
                        "Birla Mandir & Naubat Pahad",
                        "Magnificent white Rajasthani marble temple perched atop a 280-foot hill offering panoramic vistas over the twin cities.",
                        "Hyderabad", "Telangana", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(7, 0), LocalTime.of(21, 0), 1.5,
                        17.4062, 78.4691, 4.7, 2100,
                        "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=1000&q=80",
                        "Hill Fort Rd, Ambedkar Colony, Khairtabad, Hyderabad", true
                ),

                // 2. Mumbai (Maharashtra)
                new Destination(
                        "Gateway of India",
                        "Majestic 20th-century basalt arch monument overlooking the Arabian Sea, standing opposite the historic Taj Mahal Palace hotel.",
                        "Mumbai", "Maharashtra", DestinationCategory.HERITAGE,
                        0.0, LocalTime.of(6, 0), LocalTime.of(22, 0), 1.0,
                        18.9220, 72.8347, 4.8, 4500,
                        "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1000&q=80",
                        "Apollo Bandar, Colaba, Mumbai", true
                ),
                new Destination(
                        "Marine Drive (Queen's Necklace)",
                        "Sweeping 3.6-kilometer seaside promenade offering spectacular sunset views and Arabian Sea breeze.",
                        "Mumbai", "Maharashtra", DestinationCategory.NATURE,
                        0.0, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.5,
                        18.9432, 72.8230, 4.9, 5200,
                        "https://images.unsplash.com/photo-1567157577867-05ccb1388e66?auto=format&fit=crop&w=1000&q=80",
                        "Netaji Subhash Chandra Bose Road, Mumbai", true
                ),
                new Destination(
                        "Elephanta Caves",
                        "UNESCO World Heritage rock-cut cave temples dedicated to Lord Shiva situated on an island in Mumbai Harbour.",
                        "Mumbai", "Maharashtra", DestinationCategory.HERITAGE,
                        260.0, LocalTime.of(9, 0), LocalTime.of(17, 30), 3.5,
                        18.9633, 72.9315, 4.6, 1890,
                        "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=1000&q=80",
                        "Gharapuri Island, Mumbai", false
                ),
                new Destination(
                        "Chhatrapati Shivaji Maharaj Terminus (CSMT)",
                        "UNESCO World Heritage Victorian Gothic Revival railway terminus showcasing magnificent stained glass and domes.",
                        "Mumbai", "Maharashtra", DestinationCategory.HERITAGE,
                        0.0, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.0,
                        18.9398, 72.8355, 4.8, 3100,
                        "https://images.unsplash.com/photo-1566552881560-0be862a7c445?auto=format&fit=crop&w=1000&q=80",
                        "Fort, Mumbai, Maharashtra", true
                ),
                new Destination(
                        "Siddhivinayak Temple",
                        "Prominent two-century-old temple dedicated to Lord Ganesha, revered by pilgrims and travelers across India.",
                        "Mumbai", "Maharashtra", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(5, 30), LocalTime.of(22, 0), 1.5,
                        19.0166, 72.8304, 4.8, 4100,
                        "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=1000&q=80",
                        "SK Bole Marg, Prabhadevi, Mumbai", true
                ),
                new Destination(
                        "Sanjay Gandhi National Park & Kanheri Caves",
                        "Vast protected national park within city limits featuring 109 ancient rock-cut Buddhist caves and mini-safari.",
                        "Mumbai", "Maharashtra", DestinationCategory.NATURE,
                        150.0, LocalTime.of(7, 30), LocalTime.of(18, 0), 3.5,
                        19.2147, 72.9106, 4.6, 1950,
                        "https://images.unsplash.com/photo-1511497584788-87676104235f?auto=format&fit=crop&w=1000&q=80",
                        "Western Express Hwy, Borivali East, Mumbai", false
                ),

                // 3. Delhi / New Delhi
                new Destination(
                        "Qutb Minar & Mehrauli Archaeological Park",
                        "Soaring 73-meter victory tower built in 1193, surrounded by ancient ruins, tombs, and the rust-resistant Iron Pillar.",
                        "Delhi", "Delhi", DestinationCategory.HERITAGE,
                        80.0, LocalTime.of(7, 0), LocalTime.of(18, 0), 2.0,
                        28.5245, 77.1855, 4.7, 3200,
                        "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1000&q=80",
                        "Seth Sarai, Mehrauli, New Delhi", true
                ),
                new Destination(
                        "Red Fort (Lal Qila)",
                        "UNESCO World Heritage fortified palace of red sandstone that served as the main residence of the Mughal Emperors.",
                        "Delhi", "Delhi", DestinationCategory.HERITAGE,
                        90.0, LocalTime.of(9, 30), LocalTime.of(16, 30), 2.5,
                        28.6562, 77.2410, 4.6, 4100,
                        "https://images.unsplash.com/photo-1598324789736-4861f89564a0?auto=format&fit=crop&w=1000&q=80",
                        "Netaji Subhash Marg, Chandni Chowk, New Delhi", true
                ),
                new Destination(
                        "Lotus Temple (Bahai House of Worship)",
                        "Stunning white marble structure designed in the shape of a blooming lotus flower, open to people of all faiths for silent meditation.",
                        "Delhi", "Delhi", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(9, 0), LocalTime.of(17, 30), 1.5,
                        28.5535, 77.2588, 4.7, 2800,
                        "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1000&q=80",
                        "Kalkaji, New Delhi", false
                ),
                new Destination(
                        "India Gate & Kartavya Path",
                        "Imposing 42-meter triumphal war memorial archway situated along New Delhi's ceremonial boulevard.",
                        "Delhi", "Delhi", DestinationCategory.HERITAGE,
                        0.0, LocalTime.of(0, 0), LocalTime.of(23, 59), 1.5,
                        28.6129, 77.2295, 4.8, 5400,
                        "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?auto=format&fit=crop&w=1000&q=80",
                        "Kartavya Path, India Gate, New Delhi", true
                ),
                new Destination(
                        "Humayun's Tomb",
                        "UNESCO World Heritage 16th-century Mughal garden tomb that inspired the architectural design of the Taj Mahal.",
                        "Delhi", "Delhi", DestinationCategory.HERITAGE,
                        80.0, LocalTime.of(6, 0), LocalTime.of(18, 0), 2.0,
                        28.5933, 77.2507, 4.8, 2900,
                        "https://images.unsplash.com/photo-1608958435020-e8a7109ba809?auto=format&fit=crop&w=1000&q=80",
                        "Mathura Rd, Nizamuddin East, New Delhi", true
                ),
                new Destination(
                        "Swaminarayan Akshardham Temple",
                        "Sprawling spiritual-cultural campus displaying millennia of traditional Indian art, wisdom, boat ride, and musical fountain.",
                        "Delhi", "Delhi", DestinationCategory.RELIGIOUS,
                        250.0, LocalTime.of(10, 0), LocalTime.of(20, 0), 3.5,
                        28.6127, 77.2773, 4.9, 4300,
                        "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=1000&q=80",
                        "Noida Mor, Pandav Nagar, New Delhi", true
                ),

                // 4. Jaipur (Rajasthan)
                new Destination(
                        "Amber Palace (Amer Fort)",
                        "Opulent hilltop fortress known for its artistic Hindu elements, grand courtyards, and the glittering Sheesh Mahal mirror palace.",
                        "Jaipur", "Rajasthan", DestinationCategory.HERITAGE,
                        100.0, LocalTime.of(8, 0), LocalTime.of(17, 30), 3.0,
                        26.9855, 75.8513, 4.8, 3600,
                        "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1000&q=80",
                        "Devisinghpura, Amer, Jaipur", true
                ),
                new Destination(
                        "Hawa Mahal (Palace of Winds)",
                        "Five-story pink sandstone palace with 953 intricately carved jharokhas designed for royal ladies to observe street festivals.",
                        "Jaipur", "Rajasthan", DestinationCategory.HERITAGE,
                        50.0, LocalTime.of(9, 0), LocalTime.of(17, 0), 1.0,
                        26.9239, 75.8267, 4.6, 2950,
                        "https://images.unsplash.com/photo-1609137144820-74191baeb38b?auto=format&fit=crop&w=1000&q=80",
                        "Hawa Mahal Rd, Badi Choupad, Jaipur", true
                ),
                new Destination(
                        "City Palace Jaipur",
                        "Magnificent royal residence blending Rajput, Mughal, and European architecture with grand courtyards and royal museums.",
                        "Jaipur", "Rajasthan", DestinationCategory.HERITAGE,
                        200.0, LocalTime.of(9, 30), LocalTime.of(17, 0), 2.5,
                        26.9258, 75.8236, 4.7, 2400,
                        "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1000&q=80",
                        "Tulsi Marg, Gangori Bazaar, J.D.A. Market, Jaipur", true
                ),
                new Destination(
                        "Jantar Mantar Observatory",
                        "UNESCO World Heritage 18th-century astronomical observation site containing the world's largest stone sundial.",
                        "Jaipur", "Rajasthan", DestinationCategory.HERITAGE,
                        80.0, LocalTime.of(9, 0), LocalTime.of(17, 0), 1.5,
                        26.9248, 75.8246, 4.6, 1900,
                        "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?auto=format&fit=crop&w=1000&q=80",
                        "Gangori Bazaar, J.D.A. Market, Jaipur", false
                ),
                new Destination(
                        "Nahargarh Fort & Sunset Point",
                        "Rugged fortress standing on the edge of the Aravalli Hills overlooking the breathtaking expanse of the Pink City.",
                        "Jaipur", "Rajasthan", DestinationCategory.HERITAGE,
                        100.0, LocalTime.of(10, 0), LocalTime.of(18, 0), 2.5,
                        26.9378, 75.8155, 4.7, 2200,
                        "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1000&q=80",
                        "Krishna Nagar, Brahampuri, Jaipur", true
                ),

                // 5. Agra (Uttar Pradesh)
                new Destination(
                        "Taj Mahal",
                        "World-renowned ivory-white marble mausoleum on the right bank of the river Yamuna, an eternal symbol of architectural majesty.",
                        "Agra", "Uttar Pradesh", DestinationCategory.HERITAGE,
                        250.0, LocalTime.of(6, 0), LocalTime.of(18, 30), 3.0,
                        27.1751, 78.0421, 4.9, 9800,
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1000&q=80",
                        "Dharmapuri, Forest Colony, Tajganj, Agra", true
                ),
                new Destination(
                        "Agra Fort",
                        "UNESCO World Heritage massive 16th-century red sandstone fortress that served as the primary citadel of the Mughal Empire.",
                        "Agra", "Uttar Pradesh", DestinationCategory.HERITAGE,
                        100.0, LocalTime.of(6, 0), LocalTime.of(18, 0), 2.0,
                        27.1795, 78.0211, 4.7, 3400,
                        "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?auto=format&fit=crop&w=1000&q=80",
                        "Agra Fort, Rakabganj, Agra", true
                ),
                new Destination(
                        "Fatehpur Sikri",
                        "UNESCO World Heritage fortified ghost city built by Emperor Akbar featuring Buland Darwaza and Salim Chishti tomb.",
                        "Agra", "Uttar Pradesh", DestinationCategory.HERITAGE,
                        80.0, LocalTime.of(7, 0), LocalTime.of(18, 0), 3.0,
                        27.0945, 77.6679, 4.6, 2100,
                        "https://images.unsplash.com/photo-1590050752117-238cb0fb12b1?auto=format&fit=crop&w=1000&q=80",
                        "Fatehpur Sikri, Uttar Pradesh", false
                ),

                // 6. Bengaluru (Karnataka)
                new Destination(
                        "Lalbagh Botanical Garden",
                        "Historic 240-acre botanical haven commissioning century-old trees, a famous London Crystal Palace replica glasshouse, and a lake.",
                        "Bengaluru", "Karnataka", DestinationCategory.NATURE,
                        30.0, LocalTime.of(6, 0), LocalTime.of(19, 0), 2.0,
                        12.9507, 77.5848, 4.6, 2400,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "Mavalli, Bengaluru", true
                ),
                new Destination(
                        "Bangalore Palace",
                        "Tudor-style royal estate reminiscent of Windsor Castle, complete with fortified towers, battlements, and ornate wooden carvings.",
                        "Bengaluru", "Karnataka", DestinationCategory.HERITAGE,
                        250.0, LocalTime.of(10, 0), LocalTime.of(17, 30), 2.0,
                        12.9988, 77.5921, 4.5, 2100,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "Vasanth Nagar, Bengaluru", true
                ),
                new Destination(
                        "Cubbon Park & Vidhana Soudha",
                        "Sprawling green lung in the heart of Bangalore alongside Karnataka's monumental Neo-Dravidian legislative assembly.",
                        "Bengaluru", "Karnataka", DestinationCategory.NATURE,
                        0.0, LocalTime.of(6, 0), LocalTime.of(19, 0), 2.0,
                        12.9763, 77.5929, 4.7, 3100,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "Kasturba Road, Sampangi Rama Nagara, Bengaluru", true
                ),
                new Destination(
                        "ISKCON Temple Bangalore",
                        "Grand neo-classical Dravidian temple complex on Hare Krishna Hill featuring intricate gold-plated flagstaff and spiritual gardens.",
                        "Bengaluru", "Karnataka", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(7, 15), LocalTime.of(20, 30), 2.0,
                        13.0098, 77.5511, 4.8, 3800,
                        "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=1000&q=80",
                        "Hare Krishna Hill, Rajajinagar, Bengaluru", true
                ),
                new Destination(
                        "Bannerghatta Biological Park & Safari",
                        "Extensive wildlife preserve offering tiger and lion safaris, butterfly conservatory, and rescue zoological park.",
                        "Bengaluru", "Karnataka", DestinationCategory.ADVENTURE,
                        350.0, LocalTime.of(9, 30), LocalTime.of(17, 0), 4.0,
                        12.8009, 77.5777, 4.5, 2600,
                        "https://images.unsplash.com/photo-1511497584788-87676104235f?auto=format&fit=crop&w=1000&q=80",
                        "Bannerghatta Road, Bengaluru", false
                ),

                // 7. Goa
                new Destination(
                        "Fort Aguada & Lighthouse",
                        "Well-preserved 17th-century Portuguese fortress and lighthouse guarding the mouth of the Mandovi River with Arabian Sea panoramas.",
                        "Goa", "Goa", DestinationCategory.HERITAGE,
                        50.0, LocalTime.of(9, 0), LocalTime.of(18, 0), 2.0,
                        15.4925, 73.7735, 4.7, 3200,
                        "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1000&q=80",
                        "Sinquerim Beach, Candolim, Goa", true
                ),
                new Destination(
                        "Basilica of Bom Jesus",
                        "UNESCO World Heritage baroque church in Old Goa holding the mortal sacred relics of St. Francis Xavier.",
                        "Goa", "Goa", DestinationCategory.HERITAGE,
                        0.0, LocalTime.of(9, 0), LocalTime.of(18, 30), 1.5,
                        15.5009, 73.9116, 4.8, 3900,
                        "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1000&q=80",
                        "Old Goa Road, Bainguinim, Goa", true
                ),
                new Destination(
                        "Baga Beach & Water Sports",
                        "Vibrant golden beach renowned for its lively shacks, parasailing, jet ski water adventures, and sunset views.",
                        "Goa", "Goa", DestinationCategory.ADVENTURE,
                        800.0, LocalTime.of(6, 0), LocalTime.of(22, 0), 3.0,
                        15.5553, 73.7516, 4.6, 4600,
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1000&q=80",
                        "Baga Beach, Calangute, Goa", true
                ),
                new Destination(
                        "Dudhsagar Waterfalls",
                        "Spectacular four-tiered waterfall cascading 310 meters down Western Ghats mountain cliffs surrounded by deep green jungle.",
                        "Goa", "Goa", DestinationCategory.NATURE,
                        500.0, LocalTime.of(7, 0), LocalTime.of(17, 0), 4.5,
                        15.3144, 74.3143, 4.8, 2800,
                        "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?auto=format&fit=crop&w=1000&q=80",
                        "Sonaulim, Bhagwan Mahaveer Sanctuary, Goa", true
                ),
                new Destination(
                        "Chapora Fort & Vagator Beach",
                        "Famous cliff-top red laterite fortress commanding dramatic coastal vistas made iconic by Bollywood cinema.",
                        "Goa", "Goa", DestinationCategory.HERITAGE,
                        0.0, LocalTime.of(8, 0), LocalTime.of(18, 30), 2.0,
                        15.6059, 73.7388, 4.6, 2300,
                        "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1000&q=80",
                        "Chapora, Vagator, Goa", false
                ),

                // 8. Chennai (Tamil Nadu)
                new Destination(
                        "Marina Beach & Promenade",
                        "World's second longest natural urban beach featuring sunrise walks, historic memorials, and lighthouse ocean views.",
                        "Chennai", "Tamil Nadu", DestinationCategory.NATURE,
                        20.0, LocalTime.of(5, 0), LocalTime.of(22, 0), 2.0,
                        13.0500, 80.2824, 4.6, 4100,
                        "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1000&q=80",
                        "Kamarajar Salai, Marina Beach, Chennai", true
                ),
                new Destination(
                        "Kapaleeshwarar Temple Mylapore",
                        "7th-century architectural marvel of Dravidian style dedicated to Lord Shiva with an ornate towering rainbow gopuram.",
                        "Chennai", "Tamil Nadu", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(6, 0), LocalTime.of(21, 0), 1.5,
                        13.0336, 80.2699, 4.8, 3300,
                        "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=1000&q=80",
                        "Vadakkumadaveedhi, Mylapore, Chennai", true
                ),
                new Destination(
                        "Fort St. George & Museum",
                        "First English fortress in India established in 1644, containing historic relics, weaponry, and St. Mary's Church.",
                        "Chennai", "Tamil Nadu", DestinationCategory.HERITAGE,
                        50.0, LocalTime.of(9, 0), LocalTime.of(17, 0), 2.0,
                        13.0797, 80.2872, 4.5, 1600,
                        "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1000&q=80",
                        "Rajaji Salai, Fort St George, Chennai", false
                ),
                new Destination(
                        "San Thome Cathedral Basilica",
                        "Neo-Gothic Roman Catholic minor basilica built over the tomb of Saint Thomas the Apostle.",
                        "Chennai", "Tamil Nadu", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(6, 0), LocalTime.of(20, 0), 1.0,
                        13.0335, 80.2782, 4.7, 2100,
                        "https://images.unsplash.com/photo-1548013146-72479768bbaa?auto=format&fit=crop&w=1000&q=80",
                        "Santhome High Rd, Mylapore, Chennai", true
                ),

                // 9. Kolkata (West Bengal)
                new Destination(
                        "Victoria Memorial Hall",
                        "Grandiose white Makrana marble monument honoring Queen Victoria surrounded by 64 acres of landscaped royal gardens.",
                        "Kolkata", "West Bengal", DestinationCategory.HERITAGE,
                        50.0, LocalTime.of(10, 0), LocalTime.of(18, 0), 2.5,
                        22.5448, 88.3426, 4.8, 4800,
                        "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1000&q=80",
                        "Victoria Memorial, Queens Way, Maidan, Kolkata", true
                ),
                new Destination(
                        "Howrah Bridge & Prinsep Ghat",
                        "Legendary cantilever bridge suspended across the Hooghly River alongside romantic Palladian riverfront ghats.",
                        "Kolkata", "West Bengal", DestinationCategory.HERITAGE,
                        100.0, LocalTime.of(6, 0), LocalTime.of(21, 0), 2.0,
                        22.5851, 88.3468, 4.7, 3900,
                        "https://images.unsplash.com/photo-1558431382-27e303142255?auto=format&fit=crop&w=1000&q=80",
                        "Strand Rd, Vidyasagar Setu, Kolkata", true
                ),
                new Destination(
                        "Dakshineswar Kali Temple",
                        "Navaratna-style Hindu temple on the eastern bank of Hooghly River associated with the mystical teachings of Ramakrishna.",
                        "Kolkata", "West Bengal", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(6, 0), LocalTime.of(20, 0), 2.0,
                        22.6548, 88.3576, 4.8, 4200,
                        "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=1000&q=80",
                        "Mayapuri, Dakshineswar, Kolkata", true
                ),
                new Destination(
                        "Indian Museum Kolkata",
                        "Ninth oldest museum in the world, preserving ancient Egyptian mummies, Ashoka lion capitals, and fossil galleries.",
                        "Kolkata", "West Bengal", DestinationCategory.ART_CULTURE,
                        50.0, LocalTime.of(10, 0), LocalTime.of(17, 0), 2.5,
                        22.5579, 88.3511, 4.6, 2200,
                        "https://images.unsplash.com/photo-1566127444979-b3d2b654e3d7?auto=format&fit=crop&w=1000&q=80",
                        "27, Jawaharlal Nehru Rd, Colootola, New Market Area, Kolkata", false
                ),

                // 10. Varanasi (Uttar Pradesh)
                new Destination(
                        "Dashashwamedh Ghat & Ganga Aarti",
                        "The primary sacred riverfront ghat on the holy Ganges hosting world-famous choreographed evening brass lamp Aarti ceremonies.",
                        "Varanasi", "Uttar Pradesh", DestinationCategory.RELIGIOUS,
                        200.0, LocalTime.of(5, 0), LocalTime.of(21, 30), 2.5,
                        25.3059, 83.0104, 4.9, 6200,
                        "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1000&q=80",
                        "Dashashwamedh Ghat Rd, Bangali Tola, Varanasi", true
                ),
                new Destination(
                        "Kashi Vishwanath Temple",
                        "One of the 12 sacred Jyotirlingas of Lord Shiva featuring golden spires and the newly inaugurated grand river corridor.",
                        "Varanasi", "Uttar Pradesh", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(4, 0), LocalTime.of(23, 0), 2.0,
                        25.3109, 83.0107, 4.9, 5800,
                        "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1000&q=80",
                        "Lahori Tola, Varanasi, Uttar Pradesh", true
                ),
                new Destination(
                        "Sarnath Archaeological Site & Dhamek Stupa",
                        "Sacred deer park where Gautama Buddha first preached the Dharma, home to the massive cylindrical Dhamek Stupa.",
                        "Varanasi", "Uttar Pradesh", DestinationCategory.HERITAGE,
                        40.0, LocalTime.of(8, 0), LocalTime.of(18, 0), 2.5,
                        25.3811, 83.0227, 4.7, 2700,
                        "https://images.unsplash.com/photo-1587474260584-136574528ed5?auto=format&fit=crop&w=1000&q=80",
                        "Sarnath, Varanasi, Uttar Pradesh", true
                ),
                new Destination(
                        "Assi Ghat & Subah-e-Banaras",
                        "Southernmost spiritual ghat famous for dawn yoga, morning classical music ragas, and spiritual tranquility.",
                        "Varanasi", "Uttar Pradesh", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(5, 0), LocalTime.of(22, 0), 1.5,
                        25.2899, 83.0063, 4.7, 2400,
                        "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1000&q=80",
                        "Assi Ghat, Anandbagh, Bhelupur, Varanasi", false
                ),

                // 11. Kochi (Kerala)
                new Destination(
                        "Fort Kochi & Chinese Fishing Nets",
                        "Charming coastal quarter adorned with massive shore-operated cantilevered Chinese fishing nets and colonial cottages.",
                        "Kochi", "Kerala", DestinationCategory.HERITAGE,
                        50.0, LocalTime.of(6, 0), LocalTime.of(20, 0), 2.0,
                        9.9658, 76.2421, 4.7, 3400,
                        "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1000&q=80",
                        "River Rd, Fort Kochi, Kochi", true
                ),
                new Destination(
                        "Mattancherry Palace (Dutch Palace)",
                        "Portuguese-built palace renowned for exquisite mythological Hindu murals depicting Ramayana scenes and royal palanquins.",
                        "Kochi", "Kerala", DestinationCategory.HERITAGE,
                        30.0, LocalTime.of(10, 0), LocalTime.of(17, 0), 1.5,
                        9.9583, 76.2592, 4.6, 2100,
                        "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1000&q=80",
                        "Mattancherry, Kochi, Kerala", true
                ),
                new Destination(
                        "Paradesi Synagogue & Jew Town",
                        "Oldest active synagogue in the Commonwealth, featuring hand-painted 18th-century Chinese porcelain floor tiles and antique markets.",
                        "Kochi", "Kerala", DestinationCategory.HERITAGE,
                        20.0, LocalTime.of(10, 0), LocalTime.of(18, 0), 1.5,
                        9.9576, 76.2597, 4.6, 1850,
                        "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1000&q=80",
                        "Synagogue Ln, Jew Town, Kappalandimukku, Mattancherry, Kochi", false
                ),
                new Destination(
                        "Cherai Beach & Backwaters",
                        "Picturesque 10-km palm-fringed coastal paradise where Arabian sea waves meet serene coconut-dotted backwaters.",
                        "Kochi", "Kerala", DestinationCategory.NATURE,
                        0.0, LocalTime.of(6, 0), LocalTime.of(19, 0), 2.5,
                        10.1416, 76.1783, 4.7, 2900,
                        "https://images.unsplash.com/photo-1602216056096-3b40cc0c9944?auto=format&fit=crop&w=1000&q=80",
                        "Cherai Beach, Vypin Island, Kochi", true
                ),

                // 12. Amritsar (Punjab)
                new Destination(
                        "Golden Temple (Harmandir Sahib)",
                        "Holiest Gurdwara of Sikhism covered with pure gold foil, surrounded by the sacred Amrit Sarovar and world's largest free community langar.",
                        "Amritsar", "Punjab", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(4, 0), LocalTime.of(23, 0), 3.0,
                        31.6200, 74.8765, 4.9, 8500,
                        "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1000&q=80",
                        "Golden Temple Rd, Atta Mandi, Amritsar", true
                ),
                new Destination(
                        "Jallianwala Bagh Memorial",
                        "Sobering national historic garden memorial with preserved bullet marks and the eternal flame commemorating the 1919 martyrs.",
                        "Amritsar", "Punjab", DestinationCategory.HERITAGE,
                        0.0, LocalTime.of(6, 30), LocalTime.of(19, 30), 1.5,
                        31.6206, 74.8801, 4.7, 3400,
                        "https://images.unsplash.com/photo-1598324789736-4861f89564a0?auto=format&fit=crop&w=1000&q=80",
                        "Golden Temple Rd, Jallan Wala Bagh, Amritsar", true
                ),
                new Destination(
                        "Wagah Border Beating Retreat Ceremony",
                        "Electrifying patriotic military drill and sunset flag-lowering ceremony conducted daily by Indian BSF and Pakistan Rangers.",
                        "Amritsar", "Punjab", DestinationCategory.HERITAGE,
                        150.0, LocalTime.of(15, 30), LocalTime.of(18, 30), 3.0,
                        31.6046, 74.5724, 4.8, 5100,
                        "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?auto=format&fit=crop&w=1000&q=80",
                        "Attari-Wagah Border, Amritsar", true
                ),

                // 13. Udaipur (Rajasthan)
                new Destination(
                        "City Palace Udaipur",
                        "Monumental lakeside palace complex spanning 400 years of Mewar history with peacock courtyards and royal silver galleries.",
                        "Udaipur", "Rajasthan", DestinationCategory.HERITAGE,
                        300.0, LocalTime.of(9, 30), LocalTime.of(17, 30), 3.0,
                        24.5764, 73.6835, 4.8, 4100,
                        "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1000&q=80",
                        "Old City, Udaipur, Rajasthan", true
                ),
                new Destination(
                        "Lake Pichola & Jag Mandir",
                        "Serene romantic freshwater lake adorned with island palaces and enchanting sunset boat rides beneath the Aravalli peaks.",
                        "Udaipur", "Rajasthan", DestinationCategory.NATURE,
                        400.0, LocalTime.of(9, 0), LocalTime.of(18, 0), 2.0,
                        24.5700, 73.6780, 4.9, 4600,
                        "https://images.unsplash.com/photo-1599661046289-e31897846e41?auto=format&fit=crop&w=1000&q=80",
                        "Pichola, Udaipur, Rajasthan", true
                ),
                new Destination(
                        "Saheliyon Ki Bari",
                        "Courtyard of the Maidens featuring marble elephants, lotus pools, and celestial water fountains engineered by Maharana Sangram Singh.",
                        "Udaipur", "Rajasthan", DestinationCategory.NATURE,
                        50.0, LocalTime.of(9, 0), LocalTime.of(19, 0), 1.5,
                        24.6022, 73.6872, 4.6, 2100,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "Saheli Marg, New Fatehpura, Udaipur", false
                ),
                new Destination(
                        "Jagdish Temple",
                        "Grand 1651 carved Indo-Aryan three-story temple dedicated to Lord Vishnu situated near the royal palace gates.",
                        "Udaipur", "Rajasthan", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(5, 0), LocalTime.of(21, 0), 1.0,
                        24.5794, 73.6841, 4.7, 1800,
                        "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=1000&q=80",
                        "RJ SH 50, Old City, Udaipur", true
                ),

                // 14. Mysore / Mysuru (Karnataka)
                new Destination(
                        "Mysore Palace (Amba Vilas)",
                        "Glorious Indo-Saracenic royal seat of the Wadiyar dynasty, illuminated by nearly 100,000 electric bulbs on festive nights.",
                        "Mysore", "Karnataka", DestinationCategory.HERITAGE,
                        100.0, LocalTime.of(10, 0), LocalTime.of(17, 30), 2.5,
                        12.3051, 76.6551, 4.8, 5600,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "Sayyaji Rao Rd, Agrahara, Chamrajpura, Mysuru", true
                ),
                new Destination(
                        "Chamundi Hills & Sri Chamundeshwari Temple",
                        "Sacred hilltop shrine dedicated to Goddess Durga featuring an iconic monolithic five-meter statue of Nandi the bull.",
                        "Mysore", "Karnataka", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(7, 30), LocalTime.of(21, 0), 2.0,
                        12.2727, 76.6713, 4.7, 3100,
                        "https://images.unsplash.com/photo-1621847468516-1ed5d0df56fe?auto=format&fit=crop&w=1000&q=80",
                        "Chamundi Hill, Mysuru, Karnataka", true
                ),
                new Destination(
                        "Brindavan Gardens & Musical Fountain",
                        "Terraced ornamental gardens adjoining the Krishnarajasagara dam featuring synchronized color-lit water fountains.",
                        "Mysore", "Karnataka", DestinationCategory.NATURE,
                        50.0, LocalTime.of(6, 30), LocalTime.of(20, 0), 2.5,
                        12.4244, 76.5728, 4.5, 2700,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "KRS Dam Rd, Srirangapatna, Mysuru", false
                ),

                // 15. Pune (Maharashtra)
                new Destination(
                        "Shaniwar Wada Palace",
                        "Historical 18th-century fortification seat of the Peshwa rulers featuring the famous Delhi Gate and fountain courtyards.",
                        "Pune", "Maharashtra", DestinationCategory.HERITAGE,
                        30.0, LocalTime.of(9, 30), LocalTime.of(17, 30), 2.0,
                        18.5196, 73.8553, 4.6, 3400,
                        "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1000&q=80",
                        "Shaniwar Peth, Pune, Maharashtra", true
                ),
                new Destination(
                        "Aga Khan Palace",
                        "Noble Italianate palace that served as Mahatma Gandhi's internment camp and memorial, surrounded by landscaped lawns.",
                        "Pune", "Maharashtra", DestinationCategory.HERITAGE,
                        25.0, LocalTime.of(9, 0), LocalTime.of(17, 30), 2.0,
                        18.5526, 73.9015, 4.6, 2200,
                        "https://images.unsplash.com/photo-1570168007204-dfb528c6958f?auto=format&fit=crop&w=1000&q=80",
                        "Pune Nagar Rd, Kalyani Nagar, Pune", true
                ),
                new Destination(
                        "Sinhagad Fort & Trek",
                        "Ancient fortress perched atop an isolated cliff of the Sahyadri mountains celebrated for Maratha valor and monsoon clouds.",
                        "Pune", "Maharashtra", DestinationCategory.ADVENTURE,
                        50.0, LocalTime.of(6, 0), LocalTime.of(18, 0), 3.5,
                        18.3663, 73.7558, 4.7, 3600,
                        "https://images.unsplash.com/photo-1511497584788-87676104235f?auto=format&fit=crop&w=1000&q=80",
                        "Sinhagad Ghat Rd, Thoptewadi, Pune", true
                ),

                // 16. Pondicherry (Puducherry)
                new Destination(
                        "Promenade Rock Beach",
                        "Scenic 1.5-km seaside walkway fringed by French colonial heritage buildings, Gandhi statue, and coastal cafes.",
                        "Pondicherry", "Puducherry", DestinationCategory.NATURE,
                        0.0, LocalTime.of(5, 0), LocalTime.of(23, 0), 2.0,
                        11.9338, 79.8353, 4.8, 3800,
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1000&q=80",
                        "White Town, Pondicherry", true
                ),
                new Destination(
                        "Sri Aurobindo Ashram",
                        "Peaceful spiritual sanctuary founded by Sri Aurobindo and Mirra Alfassa, holding the fragrant flower-covered Samadhi.",
                        "Pondicherry", "Puducherry", DestinationCategory.RELIGIOUS,
                        0.0, LocalTime.of(8, 0), LocalTime.of(18, 0), 1.5,
                        11.9365, 79.8346, 4.7, 2400,
                        "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=1000&q=80",
                        "Marine St, White Town, Pondicherry", true
                ),
                new Destination(
                        "French White Town & Heritage Quarter",
                        "Charming French colonial quarter characterized by mustard-yellow villas, bougainvillea-covered arches, and chic bakeries.",
                        "Pondicherry", "Puducherry", DestinationCategory.HERITAGE,
                        100.0, LocalTime.of(7, 0), LocalTime.of(22, 0), 2.5,
                        11.9325, 79.8336, 4.8, 3100,
                        "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&w=1000&q=80",
                        "White Town, Puducherry", true
                )
        );

        List<Destination> newlySaved = new java.util.ArrayList<>();
        for (Destination d : destinations) {
            if (!destinationRepository.existsByName(d.getName())) {
                newlySaved.add(destinationRepository.save(d));
            }
        }
        log.info("Successfully seeded {} new destinations. Total catalog size: {} spots.", newlySaved.size(), destinationRepository.count());
        return destinationRepository.findAll();
    }

    private void seedSampleTrip(User user, List<Destination> destinations) {
        if (user == null || tripRepository.count() > 0 || destinations.isEmpty()) {
            return;
        }

        log.info("Seeding initial sample day-trip for user: {}", user.getEmail());

        Trip sampleTrip = new Trip(
                user,
                "Hitec City, Hyderabad",
                "Hyderabad",
                LocalDate.now().plusDays(2),
                2500.0,
                8,
                2,
                "Heritage & Food"
        );
        sampleTrip.setWeatherForecastSummary("Sunny, 28°C, Clear Skies");
        sampleTrip.setStatus(TripStatus.PLANNED);
        sampleTrip = tripRepository.save(sampleTrip);

        // Create Itinerary
        Itinerary itinerary = new Itinerary(
                sampleTrip,
                "Optimized Cultural & Heritage Trail in Historic Hyderabad with traditional lunch stop."
        );
        itinerary.setTotalDistanceKm(24.5);
        itinerary.setTotalDurationMinutes(420);

        Destination charminar = destinations.stream()
                .filter(d -> d.getName().contains("Charminar"))
                .findFirst().orElse(destinations.get(0));

        Destination salarJung = destinations.stream()
                .filter(d -> d.getName().contains("Salar Jung"))
                .findFirst().orElse(destinations.get(1));

        Destination golconda = destinations.stream()
                .filter(d -> d.getName().contains("Golconda"))
                .findFirst().orElse(destinations.get(2));

        ItineraryItem stop1 = new ItineraryItem(
                itinerary, charminar, 1,
                charminar.getName(),
                "Morning visit to Charminar and morning tea at Nimrah Cafe.",
                LocalTime.of(9, 30), LocalTime.of(11, 0),
                100.0, "CAB", 20, 4.5
        );

        ItineraryItem stop2 = new ItineraryItem(
                itinerary, salarJung, 2,
                salarJung.getName(),
                "Explore the royal artefacts and the famous musical clock exhibition.",
                LocalTime.of(11, 30), LocalTime.of(13, 30),
                200.0, "WALK", 15, 1.2
        );

        ItineraryItem stop3 = new ItineraryItem(
                itinerary, null, 3,
                "Authentic Hyderabadi Lunch (Paradise Biryani)",
                "Relaxing lunch tasting authentic Dum Biryani and Irani Chai.",
                LocalTime.of(13, 45), LocalTime.of(15, 0),
                600.0, "CAB", 35, 12.0
        );

        ItineraryItem stop4 = new ItineraryItem(
                itinerary, golconda, 4,
                golconda.getName(),
                "Afternoon exploration of Golconda Fort acoustics and hilltop sunset view.",
                LocalTime.of(15, 45), LocalTime.of(18, 0),
                160.0, "CAB", 0, 0.0
        );

        itinerary.addItem(stop1);
        itinerary.addItem(stop2);
        itinerary.addItem(stop3);
        itinerary.addItem(stop4);

        itineraryRepository.save(itinerary);

        // Calculate estimated cost
        sampleTrip.setEstimatedTotalCost(1060.0);
        sampleTrip.setItinerary(itinerary);

        // Add 2 initial expenses
        Expense exp1 = new Expense(
                sampleTrip,
                "Morning Cab Ride to Charminar",
                320.0,
                ExpenseCategory.TRANSPORT,
                LocalDateTime.now().minusHours(2),
                "Ola Cab from Hitec City"
        );
        Expense exp2 = new Expense(
                sampleTrip,
                "Irani Chai & Osmania Biscuits at Nimrah",
                120.0,
                ExpenseCategory.FOOD,
                LocalDateTime.now().minusHours(1),
                "Morning snack"
        );
        sampleTrip.addExpense(exp1);
        sampleTrip.addExpense(exp2);

        expenseRepository.save(exp1);
        expenseRepository.save(exp2);
        tripRepository.save(sampleTrip);

        // Add Favorite & Review
        favoriteRepository.save(new Favorite(user, charminar));
        reviewRepository.save(new Review(user, charminar, 5, "Absolutely breathtaking early morning atmosphere!"));

        log.info("Seeded sample trip with 4 stops, 2 expenses, 1 favorite, and 1 review!");
    }
}
