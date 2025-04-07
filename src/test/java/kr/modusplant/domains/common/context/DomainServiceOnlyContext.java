<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/context/DomainServiceOnlyContext.java
package kr.modusplant.domains.common.context;

import kr.modusplant.domains.common.postprocessor.MockDomainRepositoryBeanFactoryPostProcessor;
import kr.modusplant.domains.common.scan.ScanDomainService;
========
package kr.modusplant.api.crud.common.context;

import kr.modusplant.api.crud.common.postprocessor.MockCrudRepositoryBeanFactoryPostProcessor;
import kr.modusplant.api.crud.common.scan.ScanCrudService;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/context/CrudServiceOnlyContext.java
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
<<<<<<<< HEAD:src/test/java/kr/modusplant/domains/common/context/DomainServiceOnlyContext.java
@SpringBootTest(classes = ScanDomainService.class)
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({TestJpaConfig.class, MockDomainRepositoryBeanFactoryPostProcessor.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface DomainServiceOnlyContext {
========
@SpringBootTest(classes = ScanCrudService.class)
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({TestJpaConfig.class, MockCrudRepositoryBeanFactoryPostProcessor.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface CrudServiceOnlyContext {
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/test/java/kr/modusplant/api/crud/common/context/CrudServiceOnlyContext.java
}
