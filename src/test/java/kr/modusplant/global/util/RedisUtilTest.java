package kr.modusplant.global.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisUtilTest {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    void testSetAndGetString() {
        String stringKey = "test:string";
        String stringValue = "stringValue";

        redisUtil.setString(stringKey, stringValue, Duration.ofSeconds(10));

        Optional<String> result = redisUtil.getString(stringKey);
        assertThat(result).isPresent().contains(stringValue);
    }

    @Test
    void testSetAndGetObject() {
        String objectKey = "test:object";
        TestDto objectValue = new TestDto("John",30);

        redisUtil.setObject(objectKey, objectValue, Duration.ofSeconds(10));

        Optional<TestDto> result = redisUtil.getObject(objectKey, TestDto.class);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(objectValue.getName());
        assertThat(result.get().getAge()).isEqualTo(objectValue.getAge());
    }

    @Test
    void testDeleteAndExists() {
        String deleteKey = "test:delete";
        String deleteValue = "deleteValue";

        redisUtil.setString(deleteKey,deleteValue, Duration.ofSeconds(10));
        assertThat(redisUtil.exists(deleteKey)).isTrue();

        redisUtil.delete(deleteKey);
        assertThat(redisUtil.exists(deleteKey)).isFalse();
    }

    @Test
    void testExpiredAndGetTTL() throws InterruptedException {
        String expireKey = "test:expire";
        String expireValue = "expireValue";

        redisUtil.setString(expireKey, expireValue, Duration.ofSeconds(5));
        Thread.sleep(2000);

        Optional<Duration> ttl = redisUtil.getTTL(expireKey);
        assertThat(ttl).isPresent();
        assertThat(ttl.get().getSeconds()).isLessThan(5).isGreaterThan(1);

        redisUtil.expire(expireKey, Duration.ofSeconds(10));
        Optional<Duration> ttl2 = redisUtil.getTTL(expireKey);
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