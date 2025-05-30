package kr.modusplant.modules.example;

import kr.modusplant.global.middleware.redis.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExampleService {

    private final RedisHelper redisHelper;

    public String performBusinessLogic(boolean shouldThrowError) {
        if (shouldThrowError) {
            return "Business logic executed successfully!"; // 정상 흐름
        } else {
            throw new RuntimeException("Something went wrong during the business logic execution!"); // 예외 발생
        }
    }

    public String testRedisHelper() {
        try {
            // 영구적
            String redisKey = "test-redis-key";
            String value = "Test String Value";
            redisHelper.setString(redisKey, value);

            // 만료시간 : 10초
            String redisKey2 = "test-redis-expire-key-10sec";
            String value2 = "Test String Value 2(10sec), Expire Time : " + LocalDateTime.now().plusSeconds(10).toString();
            redisHelper.setString(redisKey2, value2, Duration.ofSeconds(10));

            // 만료시간 : 1분
            String redisKey3 = "test-redis-expire-key-1min";
            String value3 = "Test String Value 3(1min), Expire Time : " + LocalDateTime.now().plusMinutes(1).toString();
            redisHelper.setString(redisKey3, value3, Duration.ofMinutes(1));
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 Test 에러"); // 예외 발생
        }

        return "RedisHelper Test Successful";
    }
}
