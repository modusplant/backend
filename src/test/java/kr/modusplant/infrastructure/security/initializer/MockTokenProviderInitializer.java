package kr.modusplant.infrastructure.security.initializer;

import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockTokenProviderInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        applicationContext.getBeanFactory().registerSingleton("tokenProvider", tokenProvider);
    }
}
