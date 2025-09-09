package kr.modusplant.infrastructure.monitor;

import kr.modusplant.framework.out.redis.RedisHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MonitorServiceTest {
    RedisHelper redisHelper = Mockito.mock(RedisHelper.class);
    MonitorService monitorService = new MonitorService(redisHelper);

    @Nested
    @DisplayName("performBusinessLogic 테스트")
    class PerformBusinessLogicTest {

        @Test
        @DisplayName("true와 함께 호출 시 성공 메시지 반환")
        void callPerformBusinessLogic_withTrue_returnsSuccessMessage() {
            String result = monitorService.performBusinessLogic(true);
            assertThat(result).isEqualTo("Business logic executed successfully!");
        }

        @Test
        @DisplayName("false와 함께 호출 시 예외 발생")
        void callPerformBusinessLogic_withFalse_throwsException() {
            assertThatThrownBy(() -> monitorService.performBusinessLogic(false))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Exception occurred during the business logic execution");
        }
    }

    @Nested
    @DisplayName("monitorRedisHelper 테스트")
    class MonitorRedisHelperTest {

        @Test
        @DisplayName("정상 호출 시 성공 메시지 반환")
        void callMonitorRedisHelper_withNormalState_returnsSuccessMessage() {
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
        @DisplayName("RedisHelper 가동 실패 시 예외 발생")
        void callMonitorRedisHelper_withRedisHelperFailure_throwsException() {
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
