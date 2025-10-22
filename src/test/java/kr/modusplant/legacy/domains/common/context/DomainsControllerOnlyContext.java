 package kr.modusplant.legacy.domains.common.context;

 import kr.modusplant.framework.out.redis.initializer.MockRedisHelperInitializer;
 import kr.modusplant.infrastructure.config.aws.TestS3Config;
 import kr.modusplant.infrastructure.config.jdbc.TestDataSourceConfig;
 import kr.modusplant.infrastructure.config.jpa.TestJpaConfig;
 import kr.modusplant.infrastructure.config.redis.TestRedisConfig;
 import kr.modusplant.infrastructure.security.initializer.MockTokenProviderInitializer;
 import kr.modusplant.legacy.domains.common.postprocessor.MockDomainsRepositoryBeanFactoryPostProcessor;
 import kr.modusplant.legacy.domains.common.postprocessor.MockDomainsServiceBeanFactoryPostProcessor;
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

 import static kr.modusplant.legacy.domains.common.constant.Reference.NOTATION_DOMAINS;

 @Target({ElementType.TYPE})
 @Retention(RetentionPolicy.RUNTIME)
 @Documented
 @WebMvcTest(useDefaultFilters = false)
 @AutoConfigureMockMvc(addFilters = false)
 @ContextConfiguration(
         classes = {
                 TestDataSourceConfig.class,
                 TestJpaConfig.class,
                 TestRedisConfig.class,
                 TestS3Config.class,
                 MockDomainsRepositoryBeanFactoryPostProcessor.class,
                 MockDomainsServiceBeanFactoryPostProcessor.class},
         initializers = {MockRedisHelperInitializer.class, MockTokenProviderInitializer.class}
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