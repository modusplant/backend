package kr.modusplant.framework.out.redis.initializer;

import kr.modusplant.framework.out.redis.RedisHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockRedisHelperInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        RedisHelper redisHelper = mock(RedisHelper.class);
        applicationContext.getBeanFactory().registerSingleton("redisHelper", redisHelper);
    }
}
