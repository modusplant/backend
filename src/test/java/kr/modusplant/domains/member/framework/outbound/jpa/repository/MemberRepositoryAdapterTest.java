package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectCommentJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectPostJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.MemberProfileJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.post.framework.outbound.jooq.repository.PostJooqRepository;
import kr.modusplant.shared.event.ImageRemoveEvent;
import kr.modusplant.shared.event.ImagesRemoveEvent;
import kr.modusplant.shared.event.RecentlyViewPostRemoveEvent;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberWithdrawOpinionTestUtils.testMemberWithdrawOpinion;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER;
import static kr.modusplant.domains.member.framework.outbound.jooq.record.common.util.ActivitySubjectCommentIdRecordTestUtils.testActivitySubjectCommentIdRecord;
import static kr.modusplant.domains.post.common.constant.PostConstant.*;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class MemberRepositoryAdapterTest implements MemberTestUtils, MemberEntityTestUtils {
    MockDataProvider mockDataProvider = ctx -> {
        DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
        return new MockResult[]{new MockResult(0, dsl.newResult())};
    };

    MockConnection mockConnection = new MockConnection(mockDataProvider);
    DSLContext dslContext =  DSL.using(mockConnection, SQLDialect.POSTGRES);

    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final ActivitySubjectPostJooqRepository activitySubjectPostJooqRepository = Mockito.mock(ActivitySubjectPostJooqRepository.class);
    private final ActivitySubjectCommentJooqRepository activitySubjectCommentJooqRepository = Mockito.mock(ActivitySubjectCommentJooqRepository.class);
    private final MemberProfileJooqRepository memberProfileJooqRepository = Mockito.mock(MemberProfileJooqRepository.class);
    private final PostJooqRepository postJooqRepository = Mockito.mock(PostJooqRepository.class);
    private final MemberRepositoryAdapter memberRepositoryAdapter = new MemberRepositoryAdapter(
            stringRedisTemplate, dslContext, eventPublisher, memberJpaMapper, memberJpaRepository, activitySubjectPostJooqRepository, activitySubjectCommentJooqRepository, memberProfileJooqRepository, postJooqRepository);

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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
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
        willDoNothing().given(eventPublisher).publishEvent(any(ImagesRemoveEvent.class));
        willDoNothing().given(eventPublisher).publishEvent(any(RecentlyViewPostRemoveEvent.class));
        given(postJooqRepository.getPublishedPostUlidsByMemberId(any())).willReturn(TEST_POST_ULID_ARRAY);
        given(postJooqRepository.getPostContentsFromPublishedPostUlids(any())).willReturn(List.of(TEST_POST_CONTENT_JSON_NODE));
        given(activitySubjectPostJooqRepository.getPostIdsLikedByMemberId(any())).willReturn(List.of(TEST_POST_ULID));
        given(activitySubjectCommentJooqRepository.getCommentIdsThatHaveCommentLikedByMemberId(any())).willReturn(List.of(testActivitySubjectCommentIdRecord));
        given(memberProfileJooqRepository.getImageFileKeyFromMemberId(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        willDoNothing().given(eventPublisher).publishEvent(any(ImageRemoveEvent.class));

        // when
        memberRepositoryAdapter.withdraw(testMemberId, MemberWithdrawReason.UNCOMFORTABLE_TO_USE, testMemberWithdrawOpinion);

        // then
        verify(stringRedisTemplate).unlink("recentlyView:member:%s:posts".formatted(MEMBER_BASIC_USER_UUID));
        verify(eventPublisher, times(1)).publishEvent(any(ImagesRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(RecentlyViewPostRemoveEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(ImageRemoveEvent.class));
    }

    @Test
    @DisplayName("발행된 게시글과 프로필 이미지가 없을 때 withdraw로 회원 탈퇴하면서 이벤트를 발행하지 않음")
    void testWithdraw_givenNotFoundPublishedPostsAndProfileImage_willSkipPublishingEventsWhileWithdraw() {
        // given
        given(postJooqRepository.getPublishedPostUlidsByMemberId(any())).willReturn(new String[]{});
        given(postJooqRepository.getPostContentsFromPublishedPostUlids(any())).willReturn(List.of());
        given(activitySubjectPostJooqRepository.getPostIdsLikedByMemberId(any())).willReturn(List.of());
        given(activitySubjectCommentJooqRepository.getCommentIdsThatHaveCommentLikedByMemberId(any())).willReturn(List.of());
        given(memberProfileJooqRepository.getImageFileKeyFromMemberId(any())).willReturn(null);

        // when
        memberRepositoryAdapter.withdraw(testMemberId, MemberWithdrawReason.UNCOMFORTABLE_TO_USE, testMemberWithdrawOpinion);

        // then
        verify(stringRedisTemplate).unlink("recentlyView:member:%s:posts".formatted(MEMBER_BASIC_USER_UUID));
        verify(eventPublisher, never()).publishEvent(any(ImagesRemoveEvent.class));
        verify(eventPublisher, never()).publishEvent(any(RecentlyViewPostRemoveEvent.class));
        verify(eventPublisher, never()).publishEvent(any(ImageRemoveEvent.class));
    }
}