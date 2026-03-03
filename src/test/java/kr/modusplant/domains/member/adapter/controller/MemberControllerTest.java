package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberImageIOHelper;
import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.adapter.mapper.MemberProfileMapperImpl;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberProfileTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileIntroduction;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberProfileRepositoryJpaAdapter;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberRepositoryJpaAdapter;
import kr.modusplant.domains.member.framework.out.jpa.repository.TargetCommentIdRepositoryJpaAdapter;
import kr.modusplant.domains.member.framework.out.jpa.repository.TargetPostIdRepositoryJpaAdapter;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.domains.member.usecase.record.MemberProfileOverrideRecord;
import kr.modusplant.domains.member.usecase.record.ProposalOrBugReportRecord;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.entity.common.util.*;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.event.consumer.CommentEventConsumer;
import kr.modusplant.infrastructure.event.consumer.PostEventConsumer;
import kr.modusplant.infrastructure.event.consumer.ReportEventConsumer;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.swear.exception.SwearContainedException;
import kr.modusplant.infrastructure.swear.exception.enums.SwearErrorCode;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
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
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportRecordTestUtils.testPostAbuseReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordTestUtils.testProposalOrBugReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.request.MemberRegisterRequestTestUtils.testMemberRegisterRequest;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberResponseTestUtils.testMemberResponse;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.shared.event.common.util.CommentLikeEventTestUtils.testCommentLikeEvent;
import static kr.modusplant.shared.event.common.util.PostBookmarkEventTestUtils.testPostBookmarkEvent;
import static kr.modusplant.shared.event.common.util.PostCancelPostBookmarkEventTestUtils.testPostBookmarkCancelEvent;
import static kr.modusplant.shared.event.common.util.PostLikeEventTestUtils.testPostLikeEvent;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberControllerTest implements
        MemberTestUtils, MemberProfileTestUtils,
        SiteMemberProfileEntityTestUtils, CommPostEntityTestUtils, CommCommentEntityTestUtils,
        PropBugRepEntityTestUtils, CommPostAbuRepEntityTestUtils, CommCommentAbuRepEntityTestUtils {
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
    private final SwearService swearService = Mockito.mock(SwearService.class);
    private final JwtTokenProvider jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final MemberImageIOHelper memberImageIOHelper = Mockito.mock(MemberImageIOHelper.class);
    private final MemberValidationHelper memberValidationHelper = Mockito.mock(MemberValidationHelper.class);
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl(s3FileService);

    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final MemberProfileRepository memberProfileRepository = Mockito.mock(MemberProfileRepositoryJpaAdapter.class);
    private final TargetPostIdRepository targetPostIdRepository = Mockito.mock(TargetPostIdRepositoryJpaAdapter.class);
    private final TargetCommentIdRepository targetCommentIdRepository = Mockito.mock(TargetCommentIdRepositoryJpaAdapter.class);

    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommPostLikeJpaRepository postLikeJpaRepository = Mockito.mock(CommPostLikeJpaRepository.class);
    private final CommPostBookmarkJpaRepository postBookmarkJpaRepository = Mockito.mock(CommPostBookmarkJpaRepository.class);
    private final CommCommentJpaRepository commentJpaRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final CommCommentLikeJpaRepository commentLikeJpaRepository = Mockito.mock(CommCommentLikeJpaRepository.class);
    private final PropBugRepJpaRepository propBugRepJpaRepository = Mockito.mock(PropBugRepJpaRepository.class);
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository = Mockito.mock(CommPostAbuRepJpaRepository.class);
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository = Mockito.mock(CommCommentAbuRepJpaRepository.class);

    private final EventBus eventBus = new EventBus();
    private final PostEventConsumer postEventConsumer = new PostEventConsumer(eventBus, postLikeJpaRepository, postBookmarkJpaRepository, postJpaRepository);
    private final CommentEventConsumer commentEventConsumer = new CommentEventConsumer(eventBus, commentLikeJpaRepository, commentJpaRepository);
    private final ReportEventConsumer reportEventConsumer = new ReportEventConsumer(eventBus, memberJpaRepository, postJpaRepository, commentJpaRepository, propBugRepJpaRepository, postAbuRepJpaRepository, commentAbuRepJpaRepository);
    private final MemberController memberController = new MemberController(s3FileService, swearService, jwtTokenProvider, memberMapper, memberProfileMapper, memberImageIOHelper, memberValidationHelper, memberRepository, memberProfileRepository, targetPostIdRepository, targetCommentIdRepository, eventBus);

    private final NotFoundEntityException notFoundEntityExceptionForMember = new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
    private final ExistsEntityException existsEntityExceptionForNickname = new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "nickname");
    private final NotFoundEntityException notFoundEntityExceptionForTargetPost = new NotFoundEntityException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
    private final NotFoundEntityException notFoundEntityExceptionForTargetComment = new NotFoundEntityException(NOT_FOUND_TARGET_COMMENT_ID, "targetCommentId");

    @Test
    @DisplayName("register로 회원 등록")
    void testRegister_givenValidRegisterMemberRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfNicknameNotDuplicated(any());
        given(memberRepository.add(any())).willReturn(createMember());

        // when & then
        assertThat(memberController.registerMember(testMemberRegisterRequest)).isEqualTo(testMemberResponse);
    }

    @Test
    @DisplayName("중복된 닉네임으로 인해 register로 회원 등록 실패")
    void testValidateBeforeRegister_Member_givenAlreadyExistedNickname_willThrowException() {
        // given
        willThrow(existsEntityExceptionForNickname).given(memberValidationHelper).validateIfNicknameNotDuplicated(any());

        // when & then
        ExistsEntityException alreadyExistedNicknameException = assertThrows(
                ExistsEntityException.class, () -> memberController.registerMember(testMemberRegisterRequest));
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(KernelErrorCode.EXISTS_NICKNAME);
    }

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
        given(memberProfileRepository.getById(any())).willReturn(Optional.of(createMemberProfile()));
        given(s3FileService.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when & then
        assertThat(memberController.getProfile(testMemberProfileGetRecord)).isEqualTo(testMemberProfileResponse);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 getProfile로 회원 프로필 조회 실패")
    void testGetProfile_givenNotFoundMemberId_willThrowException() {
        // given
        willThrow(notFoundEntityExceptionForMember).given(memberValidationHelper).validateIfMemberExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.getProfile(testMemberProfileGetRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 회원 프로필로 인해 getProfile로 회원 프로필 조회 실패")
    void testGetProfile_givenNotFoundMemberProfile_willThrowException() throws IOException {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberProfileRepository.getById(any())).willReturn(Optional.empty());

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> memberController.getProfile(testMemberProfileGetRecord));
        assertThat(exception.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_MEMBER_PROFILE);
    }

    @Test
    @DisplayName("이미지 경로를 포함해서 존재하는 모든 데이터로 overrideProfile로 프로필 덮어쓰기")
    void testOverrideProfile_givenExistedData_willReturnResponse() throws IOException {
        // given
        MemberProfile memberProfile = createMemberProfile();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberRepository.getByNickname(any())).willReturn(Optional.empty());
        given(memberProfileRepository.getById(any())).willReturn(Optional.of(memberProfile));
        given(swearService.filterSwear(any())).willReturn(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
        given(memberImageIOHelper.uploadImage(any(MemberId.class), any(MemberProfileOverrideRecord.class))).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        willDoNothing().given(s3FileService).deleteFiles(any());
        given(memberProfileRepository.update(any())).willReturn(memberProfile);
        given(s3FileService.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

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
        given(memberProfileRepository.getById(any())).willReturn(Optional.of(
                MemberProfile.create(
                        testMemberId,
                        EmptyMemberProfileImage.create(),
                        testMemberProfileIntroduction,
                        testNormalUserNickname))
        );
        given(swearService.filterSwear(any())).willReturn(MEMBER_PROFILE_BASIC_USER_INTRODUCTION);
        willDoNothing().given(s3FileService).deleteFiles(any());
        given(memberImageIOHelper.uploadImage(any(MemberId.class), any(MemberProfileOverrideRecord.class))).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH);
        given(memberProfileRepository.update(any())).willReturn(memberProfile);
        given(s3FileService.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

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
        given(memberProfileRepository.getById(any())).willReturn(Optional.of(memberProfile));
        given(memberProfileRepository.update(any())).willReturn(memberProfile);
        willDoNothing().given(s3FileService).deleteFiles(any());

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
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
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
        given(memberRepository.getByNickname(any())).willReturn(Optional.of(Member.create(MemberId.generate(), testMemberActiveStatus, testNormalUserNickname, testMemberBirthDate)));

        // when & then
        ExistsEntityException alreadyExistedNicknameException = assertThrows(
                ExistsEntityException.class, () -> memberController.overrideProfile(testMemberProfileOverrideRecord));
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(KernelErrorCode.EXISTS_NICKNAME);
    }

    @Test
    @DisplayName("존재하는 않는 회원 프로필로 인해 overrideProfile로 프로필 덮어쓰기 실패")
    void testOverrideProfile_givenNotFoundMemberProfile_willThrowException() throws IOException {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(swearService.isSwearContained(any())).willReturn(false);
        given(memberRepository.getByNickname(any())).willReturn(Optional.empty());
        given(memberProfileRepository.getById(any())).willReturn(Optional.empty());

        // when
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class, () -> memberController.overrideProfile(
                new MemberProfileOverrideRecord(MEMBER_BASIC_USER_UUID, null, null, MEMBER_BASIC_USER_NICKNAME)));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_MEMBER_PROFILE);
    }

    @Test
    @DisplayName("likePost로 게시글 좋아요")
    void testLikePost_givenValidParameter_willLikePost() {
        // given
        UUID memberId = testPostLikeEvent.getMemberId();
        String postId = testPostLikeEvent.getPostId();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(true);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        given(postLikeJpaRepository.save(postLikeEntity)).willReturn(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(postJpaRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.likePost(testMemberPostLikeRecord);

        // then
        verify(postLikeJpaRepository, times(1)).save(any());
        verify(postJpaRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likePost로 게시글 좋아요")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyLikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likePost(testMemberPostLikeRecord);

        // then
        verify(postLikeJpaRepository, times(0)).save(any());
        verify(postJpaRepository, times(0)).findByUlid(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likePost 실패")
    void testValidateBeforeLikePost_givenNotFoundTargetPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetPost).given(memberValidationHelper).validateIfTargetPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 likePost 실패")
    void testValidateBeforeLikePost_givenNotPublishedTargetPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

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
        UUID memberId = testPostLikeEvent.getMemberId();
        String postId = testPostLikeEvent.getPostId();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(true);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        willDoNothing().given(postLikeJpaRepository).delete(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(postJpaRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.unlikePost(testMemberPostUnlikeRecord);

        // then
        verify(postLikeJpaRepository, times(1)).delete(any());
        verify(postJpaRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikePost로 게시글 좋아요 취소")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikePost(testMemberPostUnlikeRecord);

        // then
        verify(postLikeJpaRepository, times(0)).delete(any());
        verify(postJpaRepository, times(0)).findByUlid(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 unlikePost 실패")
    void testValidateBeforeUnlikePost_givenNotFoundTargetPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetPost).given(memberValidationHelper).validateIfTargetPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 unlikePost 실패")
    void testValidateBeforeUnlikePost_givenNotPublishedTargetPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

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
        UUID memberId = testPostBookmarkEvent.getMemberId();
        String postId = testPostBookmarkEvent.getPostId();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isNotBookmarked(any(), any())).willReturn(true);
        CommPostBookmarkEntity postBookmarkEntity = CommPostBookmarkEntity.of(postId, memberId);
        given(postBookmarkJpaRepository.save(postBookmarkEntity)).willReturn(postBookmarkEntity);

        // when
        memberController.bookmarkPost(testMemberPostBookmarkRecord);

        // then
        verify(postBookmarkJpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미 북마크를 누른 상태로 bookmarkPost로 게시글 북마크")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyBookmarkedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isNotBookmarked(any(), any())).willReturn(false);

        // when
        memberController.bookmarkPost(testMemberPostBookmarkRecord);

        // then
        verify(postBookmarkJpaRepository, times(0)).save(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 bookmarkPost 실패")
    void testValidateBeforeBookmark_givenNotFoundTargetPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetPost).given(memberValidationHelper).validateIfTargetPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 bookmarkPost 실패")
    void testValidateBeforeBookmark_givenNotPublishedTargetPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

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
        UUID memberId = testPostBookmarkCancelEvent.getMemberId();
        String postId = testPostBookmarkCancelEvent.getPostId();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isBookmarked(any(), any())).willReturn(true);
        CommPostBookmarkEntity postBookmarkEntity = CommPostBookmarkEntity.of(postId, memberId);
        willDoNothing().given(postBookmarkJpaRepository).delete(postBookmarkEntity);

        // when
        memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // then
        verify(postBookmarkJpaRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("이미 북마크를 취소한 상태로 cancelPostBookmark로 게시글 북마크 취소")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyCancelledBookmarkValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isBookmarked(any(), any())).willReturn(false);

        // when
        memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // then
        verify(postBookmarkJpaRepository, times(0)).delete(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 cancelPostBookmark 실패")
    void testValidateBeforeCancelBookmark_givenNotFoundTargetPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetPost).given(memberValidationHelper).validateIfTargetPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 cancelPostBookmark 실패")
    void testValidateBeforeCancelBookmark_givenNotPublishedTargetPost_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

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
        UUID memberId = testCommentLikeEvent.getMemberId();
        String postId = testCommentLikeEvent.getPostId();
        String path = testCommentLikeEvent.getPath();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetCommentExists(any());
        given(targetCommentIdRepository.isUnliked(any(), any())).willReturn(true);
        CommCommentLikeEntity commentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        given(commentLikeJpaRepository.save(commentLikeEntity)).willReturn(commentLikeEntity);
        Optional<CommCommentEntity> commentEntity = Optional.of(CommCommentEntity.builder().postEntity(createCommPostEntityBuilder().ulid(postId).build()).path(path).likeCount(1).build());
        given(commentJpaRepository.findByPostUlidAndPath(postId, path)).willReturn(commentEntity);

        // when
        memberController.likeComment(testMemberCommentLikeRecord);

        // then
        verify(commentLikeJpaRepository, times(1)).save(any());
        verify(commentJpaRepository, times(1)).findByPostUlidAndPath(any(), any());
        assertThat(commentEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likeComment로 댓글 좋아요")
    void testValidateBeforeLikeOrUnlikeComment_givenAlreadyLikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetCommentExists(any());
        given(targetCommentIdRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likeComment(testMemberCommentLikeRecord);

        // then
        verify(commentLikeJpaRepository, times(0)).save(any());
        verify(commentJpaRepository, times(0)).findByPostUlidAndPath(any(), any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 댓글 아이디로 인해 likeComment 실패")
    void testValidateBeforeLikeComment_givenNotFoundTargetPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetComment).given(memberValidationHelper).validateIfTargetCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.likeComment(testMemberCommentLikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID);
    }

    @Test
    @DisplayName("unlikeComment로 댓글 좋아요 취소")
    void testUnlikeComment_givenValidParameter_willUnlikeComment() {
        // given
        UUID memberId = testCommentLikeEvent.getMemberId();
        String postId = testCommentLikeEvent.getPostId();
        String path = testCommentLikeEvent.getPath();
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetCommentExists(any());
        given(targetCommentIdRepository.isLiked(any(), any())).willReturn(true);
        CommCommentLikeEntity commentLikeEntity = CommCommentLikeEntity.of(postId, path, memberId);
        given(commentLikeJpaRepository.save(commentLikeEntity)).willReturn(commentLikeEntity);
        Optional<CommCommentEntity> commentEntity = Optional.of(CommCommentEntity.builder().postEntity(createCommPostEntityBuilder().ulid(postId).build()).path(path).likeCount(1).build());
        given(commentJpaRepository.findByPostUlidAndPath(postId, path)).willReturn(commentEntity);

        // when
        memberController.unlikeComment(testMemberCommentUnlikeRecord);

        // then
        verify(commentLikeJpaRepository, times(1)).delete(any());
        verify(commentJpaRepository, times(1)).findByPostUlidAndPath(any(), any());
        assertThat(commentEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikeComment로 댓글 좋아요 취소")
    void testValidateBeforeLikeOrUnlikeComment_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetCommentExists(any());
        given(targetCommentIdRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikeComment(testMemberCommentUnlikeRecord);

        // then
        verify(commentLikeJpaRepository, times(0)).delete(any());
        verify(commentJpaRepository, times(0)).findByPostUlidAndPath(any(), any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 댓글 아이디로 인해 unlikeComment 실패")
    void testValidateBeforeUnlikeComment_givenNotFoundTargetPostId_willThrowException() {
        // given
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetComment).given(memberValidationHelper).validateIfTargetCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikeComment(testMemberCommentUnlikeRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID);
    }

    @Test
    @DisplayName("이미지 경로를 포함해서 존재하는 모든 데이터로 reportProposalOrBug로 건의 및 버그 제보")
    void testReportProposalOrBug_givenExistedData_willReportProposalOrBug() throws IOException {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        PropBugRepEntity propBugRepEntity = createPropBugRepEntityBuilderWithUlid().member(memberEntity).build();

        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberImageIOHelper.uploadImage(any(MemberId.class), any(ProposalOrBugReportRecord.class))).willReturn(REPORT_IMAGE_PATH);
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberEntity));
        given(propBugRepJpaRepository.save(propBugRepEntity)).willReturn(propBugRepEntity);

        // when
        memberController.reportProposalOrBug(testProposalOrBugReportRecord);

        // then
        verify(memberJpaRepository, times(1)).findByUuid(any());
        verify(propBugRepJpaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미지 경로를 제외한 존재하는 모든 데이터로 reportProposalOrBug로 건의 및 버그 제보")
    void testReportProposalOrBug_givenExistedDataExceptOfImagePath_willReportProposalOrBug() throws IOException {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        PropBugRepEntity propBugRepEntity = createPropBugRepEntityBuilderWithUlid().member(memberEntity).imagePath(null).build();

        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberEntity));
        given(propBugRepJpaRepository.save(propBugRepEntity)).willReturn(propBugRepEntity);

        // when
        memberController.reportProposalOrBug(new ProposalOrBugReportRecord(MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN, REPORT_TITLE, REPORT_CONTENT, null));

        // then
        verify(memberJpaRepository, times(1)).findByUuid(any());
        verify(propBugRepJpaRepository, times(1)).save(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("reportPostAbuse로 게시글 신고")
    void testReportPostAbuse_givenValidPostAbuseReportRecord_willDoNothing() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        CommPostEntity postEntity = createCommPostEntityBuilder().authMember(memberEntity).build();
        CommPostAbuRepEntity postAbuRepEntity = createCommPostAbuRepEntityBuilder().member(memberEntity).post(postEntity).build();

        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(any())).willReturn(Optional.of(postEntity));
        given(postAbuRepJpaRepository.save(any())).willReturn(postAbuRepEntity);

        // when
        memberController.reportPostAbuse(testPostAbuseReportRecord);

        // then
        verify(memberJpaRepository, times(1)).findByUuid(any());
        verify(postJpaRepository, times(1)).findByUlid(any());
        verify(postAbuRepJpaRepository, times(1)).save(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 reportPostAbuse로 게시글 신고 실패")
    void testReportPostAbuse_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetPost).given(memberValidationHelper).validateIfTargetPostExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportPostAbuse(testPostAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 reportPostAbuse로 게시글 신고 실패")
    void testReportPostAbuse_givenNotPublishedTargetPost_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetPostExists(any());
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException notFoundEntityException = assertThrows(NotAccessibleException.class,
                () -> memberController.reportPostAbuse(testPostAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_REPORT_FOR_ABUSE);
    }

    @Test
    @DisplayName("reportCommentAbuse로 댓글 신고")
    void testReportCommentAbuse_givenValidCommentAbuseReportRecord_willDoNothing() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        CommPostEntity postEntity = createCommPostEntityBuilder().authMember(memberEntity).build();
        CommCommentEntity commentEntity = createCommCommentEntityBuilder().authMember(memberEntity).postEntity(postEntity).build();
        CommCommentAbuRepEntity commentAbuRepEntity = createCommCommentAbuRepEntityBuilder().member(memberEntity).comment(commentEntity).build();

        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willDoNothing().given(memberValidationHelper).validateIfTargetCommentExists(any());
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.of(memberEntity));
        given(commentJpaRepository.findById(any())).willReturn(Optional.of(commentEntity));
        given(commentAbuRepJpaRepository.save(any())).willReturn(commentAbuRepEntity);

        // when
        memberController.reportCommentAbuse(testCommentAbuseReportRecord);

        // then
        verify(memberJpaRepository, times(1)).findByUuid(any());
        verify(commentJpaRepository, times(1)).findById(any());
        verify(commentAbuRepJpaRepository, times(1)).save(any());
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
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 댓글 아이디로 인해 reportCommentAbuse로 댓글 신고 실패")
    void testReportCommentAbuse_givenNotFoundTargetCommentId_willThrowException() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberValidationHelper).validateIfMemberExists(any());
        willThrow(notFoundEntityExceptionForTargetComment).given(memberValidationHelper).validateIfTargetCommentExists(any());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportCommentAbuse(testCommentAbuseReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID);
    }
}