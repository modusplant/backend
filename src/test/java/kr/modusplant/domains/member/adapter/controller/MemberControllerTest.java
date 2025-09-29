package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.common.utils.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.exception.AlreadyExistedNicknameException;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberRepositoryJpaAdapter;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.event.consumer.PostEventConsumer;
import kr.modusplant.shared.event.PostLikeEventTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.ALREADY_EXISTED_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberControllerTest implements MemberTestUtils, PostLikeEventTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final CommPostLikeRepository commPostLikeRepository = Mockito.mock(CommPostLikeRepository.class);
    private final CommPostRepository commPostRepository = Mockito.mock(CommPostRepository.class);
    private final EventBus eventBus = new EventBus();
    private final PostEventConsumer postEventConsumer = new PostEventConsumer(eventBus, commPostLikeRepository, commPostRepository);
    private final MemberController memberController = new MemberController(memberMapper, memberRepository, eventBus);

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
    void testValidateMemberBeforeRegister_givenAlreadyExistedNickname_willThrowException() {
        // given
        given(memberRepository.isNicknameExist(any())).willReturn(true);

        // when & then
        AlreadyExistedNicknameException alreadyExistedNicknameException = assertThrows(
                AlreadyExistedNicknameException.class, () -> memberController.register(testMemberNickname));
        assertThat(alreadyExistedNicknameException.getMessage()).isEqualTo(ALREADY_EXISTED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("updateNickname으로 닉네임 갱신")
    void testUpdateNickname_givenValidRequest_willReturnResponse() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // when & then
        assertThat(memberController.updateNickname(testMemberId, testMemberNickname).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }

    @Test
    @DisplayName("중복된 닉네임으로 인해 updateNickname으로 닉네임 갱신 실패")
    void testValidateMemberBeforeUpdateNickname_givenAlreadyExistedNickname_willThrowException() {
        // given
        given(memberRepository.getByNickname(any())).willReturn(Optional.of(Member.create(MemberId.generate(), testMemberActiveStatus, testMemberNickname, testMemberBirthDate)));

        // when & then
        AlreadyExistedNicknameException alreadyExistedNicknameException = assertThrows(
                AlreadyExistedNicknameException.class, () -> memberController.updateNickname(testMemberId, testMemberNickname));
        assertThat(alreadyExistedNicknameException.getMessage()).isEqualTo(ALREADY_EXISTED_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("likePost로 소통 게시글 좋아요")
    void testLikePost_givenValidParameter_willLikePost() {
        // given
        UUID memberId = TEST_POST_LIKE_EVENT.getMemberId();
        String postId = TEST_POST_LIKE_EVENT.getPostId();
        CommPostLikeEntity entity = CommPostLikeEntity.of(postId, memberId);
        given(commPostLikeRepository.save(entity)).willReturn(entity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.likePost(memberId, postId);

        // then
        verify(commPostLikeRepository, times(1)).save(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("unlikePost로 소통 게시글 좋아요")
    void testUnlikePost_givenValidParameter_willUnlikePost() {
        // given
        UUID memberId = TEST_POST_LIKE_EVENT.getMemberId();
        String postId = TEST_POST_LIKE_EVENT.getPostId();
        CommPostLikeEntity entity = CommPostLikeEntity.of(postId, memberId);
        willDoNothing().given(commPostLikeRepository).delete(entity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.unlikePost(memberId, postId);

        // then
        verify(commPostLikeRepository, times(1)).delete(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }
}