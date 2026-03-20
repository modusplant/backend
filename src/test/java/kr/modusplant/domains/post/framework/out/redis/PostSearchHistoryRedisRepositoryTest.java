package kr.modusplant.domains.post.framework.out.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostSearchHistoryRedisRepositoryTest {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ZSetOperations<String,String> zSetOperations;

    @InjectMocks
    private PostSearchHistoryRedisRepository postSearchHistoryRedisRepository;

    private static final String KEY_FORMAT = "searchHistory:member:%s";
    private static final int MAX_HISTORY = 20;
    private static final long TTL_DAYS = 30;

    @Test
    @DisplayName("검색어를 저장하고 TTL 설정")
    void testSaveSearchKeyword_givenMemberUuidAndKeyword_willSaveKeyword() {
        // given
        long beforeTime = System.currentTimeMillis() / 1000;
        String keyword = "식물";
        String key = String.format(KEY_FORMAT,MEMBER_BASIC_USER_UUID);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postSearchHistoryRedisRepository.saveSearchKeyword(MEMBER_BASIC_USER_UUID,keyword);

        // then
        verify(zSetOperations).add(
                eq(key),
                eq(keyword),
                doubleThat(score -> score >= beforeTime)
        );
        verify(stringRedisTemplate).expire(key,TTL_DAYS, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("검색어 개수가 최대를 넘었을 때, 가장 오래된 검색어를 삭제한 후 검색어를 저장하고 TTL 설정")
    void testSaveSearchKeyword_givenMemberUuidAndKeyword_willSaveKeywordAndDeleteOldestKeyword() {
        // given
        String keyword = "식물";
        String key = String.format(KEY_FORMAT,MEMBER_BASIC_USER_UUID);
        long currentSize = MAX_HISTORY + 1L;
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.zCard(key)).willReturn(currentSize);

        // when
        postSearchHistoryRedisRepository.saveSearchKeyword(MEMBER_BASIC_USER_UUID, keyword);

        // then
        verify(zSetOperations).removeRange(key, 0, currentSize - MAX_HISTORY - 1);
        verify(stringRedisTemplate).expire(key, TTL_DAYS, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("회원 id가 null이거나 keyword가 문자열가 아니면 기록하지 않는다")
    void testSaveSearchKeyword_givenNullMemberUuidAndKeyword_willNotSaveKeyword() {
        // given & when
        postSearchHistoryRedisRepository.saveSearchKeyword(null," ");

        // then
        verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
        verify(stringRedisTemplate,never()).expire(anyString(),anyLong(),any(TimeUnit.class));
    }

    @Test
    @DisplayName("검색 기록 조회 시 30일 지난 검색어를 제거하고 최신순으로 반환한다")
    void testGetSearchHistory_givenMemberUuidAndSize_willRemoveExpiredAndReturnKeywords() {
        // given
        int size = 10;
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        Set<String> keywords = new LinkedHashSet<>(List.of("식물", "벌레", "화분"));

        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.reverseRange(key, 0, size - 1)).willReturn(keywords);

        // when
        List<String> result = postSearchHistoryRedisRepository.getSearchHistory(MEMBER_BASIC_USER_UUID, size);

        // then
        verify(zSetOperations).removeRangeByScore(
                eq(key),
                eq(0.0),
                doubleThat(score -> score <= System.currentTimeMillis())
        );
        verify(zSetOperations).reverseRange(key, 0, size - 1);
        assertThat(result).containsExactlyElementsOf(keywords);
    }


    @Test
    @DisplayName("검색어로 검색 기록을 단건 삭제한다.")
    void testRemoveSearchKeyword_givenMemberUuidAndKeyword_willRemoveKeyword() {
        // given
        String keyword = "식물";
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postSearchHistoryRedisRepository.removeSearchKeyword(MEMBER_BASIC_USER_UUID,keyword);

        // then
        verify(zSetOperations).remove(key,keyword);
    }

    @Test
    @DisplayName("전체 검색 기록을 삭제한다.")
    void testRemoveAllSearchHistory_givenMemberUuid_willRemoveHistory() {
        // given
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);

        // when
        postSearchHistoryRedisRepository.removeAllSearchHistory(MEMBER_BASIC_USER_UUID);

        // then
        verify(stringRedisTemplate).delete(key);
    }

}