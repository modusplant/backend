package kr.modusplant.shared.framework.redis;

import java.util.UUID;

/**
 * Redis 저장소 Key 를 관리하는 클래스
 */
public final class RedisKeys {

    private RedisKeys() {}

    public static final String BLACKLIST_ACCESS_TOKEN_PREFIX = "blacklist:access_token:"; // 접근 토큰 블랙리스팅 key prefix
    public static final String MEMBER_SEARCH_HISTORY_PREFIX = "searchHistory:member:";    // 회원 검색 기록 key prefix
    public static final String RESET_PASSWORD_PREFIX = "auth:reset-password:email:";      // 비밀번호 재설정 key prefix

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

