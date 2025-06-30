package kr.modusplant.global.context;

import kr.modusplant.global.config.TestJpaConfig;
import kr.modusplant.global.config.TestRedisConfig;
import kr.modusplant.global.config.TestS3Config;
import kr.modusplant.global.middleware.redis.RedisHelper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

import static kr.modusplant.global.vo.Reference.NOTATION_ALL;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(basePackages = NOTATION_ALL)
@Transactional
@Import({TestJpaConfig.class,
        TestRedisConfig.class,
        TestS3Config.class,
        RedisHelper.class})
@ComponentScan(
        basePackageClasses = RedisHelper.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedisHelper.class),
        useDefaultFilters = false
)
public @interface RepositoryOnlyContext {
}
