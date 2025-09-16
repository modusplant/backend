package kr.modusplant.infrastructure.config.redis;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.domains.identity.framework.legacy.enums.Role;
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
    private RedisTemplate<String,Object> redisTemplate;

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
    @DisplayName("문자열 Redis 템플릿으로 문자열 저장")
    void storeString_givenValidStringRedisTemplate_willReturnString() {
        stringRedisTemplate.opsForValue().set("testStringKey", "testStringValue");

        String result = stringRedisTemplate.opsForValue().get("testStringKey");
        assertEquals("testStringValue",result);
    }

    @Test
    @DisplayName("Redis 템플릿으로 문자열 저장")
    void storeString_givenValidRedisTemplate_willReturnString() {
        redisTemplate.opsForValue().set("testStringKey", "testStringValue");

        String result = (String) redisTemplate.opsForValue().get("testStringKey");
        assertEquals("testStringValue",result);
    }

    @Test
    @DisplayName("Redis 템플릿으로 객체 저장")
    void storeObject_givenValidRedisTemplate_willReturnObject() {
        TestObject testObject = new TestObject("John",28, LocalDateTime.now());

        redisTemplate.opsForValue().set("testObjectKey",testObject);

        TestObject result = (TestObject) redisTemplate.opsForValue().get("testObjectKey");

        assertNotNull(result);
        assertEquals(testObject.getName(), result.getName());
        assertEquals(testObject.getAge(), result.getAge());
        assertEquals(testObject.getCreatedAt(), result.getCreatedAt());
    }


    @Test
    @DisplayName("Redis 템플릿으로 집합 저장")
    void storeSet_givenValidRedisTemplate_willReturnSet() {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        setOps.add("testSetKey","Item1", "Item2", "Item3");

        Set<Object> result = setOps.members("testSetKey");
        assertNotNull(result);
        assertTrue(result.contains("Item1"));
        assertTrue(result.contains("Item2"));
        assertTrue(result.contains("Item3"));
    }

    @Test
    @DisplayName("Redis 템플릿으로 리스트 저장")
    void storeList_givenValidRedisTemplate_willReturnList() {
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        listOps.rightPush("testListKey","Item1");
        listOps.rightPush("testListKey","Item2");
        listOps.rightPush("testListKey","Item3");

        assertEquals(3, listOps.size("testListKey"));
        assertEquals("Item3",listOps.rightPop("testListKey"));
        assertEquals("Item2",listOps.rightPop("testListKey"));
        assertEquals("Item1",listOps.rightPop("testListKey"));
    }

    @Test
    @DisplayName("Redis 템플릿으로 해시 저장")
    void storeHash_givenValidRedisTemplate_willReturnHash() {
        Date birthday = new Date();

        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Map<String, Object> map = new HashMap<>();
        map.put("name","John");
        map.put("age",28);
        map.put("birthday",birthday);

        hashOps.putAll("testHashKey",map);

        assertEquals("John",hashOps.get("testHashKey","name"));
        assertEquals(28,hashOps.get("testHashKey","age"));
        assertEquals(birthday,hashOps.get("testHashKey","birthday"));
    }

    @Test
    @DisplayName("Redis 템플릿으로 열거형 저장")
    void storeEnum_givenValidRedisTemplate_willReturnEnum() {
        redisTemplate.opsForValue().set("testEnumKey", Role.USER);

        Role role = (Role) redisTemplate.opsForValue().get("testEnumKey");

        assertNotNull(role);
        assertEquals(Role.USER,role);
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
