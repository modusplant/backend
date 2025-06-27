package kr.modusplant.global.context;

import kr.modusplant.ModusplantApplication;
import kr.modusplant.global.config.TestJpaConfig;
import kr.modusplant.global.config.TestRedisConfig;
import kr.modusplant.global.config.TestS3Config;
import kr.modusplant.global.middleware.redis.RedisHelper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

import static kr.modusplant.global.vo.Reference.NOTATION_ALL;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ModusplantApplication.class)
@EnableJpaRepositories(basePackages = NOTATION_ALL)
@Transactional
@Import({TestJpaConfig.class,
        TestRedisConfig.class,
        TestS3Config.class,
        RedisHelper.class})
public @interface RepositoryOnlyContext {
}
