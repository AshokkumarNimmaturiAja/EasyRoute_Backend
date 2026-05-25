package com.logistics.platform;

import com.logistics.platform.config.DatabaseInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = DatabaseInitializer.class)
class LogisticsPlatformApplicationTests {

    @Test
    void contextLoads() {
    }

}
