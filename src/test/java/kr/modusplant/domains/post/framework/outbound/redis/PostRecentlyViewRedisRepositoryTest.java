package kr.modusplant.domains.post.framework.outbound.redis;

import kr.modusplant.domains.post.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.post.domain.vo.PostId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_GOOGLE_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PostRecentlyViewRedisRepositoryTest implements PostIdTestUtils {
    private final StringRedisTemplate stringRedisTemplate = Mockito.mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    private final ZSetOperations<String,String> zSetOperations = Mockito.mock(ZSetOperations.class);
    private final PostRecentlyViewRedisRepository postRecentlyViewRedisRepository = new PostRecentlyViewRedisRepository(stringRedisTemplate);

    private static final String KEY_FORMAT = "recentlyView:member:%s:posts";
    private static final long TTL_DAYS = 7;

    @Test
    @DisplayName("유효한 파라미터로 조회 기록 저장")
    void testRecordViewPost_givenValidParameters_willRecordViewPost() {
        // given
        long beforeTime = System.currentTimeMillis() / 1000;
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postRecentlyViewRedisRepository.recordViewPost(MEMBER_BASIC_USER_UUID,testPostId);

        // then
        verify(zSetOperations).add(
                eq(key),
                eq(testPostId.getValue()),
                doubleThat(score -> score >= beforeTime)
        );
        verify(stringRedisTemplate).expire(key,TTL_DAYS, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("회원 식별자가 null일 때 조회 기록 저장 생략")
    void testRecordViewPost_givenNullMemberId_willSkipRecord() {
        // given & when
        postRecentlyViewRedisRepository.recordViewPost(null,testPostId);

        // then
        verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
        verify(stringRedisTemplate,never()).expire(anyString(),anyLong(),any(TimeUnit.class));
    }

    @Test
    @DisplayName("저장된 조회 기록이 있을 때 List 반환")
    void testGetRecentlyViewPostIds_givenStoredRecords_willReturnList() {
        // given
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        int page = 0;
        int size = 2;
        long offset = (long) page * size;
        Set<String> mockUlids = new LinkedHashSet<>();
        mockUlids.add(testPostId.getValue());
        mockUlids.add(testPostId2.getValue());
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.reverseRange(key,offset,offset + size - 1)).willReturn(mockUlids);

        // when
        List<PostId> results = postRecentlyViewRedisRepository.getRecentlyViewPostIds(MEMBER_BASIC_USER_UUID,page,size);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getValue()).isEqualTo(testPostId.getValue());
        assertThat(results)
                .containsExactly(testPostId,testPostId2);
        verify(zSetOperations).reverseRange(key, offset,offset + size - 1);
    }

    @Test
    @DisplayName("저장된 조회 기록이 없을 때 빈 List 반환")
    void testGetRecentlyViewPostIds_givenNoStoredRecords_willReturnEmptyList() {
        // given
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        int page = 0;
        int size = 5;
        long offset = (long) page * size;
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.reverseRange(key,offset,offset + size - 1)).willReturn(null);

        // when
        List<PostId> results = postRecentlyViewRedisRepository.getRecentlyViewPostIds(MEMBER_BASIC_USER_UUID,page,size);

        // then
        assertThat(results).isEmpty();
        verify(zSetOperations).reverseRange(key,offset,offset + size - 1);
    }

    @Test
    @DisplayName("회원 식별자로 long 반환")
    void testGetTotalRecentlyViewPosts_givenMemberId_willReturnLong() {
        // given
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);
        given(zSetOperations.zCard(key)).willReturn(15L);

        // when
        long totalCount = postRecentlyViewRedisRepository.getTotalRecentlyViewPosts(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(totalCount).isEqualTo(15L);
        verify(zSetOperations).zCard(key);

    }

    @Test
    @DisplayName("회원 식별자와 게시글 식별자로 최근 본 게시글 삭제")
    void testRemoveViewPost_givenMemberIdAndPostId_willRemoveViewPost() {
        // given
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postRecentlyViewRedisRepository.removeViewPost(MEMBER_BASIC_USER_UUID,testPostId);

        // then
        verify(zSetOperations).remove(key,testPostId.getValue());
    }

    @Test
    @DisplayName("게시글 식별자로 모든 회원의 최근 본 게시글 삭제")
    void testRemovePostFromAllMembers_givenPostId_willRemovePostFromAllMembers() {
        // given
        List<String> keys = List.of(
                "recentlyView:member:"+MEMBER_BASIC_USER_UUID+":posts",
                "recentlyView:member:"+MEMBER_GOOGLE_USER_UUID+":posts"
        );
        @SuppressWarnings("unchecked")
        Cursor<String> cursor = Mockito.mock(Cursor.class);
        given(cursor.hasNext()).willReturn(true, true, false);
        given(cursor.next()).willReturn(keys.get(0), keys.get(1));
        given(stringRedisTemplate.scan(any(ScanOptions.class))).willReturn(cursor);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postRecentlyViewRedisRepository.removePostFromAllMembers(testPostId);

        // then
        for (String key : keys) {
            verify(zSetOperations).remove(key,testPostId.getValue());
        }
        verify(cursor).close();
    }
}
