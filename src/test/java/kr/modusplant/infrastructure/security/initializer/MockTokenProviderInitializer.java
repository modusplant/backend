package kr.modusplant.infrastructure.security.initializer;

import kr.modusplant.legacy.modules.jwt.app.service.TokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockTokenProviderInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TokenProvider tokenProvider = mock(TokenProvider.class);
        applicationContext.getBeanFactory().registerSingleton("tokenProvider", tokenProvider);
    }
}
