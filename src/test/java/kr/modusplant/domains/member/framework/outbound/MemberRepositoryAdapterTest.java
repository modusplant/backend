package kr.modusplant.domains.member.framework.outbound;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberProfileEntityTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import kr.modusplant.domains.member.domain.event.CommentChooseCachesRemoveEvent;
import kr.modusplant.domains.member.domain.event.MemberSearchHistoryRemoveEvent;
import kr.modusplant.domains.member.domain.event.PostViewCountRemoveEvent;
import kr.modusplant.domains.member.domain.event.RecentlyViewMemberRemoveEvent;
import kr.modusplant.domains.member.domain.event.RecentlyViewPostRemoveEvent;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectCommentJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectPostJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.MemberJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberWithdrawJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jooq.record.PostIsPublishedAndContentRecord;
import kr.modusplant.domains.post.framework.outbound.jooq.repository.PostJooqRepository;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jdbc.common.util.SwearHolderTestUtils;
import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_ROLE;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberWithdrawOpinionTestUtils.testMemberWithdrawOpinion;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class MemberRepositoryAdapterTest implements MemberTestUtils, MemberProfileEntityTestUtils {
    @SuppressWarnings("unused")
    private final SwearHolder swearHolder = SwearHolderTestUtils.createSwearHolder();

    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberWithdrawJpaRepository memberWithdrawJpaRepository = Mockito.mock(MemberWithdrawJpaRepository.class);
    private final ActivitySubjectPostJooqRepository activitySubjectPostJooqRepository = Mockito.mock(ActivitySubjectPostJooqRepository.class);
    private final ActivitySubjectCommentJooqRepository activitySubjectCommentJooqRepository = Mockito.mock(ActivitySubjectCommentJooqRepository.class);
    private final MemberJooqRepository memberJooqRepository = Mockito.mock(MemberJooqRepository.class);
    private final PostJooqRepository postJooqRepository = Mockito.mock(PostJooqRepository.class);
    private final MemberRepositoryAdapter memberRepositoryAdapter = new MemberRepositoryAdapter(
            eventPublisher, memberJpaMapper, memberJpaRepository, memberWithdrawJpaRepository,
            activitySubjectPostJooqRepository, activitySubjectCommentJooqRepository,
            memberJooqRepository, postJooqRepository);

    @Test
    @DisplayName("getById로 가용한 Member 반환(가용할 때)")
    void testGetById_givenAvailableMemberId_willReturnOptionalAvailableMember() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryAdapter.getById(testMemberId)).isEqualTo(createMember());
    }

    @Test
    @DisplayName("getById로 예외 반환(가용히지 않을 때)")
    void testGetById_givenNotAvailableMemberId_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // when
        NotFoundEntityException notFoundEntityException =
                assertThrows(NotFoundEntityException.class, () -> memberRepositoryAdapter.getById(testMemberId));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("getRole로 가용한 Role 반환(가용할 때)")
    void testGetRole_givenAvailableMemberId_willReturnRole() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryAdapter.getRole(testMemberId)).isEqualTo(MEMBER_BASIC_USER_ROLE);
    }

    @Test
    @DisplayName("getRole로 예외 반환(가용하지 않을 때)")
    void testGetRole_givenNotAvailableMemberId_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // when
        NotFoundEntityException notFoundEntityException =
                assertThrows(NotFoundEntityException.class, () -> memberRepositoryAdapter.getRole(testMemberId));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용하지 않을 때)")
    void testGetByNickname_givenNotAvailableNickname_willReturnOptionalEmptyMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberRepositoryAdapter.getByNickname(testNormalUserNickname)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용할 때)")
    void testGetByNickname_givenAvailableNickname_willReturnOptionalAvailableMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryAdapter.getByNickname(testNormalUserNickname)).isEqualTo(Optional.of(createMember()));
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용하지 않을 때)")
    void testGetByNickname_givenValidNickname_willReturnOptionalEmptyMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberRepositoryAdapter.getByNickname(testNormalUserNickname)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("add(Nickname nickname)로 Member 반환")
    void testAdd_givenValidNickname_willReturnMember() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        given(memberJpaRepository.save(any())).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryAdapter.add(testNormalUserNickname).getNickname()).isEqualTo(testNormalUserNickname);
    }

    @Test
    @DisplayName("add(MemberId memberId, Nickname nickname)로 Member 반환")
    void testAdd_givenValidMemberIdAndNickname_willReturnMember() {
        // given
        MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        given(memberJpaRepository.save(any())).willReturn(memberEntity);

        // when & then
        Member member = memberRepositoryAdapter.add(testMemberId, testNormalUserNickname);
        assertThat(member.getMemberId()).isEqualTo(testMemberId);
        assertThat(member.getNickname()).isEqualTo(testNormalUserNickname);
    }

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given & when
        given(memberJpaRepository.existsByUuid(testMemberId.getValue())).willReturn(true);

        // when & then
        assertThat(memberRepositoryAdapter.isIdExist(testMemberId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given & when
        given(memberJpaRepository.existsByUuid(testMemberId.getValue())).willReturn(false);

        // when & then
        assertThat(memberRepositoryAdapter.isIdExist(testMemberId)).isEqualTo(false);
    }

    @Test
    @DisplayName("isNicknameExist로 true 반환")
    void testIsNicknameExist_givenNicknameThatExists_willReturnTrue() {
        // given & when
        given(memberJpaRepository.existsByNickname(testNormalUserNickname.getValue())).willReturn(true);

        // when & then
        assertThat(memberRepositoryAdapter.isNicknameExist(testNormalUserNickname)).isEqualTo(true);
    }

    @Test
    @DisplayName("isNicknameExist로 false 반환")
    void testIsNicknameExist_givenNicknameThatIsNotExist_willReturnFalse() {
        // given & when
        given(memberJpaRepository.existsByNickname(testNormalUserNickname.getValue())).willReturn(false);

        // when & then
        assertThat(memberRepositoryAdapter.isNicknameExist(testNormalUserNickname)).isEqualTo(false);
    }

    @Test
    @DisplayName("발행된 게시글과 프로필 이미지가 있을 때 withdraw로 회원 탈퇴하면서 이벤트 발행")
    void testWithdraw_givenExistedPublishedPostsAndProfileImage_willPublishEventsWhileWithdraw() {
        // given
        given(postJooqRepository.getPostIsPublishedAndContentsByMemberId(any())).willReturn(
                List.of(new PostIsPublishedAndContentRecord(TEST_POST_ULID, true, TEST_POST_CONTENT_JSON_NODE)));
        given(memberJooqRepository.getPlantInfoPhotoPathsByMemberId(any())).willReturn(List.of());
        given(memberJooqRepository.getProfileImagePathByMemberId(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        given(memberJpaRepository.getReferenceById(any())).willReturn(createMemberBasicUserEntityWithUuid());
        given(activitySubjectCommentJooqRepository.getChosenCommentPathsByMemberId(any()))
                .willReturn(Map.of(TEST_POST_ULID, TEST_COMMENT_PATH));

        // when
        memberRepositoryAdapter.withdraw(testMemberId, MemberWithdrawReason.UNCOMFORTABLE_TO_USE, testMemberWithdrawOpinion);

        // then
        verify(eventPublisher, times(1)).publishEvent(any(CommentChooseCachesRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(ImagesRemoveTask.class));
        verify(eventPublisher, times(1)).publishEvent(any(RecentlyViewPostRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(PostViewCountRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(RecentlyViewMemberRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(MemberSearchHistoryRemoveEvent.class));
        verify(memberJooqRepository, times(1)).deleteNotificationsByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectPostJooqRepository, times(1)).deleteRelatedRecordsByPostUlids(any());
        verify(activitySubjectPostJooqRepository, times(1)).archiveAllByPostUlids(any());
        verify(activitySubjectCommentJooqRepository, times(1)).deleteRelatedRecordsByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectCommentJooqRepository, times(1)).archiveAllByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectCommentJooqRepository, times(1)).deleteAllByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectPostJooqRepository, times(1)).deleteAllByMemberId(any());
        verify(activitySubjectPostJooqRepository, times(1)).decreaseLikeCountOfPostsLikedByMemberId(any());
        verify(activitySubjectCommentJooqRepository, times(1)).decreaseLikeCountOfCommentsLikedByMemberId(any());
        verify(memberWithdrawJpaRepository, times(1)).save(any());
        verify(memberJooqRepository, times(1)).updateMemberIdToNullByMemberId(any());
        verify(memberJooqRepository, times(1)).deleteMemberRelatedRecordsExceptOfPostAndCommentByMemberId(any());
    }

    @Test
    @DisplayName("발행된 게시글과 프로필 이미지가 없을 때 withdraw로 회원 탈퇴하면서 이벤트를 발행하지 않음")
    void testWithdraw_givenNotFoundPublishedPostsAndProfileImage_willSkipPublishingEventsWhileWithdraw() {
        // given
        given(postJooqRepository.getPostIsPublishedAndContentsByMemberId(any())).willReturn(List.of());
        given(memberJooqRepository.getPlantInfoPhotoPathsByMemberId(any())).willReturn(List.of());
        given(memberJooqRepository.getProfileImagePathByMemberId(any())).willReturn(null);
        given(memberJpaRepository.getReferenceById(any())).willReturn(createMemberBasicUserEntityWithUuid());

        // when
        memberRepositoryAdapter.withdraw(testMemberId, MemberWithdrawReason.UNCOMFORTABLE_TO_USE, testMemberWithdrawOpinion);

        // then
        verify(eventPublisher, never()).publishEvent(any(CommentChooseCachesRemoveEvent.class));
        verify(eventPublisher, never()).publishEvent(any(ImagesRemoveTask.class));
        verify(eventPublisher, never()).publishEvent(any(RecentlyViewPostRemoveEvent.class));
        verify(eventPublisher, never()).publishEvent(any(PostViewCountRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(RecentlyViewMemberRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(MemberSearchHistoryRemoveEvent.class));
        verify(memberJooqRepository, times(1)).deleteNotificationsByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectPostJooqRepository, never()).deleteRelatedRecordsByPostUlids(any());
        verify(activitySubjectPostJooqRepository, never()).archiveAllByPostUlids(any());
        verify(activitySubjectCommentJooqRepository, times(1)).deleteRelatedRecordsByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectCommentJooqRepository, times(1)).archiveAllByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectCommentJooqRepository, times(1)).deleteAllByMemberIdAndPostUlids(any(), any());
        verify(activitySubjectPostJooqRepository, never()).deleteAllByMemberId(any());
        verify(activitySubjectPostJooqRepository, times(1)).decreaseLikeCountOfPostsLikedByMemberId(any());
        verify(activitySubjectCommentJooqRepository, times(1)).decreaseLikeCountOfCommentsLikedByMemberId(any());
        verify(memberWithdrawJpaRepository, times(1)).save(any());
        verify(memberJooqRepository, times(1)).updateMemberIdToNullByMemberId(any());
        verify(memberJooqRepository, times(1)).deleteMemberRelatedRecordsExceptOfPostAndCommentByMemberId(any());
    }
}
