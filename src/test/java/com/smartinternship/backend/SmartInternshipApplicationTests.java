package com.smartinternship.backend;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SmartInternshipApplicationTests {

    @Test
    void applicationClassLoads() {
        assertNotNull(SmartInternshipApplication.class, "Application class should be present and loadable");
    }
}
