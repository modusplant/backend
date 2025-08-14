package kr.modusplant.infrastructure.context;

import kr.modusplant.framework.outbound.config.aws.TestS3Config;
import kr.modusplant.framework.outbound.config.jpa.TestJpaConfig;
import kr.modusplant.framework.outbound.config.redis.TestRedisConfig;
import kr.modusplant.framework.outbound.persistence.redis.RedisHelper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

import static kr.modusplant.shared.vo.Reference.NOTATION_ALL;

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
