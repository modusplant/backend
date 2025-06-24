package kr.modusplant.domains.common.context;

import kr.modusplant.domains.common.postprocessor.MockDomainsClientBeanFactoryPostProcessor;
import kr.modusplant.domains.common.postprocessor.MockDomainsRepositoryBeanFactoryPostProcessor;
import kr.modusplant.domains.common.scan.ScanDomainsService;
import kr.modusplant.domains.common.scan.ScanGlobalService;
import kr.modusplant.global.config.TestJpaConfig;
import kr.modusplant.global.config.TestRedisConfig;
import kr.modusplant.global.config.TestS3Config;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({TestJpaConfig.class,
        TestRedisConfig.class,
        TestS3Config.class,
        MockDomainsRepositoryBeanFactoryPostProcessor.class,
        MockDomainsClientBeanFactoryPostProcessor.class})
@SpringBootTest(classes = {ScanGlobalService.class, ScanDomainsService.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface DomainsServiceOnlyContext {
}
