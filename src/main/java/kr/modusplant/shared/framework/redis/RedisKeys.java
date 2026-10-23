package kr.modusplant.shared.framework.redis;

import java.util.UUID;

/**
 * Redis 저장소 Key 를 관리하는 클래스
 */
public final class RedisKeys {

    private RedisKeys() {}

    /**
     * Prefix 상수와 동적으로 변하는 key 값을
     * RedisKey 로 생성하는 메소드
     *
     * @param prefix - 예시) RESET_PASSWORD_PREFIX 값
     * @param dynamicValue - 예시) 특정 유저의 email
     * @return redisKey
     */
    public static String generateRedisKeyWithLowerCase(String prefix, String dynamicValue) {
        return prefix + dynamicValue.toLowerCase();
    }

    public static String generateRedisKeyWithEqualCase(String prefix, String dynamicValue) {
        return prefix + dynamicValue;
    }

    public static String generateRedisKeyWithEqualCase(String prefix, UUID dynamicValue) {
        return prefix + dynamicValue;
    }
}

