package kr.modusplant.legacy.domains.common.context;

import kr.modusplant.framework.out.redis.initializer.MockRedisHelperInitializer;
import kr.modusplant.infrastructure.config.aws.TestS3Config;
import kr.modusplant.infrastructure.config.jpa.TestJpaConfig;
import kr.modusplant.infrastructure.config.redis.TestRedisConfig;
import kr.modusplant.legacy.domains.common.postprocessor.MockDomainsRepositoryBeanFactoryPostProcessor;
import kr.modusplant.legacy.domains.common.scan.ScanDomainsService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@ContextConfiguration(
        classes = {
                TestJpaConfig.class,
                TestRedisConfig.class,
                TestS3Config.class,
                MockDomainsRepositoryBeanFactoryPostProcessor.class},
        initializers = MockRedisHelperInitializer.class
)
@SpringBootTest(classes = {ScanDomainsService.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface DomainsServiceOnlyContext {
}