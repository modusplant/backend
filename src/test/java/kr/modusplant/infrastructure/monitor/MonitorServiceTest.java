package kr.modusplant.infrastructure.monitor;

import kr.modusplant.shared.framework.redis.RedisHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MonitorServiceTest {
    RedisHelper redisHelper = Mockito.mock(RedisHelper.class);
    S3Client s3Client = Mockito.mock(S3Client.class);
    MonitorService monitorService = new MonitorService(redisHelper, s3Client);

    {
        ReflectionTestUtils.setField(monitorService, "bucket", "test-bucket");
    }

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
        @DisplayName("정상 응답 시 성공 메시지 반환")
        void testMonitorAmazonS3_givenNormalResponse_willReturnSuccessMessage() {
            // given
            when(s3Client.headBucket(any(HeadBucketRequest.class)))
                    .thenReturn(HeadBucketResponse.builder().build());

            // when
            String result = monitorService.monitorAmazonS3();

            // then
            assertThat(result).isEqualTo("Amazon S3 test executed successfully!");
        }

        @Test
        @DisplayName("S3Exception 발생 시 예외 발생")
        void testMonitorAmazonS3_givenS3Exception_willThrowException() {
            // given
            when(s3Client.headBucket(any(HeadBucketRequest.class)))
                    .thenThrow(S3Exception.builder().message("Not Found").statusCode(404).build());

            // when + then
            assertThatThrownBy(() -> monitorService.monitorAmazonS3())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Wasabi health check failed!");
        }

        @Test
        @DisplayName("요청 중 예외 발생 시 예외 발생")
        void testMonitorAmazonS3_givenRequestFailure_willThrowException() {
            // given
            when(s3Client.headBucket(any(HeadBucketRequest.class)))
                    .thenThrow(SdkClientException.create("Connection refused"));

            // when + then
            assertThatThrownBy(() -> monitorService.monitorAmazonS3())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Exception occurred during testing the Amazon S3 storage!");
        }
    }
}
