package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.post.domain.vo.PostId;
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
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_GOOGLE_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostRecentlyViewRedisRepositoryTest implements PostIdTestUtils {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ZSetOperations<String,String> zSetOperations;

    @InjectMocks
    private PostRecentlyViewRedisRepository postRecentlyViewRedisRepository;

    private static final String KEY_FORMAT = "recentlyView:member:%s:posts";
    private static final long TTL_DAYS = 7;

    @Test
    @DisplayName("조회 기록을 저장하고 7일로 TTL을 설정한다.")
    void testRecordViewPost_givenMemberIdAndPostId_willSaveRecord() {
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
    @DisplayName("회원 id가 null이면 기록하지 않는다.")
    void testRecordViewPost_givenNullMemberIdAndPostId_willNotSaveRecord() {
        // given & when
        postRecentlyViewRedisRepository.recordViewPost(null,testPostId);

        // then
        verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
        verify(stringRedisTemplate,never()).expire(anyString(),anyLong(),any(TimeUnit.class));
    }

    @Test
    @DisplayName("원하는 페이지의 최근 본 게시글 조회 기록 목록을 가져온다.")
    void testGetRecentlyViewPostIds_givenMemberIdAndPostId_willReturnPostIds() {
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
    @DisplayName("빈 최근 본 게시글 조회 기록 목록을 가져온다")
    void testGetRecentlyViewPostIds_givenMemberIdAndPostId_willReturnEmptyPostIdList() {
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
    @DisplayName("최근 본 게시글 전체 개수를 가져온다")
    void getTotalRecentlyViewPosts_givenMemberId_willReturnTotalCount() {
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
    @DisplayName("회원 Id와 게시글 Id로 최근 본 게시글을 삭제한다")
    void testRemoveViewPost_givenMemberIdAndPostId_willRemovePost() {
        // given
        String key = String.format(KEY_FORMAT, MEMBER_BASIC_USER_UUID);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postRecentlyViewRedisRepository.removeViewPost(MEMBER_BASIC_USER_UUID,testPostId);

        // then
        verify(zSetOperations).remove(key,testPostId.getValue());
    }

    @Test
    @DisplayName("게시글 id로 최근 본 게시글을 모두 삭제한다")
    void testRemovePostFromAllMembers_givenPostId_willRemovePosts() {
        // given
        Set<String> keys = Set.of(
                "recentlyView:member:"+MEMBER_BASIC_USER_UUID+":posts",
                "recentlyView:member:"+MEMBER_GOOGLE_USER_UUID+":posts"
        );
        given(stringRedisTemplate.keys("recentlyView:member:*:posts")).willReturn(keys);
        given(stringRedisTemplate.opsForZSet()).willReturn(zSetOperations);

        // when
        postRecentlyViewRedisRepository.removePostFromAllMembers(testPostId);

        // then
        for (String key : keys) {
            verify(zSetOperations).remove(key,testPostId.getValue());
        }
    }
}