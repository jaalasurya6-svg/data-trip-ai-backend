package com.daytripai;

import com.daytripai.entity.User;
import com.daytripai.enums.RoleType;
import com.daytripai.repository.DestinationRepository;
import com.daytripai.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DayTripAiBackendApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Test
    @DisplayName("Application Context Loads and Repositories are injected")
    void contextLoads() {
        assertNotNull(userRepository, "UserRepository should be injected");
        assertNotNull(destinationRepository, "DestinationRepository should be injected");
    }

    @Test
    @DisplayName("Verify Database Initializer seeded default users and destinations")
    void verifySeedData() {
        assertTrue(userRepository.count() > 0, "Users should be seeded");
        assertTrue(destinationRepository.count() >= 10, "At least 10 destinations should be seeded");
        assertTrue(userRepository.existsByEmail("admin@daytripai.com"), "Admin user must exist");
        assertTrue(userRepository.existsByEmail("traveler@daytripai.com"), "Traveler user must exist");
    }
}
