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

		MemberDao dao = new MemberDao();

		Member member = new Member();
		member.setId("id");
		member.setName("jaegoo");
		member.setPassword("password");

		dao.add(member);

		System.out.println(member.getId() + " successfully registered");

		Member member2 = dao.get(member.getId());
		System.out.println(member2.getName());
		System.out.println(member2.getPassword());

		System.out.println(member2.getId() + " successfully checked");
	}

}
