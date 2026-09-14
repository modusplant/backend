package kr.modusplant.infrastructure.config.redis;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.shared.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryOnlyContext
public class RedisConfigTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.delete("testStringKey");
        redisTemplate.delete("testObjectKey");
        redisTemplate.delete("testSetKey");
        redisTemplate.delete("testListKey");
        redisTemplate.delete("testHashKey");
        redisTemplate.delete("testEnumKey");
    }

    @Test
    @DisplayName("StringRedisTemplate 왕복 시 문자열 반환")
    void testStringRedisTemplate_givenStringValue_willReturnString() {
        // given & when
        stringRedisTemplate.opsForValue().set("testStringKey", "testStringValue");

        // then
        String result = stringRedisTemplate.opsForValue().get("testStringKey");
        assertEquals("testStringValue", result);
    }

    @Test
    @DisplayName("RedisTemplate 왕복 시 문자열 반환")
    void testRedisTemplate_givenStringValue_willReturnString() {
        // given & when
        redisTemplate.opsForValue().set("testStringKey", "testStringValue");

        // then
        String result = (String) redisTemplate.opsForValue().get("testStringKey");
        assertEquals("testStringValue", result);
    }

    @Test
    @DisplayName("RedisTemplate 왕복 시 객체 반환")
    void testRedisTemplate_givenObjectValue_willReturnObject() {
        // given
        TestObject testObject = new TestObject("John", 28, LocalDateTime.now());

        // when
        redisTemplate.opsForValue().set("testObjectKey", testObject);
        TestObject result = (TestObject) redisTemplate.opsForValue().get("testObjectKey");

        // then
        assertNotNull(result);
        assertEquals(testObject.getName(), result.getName());
        assertEquals(testObject.getAge(), result.getAge());
        assertEquals(testObject.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    @DisplayName("RedisTemplate 왕복 시 집합 반환")
    void testRedisTemplate_givenSetValue_willReturnSet() {
        // given
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();

        // when
        setOps.add("testSetKey", "Item1", "Item2", "Item3");
        Set<Object> result = setOps.members("testSetKey");

        // then
        assertNotNull(result);
        assertTrue(result.contains("Item1"));
        assertTrue(result.contains("Item2"));
        assertTrue(result.contains("Item3"));
    }

    @Test
    @DisplayName("RedisTemplate 왕복 시 리스트 반환")
    void testRedisTemplate_givenListValue_willReturnList() {
        // given
        ListOperations<String, Object> listOps = redisTemplate.opsForList();

        // when
        listOps.rightPush("testListKey", "Item1");
        listOps.rightPush("testListKey", "Item2");
        listOps.rightPush("testListKey", "Item3");

        // then
        assertEquals(3, listOps.size("testListKey"));
        assertEquals("Item3", listOps.rightPop("testListKey"));
        assertEquals("Item2", listOps.rightPop("testListKey"));
        assertEquals("Item1", listOps.rightPop("testListKey"));
    }

    @Test
    @DisplayName("RedisTemplate 왕복 시 Map 반환")
    void testRedisTemplate_givenHashValue_willReturnMap() {
        // given
        Date birthday = new Date();
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John");
        map.put("age", 28);
        map.put("birthday", birthday);

        // when
        hashOps.putAll("testHashKey", map);

        // then
        assertEquals("John", hashOps.get("testHashKey", "name"));
        assertEquals(28, hashOps.get("testHashKey", "age"));
        assertEquals(birthday, hashOps.get("testHashKey", "birthday"));
    }

    @Test
    @DisplayName("RedisTemplate 왕복 시 열거형 반환")
    void testRedisTemplate_givenEnumValue_willReturnEnum() {
        // given & when
        redisTemplate.opsForValue().set("testEnumKey", Role.USER);
        Role role = (Role) redisTemplate.opsForValue().get("testEnumKey");

        // then
        assertNotNull(role);
        assertEquals(Role.USER, role);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestObject {
        private String name;
        private int age;
        private LocalDateTime createdAt;
    }
}
