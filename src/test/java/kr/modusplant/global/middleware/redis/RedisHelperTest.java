package kr.modusplant.global.middleware.redis;

import kr.modusplant.global.middleware.redis.RedisHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class RedisHelperTest {
    @Autowired
    private RedisHelper redisHelper;

    @Test
    void testSetAndGetString() {
        String stringKey = "test:string";
        String stringValue = "stringValue";

        redisHelper.setString(stringKey, stringValue, Duration.ofSeconds(10));

        Optional<String> result = redisHelper.getString(stringKey);
        assertThat(result).isPresent().contains(stringValue);
    }

    @Test
    void testSetAndGetObject() {
        String objectKey = "test:object";
        TestDto objectValue = new TestDto("John",30);

        redisHelper.setObject(objectKey, objectValue, Duration.ofSeconds(10));

        Optional<TestDto> result = redisHelper.getObject(objectKey, TestDto.class);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(objectValue.getName());
        assertThat(result.get().getAge()).isEqualTo(objectValue.getAge());
    }

    @Test
    void testDeleteAndExists() {
        String deleteKey = "test:delete";
        String deleteValue = "deleteValue";

        redisHelper.setString(deleteKey,deleteValue, Duration.ofSeconds(10));
        assertThat(redisHelper.exists(deleteKey)).isTrue();

        redisHelper.delete(deleteKey);
        assertThat(redisHelper.exists(deleteKey)).isFalse();
    }

    @Test
    void testExpiredAndGetTTL() throws InterruptedException {
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


    static class TestDto implements Serializable {
        private String name;
        private int age;

        public TestDto() { }

        public TestDto(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}