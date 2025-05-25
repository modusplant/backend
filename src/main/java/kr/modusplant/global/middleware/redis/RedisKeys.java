package kr.modusplant.global.middleware.redis;

/**
 * Redis 저장소 Key 를 관리하는 클래스
 */
public final class RedisKeys {

    private RedisKeys() {}

    public static final String RESET_PASSWORD_PREFIX = "auth:reset-password:email:";    // 비밀번호 재설정 key prefix

    /**
     * Prefix 상수와 동적으로 변하는 key 값을
     * RedisKey 로 생성하는 메소드
     *
     * @param prefix
     * @Param dynamicValue - 예시) 특정 유저의 email
     * @return redisKey
     */
    public static String generateRedisKey(String prefix, String dynamicValue) {
        return prefix + dynamicValue.toLowerCase();
    }
}

