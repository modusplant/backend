package kr.modusplant.infrastructure.monitor;

import kr.modusplant.shared.framework.redis.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorService {

    private final RedisHelper redisHelper;
    private final S3Client s3Client;

    @Value("${cloud.wasabi.s3.bucket}")
    private String bucket;

    public String performBusinessLogic(boolean shouldNotThrowError) {
        if (shouldNotThrowError) {
            return "Business logic executed successfully!"; // 정상 흐름
        } else {
            throw new RuntimeException("Exception occurred during the business logic execution!"); // 예외 발생
        }
    }

    public String monitorRedisHelper() {
        try {
            // 영구적
            String redisKey = "test-redis-key";
            String value = "Test String Value";
            redisHelper.setString(redisKey, value);

            // 만료시간 : 10초
            String redisKey2 = "test-redis-expire-key-10sec";
            String value2 = "Test String Value 2(10sec), Expire Time : " + LocalDateTime.now().plusSeconds(10);
            redisHelper.setString(redisKey2, value2, Duration.ofSeconds(10));

            // 만료시간 : 1분
            String redisKey3 = "test-redis-expire-key-1min";
            String value3 = "Test String Value 3(1min), Expire Time : " + LocalDateTime.now().plusMinutes(1);
            redisHelper.setString(redisKey3, value3, Duration.ofMinutes(1));
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during testing the Redis storage!: ", e); // 예외 발생
        }

        return "RedisHelper test executed successfully!"; // 정상 흐름
    }

    public String monitorAmazonS3() {
        long startTime = System.nanoTime(); // 지연 측정을 위한 시작 시간

        try {
            s3Client.headBucket(HeadBucketRequest.builder()
                    .bucket(bucket)
                    .build());
        } catch (S3Exception e) {
            throw new RuntimeException("Wasabi health check failed!: " + e); // 4xx 및 5xx 에러 응답
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during testing the Amazon S3 storage!: ", e); // 예외 발생
        }

        double durationInMs = (System.nanoTime() - startTime) / 1_000_000.0; // 지연 측정을 위한 종료 시간

        log.info("[Amazon S3] duration:{}ms", String.format("%.2f", durationInMs));
        return "Amazon S3 test executed successfully!"; // 정상 흐름
    }
}
