package kr.modusplant.domains.common.context;

<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/context/DomainServiceOnlyContext.java
<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/context/DomainServiceOnlyContext.java
import kr.modusplant.domains.common.postprocessor.MockDomainRepositoryBeanFactoryPostProcessor;
import kr.modusplant.domains.common.scan.ScanDomainService;
========
import kr.modusplant.domains.common.postprocessor.MockCrudRepositoryBeanFactoryPostProcessor;
import kr.modusplant.domains.common.scan.ScanCrudService;
>>>>>>>> d0b5343 (MP-145 :truck: Rename: modules 및 domains 분리):src/test/java/kr/modusplant/domains/common/context/CrudServiceOnlyContext.java
========
import kr.modusplant.domains.common.postprocessor.MockCrudRepositoryBeanFactoryPostProcessor;
import kr.modusplant.domains.common.scan.ScanCrudService;
>>>>>>>> d0b5343 (MP-145 :truck: Rename: modules 및 domains 분리):src/test/java/kr/modusplant/domains/common/context/CrudServiceOnlyContext.java
import kr.modusplant.global.config.TestJpaConfig;
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
@SpringBootTest(classes = ScanDomainService.class)
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({TestJpaConfig.class, MockDomainRepositoryBeanFactoryPostProcessor.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface DomainServiceOnlyContext {
}
