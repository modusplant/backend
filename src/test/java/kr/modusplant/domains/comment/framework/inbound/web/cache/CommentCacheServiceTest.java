package kr.modusplant.domains.comment.framework.inbound.web.cache;

import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.inbound.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

class CommentCacheServiceTest {

    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final CommentQueryRepository commentQueryRepository = Mockito.mock(CommentQueryRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final CommentCacheService service = new CommentCacheService(
            postJpaRepository, memberJpaRepository, commentQueryRepository, passwordEncoder);

    private final PostId testPostId = PostId.create(TEST_POST_ULID);
    private final MemberId testMemberId = MemberId.fromUuid(MEMBER_BASIC_USER_UUID);

    private final PostEntity postEntity = Mockito.mock(PostEntity.class);
    private final LocalDateTime postUpdatedAt = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
    private final String postEtagSource = "post-etag-source";
    private final String expectedETagSource = postEtagSource + LocalDateTime.MIN;

    private void givenPostFoundWithNoCommentActivity() {
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.of(postEntity));
        given(postEntity.getUpdatedAtAsTruncatedToSeconds()).willReturn(postUpdatedAt);
        given(postEntity.getETagSource()).willReturn(postEtagSource);
        given(commentQueryRepository.findLatestUpdatedAtByPost(testPostId)).willReturn(Optional.empty());
    }

    @Test
    @DisplayName("존재하지 않는 postUlid로 캐시 데이터 조회 시 NotFoundEntityException 발생")
    void testGetCacheDataByPost_givenNonExistentPost_willThrowNotFoundEntityException() {
        // given
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> service.getCacheData(null, null, testPostId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        assertThat(ex.getEntityName()).isEqualTo("post");
    }

    @Test
    @DisplayName("If-None-Match 헤더가 없으면 캐시 불가능한 결과 반환")
    void testGetCacheDataByPost_givenNoIfNoneMatch_willReturnNotCacheable() {
        // given
        givenPostFoundWithNoCommentActivity();
        given(passwordEncoder.encode(expectedETagSource)).willReturn("encoded-tag");

        // when
        CommentCacheData result = service.getCacheData(null, null, testPostId);

        // then
        assertThat(result.isCacheable()).isFalse();
        assertThat(result.entityTag()).isEqualTo("encoded-tag");
        assertThat(result.lastModifiedAt()).isEqualTo(postUpdatedAt);
    }

    @Test
    @DisplayName("If-None-Match의 ETag가 일치하지 않으면 캐시 불가능한 결과 반환")
    void testGetCacheDataByPost_givenNonMatchingETag_willReturnNotCacheable() {
        // given
        givenPostFoundWithNoCommentActivity();
        given(passwordEncoder.matches(expectedETagSource, "abc123")).willReturn(false);
        given(passwordEncoder.encode(expectedETagSource)).willReturn("encoded-tag");

        // when
        CommentCacheData result = service.getCacheData("\"abc123\"", null, testPostId);

        // then
        assertThat(result.isCacheable()).isFalse();
    }

    @Test
    @DisplayName("ETag가 일치하고 If-Modified-Since가 없으면 캐시 가능한 결과 반환")
    void testGetCacheDataByPost_givenMatchingETagAndNoIfModifiedSince_willReturnCacheable() {
        // given
        givenPostFoundWithNoCommentActivity();
        given(passwordEncoder.matches(expectedETagSource, "abc123")).willReturn(true);
        given(passwordEncoder.encode(expectedETagSource)).willReturn("encoded-tag");

        // when
        CommentCacheData result = service.getCacheData("\"abc123\"", null, testPostId);

        // then
        assertThat(result.isCacheable()).isTrue();
    }

    @Test
    @DisplayName("ETag가 일치하고 If-Modified-Since가 마지막 변경 이전이면 캐시 불가능한 결과 반환")
    void testGetCacheDataByPost_givenMatchingETagAndIfModifiedSinceBeforeLastModified_willReturnNotCacheable() {
        // given
        givenPostFoundWithNoCommentActivity();
        given(passwordEncoder.matches(expectedETagSource, "abc123")).willReturn(true);
        given(passwordEncoder.encode(expectedETagSource)).willReturn("encoded-tag");

        // when: GMT 00:00 -> Asia/Seoul 09:00, before postUpdatedAt(12:00)
        CommentCacheData result = service.getCacheData("\"abc123\"", "Sun, 01 Jun 2025 00:00:00 GMT", testPostId);

        // then
        assertThat(result.isCacheable()).isFalse();
    }

    @Test
    @DisplayName("ETag가 일치하고 If-Modified-Since가 마지막 변경 이후이면 캐시 가능한 결과 반환")
    void testGetCacheDataByPost_givenMatchingETagAndIfModifiedSinceAfterLastModified_willReturnCacheable() {
        // given
        givenPostFoundWithNoCommentActivity();
        given(passwordEncoder.matches(expectedETagSource, "abc123")).willReturn(true);
        given(passwordEncoder.encode(expectedETagSource)).willReturn("encoded-tag");

        // when: GMT 04:00 -> Asia/Seoul 13:00, after postUpdatedAt(12:00)
        CommentCacheData result = service.getCacheData("\"abc123\"", "Sun, 01 Jun 2025 04:00:00 GMT", testPostId);

        // then
        assertThat(result.isCacheable()).isTrue();
    }

    @Test
    @DisplayName("post 변경 시각보다 댓글의 최신 변경 시각이 더 최근이면 그 시각을 기준으로 함")
    void testGetCacheDataByPost_givenLatestCommentActivityAfterPost_willUseCommentTimestamp() {
        // given
        LocalDateTime latestCommentUpdatedAt = postUpdatedAt.plusDays(1);
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.of(postEntity));
        given(postEntity.getUpdatedAtAsTruncatedToSeconds()).willReturn(postUpdatedAt);
        given(postEntity.getETagSource()).willReturn(postEtagSource);
        given(commentQueryRepository.findLatestUpdatedAtByPost(testPostId)).willReturn(Optional.of(latestCommentUpdatedAt));
        given(passwordEncoder.encode(postEtagSource + latestCommentUpdatedAt)).willReturn("encoded-tag");

        // when
        CommentCacheData result = service.getCacheData(null, null, testPostId);

        // then
        assertThat(result.lastModifiedAt()).isEqualTo(latestCommentUpdatedAt);
    }

