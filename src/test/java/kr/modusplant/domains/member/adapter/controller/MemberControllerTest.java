package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.exception.CommentAlreadyLikedException;
import kr.modusplant.domains.member.domain.exception.CommentAlreadyUnlikedException;
import kr.modusplant.domains.member.domain.exception.PostAlreadyLikedException;
import kr.modusplant.domains.member.domain.exception.PostAlreadyUnlikedException;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetCommentPath;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberRepositoryJpaAdapter;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
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

import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
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
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final CommPostLikeJpaRepository commPostLikeRepository = Mockito.mock(CommPostLikeJpaRepository.class);
    private final CommPostJpaRepository commPostRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommCommentLikeJpaRepository commCommentLikeRepository = Mockito.mock(CommCommentLikeJpaRepository.class);
    private final CommCommentJpaRepository commCommentRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final TargetPostIdRepository targetPostIdRepository = Mockito.mock(TargetPostIdRepository.class);
    private final TargetCommentIdRepository targetCommentIdRepository = Mockito.mock(TargetCommentIdRepository.class);
    private final EventBus eventBus = new EventBus();
    private final PostEventConsumer postEventConsumer = new PostEventConsumer(eventBus, commPostLikeRepository, commPostRepository);
    private final CommentEventConsumer commentEventConsumer = new CommentEventConsumer(eventBus, commCommentLikeRepository, commCommentRepository);
    private final MemberController memberController = new MemberController(memberMapper, memberRepository, targetPostIdRepository, targetCommentIdRepository, eventBus);

    @Test
    @DisplayName("register로 회원 등록")
    void testRegister_givenValidNickname_willReturnResponse() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // when & then
        assertThat(memberController.register(testMemberNickname).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }

    @Test
    @DisplayName("중복된 닉네임으로 인해 register로 회원 등록 실패")
    void testValidateBeforeRegister_givenAlreadyExistedNickname_willThrowException() {
        // given
        given(memberRepository.isNicknameExist(any())).willReturn(true);

        // when & then
        EntityExistsException alreadyExistedNicknameException = assertThrows(
                EntityExistsException.class, () -> memberController.register(testMemberNickname));
        assertThat(alreadyExistedNicknameException.getMessage()).isEqualTo(ALREADY_EXISTED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("updateNickname으로 닉네임 갱신")
    void testUpdateNickname_givenValidRequest_willReturnResponse() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // 해당 닉네임이 존재하지 않는 경우
        // given
        given(memberRepository.getByNickname(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberController.updateNickname(testMemberId, testMemberNickname).nickname()).isEqualTo(member.getMemberNickname().getValue());

        // 해당 닉네임이 수정되지 않은 경우
        // given
        given(memberRepository.getByNickname(any())).willReturn(Optional.of(createMember()));

        // when & then
        assertThat(memberController.updateNickname(testMemberId, testMemberNickname).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }

    @Test
    @DisplayName("중복된 닉네임으로 인해 updateNickname으로 닉네임 갱신 실패")
    void testValidateBeforeUpdateNickname_givenAlreadyExistedNickname_willThrowException() {
        // given
        given(memberRepository.getByNickname(any())).willReturn(Optional.of(Member.create(MemberId.generate(), testMemberActiveStatus, testMemberNickname, testMemberBirthDate)));

        // when & then
        EntityExistsException alreadyExistedNicknameException = assertThrows(
                EntityExistsException.class, () -> memberController.updateNickname(testMemberId, testMemberNickname));
        assertThat(alreadyExistedNicknameException.getMessage()).isEqualTo(ALREADY_EXISTED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("likePost로 소통 게시글 좋아요")
    void testLikePost_givenValidParameter_willLikePost() {
        // given
        UUID memberId = testPostLikeEvent.getMemberId();
        String postId = testPostLikeEvent.getPostId();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(false);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        given(commPostLikeRepository.save(postLikeEntity)).willReturn(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.likePost(MemberId.fromUuid(memberId), TargetPostId.create(postId));

        // then
        verify(commPostLikeRepository, times(1)).save(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likePost로 소통 게시글 좋아요 실패")
    void testValidateBeforeLikePost_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.likePost(testMemberId, testTargetPostId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likePost로 소통 게시글 좋아요 실패")
    void testValidateBeforeLikePost_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.likePost(testMemberId, testTargetPostId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_TARGET_POST_ID.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태여서 likePost로 소통 게시글 좋아요 실패")
    void testValidateBeforeLikePost_givenAlreadyLikedValue_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(true);

        // when
        PostAlreadyLikedException entityExistsException = assertThrows(PostAlreadyLikedException.class,
                () -> memberController.likePost(testMemberId, testTargetPostId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(POST_ALREADY_LIKED.getMessage());
    }

    @Test
    @DisplayName("unlikePost로 소통 게시글 좋아요 취소")
    void testUnlikePost_givenValidParameter_willUnlikePost() {
        // given
        UUID memberId = testPostLikeEvent.getMemberId();
        String postId = testPostLikeEvent.getPostId();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(false);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        willDoNothing().given(commPostLikeRepository).delete(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.unlikePost(MemberId.fromUuid(memberId), TargetPostId.create(postId));

        // then
        verify(commPostLikeRepository, times(1)).delete(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 unlikePost로 소통 게시글 좋아요 취소 실패")
    void testValidateBeforeUnlikePost_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikePost(testMemberId, testTargetPostId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 unlikePost로 소통 게시글 좋아요 취소 실패")
    void testValidateBeforeUnlikePost_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikePost(testMemberId, testTargetPostId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_TARGET_POST_ID.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태여서 unlikePost로 소통 게시글 좋아요 취소 실패")
    void testValidateBeforeLikePost_givenAlreadyUnlikedValue_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(true);

        // when
        PostAlreadyUnlikedException entityExistsException = assertThrows(PostAlreadyUnlikedException.class,
                () -> memberController.unlikePost(testMemberId, testTargetPostId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(POST_ALREADY_UNLIKED.getMessage());
    }

    @Test
    @DisplayName("likeComment로 소통 댓글 좋아요")
    void testLikeComment_givenValidParameter_willLikeComment() {
        // given
        UUID memberId = testCommentLikeEvent.getMemberId();
        String postId = testCommentLikeEvent.getPostId();
        String path = testCommentLikeEvent.getPath();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isLiked(any(), any())).willReturn(false);
        CommCommentLikeEntity commentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        given(commCommentLikeRepository.save(commentLikeEntity)).willReturn(commentLikeEntity);
        Optional<CommCommentEntity> commentEntity = Optional.of(CommCommentEntity.builder().postEntity(createCommPostEntityBuilder().ulid(postId).build()).path(path).likeCount(1).build());
        given(commCommentRepository.findByPostUlidAndPath(postId, path)).willReturn(commentEntity);

        // when
        memberController.likeComment(MemberId.fromUuid(memberId), TargetCommentId.create(TargetPostId.create(postId), TargetCommentPath.create(path)));

        // then
        verify(commCommentLikeRepository, times(1)).save(any());
        verify(commCommentRepository, times(1)).findByPostUlidAndPath(any(), any());
        assertThat(commentEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likeComment로 소통 댓글 좋아요 실패")
    void testValidateBeforeLikeComment_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.likeComment(testMemberId, testTargetCommentId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likeComment로 소통 댓글 좋아요 실패")
    void testValidateBeforeLikeComment_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.likeComment(testMemberId, testTargetCommentId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태여서 likeComment로 소통 댓글 좋아요 실패")
    void testValidateBeforeLikeComment_givenAlreadyLikedValue_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isLiked(any(), any())).willReturn(true);

        // when
        CommentAlreadyLikedException entityExistsException = assertThrows(CommentAlreadyLikedException.class,
                () -> memberController.likeComment(testMemberId, testTargetCommentId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(COMMENT_ALREADY_LIKED.getMessage());
    }

    @Test
    @DisplayName("unlikeComment로 소통 댓글 좋아요 취소")
    void testUnlikeComment_givenValidParameter_willUnlikeComment() {
        // given
        UUID memberId = testCommentLikeEvent.getMemberId();
        String postId = testCommentLikeEvent.getPostId();
        String path = testCommentLikeEvent.getPath();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isUnliked(any(), any())).willReturn(false);
        CommCommentLikeEntity commentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        given(commCommentLikeRepository.save(commentLikeEntity)).willReturn(commentLikeEntity);
        Optional<CommCommentEntity> commentEntity = Optional.of(CommCommentEntity.builder().postEntity(createCommPostEntityBuilder().ulid(postId).build()).path(path).likeCount(1).build());
        given(commCommentRepository.findByPostUlidAndPath(postId, path)).willReturn(commentEntity);

        // when
        memberController.unlikeComment(MemberId.fromUuid(memberId), TargetCommentId.create(TargetPostId.create(postId), TargetCommentPath.create(path)));

        // then
        verify(commCommentLikeRepository, times(1)).delete(any());
        verify(commCommentRepository, times(1)).findByPostUlidAndPath(any(), any());
        assertThat(commentEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 unlikeComment로 소통 댓글 좋아요 취소 실패")
    void testValidateBeforeUnlikeComment_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikeComment(testMemberId, testTargetCommentId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_MEMBER_ID.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 unlikeComment로 소통 댓글 좋아요 취소 실패")
    void testValidateBeforeUnlikeComment_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(false);

        // when
        EntityNotFoundException entityExistsException = assertThrows(EntityNotFoundException.class,
                () -> memberController.unlikeComment(testMemberId, testTargetCommentId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID.getMessage());
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태여서 unlikeComment로 소통 댓글 좋아요 취소 실패")
    void testValidateBeforeLikeComment_givenAlreadyUnlikedValue_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isUnliked(any(), any())).willReturn(true);

        // when
        CommentAlreadyUnlikedException entityExistsException = assertThrows(CommentAlreadyUnlikedException.class,
                () -> memberController.unlikeComment(testMemberId, testTargetCommentId));

        // then
        assertThat(entityExistsException.getMessage()).isEqualTo(COMMENT_ALREADY_UNLIKED.getMessage());
    }
}