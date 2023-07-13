package study.springbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import study.springbook.dao.MemberDao;
import study.springbook.domain.Member;

import java.sql.SQLException;

@SpringBootApplication
public class SpringbookApplication {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		SpringApplication.run(SpringbookApplication.class, args);
	}

}
