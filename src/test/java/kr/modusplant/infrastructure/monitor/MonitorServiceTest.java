package kr.modusplant.infrastructure.monitor;

import kr.modusplant.shared.framework.redis.RedisHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MonitorServiceTest {
    RedisHelper redisHelper = Mockito.mock(RedisHelper.class);
    RestClient.Builder restClientBuilder = Mockito.mock(RestClient.Builder.class, Answers.RETURNS_DEEP_STUBS);
    MonitorService monitorService = new MonitorService(redisHelper, restClientBuilder);

    @Nested
    @DisplayName("performBusinessLogic 테스트")
    class PerformBusinessLogicTest {

        @Test
        @DisplayName("true와 함께 호출 시 성공 메시지 반환")
        void testPerformBusinessLogic_givenTrue_willReturnSuccessMessage() {
            String result = monitorService.performBusinessLogic(true);
            assertThat(result).isEqualTo("Business logic executed successfully!");
        }

        @Test
        @DisplayName("false와 함께 호출 시 예외 발생")
        void testPerformBusinessLogic_givenFalse_willThrowException() {
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
        void testMonitorRedisHelper_givenNormalState_willReturnSuccessMessage() {
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
        void testMonitorRedisHelper_givenRedisHelperFailure_willThrowException() {
            // given
            doThrow(new RuntimeException("Redis failure"))
                    .when(redisHelper).setString(eq("test-redis-key"), any());

            // when + then
            assertThatThrownBy(() -> monitorService.monitorRedisHelper())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Exception occurred during testing the Redis storage");
        }
    }

    @Nested
    @DisplayName("monitorAmazonS3 테스트")
    class MonitorAmazonS3Test {

        @Test
        @DisplayName("200 OK 응답 시 성공 메시지 반환")
        void testMonitorAmazonS3_given200Response_willReturnSuccessMessage() {
            // given
            when(restClientBuilder.build()
                    .get()
                    .uri(anyString())
                    .retrieve()
                    .onStatus(any(), any())
                    .toBodilessEntity())
                    .thenReturn(ResponseEntity.ok().build());

            // when
            String result = monitorService.monitorAmazonS3();

            // then
            assertThat(result).isEqualTo("Amazon S3 test executed successfully!");
        }

        @Test
        @DisplayName("4xx 응답 시 상태 코드와 함께 예외 발생")
        void testMonitorAmazonS3_given4xxResponse_willThrowExceptionWithStatusCode() {
            // given
            when(restClientBuilder.build()
                    .get()
                    .uri(anyString())
                    .retrieve()
                    .onStatus(any(), any())
                    .toBodilessEntity())
                    .thenReturn(ResponseEntity.status(404).build());

            // when + then
            assertThatThrownBy(() -> monitorService.monitorAmazonS3())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("The request had problem!")
                    .hasMessageContaining("404");
        }

        @Test
        @DisplayName("5xx 응답 시 상태 코드와 함께 예외 발생")
        void testMonitorAmazonS3_given5xxResponse_willThrowExceptionWithStatusCode() {
            // given
            when(restClientBuilder.build()
                    .get()
                    .uri(anyString())
                    .retrieve()
                    .onStatus(any(), any())
                    .toBodilessEntity())
                    .thenReturn(ResponseEntity.status(503).build());

            // when + then
            assertThatThrownBy(() -> monitorService.monitorAmazonS3())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Amazon S3 had problem!")
                    .hasMessageContaining("503");
        }

        @Test
        @DisplayName("요청 중 예외 발생 시 예외 발생")
        void testMonitorAmazonS3_givenRequestFailure_willThrowException() {
            // given
            when(restClientBuilder.build()
                    .get()
                    .uri(anyString())
                    .retrieve()
                    .onStatus(any(), any())
                    .toBodilessEntity())
                    .thenThrow(new RestClientException("Connection refused"));

            // when + then
            assertThatThrownBy(() -> monitorService.monitorAmazonS3())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Exception occurred during testing the Amazon S3 storage!");
        }
    }
}
