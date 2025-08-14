package kr.modusplant.global.context;

import kr.modusplant.framework.outbound.config.aws.TestS3Config;
import kr.modusplant.framework.outbound.config.jpa.TestJpaConfig;
import kr.modusplant.framework.outbound.config.redis.TestRedisConfig;
import kr.modusplant.global.initializer.MockPasswordEncoderInitializer;
import kr.modusplant.infrastructure.security.config.TestSecurityConfig;
import kr.modusplant.legacy.modules.common.postprocessor.MockModulesRepositoryBeanFactoryPostProcessor;
import kr.modusplant.legacy.modules.common.postprocessor.MockModulesServiceBeanFactoryPostProcessor;
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
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.*;

import static kr.modusplant.global.vo.Reference.NOTATION_GLOBAL;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest(useDefaultFilters = false)
@ContextConfiguration(
        classes = {
                TestJpaConfig.class,
                TestRedisConfig.class,
                TestS3Config.class,
                TestSecurityConfig.class,
                MockModulesRepositoryBeanFactoryPostProcessor.class,
                MockModulesServiceBeanFactoryPostProcessor.class},
        initializers = MockPasswordEncoderInitializer.class
)
@AutoConfigureMockMvc
@ComponentScan(
        // HACK: 임시 방편, 추후 보안 속박된 맥락을 대상으로 하는 어노테이션 개발 필요
        basePackages = "kr.modusplant.infrastructure.security",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                Controller.class,
                RestControllerAdvice.class}),
        useDefaultFilters = false
)
@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public @interface SecurityOnlyContext {
}