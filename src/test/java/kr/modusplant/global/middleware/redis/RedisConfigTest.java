package kr.modusplant.global.middleware.redis;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.global.enums.Role;
import org.junit.jupiter.api.BeforeEach;
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
    void testStringWithStringRedisTemplate() {
        stringRedisTemplate.opsForValue().set("testStringKey", "testStringValue");

        String result = stringRedisTemplate.opsForValue().get("testStringKey");
        assertEquals("testStringValue",result);
    }

    @Test
    void testStringWithRedisTemplate() {
        redisTemplate.opsForValue().set("testStringKey", "testStringValue");

        String result = (String) redisTemplate.opsForValue().get("testStringKey");
        assertEquals("testStringValue",result);
    }

    @Test
    void testObjectWithRedisTemplate() {
        TestObject testObject = new TestObject("John",28, LocalDateTime.now());

        redisTemplate.opsForValue().set("testObjectKey",testObject);

        TestObject result = (TestObject) redisTemplate.opsForValue().get("testObjectKey");

        assertNotNull(result);
        assertEquals(testObject.getName(), result.getName());
        assertEquals(testObject.getAge(), result.getAge());
        assertEquals(testObject.getCreatedAt(), result.getCreatedAt());
    }


    @Test
    void testSetWithRedisTemplate() {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        setOps.add("testSetKey","Item1", "Item2", "Item3");

        Set<Object> result = setOps.members("testSetKey");
        assertNotNull(result);
        assertTrue(result.contains("Item1"));
        assertTrue(result.contains("Item2"));
        assertTrue(result.contains("Item3"));
    }

    @Test
    void testListWithRedisTemplate() {
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
    void testHashWithRedisTemplate() {
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
    void testEnumWithRedisTemplate() {
        redisTemplate.opsForValue().set("testEnumKey", Role.USER);

        Role role = (Role) redisTemplate.opsForValue().get("testEnumKey");

        assertNotNull(role);
        assertEquals(Role.USER,role);
    }


    private static class TestObject {
        private String name;
        private int age;
        private LocalDateTime createdAt;

        public TestObject() {}

        public TestObject(String name, int age, LocalDateTime createdAt) {
            this.name = name;
            this.age = age;
            this.createdAt = createdAt;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
