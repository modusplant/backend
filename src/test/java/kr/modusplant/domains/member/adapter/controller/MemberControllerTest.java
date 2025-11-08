package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.adapter.mapper.MemberProfileMapperImpl;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberRepositoryJpaAdapter;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.framework.out.aws.service.S3FileService;
import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.CommCommentLikeEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommCommentLikeJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.event.consumer.CommentEventConsumer;
import kr.modusplant.infrastructure.event.consumer.PostEventConsumer;
import kr.modusplant.shared.event.common.util.PostLikeEventTestUtils;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentLikeRecordTestUtils.TEST_MEMBER_COMMENT_LIKE_RECORD;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentUnlikeRecordTestUtils.TEST_MEMBER_COMMENT_UNLIKE_RECORD;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberNicknameUpdateRecordTestUtils.TEST_MEMBER_NICKNAME_UPDATE_RECORD;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostLikeRecordTestUtils.TEST_MEMBER_POST_LIKE_DTO;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostUnlikeRecordTestUtils.TEST_MEMBER_POST_UNLIKE_RECORD;
import static kr.modusplant.domains.member.common.util.usecase.request.MemberRegisterRequestTestUtils.testMemberRegisterRequest;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.shared.event.common.util.CommentLikeEventTestUtils.testCommentLikeEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberControllerTest implements MemberTestUtils, PostLikeEventTestUtils, CommPostEntityTestUtils {
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl();
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final MemberProfileRepository memberProfileRepository = Mockito.mock(MemberProfileRepository.class);
    private final CommPostLikeJpaRepository commPostLikeRepository = Mockito.mock(CommPostLikeJpaRepository.class);
    private final CommPostJpaRepository commPostRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommCommentLikeJpaRepository commCommentLikeRepository = Mockito.mock(CommCommentLikeJpaRepository.class);
    private final CommCommentJpaRepository commCommentRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final TargetPostIdRepository targetPostIdRepository = Mockito.mock(TargetPostIdRepository.class);
    private final TargetCommentIdRepository targetCommentIdRepository = Mockito.mock(TargetCommentIdRepository.class);
    private final EventBus eventBus = new EventBus();
    private final PostEventConsumer postEventConsumer = new PostEventConsumer(eventBus, commPostLikeRepository, commPostRepository);
    private final CommentEventConsumer commentEventConsumer = new CommentEventConsumer(eventBus, commCommentLikeRepository, commCommentRepository);
    private final MemberController memberController = new MemberController(s3FileService, memberMapper, memberProfileMapper, memberRepository, memberProfileRepository, targetPostIdRepository, targetCommentIdRepository, eventBus);

    @Test
    @DisplayName("중복된 닉네임으로 인해 register로 회원 등록 실패")
    void testValidateBeforeRegister_givenAlreadyExistedNickname_willThrowException() {
        // given
        given(memberRepository.isNicknameExist(any())).willReturn(true);

        // when & then
        EntityExistsException alreadyExistedNicknameException = assertThrows(
                EntityExistsException.class, () -> memberController.register(testMemberRegisterRequest));
        assertThat(alreadyExistedNicknameException.getMessage()).isEqualTo(ALREADY_EXISTED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("중복된 닉네임으로 인해 overrideProfile로 닉네임 갱신 실패")
    void testValidateBeforeOverrideProfile_givenAlreadyExistedProfile_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(memberProfileRepository.isIdExist(any())).willReturn(true);
        given(memberRepository.getByNickname(any())).willReturn(Optional.of(Member.create(MemberId.generate(), testMemberActiveStatus, testMemberNickname, testMemberBirthDate)));

        // when & then
        EntityExistsException alreadyExistedNicknameException = assertThrows(
                EntityExistsException.class, () -> memberController.overrideProfile(TEST_MEMBER_NICKNAME_UPDATE_RECORD));
        assertThat(alreadyExistedNicknameException.getMessage()).isEqualTo(ALREADY_EXISTED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("likePost로 게시글 좋아요")
    void testLikePost_givenValidParameter_willLikePost() {
        // given
        UUID memberId = testPostLikeEvent.getMemberId();
        String postId = testPostLikeEvent.getPostId();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(true);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        given(commPostLikeRepository.save(postLikeEntity)).willReturn(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.likePost(TEST_MEMBER_POST_LIKE_DTO);

        // then
        verify(commPostLikeRepository, times(1)).save(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likePost로 게시글 좋아요")
    void testValidateBeforeLikeOrUnlikePost_givenAlreadyLikedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likePost(TEST_MEMBER_POST_LIKE_DTO);

        // then
        verify(commPostLikeRepository, times(0)).save(any());
        verify(commPostRepository, times(0)).findByUlid(any());
    }

    @Test
    @DisplayName("unlikePost로 게시글 좋아요 취소")
    void testUnlikePost_givenValidParameter_willUnlikePost() {
        // given
        UUID memberId = testPostLikeEvent.getMemberId();
        String postId = testPostLikeEvent.getPostId();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(true);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        willDoNothing().given(commPostLikeRepository).delete(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.unlikePost(TEST_MEMBER_POST_UNLIKE_RECORD);

        // then
        verify(commPostLikeRepository, times(1)).delete(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikePost로 게시글 좋아요 취소")
    void testValidateBeforeLikeOrUnlikePost_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikePost(TEST_MEMBER_POST_UNLIKE_RECORD);

        // then
        verify(commPostLikeRepository, times(0)).delete(any());
        verify(commPostRepository, times(0)).findByUlid(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likePost 또는 unlikePost 실패")
    void testValidateBeforeLikeOrUnlikePost_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityNotFoundExceptionForLike = assertThrows(EntityNotFoundException.class,
                () -> memberController.likePost(TEST_MEMBER_POST_LIKE_DTO));
        EntityNotFoundException entityNotFoundExceptionForUnlike = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikePost(TEST_MEMBER_POST_UNLIKE_RECORD));

        // then
        assertThat(entityNotFoundExceptionForLike.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
        assertThat(entityNotFoundExceptionForUnlike.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likePost 또는 unlikePost 실패")
    void testValidateBeforeLikeOrUnlikePost_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityNotFoundExceptionForLike = assertThrows(EntityNotFoundException.class,
                () -> memberController.likePost(TEST_MEMBER_POST_LIKE_DTO));
        EntityNotFoundException entityNotFoundExceptionForUnlike = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikePost(TEST_MEMBER_POST_UNLIKE_RECORD));

        // then
        assertThat(entityNotFoundExceptionForLike.getMessage()).isEqualTo(NOT_FOUND_TARGET_POST_ID.getMessage());
        assertThat(entityNotFoundExceptionForUnlike.getMessage()).isEqualTo(NOT_FOUND_TARGET_POST_ID.getMessage());
    }

    @Test
    @DisplayName("likeComment로 댓글 좋아요")
    void testLikeComment_givenValidParameter_willLikeComment() {
        // given
        UUID memberId = testCommentLikeEvent.getMemberId();
        String postId = testCommentLikeEvent.getPostId();
        String path = testCommentLikeEvent.getPath();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isUnliked(any(), any())).willReturn(true);
        CommCommentLikeEntity commentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        given(commCommentLikeRepository.save(commentLikeEntity)).willReturn(commentLikeEntity);
        Optional<CommCommentEntity> commentEntity = Optional.of(CommCommentEntity.builder().postEntity(createCommPostEntityBuilder().ulid(postId).build()).path(path).likeCount(1).build());
        given(commCommentRepository.findByPostUlidAndPath(postId, path)).willReturn(commentEntity);

        // when
        memberController.likeComment(TEST_MEMBER_COMMENT_LIKE_RECORD);

        // then
        verify(commCommentLikeRepository, times(1)).save(any());
        verify(commCommentRepository, times(1)).findByPostUlidAndPath(any(), any());
        assertThat(commentEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likeComment로 댓글 좋아요")
    void testValidateBeforeLikeOrUnlikeComment_givenAlreadyLikedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likeComment(TEST_MEMBER_COMMENT_LIKE_RECORD);

        // then
        verify(commCommentLikeRepository, times(0)).save(any());
        verify(commCommentRepository, times(0)).findByPostUlidAndPath(any(), any());
    }

    @Test
    @DisplayName("unlikeComment로 댓글 좋아요 취소")
    void testUnlikeComment_givenValidParameter_willUnlikeComment() {
        // given
        UUID memberId = testCommentLikeEvent.getMemberId();
        String postId = testCommentLikeEvent.getPostId();
        String path = testCommentLikeEvent.getPath();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isLiked(any(), any())).willReturn(true);
        CommCommentLikeEntity commentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        given(commCommentLikeRepository.save(commentLikeEntity)).willReturn(commentLikeEntity);
        Optional<CommCommentEntity> commentEntity = Optional.of(CommCommentEntity.builder().postEntity(createCommPostEntityBuilder().ulid(postId).build()).path(path).likeCount(1).build());
        given(commCommentRepository.findByPostUlidAndPath(postId, path)).willReturn(commentEntity);

        // when
        memberController.unlikeComment(TEST_MEMBER_COMMENT_UNLIKE_RECORD);

        // then
        verify(commCommentLikeRepository, times(1)).delete(any());
        verify(commCommentRepository, times(1)).findByPostUlidAndPath(any(), any());
        assertThat(commentEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikeComment로 댓글 좋아요 취소")
    void testValidateBeforeLikeOrUnlikeComment_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikeComment(TEST_MEMBER_COMMENT_UNLIKE_RECORD);

        // then
        verify(commCommentLikeRepository, times(0)).delete(any());
        verify(commCommentRepository, times(0)).findByPostUlidAndPath(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likeComment 또는 unlikeComment 실패")
    void testValidateBeforeLikeOrUnlikeComment_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityNotFoundExceptionForLike = assertThrows(EntityNotFoundException.class,
                () -> memberController.likeComment(TEST_MEMBER_COMMENT_LIKE_RECORD));
        EntityNotFoundException entityNotFoundExceptionForUnlike = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikeComment(TEST_MEMBER_COMMENT_UNLIKE_RECORD));

        // then
        assertThat(entityNotFoundExceptionForLike.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
        assertThat(entityNotFoundExceptionForUnlike.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likeComment 또는 unlikeComment 실패")
    void testValidateBeforeLikeOrUnlikeComment_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityNotFoundExceptionForLike = assertThrows(EntityNotFoundException.class,
                () -> memberController.likeComment(TEST_MEMBER_COMMENT_LIKE_RECORD));
        EntityNotFoundException entityNotFoundExceptionForUnlike = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikeComment(TEST_MEMBER_COMMENT_UNLIKE_RECORD));

        // then
        assertThat(entityNotFoundExceptionForLike.getMessage()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID.getMessage());
        assertThat(entityNotFoundExceptionForUnlike.getMessage()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID.getMessage());
    }
}