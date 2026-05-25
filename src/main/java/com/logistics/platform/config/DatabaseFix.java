package com.logistics.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFix implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE trucks DROP CONSTRAINT trucks_truck_type_check");
            System.out.println("Successfully dropped the check constraint trucks_truck_type_check");
        } catch (Exception e) {
            System.out.println("Could not drop constraint (it might already be dropped): " + e.getMessage());
        }
    }
}
