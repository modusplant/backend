package kr.modusplant.global.initializer;

import kr.modusplant.global.middleware.redis.RedisHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockRedisComponentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        RedisHelper redisHelper = mock(RedisHelper.class);
        applicationContext.getBeanFactory().registerSingleton("redisHelper", redisHelper);
    }
}
