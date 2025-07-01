 package kr.modusplant.domains.common.context;

 import kr.modusplant.domains.common.postprocessor.MockDomainsRepositoryBeanFactoryPostProcessor;
 import kr.modusplant.domains.common.postprocessor.MockDomainsServiceBeanFactoryPostProcessor;
 import kr.modusplant.global.config.TestJpaConfig;
 import kr.modusplant.global.config.TestRedisConfig;
 import kr.modusplant.global.config.TestS3Config;
 import kr.modusplant.global.initializer.MockRedisHelperInitializer;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.junit.jupiter.api.parallel.Execution;
 import org.junit.jupiter.api.parallel.ExecutionMode;
 import org.mockito.junit.jupiter.MockitoExtension;
 import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
 import org.springframework.context.annotation.ComponentScan;
 import org.springframework.context.annotation.FilterType;
 import org.springframework.stereotype.Controller;
 import org.springframework.test.context.ContextConfiguration;

 import java.lang.annotation.*;

 import static kr.modusplant.domains.common.vo.Reference.NOTATION_DOMAINS;

 @Target({ElementType.TYPE})
 @Retention(RetentionPolicy.RUNTIME)
 @Documented
 @WebMvcTest(useDefaultFilters = false)
 @AutoConfigureMockMvc(addFilters = false)
 @ContextConfiguration(
         classes = {
                 TestJpaConfig.class,
                 TestRedisConfig.class,
                 TestS3Config.class,
                 MockDomainsRepositoryBeanFactoryPostProcessor.class,
                 MockDomainsServiceBeanFactoryPostProcessor.class},
         initializers = MockRedisHelperInitializer.class
 )
 @ComponentScan(
         basePackages = NOTATION_DOMAINS,
         includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class),
         useDefaultFilters = false
 )
 @ExtendWith(MockitoExtension.class)
 @Execution(ExecutionMode.CONCURRENT)
 public @interface DomainsControllerOnlyContext {
 }