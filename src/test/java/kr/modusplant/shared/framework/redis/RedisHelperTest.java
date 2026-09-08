package kr.modusplant.shared.framework.redis;

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
    @DisplayName("저장된 문자열로 Optional 반환")
    void testGetString_givenStoredStringValue_willReturnOptional() {
        // given
        String stringKey = "test:string";
        String stringValue = "stringValue";
        redisHelper.setString(stringKey, stringValue, Duration.ofSeconds(10));

        // when
        Optional<String> result = redisHelper.getString(stringKey);

        // then
        assertThat(result).isPresent().contains(stringValue);
    }

    @Test
    @DisplayName("저장된 객체로 Optional 반환")
    void testGetObject_givenStoredObjectValue_willReturnOptional() {
        // given
        String objectKey = "test:object";
        TestDto objectValue = new TestDto("John", 30);
        redisHelper.setObject(objectKey, objectValue, Duration.ofSeconds(10));

        // when
        Optional<TestDto> result = redisHelper.getObject(objectKey, TestDto.class);

        // then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getName()).isEqualTo(objectValue.getName());
        assertThat(result.orElseThrow().getAge()).isEqualTo(objectValue.getAge());
    }

    @Test
    @DisplayName("존재하는 키 삭제 활동 수행")
    void testDelete_givenExistingKey_willRemoveValue() {
        // given
        String deleteKey = "test:delete";
        String deleteValue = "deleteValue";
        redisHelper.setString(deleteKey, deleteValue, Duration.ofSeconds(10));
        assertThat(redisHelper.exists(deleteKey)).isTrue();

        // when
        redisHelper.delete(deleteKey);

        // then
        assertThat(redisHelper.exists(deleteKey)).isFalse();
    }

    @Test
    @DisplayName("키와 기간으로 TTL 갱신 활동 수행")
    void testExpire_givenKeyAndDuration_willUpdateTtl() {
        // given
        String expireKey = "test:expire:" + UUID.randomUUID();
        String expireValue = "expireValue";

        try {
            redisHelper.setString(expireKey, expireValue, Duration.ofSeconds(10));

            Optional<Duration> initialTtl = redisHelper.getTTL(expireKey);
            assertThat(initialTtl).isPresent();
            assertThat(initialTtl.orElseThrow().getSeconds())
                    .isLessThanOrEqualTo(10)
                    .isGreaterThanOrEqualTo(1);

            // when
            redisHelper.expire(expireKey, Duration.ofSeconds(10));
            Optional<Duration> updatedTtl = redisHelper.getTTL(expireKey);

            // then
            assertThat(updatedTtl).isPresent();
            assertThat(updatedTtl.orElseThrow().getSeconds())
                    .isLessThanOrEqualTo(10)
                    .isGreaterThanOrEqualTo(1);
        } finally {
            redisHelper.delete(expireKey);
        }
    }

    @Test
    @DisplayName("만료 있는 키로 Optional 반환")
    void testGetTTL_givenKeyWithExpiration_willReturnOptional() {
        // given
        String key = "test:ttl:exists";
        String value = "someValue";
        redisHelper.setString(key, value, Duration.ofSeconds(5));

        // when
        Optional<Duration> ttl = redisHelper.getTTL(key);

        // then
        assertThat(ttl).isPresent();
        assertThat(ttl.orElseThrow().getSeconds()).isLessThanOrEqualTo(5).isGreaterThan(1);
    }

    @Test
    @DisplayName("만료 없는 키로 Optional 반환")
    void testGetTTL_givenKeyWithoutExpiration_willReturnOptional() {
        // given
        String key = "test:ttl:infinite";
        String value = "persistentValue";
        redisHelper.setString(key, value); // 만료 시간 없이 설정

        // when
        Optional<Duration> ttl = redisHelper.getTTL(key);

        // then
        assertThat(ttl).isPresent();
        assertThat(ttl.orElseThrow().getSeconds()).isEqualTo(999_999_999);
    }

    @Test
    @DisplayName("존재하지 않는 키로 빈 Optional 반환")
    void testGetTTL_givenNonExistentKey_willReturnOptional() {
        // given
        String key = "test:ttl:nonexistent";

        // when
        Optional<Duration> ttl = redisHelper.getTTL(key);

        // then
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
