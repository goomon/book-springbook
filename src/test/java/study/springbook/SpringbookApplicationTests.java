package study.springbook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {AppContext.class, TestAppContext.class})
class SpringbookApplicationTests {

	@Test
	void contextLoads() {
	}

}
