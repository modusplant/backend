package kr.modusplant.legacy.modules.common.context;

import kr.modusplant.framework.outbound.config.aws.TestS3Config;
import kr.modusplant.framework.outbound.config.jpa.TestJpaConfig;
import kr.modusplant.framework.outbound.config.redis.TestRedisConfig;
import kr.modusplant.framework.outbound.persistence.redis.initializer.MockRedisHelperInitializer;
import kr.modusplant.legacy.domains.common.scan.ScanDomainsService;
import kr.modusplant.legacy.modules.common.postprocessor.MockModulesRepositoryBeanFactoryPostProcessor;
import kr.modusplant.legacy.modules.common.postprocessor.MockModulesValidationServiceBeanFactoryPostProcessor;
import kr.modusplant.legacy.modules.common.scan.ScanModulesService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
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
                MockModulesRepositoryBeanFactoryPostProcessor.class,
                MockModulesValidationServiceBeanFactoryPostProcessor.class,
                RestClientAutoConfiguration.class},
        initializers = MockRedisHelperInitializer.class
)
@SpringBootTest(classes = {ScanDomainsService.class, ScanModulesService.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface ModulesServiceWithoutValidationServiceContext {
}
