package kr.modusplant.legacy.modules.monitor;

import kr.modusplant.framework.outbound.persistence.redis.RedisHelper;
import kr.modusplant.legacy.modules.common.context.ModulesServiceWithoutValidationServiceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ModulesServiceWithoutValidationServiceContext
public class MonitorServiceTest {
    @MockitoBean
    RedisHelper redisHelper;

    @Autowired
    MonitorService monitorService;

    @Nested
    @DisplayName("performBusinessLogic 테스트")
    class PerformBusinessLogicTest {

        @Test
        @DisplayName("정상 동작 시 성공 메시지 반환")
        void shouldReturnSuccessMessageWhenNoError() {
            String result = monitorService.performBusinessLogic(true);
            assertThat(result).isEqualTo("Business logic executed successfully!");
        }

        @Test
        @DisplayName("예외 플래그 시 RuntimeException 발생")
        void shouldThrowExceptionWhenError() {
            assertThatThrownBy(() -> monitorService.performBusinessLogic(false))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Exception occurred during the business logic execution");
        }
    }

    @Nested
    @DisplayName("monitorRedisHelper 테스트")
    class MonitorRedisHelperTest {

        @Test
        @DisplayName("RedisHelper 정상 호출 시 성공 메시지 반환")
        void shouldStoreStringsInRedisAndReturnSuccess() {
            // when
            String result = monitorService.monitorRedisHelper();

            // then
            verify(redisHelper, times(1))
                    .setString(eq("test-redis-key"), eq("Test String Value"));
            verify(redisHelper, times(1))
                    .setString(eq("test-redis-expire-key-10sec"), contains("10sec"), eq(Duration.ofSeconds(10)));
            verify(redisHelper, times(1))
                    .setString(eq("test-redis-expire-key-1min"), contains("1min"), eq(Duration.ofMinutes(1)));

            assertThat(result).isEqualTo("RedisHelper test executed successfully!");
        }

        @Test
        @DisplayName("RedisHelper 예외 발생 시 RuntimeException 반환")
        void shouldThrowRuntimeExceptionWhenRedisFails() {
            // given
            doThrow(new RuntimeException("Redis failure"))
                    .when(redisHelper).setString(eq("test-redis-key"), any());

            // when + then
            assertThatThrownBy(() -> monitorService.monitorRedisHelper())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Exception occurred during testing the Redis storage");
        }
    }
}