    @Test
    @DisplayName("존재하지 않는 memberUuid로 캐시 데이터 조회 시 NotFoundEntityException 발생")
    void testGetCacheDataByMember_givenNonExistentMember_willThrowNotFoundEntityException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> service.getCacheData(null, null, testMemberId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER);
        assertThat(ex.getEntityName()).isEqualTo("member");
    }

    @Test
    @DisplayName("유효한 memberUuid로 캐시 데이터 조회 성공")
    void testGetCacheDataByMember_givenValidMember_willReturnCacheData() {
        // given
        MemberEntity memberEntity = Mockito.mock(MemberEntity.class);
        String memberEtagSource = "member-etag-source";
        LocalDateTime memberLastModifiedAt = LocalDateTime.of(2025, 7, 1, 0, 0, 0);
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(memberEntity.getETagSource()).willReturn(memberEtagSource);
        given(memberEntity.getLastModifiedAtAsTruncatedToSeconds()).willReturn(memberLastModifiedAt);
        given(passwordEncoder.encode(memberEtagSource)).willReturn("encoded-tag");

        // when
        CommentCacheData result = service.getCacheData(null, null, testMemberId);

        // then
        assertThat(result.isCacheable()).isFalse();
        assertThat(result.entityTag()).isEqualTo("encoded-tag");
        assertThat(result.lastModifiedAt()).isEqualTo(memberLastModifiedAt);
    }
}
