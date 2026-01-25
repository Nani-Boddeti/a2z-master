package com.a2z.services.impl;

import com.a2z.dao.Country;
import com.a2z.dao.Customer;
import com.a2z.dao.UserGroup;
import com.a2z.data.AdminUserRequest;
import com.a2z.data.AdminUserResponse;
import com.a2z.persistence.A2zCountryRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.*;

@Service
public class AdminManagementService {

    @Autowired
    private PODCustomerRepository customerRepository;

    @Autowired
    private RootRepository rootRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    A2zCountryRepository countryRepository;

    private static final String ADMIN_GROUP_NAME = "ADMIN";

    @Transactional
    public AdminUserResponse createAdminUser(AdminUserRequest request) {
        // Check if username already exists
        Optional<Customer> existingCustomer = customerRepository.findById(request.getUsername());
        if (existingCustomer.isPresent()) {
            return new AdminUserResponse(
                request.getUsername(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                "Username already exists",
                false
            );
        }

        // Create new admin customer
        Customer admin = new Customer(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail()
        );

        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setDisabled(false);
        admin.setRole("ROLE_ADMIN");

        // Get or create ADMIN_GROUP
        Optional<UserGroup> adminGroupOpt = rootRepository.getUserGroupByName(ADMIN_GROUP_NAME);
        UserGroup adminGroup;

        if (adminGroupOpt.isPresent()) {
            adminGroup = adminGroupOpt.get();
        } else {
            adminGroup = new UserGroup();
            adminGroup.setUserGroupName(ADMIN_GROUP_NAME);
            adminGroup.setRole("ROLE_ADMIN");
            adminGroup = rootRepository.save(adminGroup);
        }

        // Assign admin to ADMIN_GROUP for app.write scope
        List<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(adminGroup);
        admin.setUserGroups(userGroups);
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
        // Save the admin user
        customerRepository.save(admin);

        return new AdminUserResponse(
            request.getUsername(),
            request.getEmail(),
            request.getFirstName(),
            request.getLastName(),
            "Admin user created successfully",
            true
        );
    }
}

