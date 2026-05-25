package com.logistics.platform;

import com.logistics.platform.config.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LogisticsPlatformApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LogisticsPlatformApplication.class);
        app.addInitializers(new DatabaseInitializer());
        app.run(args);
    }
}
