package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.common.util.CommentEntityTestUtils;
import kr.modusplant.domains.member.adapter.helper.MemberImageIOHelper;
import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.adapter.mapper.MemberProfileMapperImpl;
import kr.modusplant.domains.member.adapter.translator.MemberSocialTranslator;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.event.CommentLikeEvent;
import kr.modusplant.domains.member.domain.event.PostAbuseReportEvent;
import kr.modusplant.domains.member.domain.event.PostLikeEvent;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileIntroduction;
import kr.modusplant.domains.member.framework.outbound.MemberRepositoryAdapter;
import kr.modusplant.domains.member.framework.outbound.jpa.adapter.ActivitySubjectCommentRepositoryJpaAdapter;
import kr.modusplant.domains.member.framework.outbound.jpa.adapter.ActivitySubjectPostRepositoryJpaAdapter;
import kr.modusplant.domains.member.framework.outbound.jpa.adapter.MemberProfileRepositoryJpaAdapter;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.CommentAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberProfileEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.PostAbuseReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.ProposalBugReportEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.*;
import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord;
import kr.modusplant.domains.member.usecase.record.MemberWithdrawalRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.common.util.PostEntityTestUtils;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.swear.exception.SwearContainedException;
import kr.modusplant.infrastructure.swear.exception.enums.SwearErrorCode;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.generator.UlidGeneratorHolder;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.*;
import static kr.modusplant.domains.member.common.constant.MemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_OPINION;
import static kr.modusplant.domains.member.common.constant.MemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_REASON;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;
import static kr.modusplant.domains.member.common.util.domain.entity.nullobject.EmptyMemberProfileImageTestUtils.testEmptyMemberProfileImage;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
import static kr.modusplant.domains.member.common.util.domain.vo.ReportTimeTestUtils.testReportTime;
import static kr.modusplant.domains.member.common.util.usecase.record.CommentAbuseReportRecordTestUtils.testCommentAbuseReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCancelPostBookmarkRecordTestUtils.testMemberPostBookmarkCancelRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentLikeRecordTestUtils.testMemberCommentLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentUnlikeRecordTestUtils.testMemberCommentUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberNicknameCheckRecordTestUtils.testMemberNicknameCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostBookmarkRecordTestUtils.testMemberPostBookmarkRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostLikeRecordTestUtils.testMemberPostLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostUnlikeRecordTestUtils.testMemberPostUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileGetRecordTestUtils.testMemberProfileGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberWithdrawalRecordTestUtils.testKakaoMemberWithdrawalRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportRecordTestUtils.testPostAbuseReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordTestUtils.testProposalOrBugReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponse;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.EXISTS_COMMENT_ABUSE_REPORT;
import static kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode.EXISTS_POST_ABUSE_REPORT;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberControllerTest implements
        MemberTestUtils, MemberProfileTestUtils,
        MemberProfileEntityTestUtils, PostEntityTestUtils, CommentEntityTestUtils,
        ProposalBugReportEntityTestUtils, PostAbuseReportEntityTestUtils, CommentAbuseReportEntityTestUtils {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    @SuppressWarnings("unused")
    private final UlidGeneratorHolder ulidGeneratorHolder = new UlidGeneratorHolder(new UlidIdGenerator());

    private final ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

    private final JwtTokenProvider jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final TokenService tokenService = Mockito.mock(TokenService.class);
    private final AmazonS3Service amazonS3Service = Mockito.mock(AmazonS3Service.class);
    private final SwearService swearService = Mockito.mock(SwearService.class);
    private final MemberImageIOHelper memberImageIOHelper = Mockito.mock(MemberImageIOHelper.class);
    private final MemberValidationHelper memberValidationHelper = Mockito.mock(MemberValidationHelper.class);
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl(amazonS3Service);
    private final MemberSocialTranslator memberSocialTranslator = Mockito.mock(MemberSocialTranslator.class);

    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryAdapter.class);
    private final MemberProfileRepository memberProfileRepository = Mockito.mock(MemberProfileRepositoryJpaAdapter.class);
    private final ActivitySubjectPostRepository activitySubjectPostRepository = Mockito.mock(ActivitySubjectPostRepositoryJpaAdapter.class);
    private final ActivitySubjectCommentRepository activitySubjectCommentRepository = Mockito.mock(ActivitySubjectCommentRepositoryJpaAdapter.class);
    private final ReportRepository reportRepository = Mockito.mock(ReportRepository.class);

    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);

    private final MemberController memberController = new MemberController(jwtTokenProvider, tokenService, swearService, memberImageIOHelper, memberValidationHelper, memberProfileMapper, memberSocialTranslator, memberRepository, memberProfileRepository, activitySubjectPostRepository, activitySubjectCommentRepository, reportRepository, applicationEventPublisher);

    private final NotFoundEntityException notFoundEntityExceptionForMember = new NotFoundEntityException(NOT_FOUND_MEMBER, "memberId");
    private final NotFoundEntityException notFoundEntityExceptionForActivitySubjectPost = new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID, "activitySubjectPostId");
    private final NotFoundEntityException notFoundEntityExceptionForActivitySubjectComment = new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID, "activitySubjectCommentId");

    @Nested
    @DisplayName("checkExistedNickname으로 회원 닉네임 중복 확인")
    class CheckExistedNicknameTest {
        @Test
        @DisplayName("닉네임이 존재할 때 checkExistedNickname으로 회원 닉네임 중복 확인")
        void testCheckExistedNickname_givenExistedNicknameRequest_willReturnResponse() {
            // given
            given(memberRepository.isNicknameExist(any())).willReturn(true);

            // when & then
            assertThat(memberController.checkExistedNickname(testMemberNicknameCheckRecord)).isEqualTo(true);
        }

        @Test
        @DisplayName("닉네임이 존재하지 않을 때 checkExistedNickname으로 회원 닉네임 중복 확인")
        void testCheckExistedNickname_givenNotFoundNicknameRequest_willReturnResponse() {
            // given
            given(memberRepository.isNicknameExist(any())).willReturn(false);

            // when & then
            assertThat(memberController.checkExistedNickname(testMemberNicknameCheckRecord)).isEqualTo(false);
        }
    }

    @Test
    @DisplayName("기존에 저장된 회원 프로필이 있을 때 getProfile로 회원 프로필 조회")
    void testGetProfile_givenValidGetRecordAndStoredMemberProfile_willReturnResponse() throws IOException {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberProfileRepository.getById(any())).willReturn(createMemberProfile());
        given(amazonS3Service.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when & then
        assertThat(memberController.getProfile(testMemberProfileGetRecord)).isEqualTo(testMemberProfileResponse);
    }

    @Test
    @DisplayName("이미지 경로를 포함해서 존재하는 모든 데이터로 overrideProfile로 프로필 덮어쓰기")
    void testOverrideProfile_givenExistedData_willReturnResponse() throws IOException {
        // given
        MemberProfile memberProfile = createMemberProfile();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberRepository.getByNickname(any())).willReturn(Optional.empty());
        given(memberProfileRepository.getById(any())).willReturn(memberProfile);
        given(swearService.filterSwear(any())).willReturn(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
        willDoNothing().given(memberImageIOHelper).deleteImage(any());
        given(memberImageIOHelper.uploadImage(any(MemberId.class), any(MemberProfileOverrideRecord.class))).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        given(memberProfileRepository.update(any())).willReturn(memberProfile);
        given(amazonS3Service.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when
        MemberProfileResponse memberProfileResponse = memberController.overrideProfile(testMemberProfileOverrideRecord);

        // then
        assertThat(memberProfileResponse.id()).isEqualTo(MEMBER_BASIC_USER_UUID);
        assertThat(memberProfileResponse.imageUrl()).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);
        assertThat(memberProfileResponse.introduction()).isEqualTo(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
        assertThat(memberProfileResponse.nickname()).isEqualTo(MEMBER_BASIC_USER_NICKNAME);
    }

    @Test
    @DisplayName("이미지 경로를 제외한 존재하는 모든 데이터로 overrideProfile로 프로필 덮어쓰기")
    void testOverrideProfile_givenExistedDataExceptOfImagePath_willReturnResponse() throws IOException {
        // given
        MemberProfile memberProfile = createMemberProfile();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberRepository.getByNickname(any())).willReturn(Optional.empty());
        given(memberProfileRepository.getById(any())).willReturn(
                MemberProfile.create(
                        testMemberId,
                        testEmptyMemberProfileImage,
                        testMemberProfileIntroduction,
                        testNormalUserNickname));
        given(swearService.filterSwear(any())).willReturn(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
        willDoNothing().given(memberImageIOHelper).deleteImage(any());
        given(memberImageIOHelper.uploadImage(any(MemberId.class), any(MemberProfileOverrideRecord.class))).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        given(memberProfileRepository.update(any())).willReturn(memberProfile);
        given(amazonS3Service.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when
        MemberProfileResponse memberProfileResponse = memberController.overrideProfile(testMemberProfileOverrideRecord);

        // then
        assertThat(memberProfileResponse.id()).isEqualTo(MEMBER_BASIC_USER_UUID);
        assertThat(memberProfileResponse.imageUrl()).isEqualTo(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);
        assertThat(memberProfileResponse.introduction()).isEqualTo(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
        assertThat(memberProfileResponse.nickname()).isEqualTo(MEMBER_BASIC_USER_NICKNAME);
    }

    @Test
    @DisplayName("존재하는 않는 데이터로 overrideProfile로 프로필 덮어쓰기")
    void testOverrideProfile_givenNotFoundData_willReturnResponse() throws IOException {
        // given
        MemberProfile memberProfile = MemberProfile.create(testMemberId, EmptyMemberProfileImage.create(), EmptyMemberProfileIntroduction.create(), testNormalUserNickname);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberRepository.getByNickname(any())).willReturn(Optional.empty());
        given(memberProfileRepository.getById(any())).willReturn(memberProfile);
        given(memberProfileRepository.update(any())).willReturn(memberProfile);
        willDoNothing().given(memberImageIOHelper).deleteImage(any());

        // when
        MemberProfileResponse memberProfileResponse = memberController.overrideProfile(
                new MemberProfileOverrideRecord(MEMBER_BASIC_USER_UUID, null, null, MEMBER_BASIC_USER_NICKNAME));

        // then
        assertThat(memberProfileResponse.id()).isEqualTo(MEMBER_BASIC_USER_UUID);
        assertThat(memberProfileResponse.imageUrl()).isEqualTo(null);
        assertThat(memberProfileResponse.introduction()).isEqualTo(null);
        assertThat(memberProfileResponse.nickname()).isEqualTo(MEMBER_BASIC_USER_NICKNAME);
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 인해 overrideProfile로 프로필 덮어쓰기 실패")
    void testValidateMemberIdAndNicknameBeforeOverrideProfile_givenNotFoundId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when & then
        NotFoundEntityException alreadyExistedNicknameException = assertThrows(
                NotFoundEntityException.class, () -> memberController.overrideProfile(testMemberProfileOverrideRecord));
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("닉네임에 사용된 비속어로 인해 overrideProfile로 프로필 덮어쓰기 실패")
    void testValidateThatHasSwear_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(swearService.isSwearContained(any())).willReturn(true);

        // when & then
        SwearContainedException swearContainedException = assertThrows(
                SwearContainedException.class, () -> memberController.overrideProfile(testMemberProfileOverrideRecord));
        assertThat(swearContainedException.getErrorCode()).isEqualTo(SwearErrorCode.SWEAR_CONTAINED);
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 인해 overrideProfile로 프로필 덮어쓰기 실패")
    void testValidate_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(swearService.isSwearContained(any())).willReturn(false);
        given(memberRepository.getByNickname(any())).willReturn(Optional.of(Member.create(MemberId.generate(), testMemberActiveStatus, testNormalUserNickname)));

        // when & then
        ExistsEntityException alreadyExistedNicknameException = assertThrows(
                ExistsEntityException.class, () -> memberController.overrideProfile(testMemberProfileOverrideRecord));
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(KernelErrorCode.EXISTS_NICKNAME);
    }

    @Test
    @DisplayName("likePost로 게시글 좋아요")
    void testLikePost_givenValidParameter_willLikePost() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isUnliked(any(), any())).willReturn(true);
        willDoNothing().given(activitySubjectPostRepository).like(any(), any());
        willDoNothing().given(applicationEventPublisher).publishEvent(any(PostLikeEvent.class));

        // when
        memberController.likePost(testMemberPostLikeRecord);

        // then
        verify(activitySubjectPostRepository, times(1)).like(any(), any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(PostLikeEvent.class));
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likePost로 게시글 좋아요")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyLikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likePost(testMemberPostLikeRecord);

        // then
        verify(activitySubjectPostRepository, times(0)).like(any(), any());
        verify(applicationEventPublisher, times(0)).publishEvent(any(PostLikeEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likePost 실패")
    void testValidateBeforeLikePost_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likePost 실패")
    void testValidateBeforeLikePost_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectPost).given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 likePost 실패")
    void testValidateBeforeLikePost_givenNotPublishedActivitySubjectPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException notFoundEntityException = assertThrows(NotAccessibleException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_LIKE);
    }

    @Test
    @DisplayName("unlikePost로 게시글 좋아요 취소")
    void testUnlikePost_givenValidParameter_willUnlikePost() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isLiked(any(), any())).willReturn(true);
        willDoNothing().given(activitySubjectPostRepository).unlike(any(), any());

        // when
        memberController.unlikePost(testMemberPostUnlikeRecord);

        // then
        verify(activitySubjectPostRepository, times(1)).unlike(any(), any());
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikePost로 게시글 좋아요 취소")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikePost(testMemberPostUnlikeRecord);

        // then
        verify(activitySubjectPostRepository, times(0)).unlike(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 unlikePost 실패")
    void testValidateBeforeUnlikePost_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 unlikePost 실패")
    void testValidateBeforeUnlikePost_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectPost).given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 unlikePost 실패")
    void testValidateBeforeUnlikePost_givenNotPublishedActivitySubjectPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException notFoundEntityException = assertThrows(NotAccessibleException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_LIKE);
    }

    @Test
    @DisplayName("bookmarkPost로 게시글 북마크")
    void testBookmarkPost_givenValidParameter_willBookmarkPost() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isNotBookmarked(any(), any())).willReturn(true);
        willDoNothing().given(activitySubjectPostRepository).bookmark(any(), any());

        // when
        memberController.bookmarkPost(testMemberPostBookmarkRecord);

        // then
        verify(activitySubjectPostRepository, times(1)).bookmark(any(), any());
    }

    @Test
    @DisplayName("이미 북마크를 누른 상태로 bookmarkPost로 게시글 북마크")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyBookmarkedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isNotBookmarked(any(), any())).willReturn(false);

        // when
        memberController.bookmarkPost(testMemberPostBookmarkRecord);

        // then
        verify(activitySubjectPostRepository, times(0)).bookmark(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 bookmarkPost 실패")
    void testValidateBeforeBookmark_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 bookmarkPost 실패")
    void testValidateBeforeBookmark_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectPost).given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 bookmarkPost 실패")
    void testValidateBeforeBookmark_givenNotPublishedActivitySubjectPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException notFoundEntityException = assertThrows(NotAccessibleException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_BOOKMARK);
    }

    @Test
    @DisplayName("cancelPostBookmark로 게시글 북마크 취소")
    void testCancelPostBookmark_givenValidParameter_willCancelPostBookmark() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isBookmarked(any(), any())).willReturn(true);
        willDoNothing().given(activitySubjectPostRepository).cancelBookmark(any(), any());

        // when
        memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // then
        verify(activitySubjectPostRepository, times(1)).cancelBookmark(any(), any());
    }

    @Test
    @DisplayName("이미 북마크를 취소한 상태로 cancelPostBookmark로 게시글 북마크 취소")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyCancelledBookmarkValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(activitySubjectPostRepository.isBookmarked(any(), any())).willReturn(false);

        // when
        memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // then
        verify(activitySubjectPostRepository, times(0)).cancelBookmark(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 cancelPostBookmark 실패")
    void testValidateBeforeCancelBookmark_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 cancelPostBookmark 실패")
    void testValidateBeforeCancelBookmark_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectPost).given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 cancelPostBookmark 실패")
    void testValidateBeforeCancelBookmark_givenNotPublishedActivitySubjectPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException notFoundEntityException = assertThrows(NotAccessibleException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_BOOKMARK);
    }

    @Test
    @DisplayName("likeComment로 댓글 좋아요")
    void testLikeComment_givenValidParameter_willLikeComment() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(activitySubjectCommentRepository.isUnliked(any(), any())).willReturn(true);
        willDoNothing().given(activitySubjectCommentRepository).like(any(), any());
        willDoNothing().given(applicationEventPublisher).publishEvent(any(CommentLikeEvent.class));

        // when
        memberController.likeComment(testMemberCommentLikeRecord);

        // then
        verify(activitySubjectCommentRepository, times(1)).like(any(), any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(CommentLikeEvent.class));
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likeComment로 댓글 좋아요")
    void testValidateBeforeLikeOrUnlikeComment_givenAlreadyLikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(activitySubjectCommentRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likeComment(testMemberCommentLikeRecord);

        // then
        verify(activitySubjectCommentRepository, times(0)).like(any(), any());
        verify(applicationEventPublisher, times(0)).publishEvent(any(CommentLikeEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likeComment 실패")
    void testValidateBeforeLikeComment_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.likeComment(testMemberCommentLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 댓글 아이디로 인해 likeComment 실패")
    void testValidateBeforeLikeComment_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectComment).given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.likeComment(testMemberCommentLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
    }

    @Test
    @DisplayName("unlikeComment로 댓글 좋아요 취소")
    void testUnlikeComment_givenValidParameter_willUnlikeComment() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(activitySubjectCommentRepository.isLiked(any(), any())).willReturn(true);
        willDoNothing().given(activitySubjectCommentRepository).unlike(any(),any());

        // when
        memberController.unlikeComment(testMemberCommentUnlikeRecord);

        // then
        verify(activitySubjectCommentRepository, times(1)).unlike(any(), any());
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikeComment로 댓글 좋아요 취소")
    void testValidateBeforeLikeOrUnlikeComment_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(activitySubjectCommentRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikeComment(testMemberCommentUnlikeRecord);

        // then
        verify(activitySubjectCommentRepository, times(0)).unlike(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 unlikeComment 실패")
    void testValidateBeforeUnlikeComment_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikeComment(testMemberCommentUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 댓글 아이디로 인해 unlikeComment 실패")
    void testValidateBeforeUnlikeComment_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectComment).given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikeComment(testMemberCommentUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
    }

    @Test
    @DisplayName("이미지를 포함해서 존재하는 모든 데이터로 reportProposalOrBug로 건의 및 버그 제보")
    void testReportProposalOrBug_givenExistedImage_willReportProposalOrBug() throws IOException {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberImageIOHelper.uploadImage(
                any(MemberId.class),
                any(ReportId.class),
                anyList()))
                .willReturn(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS);
        willDoNothing().given(reportRepository).reportProposalOrBug(any(), any());

        // when
        memberController.reportProposalOrBug(testProposalOrBugReportRecord);

        // then
        verify(reportRepository, times(1)).reportProposalOrBug(any(), any());
    }

    @Test
    @DisplayName("이미지 경로를 제외한 존재하는 모든 데이터로 reportProposalOrBug로 건의 및 버그 제보")
    void testReportProposalOrBug_givenExistedDataExceptOfImage_willReportProposalOrBug() throws IOException {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(reportRepository).reportProposalOrBug(any(), any());

        // when
        memberController.reportProposalOrBug(new ProposalOrBugReportRecord(MEMBER_BASIC_USER_UUID, TEST_REPORT_TITLE, TEST_REPORT_CONTENT, null, null));

        // then
        verify(reportRepository, times(1)).reportProposalOrBug(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportProposalOrBug로 건의 및 버그 제보 실패")
    void testReportProposalOrBug_givenNotFoundMemberId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportProposalOrBug(testProposalOrBugReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("images가 null이고 imageNumber가 존재하여 reportProposalOrBug로 건의 및 버그 제보 실패")
    void testReportProposalOrBug_givenNullImagesAndNotNullImageNumber_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());

        ProposalOrBugReportRecord invalidRecord = new ProposalOrBugReportRecord(
                MEMBER_BASIC_USER_UUID,
                TEST_REPORT_TITLE,
                TEST_REPORT_CONTENT,
                null,
                TEST_REPORT_IMAGE_NUMBER_3
        );

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> memberController.reportProposalOrBug(invalidRecord));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MISMATCHED_REPORT_IMAGE_SIZE);
    }

    @Test
    @DisplayName("images가 존재하고 imageNumber가 null이어서 reportProposalOrBug로 건의 및 버그 제보 실패")
    void testReportProposalOrBug_givenNotNullImagesAndNullImageNumber_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());

        ProposalOrBugReportRecord invalidRecord = new ProposalOrBugReportRecord(
                MEMBER_BASIC_USER_UUID,
                TEST_REPORT_TITLE,
                TEST_REPORT_CONTENT,
                TEST_REPORT_IMAGES,
                null
        );

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> memberController.reportProposalOrBug(invalidRecord));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MISMATCHED_REPORT_IMAGE_SIZE);
    }

    @Test
    @DisplayName("이미지 리스트의 크기와 이미지 개수가 달라 reportProposalOrBug로 건의 및 버그 제보 실패")
    void testReportProposalOrBug_givenMismatchedSize_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());

        ProposalOrBugReportRecord invalidRecord = new ProposalOrBugReportRecord(
                MEMBER_BASIC_USER_UUID,
                TEST_REPORT_TITLE,
                TEST_REPORT_CONTENT,
                TEST_REPORT_IMAGES,
                TEST_REPORT_IMAGE_NUMBER_3 - 1
        );

        // when
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> memberController.reportProposalOrBug(invalidRecord));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MISMATCHED_REPORT_IMAGE_SIZE);
    }

    @Test
    @DisplayName("reportPostAbuse로 게시글 신고")
    void testReportPostAbuse_givenValidPostAbuseReportRecord_willDoNothing() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(reportRepository.isMemberAbusePost(any(), any())).willReturn(false);
        given(reportRepository.reportPostAbuse(any(), any())).willReturn(testReportTime);
        willDoNothing().given(applicationEventPublisher).publishEvent(any(PostAbuseReportEvent.class));

        // when
        memberController.reportPostAbuse(testPostAbuseReportRecord);

        // then
        verify(reportRepository, times(1)).reportPostAbuse(any(), any());
        verify(applicationEventPublisher, times(1)).publishEvent(any(PostAbuseReportEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportPostAbuse로 게시글 신고 실패")
    void testReportPostAbuse_givenNotFoundMemberId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportPostAbuse(testPostAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 reportPostAbuse로 게시글 신고 실패")
    void testReportPostAbuse_givenNotFoundActivitySubjectPostId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectPost).given(memberValidationHelper).validateIfActivitySubjectPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportPostAbuse(testPostAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 reportPostAbuse로 게시글 신고 실패")
    void testReportPostAbuse_givenNotPublishedActivitySubjectPost_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException notAccessibleException = assertThrows(NotAccessibleException.class,
                () -> memberController.reportPostAbuse(testPostAbuseReportRecord));

        // then
        assertThat(notAccessibleException.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_REPORT_FOR_ABUSE);
    }

    @Test
    @DisplayName("이미 존재하는 게시글 신고로 인해 reportPostAbuse로 게시글 신고 실패")
    void testReportPostAbuse_givenExistedPostAbuse_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectPostExists(any());
        given(activitySubjectPostRepository.isPublished(any())).willReturn(true);
        given(reportRepository.isMemberAbusePost(any(), any())).willReturn(true);

        // when
        ExistsEntityException existsEntityException = assertThrows(ExistsEntityException.class,
                () -> memberController.reportPostAbuse(testPostAbuseReportRecord));

        // then
        assertThat(existsEntityException.getErrorCode()).isEqualTo(EXISTS_POST_ABUSE_REPORT);
    }

    @Test
    @DisplayName("reportCommentAbuse로 댓글 신고")
    void testReportCommentAbuse_givenValidCommentAbuseReportRecord_willDoNothing() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(reportRepository.isMemberAbuseComment(any(), any())).willReturn(false);
        willDoNothing().given(reportRepository).reportCommentAbuse(any(), any());

        // when
        memberController.reportCommentAbuse(testCommentAbuseReportRecord);

        // then
        verify(reportRepository, times(1)).reportCommentAbuse(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 reportCommentAbuse로 댓글 신고 실패")
    void testReportCommentAbuse_givenNotFoundMemberId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportCommentAbuse(testCommentAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    @DisplayName("존재하지 않는 대상 댓글 아이디로 인해 reportCommentAbuse로 댓글 신고 실패")
    void testReportCommentAbuse_givenNotFoundActivitySubjectCommentId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForActivitySubjectComment).given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportCommentAbuse(testCommentAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID);
    }

    @Test
    @DisplayName("이미 존재하는 댓글 신고로 인해 reportCommentAbuse로 댓글 신고 실패")
    void testReportCommentAbuse_givenExistedCommentAbuse_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfActivitySubjectCommentExists(any());
        given(reportRepository.isMemberAbuseComment(any(), any())).willReturn(true);

        // when
        ExistsEntityException existsEntityException = assertThrows(ExistsEntityException.class,
                () -> memberController.reportCommentAbuse(testCommentAbuseReportRecord));

        // then
        assertThat(existsEntityException.getErrorCode()).isEqualTo(EXISTS_COMMENT_ABUSE_REPORT);
    }

    @Test
    @DisplayName("withdraw로 회원 탈퇴")
    void testWithdraw_givenMemberId_willWithdrawMember() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN)).willReturn(MEMBER_BASIC_USER_UUID);
        given(memberJpaRepository.existsByUuid(MEMBER_BASIC_USER_UUID)).willReturn(true);
        given(memberSocialTranslator.getSocialAccessToken(any(), any())).willReturn(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN);
        willDoNothing().given(memberSocialTranslator).deleteSocialAccountWithSocialAccessToken(TEST_SOCIAL_KAKAO_SOCIAL_ACCESS_TOKEN, SocialProvider.KAKAO.getValue(), MEMBER_BASIC_USER_UUID);
        willDoNothing().given(tokenService).blacklistAccessToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
        willDoNothing().given(memberRepository).withdraw(any(), any(), any());

        // when
        memberController.withdraw(testKakaoMemberWithdrawalRecord);

        // then
        verify(tokenService, times(1)).blacklistAccessToken(any());
        verify(memberRepository, times(1)).withdraw(any(), any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 withdraw로 오류 발생")
    void testWithdraw_givenNotFoundMemberId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.withdraw(testKakaoMemberWithdrawalRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Nested
    @DisplayName("유효하지 않은 인증 코드 및 인증 제공자로 인해 withdraw로 오류 발생")
    class invalidWithdrawCallTests {
        @Test
        @DisplayName("인증 제공자만 null인 요청으로 인해 withdraw로 오류 발생")
        void testWithdraw_givenInvalidAuthCodeOrAuthProviderWithNullProvider_willThrowException() {
            // given
            given(jwtTokenProvider.getMemberUuidFromToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN)).willReturn(MEMBER_BASIC_USER_UUID);
            given(memberJpaRepository.existsByUuid(MEMBER_BASIC_USER_UUID)).willReturn(true);

            // when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class,
                    () -> memberController.withdraw(
                            new MemberWithdrawalRecord(
                                    TEST_SOCIAL_KAKAO_CODE,
                                    null,
                                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                                    MEMBER_WITHDRAW_BASIC_USER_OPINION,
                                    MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN)));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(MISMATCHED_AUTH_INFO);
        }

        @Test
        @DisplayName("인증 코드만 null인 요청으로 인해 withdraw로 오류 발생")
        void testWithdraw_givenInvalidAuthCodeOrAuthProviderWithNullCode_willThrowException() {
            // given
            given(jwtTokenProvider.getMemberUuidFromToken(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN)).willReturn(MEMBER_BASIC_USER_UUID);
            given(memberJpaRepository.existsByUuid(MEMBER_BASIC_USER_UUID)).willReturn(true);

            // when
            InvalidValueException invalidValueException = assertThrows(InvalidValueException.class,
                    () -> memberController.withdraw(new MemberWithdrawalRecord(
                            null,
                            SocialProvider.KAKAO.getValue(),
                            MEMBER_WITHDRAW_BASIC_USER_REASON,
                            MEMBER_WITHDRAW_BASIC_USER_OPINION,
                            MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN)));

            // then
            assertThat(invalidValueException.getErrorCode()).isEqualTo(MISMATCHED_AUTH_INFO);
        }
    }
}