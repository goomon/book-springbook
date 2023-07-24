package study.springbook;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SqlServiceContext.class)
@Documented
public @interface EnableSqlService {
}
