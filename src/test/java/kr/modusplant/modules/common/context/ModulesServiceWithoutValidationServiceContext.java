package kr.modusplant.modules.common.context;

import kr.modusplant.domains.common.scan.ScanDomainsService;
import kr.modusplant.global.common.scan.ScanGlobalService;
import kr.modusplant.global.config.TestJpaConfig;
import kr.modusplant.global.config.TestRedisConfig;
import kr.modusplant.global.config.TestS3Config;
import kr.modusplant.global.initializer.MockRedisComponentInitializer;
import kr.modusplant.modules.common.postprocessor.MockModulesRepositoryBeanFactoryPostProcessor;
import kr.modusplant.modules.common.postprocessor.MockModulesValidationServiceBeanFactoryPostProcessor;
import kr.modusplant.modules.common.scan.ScanModulesService;
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
        initializers = MockRedisComponentInitializer.class
)
@SpringBootTest(classes = {ScanGlobalService.class, ScanDomainsService.class, ScanModulesService.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface ModulesServiceWithoutValidationServiceContext {
}
