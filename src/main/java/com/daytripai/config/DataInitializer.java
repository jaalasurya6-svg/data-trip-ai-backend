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
        if (destinationRepository.count() > 0) {
            log.info("Destinations already exist ({} records). Skipping destination seeding.", destinationRepository.count());
            return destinationRepository.findAll();
        }

        log.info("Seeding curated tourist destinations...");

        List<Destination> destinations = Arrays.asList(
                // Hyderabad
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

                // Mumbai
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

                // Delhi
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

                // Jaipur
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

                // Agra
                new Destination(
                        "Taj Mahal",
                        "World-renowned ivory-white marble mausoleum on the right bank of the river Yamuna, an eternal symbol of architectural majesty.",
                        "Agra", "Uttar Pradesh", DestinationCategory.HERITAGE,
                        250.0, LocalTime.of(6, 0), LocalTime.of(18, 30), 3.0,
                        27.1751, 78.0421, 4.9, 9800,
                        "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&w=1000&q=80",
                        "Dharmapuri, Forest Colony, Tajganj, Agra", true
                ),

                // Bengaluru
                new Destination(
                        "Lalbagh Botanical Garden",
                        "Historic 240-acre botanical haven commissioning century-old trees, a famous London Crystal Palace replica glasshouse, and a lake.",
                        "Bengaluru", "Karnataka", DestinationCategory.NATURE,
                        30.0, LocalTime.of(6, 0), LocalTime.of(19, 0), 2.0,
                        12.9507, 77.5848, 4.6, 2400,
                        "https://images.unsplash.com/photo-1596176530529-78163a4f7af2?auto=format&fit=crop&w=1000&q=80",
                        "Mavalli, Bengaluru", true
                )
        );

        List<Destination> saved = destinationRepository.saveAll(destinations);
        log.info("Successfully seeded {} destinations across Hyderabad, Mumbai, Delhi, Jaipur, Agra, and Bengaluru.", saved.size());
        return saved;
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
