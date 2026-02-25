package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberImageIOHelper;
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
import kr.modusplant.framework.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.PropBugRepEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberProfileEntityTestUtils;
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
import kr.modusplant.shared.event.common.util.PostLikeEventTestUtils;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberBirthDateTestUtils.testMemberBirthDate;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberProfileIntroductionTestUtils.testMemberProfileIntroduction;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberStatusTestUtils.testMemberActiveStatus;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCancelPostBookmarkRecordTestUtils.testMemberPostBookmarkCancelRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentLikeRecordTestUtils.testMemberCommentLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentUnlikeRecordTestUtils.testMemberCommentUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberNicknameCheckRecordTestUtils.testMemberNicknameCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostBookmarkRecordTestUtils.testMemberPostBookmarkRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostLikeRecordTestUtils.testMemberPostLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostUnlikeRecordTestUtils.testMemberPostUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileGetRecordTestUtils.testMemberProfileGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecordTestUtils.testProposalOrBugReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.request.MemberRegisterRequestTestUtils.testMemberRegisterRequest;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberResponseTestUtils.testMemberResponse;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;
import static kr.modusplant.shared.event.common.util.CommentLikeEventTestUtils.testCommentLikeEvent;
import static kr.modusplant.shared.event.common.util.PostBookmarkEventTestUtils.testPostBookmarkEvent;
import static kr.modusplant.shared.event.common.util.PostCancelPostBookmarkEventTestUtils.testPostBookmarkCancelEvent;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MemberControllerTest implements MemberTestUtils, MemberProfileTestUtils, PostLikeEventTestUtils, CommPostEntityTestUtils, SiteMemberProfileEntityTestUtils, PropBugRepEntityTestUtils {
    private final S3FileService s3FileService = Mockito.mock(S3FileService.class);
    private final SwearService swearService = Mockito.mock(SwearService.class);
    private final JwtTokenProvider jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final MemberImageIOHelper memberImageIOHelper = Mockito.mock(MemberImageIOHelper.class);
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberProfileMapper memberProfileMapper = new MemberProfileMapperImpl(s3FileService);

    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final MemberProfileRepository memberProfileRepository = Mockito.mock(MemberProfileRepositoryJpaAdapter.class);
    private final TargetPostIdRepository targetPostIdRepository = Mockito.mock(TargetPostIdRepositoryJpaAdapter.class);
    private final TargetCommentIdRepository targetCommentIdRepository = Mockito.mock(TargetCommentIdRepositoryJpaAdapter.class);

    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final CommPostJpaRepository commPostRepository = Mockito.mock(CommPostJpaRepository.class);
    private final CommPostLikeJpaRepository commPostLikeRepository = Mockito.mock(CommPostLikeJpaRepository.class);
    private final CommPostBookmarkJpaRepository commPostBookmarkRepository = Mockito.mock(CommPostBookmarkJpaRepository.class);
    private final CommCommentJpaRepository commCommentRepository = Mockito.mock(CommCommentJpaRepository.class);
    private final CommCommentLikeJpaRepository commCommentLikeRepository = Mockito.mock(CommCommentLikeJpaRepository.class);
    private final PropBugRepJpaRepository propBugRepJpaRepository = Mockito.mock(PropBugRepJpaRepository.class);

    private final EventBus eventBus = new EventBus();
    private final PostEventConsumer postEventConsumer = new PostEventConsumer(eventBus, commPostLikeRepository, commPostBookmarkRepository, commPostRepository);
    private final CommentEventConsumer commentEventConsumer = new CommentEventConsumer(eventBus, commCommentLikeRepository, commCommentRepository);
    private final ReportEventConsumer reportEventConsumer = new ReportEventConsumer(eventBus, memberJpaRepository, propBugRepJpaRepository);
    private final MemberController memberController = new MemberController(s3FileService, swearService, jwtTokenProvider, memberMapper, memberProfileMapper, memberImageIOHelper, memberRepository, memberProfileRepository, targetPostIdRepository, targetCommentIdRepository, eventBus);

    @Test
    @DisplayName("register로 회원 등록")
    void testRegister_givenValidRegisterMemberRequest_willReturnResponse() {
        // given
        given(memberRepository.isNicknameExist(any())).willReturn(false);
        given(memberRepository.add(any())).willReturn(createMember());

        // when & then
        assertThat(memberController.registerMember(testMemberRegisterRequest)).isEqualTo(testMemberResponse);
    }

    @Test
    @DisplayName("중복된 닉네임으로 인해 register로 회원 등록 실패")
    void testValidateBeforeRegister_Member_givenAlreadyExistedNickname_willThrowException() {
        // given
        given(memberRepository.isNicknameExist(any())).willReturn(true);

        // when & then
        ExistsEntityException alreadyExistedNicknameException = assertThrows(
                ExistsEntityException.class, () -> memberController.registerMember(testMemberRegisterRequest));
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(KernelErrorCode.EXISTS_NICKNAME);
    }

    @Test
    @DisplayName("checkExistedNickname으로 회원 닉네임 중복 확인")
    void testCheckExistedNickname_givenValidCheckNicknameRequest_willReturnResponse() {
        // given
        given(memberRepository.isNicknameExist(any())).willReturn(true);

        // when & then
        assertThat(memberController.checkExistedNickname(testMemberNicknameCheckRecord)).isEqualTo(true);
    }

    @Test
    @DisplayName("기존에 저장된 회원 프로필이 있을 때 getProfile로 회원 프로필 조회")
    void testGetProfile_givenValidGetRecordAndStoredMemberProfile_willReturnResponse() throws IOException {
        // given
        given(memberRepository.getById(any())).willReturn(Optional.of(createMember()));
        given(memberProfileRepository.getById(any())).willReturn(Optional.of(createMemberProfile()));
        given(s3FileService.generateS3SrcUrl(any())).willReturn(MEMBER_PROFILE_BASIC_USER_IMAGE_URL);

        // when & then
        assertThat(memberController.getProfile(testMemberProfileGetRecord)).isEqualTo(testMemberProfileResponse);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 getProfile로 회원 프로필 조회 실패")
    void testGetProfile_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.getById(any())).willReturn(Optional.empty());

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
        given(memberRepository.getById(any())).willReturn(Optional.of(createMember()));
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
        given(memberRepository.isIdExist(any())).willReturn(true);
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
        given(memberRepository.isIdExist(any())).willReturn(true);
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
        given(memberRepository.isIdExist(any())).willReturn(true);
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
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when & then
        NotFoundEntityException alreadyExistedNicknameException = assertThrows(
                NotFoundEntityException.class, () -> memberController.overrideProfile(testMemberProfileOverrideRecord));
        assertThat(alreadyExistedNicknameException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("닉네임에 사용된 비속어로 인해 overrideProfile로 프로필 덮어쓰기 실패")
    void testValidateThatHasSwear_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
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
        given(memberRepository.isIdExist(any())).willReturn(true);
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
        given(memberRepository.isIdExist(any())).willReturn(true);
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
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(true);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        given(commPostLikeRepository.save(postLikeEntity)).willReturn(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.likePost(testMemberPostLikeRecord);

        // then
        verify(commPostLikeRepository, times(1)).save(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태로 likePost로 게시글 좋아요")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyLikedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isUnliked(any(), any())).willReturn(false);

        // when
        memberController.likePost(testMemberPostLikeRecord);

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
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(true);
        CommPostLikeEntity postLikeEntity = CommPostLikeEntity.of(postId, memberId);
        willDoNothing().given(commPostLikeRepository).delete(postLikeEntity);
        Optional<CommPostEntity> postEntity = Optional.of(CommPostEntity.builder().ulid(postId).likeCount(1).build());
        given(commPostRepository.findByUlid(postId)).willReturn(postEntity);

        // when
        memberController.unlikePost(testMemberPostUnlikeRecord);

        // then
        verify(commPostLikeRepository, times(1)).delete(any());
        verify(commPostRepository, times(1)).findByUlid(any());
        assertThat(postEntity.orElseThrow().getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미 좋아요를 취소한 상태로 unlikePost로 게시글 좋아요 취소")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyUnlikedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isLiked(any(), any())).willReturn(false);

        // when
        memberController.unlikePost(testMemberPostUnlikeRecord);

        // then
        verify(commPostLikeRepository, times(0)).delete(any());
        verify(commPostRepository, times(0)).findByUlid(any());
    }

    @Test
    @DisplayName("bookmarkPost로 게시글 북마크")
    void testBookmarkPost_givenValidParameter_willBookmarkPost() {
        // given
        UUID memberId = testPostBookmarkEvent.getMemberId();
        String postId = testPostBookmarkEvent.getPostId();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isNotBookmarked(any(), any())).willReturn(true);
        CommPostBookmarkEntity postBookmarkEntity = CommPostBookmarkEntity.of(postId, memberId);
        given(commPostBookmarkRepository.save(postBookmarkEntity)).willReturn(postBookmarkEntity);

        // when
        memberController.bookmarkPost(testMemberPostBookmarkRecord);

        // then
        verify(commPostBookmarkRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("이미 북마크를 누른 상태로 bookmarkPost로 게시글 북마크")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyBookmarkedValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isNotBookmarked(any(), any())).willReturn(false);

        // when
        memberController.bookmarkPost(testMemberPostBookmarkRecord);

        // then
        verify(commPostBookmarkRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("cancelPostBookmark로 게시글 북마크 취소")
    void testCancelPostBookmark_givenValidParameter_willCancelPostBookmark() {
        // given
        UUID memberId = testPostBookmarkCancelEvent.getMemberId();
        String postId = testPostBookmarkCancelEvent.getPostId();
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isBookmarked(any(), any())).willReturn(true);
        CommPostBookmarkEntity postBookmarkEntity = CommPostBookmarkEntity.of(postId, memberId);
        willDoNothing().given(commPostBookmarkRepository).delete(postBookmarkEntity);

        // when
        memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // then
        verify(commPostBookmarkRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("이미 북마크를 취소한 상태로 cancelPostBookmark로 게시글 북마크 취소")
    void testValidateBeforeUsingLikeOrBookmarkFunction_givenAlreadyCancelledBookmarkValue_willDoNothing() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(true);
        given(targetPostIdRepository.isBookmarked(any(), any())).willReturn(false);

        // when
        memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // then
        verify(commPostBookmarkRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 likePost 또는 unlikePost 실패")
    void testValidateBeforeLikeOrUnlikePost_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        NotFoundEntityException NotFoundEntityExceptionForLike = assertThrows(NotFoundEntityException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));
        NotFoundEntityException NotFoundEntityExceptionForUnlike = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(NotFoundEntityExceptionForLike.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
        assertThat(NotFoundEntityExceptionForUnlike.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likePost 또는 unlikePost 실패")
    void testValidateBeforeLikeOrUnlikePost_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(false);

        // when
        NotFoundEntityException NotFoundEntityExceptionForLike = assertThrows(NotFoundEntityException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));
        NotFoundEntityException NotFoundEntityExceptionForUnlike = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(NotFoundEntityExceptionForLike.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
        assertThat(NotFoundEntityExceptionForUnlike.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 likePost 또는 unlikePost 실패")
    void testValidateBeforeLikeOrUnlikePost_givenNotPublishedTargetPost_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException NotFoundEntityExceptionForLike = assertThrows(NotAccessibleException.class,
                () -> memberController.likePost(testMemberPostLikeRecord));
        NotAccessibleException NotFoundEntityExceptionForUnlike = assertThrows(NotAccessibleException.class,
                () -> memberController.unlikePost(testMemberPostUnlikeRecord));

        // then
        assertThat(NotFoundEntityExceptionForLike.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_LIKE);
        assertThat(NotFoundEntityExceptionForUnlike.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_LIKE);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 bookmarkPost 또는 cancelPostBookmark 실패")
    void testValidateBeforeBookmarkOrCancelBookmark_givenNotFoundMemberId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(false);

        // when
        NotFoundEntityException NotFoundEntityExceptionForBookmark = assertThrows(NotFoundEntityException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));
        NotFoundEntityException NotFoundEntityExceptionForCancelBookmark = assertThrows(NotFoundEntityException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(NotFoundEntityExceptionForBookmark.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
        assertThat(NotFoundEntityExceptionForCancelBookmark.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 bookmarkPost 또는 cancelPostBookmark 실패")
    void testValidateBeforeBookmarkOrCancelBookmark_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(false);

        // when
        NotFoundEntityException NotFoundEntityExceptionForBookmark = assertThrows(NotFoundEntityException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));
        NotFoundEntityException NotFoundEntityExceptionForCancelBookmark = assertThrows(NotFoundEntityException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(NotFoundEntityExceptionForBookmark.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
        assertThat(NotFoundEntityExceptionForCancelBookmark.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_POST_ID);
    }

    @Test
    @DisplayName("발행되지 않은 대상 게시글로 인해 bookmarkPost 또는 cancelPostBookmark 실패")
    void testValidateBeforeBookmarkOrCancelBookmark_givenNotPublishedTargetPost_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isIdExist(any())).willReturn(true);
        given(targetPostIdRepository.isPublished(any())).willReturn(false);

        // when
        NotAccessibleException NotFoundEntityExceptionForBookmark = assertThrows(NotAccessibleException.class,
                () -> memberController.bookmarkPost(testMemberPostBookmarkRecord));
        NotAccessibleException NotFoundEntityExceptionForCancelBookmark = assertThrows(NotAccessibleException.class,
                () -> memberController.cancelPostBookmark(testMemberPostBookmarkCancelRecord));

        // then
        assertThat(NotFoundEntityExceptionForBookmark.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_BOOKMARK);
        assertThat(NotFoundEntityExceptionForCancelBookmark.getErrorCode()).isEqualTo(NOT_ACCESSIBLE_POST_BOOKMARK);
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
        memberController.likeComment(testMemberCommentLikeRecord);

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
        memberController.likeComment(testMemberCommentLikeRecord);

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
        memberController.unlikeComment(testMemberCommentUnlikeRecord);

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
        memberController.unlikeComment(testMemberCommentUnlikeRecord);

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
        NotFoundEntityException NotFoundEntityExceptionForLike = assertThrows(NotFoundEntityException.class,
                () -> memberController.likeComment(testMemberCommentLikeRecord));
        NotFoundEntityException NotFoundEntityExceptionForUnlike = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikeComment(testMemberCommentUnlikeRecord));

        // then
        assertThat(NotFoundEntityExceptionForLike.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
        assertThat(NotFoundEntityExceptionForUnlike.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }

    @Test
    @DisplayName("존재하지 않는 대상 게시글 아이디로 인해 likeComment 또는 unlikeComment 실패")
    void testValidateBeforeLikeOrUnlikeComment_givenNotFoundTargetPostId_willThrowException() {
        // given
        given(memberRepository.isIdExist(any())).willReturn(true);
        given(targetCommentIdRepository.isIdExist(any())).willReturn(false);

        // when
        NotFoundEntityException NotFoundEntityExceptionForLike = assertThrows(NotFoundEntityException.class,
                () -> memberController.likeComment(testMemberCommentLikeRecord));
        NotFoundEntityException NotFoundEntityExceptionForUnlike = assertThrows(NotFoundEntityException.class,
                () -> memberController.unlikeComment(testMemberCommentUnlikeRecord));

        // then
        assertThat(NotFoundEntityExceptionForLike.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID);
        assertThat(NotFoundEntityExceptionForUnlike.getErrorCode()).isEqualTo(NOT_FOUND_TARGET_COMMENT_ID);
    }

    @Test
    @DisplayName("이미지 경로를 포함해서 존재하는 모든 데이터로 reportProposalOrBug로 건의 및 버그 제보")
    void testReportProposalOrBug_givenExistedData_willReportProposalOrBug() throws IOException {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        PropBugRepEntity propBugRepEntity = createPropBugRepEntityBuilderWithUlid().member(memberEntity).build();

        given(jwtTokenProvider.getMemberUuidFromToken(any())).willReturn(MEMBER_BASIC_USER_UUID);
        given(memberRepository.getById(any())).willReturn(Optional.of(createMember()));
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
        given(memberRepository.getById(any())).willReturn(Optional.of(createMember()));
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
        given(memberRepository.getById(any())).willReturn(Optional.empty());

        // when
        NotFoundEntityException notFoundEntityException = assertThrows(NotFoundEntityException.class,
                () -> memberController.reportProposalOrBug(testProposalOrBugReportRecord));

        // then
        assertThat(notFoundEntityException.getErrorCode()).isEqualTo(NOT_FOUND_MEMBER_ID);
    }
}