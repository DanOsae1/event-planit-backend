package com.osaebros.eventplanner;

import com.osaebros.eventplanner.config.WebSecurityConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(WebSecurityConfigTest.class)
class EventPlannerApplicationTests {

	@Test
	void contextLoads() {
	}

}
