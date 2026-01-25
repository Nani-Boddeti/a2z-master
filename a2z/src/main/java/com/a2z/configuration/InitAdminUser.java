package com.a2z.configuration;

import com.a2z.dao.Country;
import com.a2z.dao.Customer;
import com.a2z.dao.UserGroup;
import com.a2z.persistence.A2zCountryRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Initializes an admin user with scopes when the application starts.
 * This component:
 * 1. Creates an admin user if it doesn't exist
 * 2. Assigns the admin to ADMIN_GROUP with app.write and app.read scopes
 * 3. Uses password encoding for security
 * 4. Does NOT use roles - scopes are managed via UserGroup
 */
@Component
public class InitAdminUser implements ApplicationRunner {

    private final PODCustomerRepository customerRepository;
    private final RootRepository rootRepository;
    private final PasswordEncoder passwordEncoder;
    private final A2zCountryRepository countryRepository;
    // Configuration - Change these values as needed
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "72KjHI0iYVkF!";
    private static final String ADMIN_EMAIL = "admin@a2z.com";
    private static final String ADMIN_PHONE = "+1234567890";
    private static final String ADMIN_GROUP_NAME = "Admin";

    public InitAdminUser(PODCustomerRepository customerRepository,
                          RootRepository rootRepository,
                          PasswordEncoder passwordEncoder,A2zCountryRepository countryRepository) {
        this.customerRepository = customerRepository;
        this.rootRepository = rootRepository;
        this.passwordEncoder = passwordEncoder;
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createAdminUser();
    }

    private void createAdminUser() {
        // Check if admin user already exists
        Optional<Customer> existingAdmin = customerRepository.findById(ADMIN_USERNAME);

        if (existingAdmin.isPresent()) {
            System.out.println("✓ Admin user already exists: " + ADMIN_USERNAME);
            return;
        }

        System.out.println("→ Creating admin user: " + ADMIN_USERNAME);

        // Create the admin user
        Customer admin = new Customer(
            ADMIN_USERNAME,
            passwordEncoder.encode(ADMIN_PASSWORD),
            ADMIN_EMAIL
        );

        // Set basic user details
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setPhoneNumber(ADMIN_PHONE);
        admin.setDisabled(false);
        admin.setRole("ROLE_ADMIN");

        Optional<Country> countryOptional = countryRepository.findById("IND");
        if(countryOptional.isPresent()){
            admin.setDefaultCountry(countryOptional.get());
        } else {
            Country country = new Country();
            country.setIsoCode("IND");
            country.setRegion("India");
            countryRepository.save(country);
            admin.setDefaultCountry(country);
        }
        // Get or create the ADMIN_GROUP
        Optional<UserGroup> adminGroupOpt = rootRepository.getUserGroupByName(ADMIN_GROUP_NAME);
        UserGroup adminGroup;

        if (adminGroupOpt.isPresent()) {
            adminGroup = adminGroupOpt.get();
            System.out.println("  ✓ Using existing admin group: " + ADMIN_GROUP_NAME);
        } else {
            // Create ADMIN_GROUP if it doesn't exist
            adminGroup = new UserGroup();
            adminGroup.setUserGroupName(ADMIN_GROUP_NAME);
            adminGroup.setRole("ROLE_ADMIN");
            adminGroup = rootRepository.save(adminGroup);
            System.out.println("  ✓ Created new admin group: " + ADMIN_GROUP_NAME);
        }

        // Assign admin to ADMIN_GROUP
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(adminGroup);
        admin.setUserGroups(userGroups);

        // Save the admin user
        customerRepository.save(admin);

        System.out.println("✓ Admin user created successfully");
        System.out.println("  Username: " + ADMIN_USERNAME);
        System.out.println("  Email: " + ADMIN_EMAIL);
        System.out.println("  User Group: " + ADMIN_GROUP_NAME);
        System.out.println("  Role: ROLE_ADMIN");
        System.out.println("  Note: This user can login with username/password and will receive app.read and app.write scopes via OAuth2");
    }
}

