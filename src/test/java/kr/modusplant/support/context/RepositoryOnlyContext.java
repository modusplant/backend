package kr.modusplant.support.context;

import kr.modusplant.ModusplantApplication;
import kr.modusplant.support.config.TestJpaConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

import static kr.modusplant.global.vo.Reference.NOTATION_REPOSITORY;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ModusplantApplication.class)
@EnableJpaRepositories(basePackages = NOTATION_REPOSITORY)
@Transactional
@Import({TestJpaConfig.class})
public @interface RepositoryOnlyContext {
}
