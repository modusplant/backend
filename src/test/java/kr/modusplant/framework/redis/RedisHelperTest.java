package kr.modusplant.framework.redis;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class RedisHelperTest {
    @Autowired
    private RedisHelper redisHelper;

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장")
    void storeString_givenValidRedisHelper_returnString() {
        String stringKey = "test:string";
        String stringValue = "stringValue";

        redisHelper.setString(stringKey, stringValue, Duration.ofSeconds(10));

        Optional<String> result = redisHelper.getString(stringKey);
        assertThat(result).isPresent().contains(stringValue);
    }

    @Test
    @DisplayName("Redis 헬퍼로 객체 저장")
    void storeObject_givenValidRedisHelper_returnObject() {
        String objectKey = "test:object";
        TestDto objectValue = new TestDto("John",30);

        redisHelper.setObject(objectKey, objectValue, Duration.ofSeconds(10));

        Optional<TestDto> result = redisHelper.getObject(objectKey, TestDto.class);
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getName()).isEqualTo(objectValue.getName());
        assertThat(result.orElseThrow().getAge()).isEqualTo(objectValue.getAge());
    }

    @Test
    @DisplayName("Redis 헬퍼로 객체 삭제 후 존재하지 않는지 확인")
    void deleteString_givenValidRedisHelper_assertNotExists() {
        String deleteKey = "test:delete";
        String deleteValue = "deleteValue";

        redisHelper.setString(deleteKey,deleteValue, Duration.ofSeconds(10));
        assertThat(redisHelper.exists(deleteKey)).isTrue();

        redisHelper.delete(deleteKey);
        assertThat(redisHelper.exists(deleteKey)).isFalse();
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 만료 시간 설정 및 연장")
    void expireString_givenValidRedisHelper_returnTTL() {
        String expireKey = "test:expire:" + UUID.randomUUID();
        String expireValue = "expireValue";

        try {
            redisHelper.setString(expireKey, expireValue, Duration.ofSeconds(10));

            Optional<Duration> initialTtl = redisHelper.getTTL(expireKey);
            assertThat(initialTtl).isPresent();
            assertThat(initialTtl.orElseThrow().getSeconds())
                    .isLessThanOrEqualTo(10)
                    .isGreaterThanOrEqualTo(1);

            redisHelper.expire(expireKey, Duration.ofSeconds(10));
            Optional<Duration> updatedTtl = redisHelper.getTTL(expireKey);

            assertThat(updatedTtl).isPresent();
            assertThat(updatedTtl.orElseThrow().getSeconds())
                    .isLessThanOrEqualTo(10)
                    .isGreaterThanOrEqualTo(1);
        } finally {
            redisHelper.delete(expireKey);
        }
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장 후 TTL 확인")
    void storeString_givenValidRedisHelper_assertTTLGreaterThan() {
        String key = "test:ttl:exists";
        String value = "someValue";

        redisHelper.setString(key, value, Duration.ofSeconds(5));

        Optional<Duration> ttl = redisHelper.getTTL(key);
        assertThat(ttl).isPresent();
        assertThat(ttl.orElseThrow().getSeconds()).isLessThanOrEqualTo(5).isGreaterThan(1);
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장 후 TTL이 만료가 없음을 확인")
    void storeString_givenValidRedisHelper_assertTTLHasNoExpiration() {
        String key = "test:ttl:infinite";
        String value = "persistentValue";

        redisHelper.setString(key, value); // 만료 시간 없이 설정
        Optional<Duration> ttl = redisHelper.getTTL(key);

        assertThat(ttl).isPresent();
        assertThat(ttl.orElseThrow().getSeconds()).isEqualTo(999_999_999);
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장 후 TTL이 비어있음을 확인")
    void storeString_givenValidRedisHelper_assertTTLEmpty() {
        String key = "test:ttl:nonexistent";

        Optional<Duration> ttl = redisHelper.getTTL(key);

        assertThat(ttl).isEmpty();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestDto implements Serializable {
        private String name;
        private int age;
    }
}