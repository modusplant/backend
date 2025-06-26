package kr.modusplant.global.context;

import kr.modusplant.global.config.TestJpaConfig;
import kr.modusplant.global.config.TestRedisConfig;
import kr.modusplant.global.config.TestS3Config;
import kr.modusplant.global.config.TestSecurityConfig;
import kr.modusplant.modules.common.postprocessor.MockModulesRepositoryBeanFactoryPostProcessor;
import kr.modusplant.modules.common.postprocessor.MockModulesServiceBeanFactoryPostProcessor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.*;

import static kr.modusplant.global.vo.Reference.NOTATION_GLOBAL;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest(useDefaultFilters = false)
@AutoConfigureMockMvc
@Import({TestJpaConfig.class,
        TestRedisConfig.class,
        TestS3Config.class,
        TestSecurityConfig.class,
        MockModulesRepositoryBeanFactoryPostProcessor.class,
        MockModulesServiceBeanFactoryPostProcessor.class}
)
@ComponentScan(
        basePackages = NOTATION_GLOBAL,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                Controller.class,
                RestControllerAdvice.class}),
        useDefaultFilters = false
)
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface SecurityOnlyContext {
}