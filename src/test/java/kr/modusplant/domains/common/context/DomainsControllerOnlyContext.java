package kr.modusplant.domains.common.context;

import kr.modusplant.domains.common.postprocessor.MockDomainsRepositoryBeanFactoryPostProcessor;
import kr.modusplant.domains.common.postprocessor.MockDomainsServiceBeanFactoryPostProcessor;
import kr.modusplant.domains.common.scan.ScanDomainsController;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest(classes = ScanDomainsController.class)
@AutoConfigureMockMvc
@Import({MockDomainsRepositoryBeanFactoryPostProcessor.class, MockDomainsServiceBeanFactoryPostProcessor.class})
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface DomainsControllerOnlyContext {
}