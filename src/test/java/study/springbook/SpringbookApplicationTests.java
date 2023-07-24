package study.springbook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = AppContext.class)
@ActiveProfiles("test")
class SpringbookApplicationTests {

	@Test
	void contextLoads() {
	}

}
