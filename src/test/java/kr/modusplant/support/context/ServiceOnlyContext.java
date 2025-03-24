package kr.modusplant.support.context;

import kr.modusplant.support.config.TestJpaConfig;
import kr.modusplant.support.postprocessor.MockRepositoryBeanFactoryPostProcessor;
import kr.modusplant.support.scan.ScanService;
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
@SpringBootTest(classes = ScanService.class)
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({TestJpaConfig.class, MockRepositoryBeanFactoryPostProcessor.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface ServiceOnlyContext {
}
