package kr.modusplant.framework.outbound.persistence.redis;

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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class RedisHelperTest {
    @Autowired
    private RedisHelper redisHelper;

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장")
    void storeString_withValidRedisHelper_returnString() {
        String stringKey = "test:string";
        String stringValue = "stringValue";

        redisHelper.setString(stringKey, stringValue, Duration.ofSeconds(10));

        Optional<String> result = redisHelper.getString(stringKey);
        assertThat(result).isPresent().contains(stringValue);
    }

    @Test
    @DisplayName("Redis 헬퍼로 객체 저장")
    void storeObject_withValidRedisHelper_returnObject() {
        String objectKey = "test:object";
        TestDto objectValue = new TestDto("John",30);

        redisHelper.setObject(objectKey, objectValue, Duration.ofSeconds(10));

        Optional<TestDto> result = redisHelper.getObject(objectKey, TestDto.class);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(objectValue.getName());
        assertThat(result.get().getAge()).isEqualTo(objectValue.getAge());
    }

    @Test
    @DisplayName("Redis 헬퍼로 객체 삭제 후 존재하지 않는지 확인")
    void deleteString_withValidRedisHelper_assertNotExists() {
        String deleteKey = "test:delete";
        String deleteValue = "deleteValue";

        redisHelper.setString(deleteKey,deleteValue, Duration.ofSeconds(10));
        assertThat(redisHelper.exists(deleteKey)).isTrue();

        redisHelper.delete(deleteKey);
        assertThat(redisHelper.exists(deleteKey)).isFalse();
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 만료")
    void expireString_withValidRedisHelper_returnTTL() throws InterruptedException {
        String expireKey = "test:expire";
        String expireValue = "expireValue";

        redisHelper.setString(expireKey, expireValue, Duration.ofSeconds(5));
        Thread.sleep(2000);

        Optional<Duration> ttl = redisHelper.getTTL(expireKey);
        assertThat(ttl).isPresent();
        assertThat(ttl.get().getSeconds()).isLessThan(5).isGreaterThan(1);

        redisHelper.expire(expireKey, Duration.ofSeconds(10));
        Optional<Duration> ttl2 = redisHelper.getTTL(expireKey);
        assertThat(ttl2).isPresent();
        assertThat(ttl2.get().getSeconds()).isGreaterThan(5);
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장 후 TTL 확인")
    void storeString_withValidRedisHelper_assertTTLGreaterThan() throws InterruptedException {
        String key = "test:ttl:exists";
        String value = "someValue";

        redisHelper.setString(key, value, Duration.ofSeconds(5));
        Thread.sleep(1000);

        Optional<Duration> ttl = redisHelper.getTTL(key);
        assertThat(ttl).isPresent();
        assertThat(ttl.get().getSeconds()).isLessThan(5).isGreaterThan(1);
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장 후 TTL이 만료가 없음을 확인")
    void storeString_withValidRedisHelper_assertTTLHasNoExpiration() {
        String key = "test:ttl:infinite";
        String value = "persistentValue";

        redisHelper.setString(key, value); // 만료 시간 없이 설정
        Optional<Duration> ttl = redisHelper.getTTL(key);

        assertThat(ttl).isPresent();
        assertThat(ttl.get().getSeconds()).isEqualTo(999_999_999);
    }

    @Test
    @DisplayName("Redis 헬퍼로 문자열 저장 후 TTL이 비어있음을 확인")
    void storeString_withValidRedisHelper_assertTTLEmpty() {
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