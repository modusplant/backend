package kr.modusplant.legacy.modules.security.initializer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockPasswordEncoderInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        applicationContext.getBeanFactory().registerSingleton("passwordEncoder", encoder);
    }
}
