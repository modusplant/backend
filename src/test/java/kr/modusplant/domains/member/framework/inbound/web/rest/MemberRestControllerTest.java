package kr.modusplant.domains.member.framework.inbound.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.inbound.web.cache.record.MemberCacheValidationResult;
import kr.modusplant.domains.member.framework.inbound.web.cache.service.MemberCacheValidationService;
import kr.modusplant.domains.member.usecase.response.MemberProfilePrepareResponse;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponseWithImageUrl;
import kr.modusplant.domains.member.usecase.response.MemberRoleResponse;
import kr.modusplant.domains.member.usecase.response.ProposalOrBugReportPrepareResponse;
import kr.modusplant.shared.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_AUTHORIZATION;
import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_CONTENT_TYPE;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_PATH;
import static kr.modusplant.domains.member.common.constant.MemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;
import static kr.modusplant.domains.member.common.constant.ReportConstant.*;
import static kr.modusplant.domains.member.common.util.usecase.record.CommentAbuseReportRecordTestUtils.testCommentAbuseReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCancelPostBookmarkRecordTestUtils.testMemberPostBookmarkCancelRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentLikeRecordTestUtils.testMemberCommentLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentUnlikeRecordTestUtils.testMemberCommentUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberNicknameCheckRecordTestUtils.testMemberNicknameCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostBookmarkRecordTestUtils.testMemberPostBookmarkRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostLikeRecordTestUtils.testMemberPostLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostUnlikeRecordTestUtils.testMemberPostUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileGetRecordTestUtils.testMemberProfileGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileImagePrepareRecord_V2TestUtils.testMemberProfileImagePrepareRecordV2;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecordV1;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecordV2;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberRoleGetRecordTestUtils.testMemberRoleGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberWithdrawalRecordTestUtils.testKakaoMemberWithdrawalRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.PostAbuseReportRecordTestUtils.testPostAbuseReportRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportImagePrepareRecord_V2TestUtils.testProposalOrBugReportImagePrepareRecord_V2;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecord_V1TestUtils.testProposalOrBugReportRecord_v1;
import static kr.modusplant.domains.member.common.util.usecase.record.ProposalOrBugReportRecord_V2TestUtils.testProposalOrBugReportRecord_v2;
import static kr.modusplant.domains.member.common.util.usecase.request.MemberWithdrawRequestTestUtils.testBasicMemberWithdrawRequest;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfilePrepareResponseTestUtils.testMemberProfilePrepareResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithImageUrlV1;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponseWithImageUrlV2;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberRoleResponseTestUtils.testMemberRoleResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.ProposalOrBugReportPrepareResponseTestUtils.testProposalOrBugReportPrepareResponse;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static kr.modusplant.infrastructure.config.jackson.JacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class MemberRestControllerTest implements MemberTestUtils {
    @SuppressWarnings("unused")
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final PasswordEncoder passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private final MemberController memberController = Mockito.mock(MemberController.class);
    private final MemberCacheValidationService memberCacheValidationService = Mockito.mock(MemberCacheValidationService.class);
    private final MemberRestController memberRestController = new MemberRestController(memberController, memberCacheValidationService);

    @Test
    @DisplayName("checkExistedMemberNickname으로 응답 반환")
    void testCheckExistedMemberNickname_givenValidRequest_willReturnResponse() {
        // given
        given(memberController.checkExistedNickname(testMemberNicknameCheckRecord)).willReturn(true);

        // when
        ResponseEntity<DataResponse<Map<String, Boolean>>> isExistedNickname =
                memberRestController.checkExistedMemberNickname(MEMBER_BASIC_USER_NICKNAME);

        // then
        assertThat(isExistedNickname.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(isExistedNickname.getHeaders().getCacheControl()).isEqualTo(CacheControl.noStore().mustRevalidate().cachePrivate().getHeaderValue());
        assertThat(Objects.requireNonNull(isExistedNickname.getBody()).getData().toString()).isEqualTo(Map.of("isNicknameExisted", true).toString());
    }

    @Test
    @DisplayName("캐시를 그대로 사용할 수 있을 때 getMemberProfile로 응답 반환")
    void testGetMemberProfile_givenValidIdAndUsableCache_willReturnResponse() throws IOException {
        // given
        String entityTag = passwordEncoder.encode(UUID.randomUUID() + "-0");
        LocalDateTime now = LocalDateTime.now();
        String ifNoneMatch = String.format("\"%s\"", entityTag);
        String ifModifiedSince = now.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME);

        
        MemberCacheValidationResult cacheValidationResult = new MemberCacheValidationResult(entityTag, now, true);
        given(memberCacheValidationService.getMemberCacheValidationResult(
                ifNoneMatch,
                ifModifiedSince,
                MEMBER_BASIC_USER_UUID
        )).willReturn(cacheValidationResult);

        // when
        ResponseEntity<DataResponse<MemberProfileResponseWithImageUrl>> memberResponseEntity = memberRestController.getMemberProfile(ifNoneMatch, ifModifiedSince, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate().getHeaderValue());
        assertThat(memberResponseEntity.getHeaders().getETag()).isEqualTo(String.format("W/\"%s\"", entityTag));
        assertThat(memberResponseEntity.getHeaders().getLastModified()).isEqualTo(now.atZone(ZoneId.of("Asia/Seoul")).toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli());
        verify(memberController, never()).getProfile(testMemberProfileGetRecord);
    }

    @Test
    @DisplayName("캐시를 그대로 사용할 수 없을 때 getMemberProfile로 응답 반환")
    void testGetMemberProfile_givenValidIdAndNotUsableCache_willReturnResponse() throws IOException {
        // given
        String entityTag = passwordEncoder.encode(UUID.randomUUID() + "-0");
        LocalDateTime now = LocalDateTime.now();
        String ifNoneMatch = String.format("\"%s\"", entityTag);
        String ifModifiedSince = now.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME);

        MemberCacheValidationResult cacheValidationResult = new MemberCacheValidationResult(entityTag, now, false);
        given(memberCacheValidationService.getMemberCacheValidationResult(
                ifNoneMatch,
                ifModifiedSince,
                MEMBER_BASIC_USER_UUID
        )).willReturn(cacheValidationResult);
        given(memberController.getProfile(testMemberProfileGetRecord)).willReturn(testMemberProfileResponseWithImageUrlV1);

        // when
        ResponseEntity<DataResponse<MemberProfileResponseWithImageUrl>> memberResponseEntity = memberRestController.getMemberProfile(ifNoneMatch, ifModifiedSince, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate().getHeaderValue());
        assertThat(memberResponseEntity.getHeaders().getETag()).isEqualTo(String.format("W/\"%s\"", entityTag));
        assertThat(memberResponseEntity.getHeaders().getLastModified()).isEqualTo(now.atZone(ZoneId.of("Asia/Seoul")).toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli());
        assertThat(Objects.requireNonNull(memberResponseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testMemberProfileResponseWithImageUrlV1).toString());
        verify(memberController, only()).getProfile(testMemberProfileGetRecord);
    }

    @Test
    @DisplayName("getMemberRole로 응답 반환")
    void testGetMemberRole_givenValidRequest_willReturnResponse() {
        // given
        given(memberController.getRole(testMemberRoleGetRecord)).willReturn(testMemberRoleResponse);

        // when
        ResponseEntity<DataResponse<MemberRoleResponse>> memberResponseEntity = memberRestController.getMemberRole(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.noStore().mustRevalidate().cachePrivate().getHeaderValue());
        assertThat(String.valueOf(memberResponseEntity.getBody())).isEqualTo(String.valueOf(DataResponse.ok(testMemberRoleResponse)));
        verify(memberController, only()).getRole(testMemberRoleGetRecord);
    }

    @Test
    @DisplayName("overrideMemberProfile V1로 응답 반환")
    void testOverrideMemberProfileV1_givenValidParameters_willReturnResponse() throws IOException {
        // given
        given(memberController.overrideProfile(testMemberProfileOverrideRecordV1)).willReturn(testMemberProfileResponseWithImageUrlV1);

        // when
        ResponseEntity<DataResponse<MemberProfileResponseWithImageUrl>> memberResponseEntity = memberRestController.overrideMemberProfile_v1(MEMBER_PROFILE_BASIC_USER_IMAGE, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.noStore().mustRevalidate().cachePrivate().getHeaderValue());
        assertThat(Objects.requireNonNull(memberResponseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testMemberProfileResponseWithImageUrlV1).toString());
    }

    @Test
    @DisplayName("prepareMemberProfileImage V2로 응답 반환")
    void testPrepareMemberProfileImageV2_givenValidParameters_willReturnResponse() throws IOException {
        // given
        given(memberController.prepareMemberProfileImage(testMemberProfileImagePrepareRecordV2)).willReturn(testMemberProfilePrepareResponse);

        // when
        ResponseEntity<DataResponse<MemberProfilePrepareResponse>> memberResponseEntity = memberRestController.prepareMemberProfileImage_v2(MEMBER_PROFILE_BASIC_USER_IMAGE_FILE_NAME, MEMBER_PROFILE_BASIC_USER_IMAGE_CONTENT_TYPE, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(memberResponseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testMemberProfilePrepareResponse).toString());
    }

    @Test
    @DisplayName("overrideMemberProfile V2로 응답 반환")
    void testOverrideMemberProfileV2_givenValidParameters_willReturnResponse() throws IOException {
        // given
        given(memberController.overrideProfile(testMemberProfileOverrideRecordV2)).willReturn(testMemberProfileResponseWithImageUrlV2);

        // when
        ResponseEntity<DataResponse<MemberProfileResponseWithImageUrl>> memberResponseEntity = memberRestController.overrideMemberProfile_v2(MEMBER_PROFILE_BASIC_USER_IMAGE_PATH, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.noStore().mustRevalidate().cachePrivate().getHeaderValue());
        assertThat(Objects.requireNonNull(memberResponseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testMemberProfileResponseWithImageUrlV2).toString());
    }

    @Test
    @DisplayName("likeCommunicationPost로 응답 반환")
    void testLikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).likePost(testMemberPostLikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationPost로 응답 반환")
    void testUnlikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).unlikePost(testMemberPostUnlikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("bookmarkCommunicationPost로 응답 반환")
    void testBookmarkCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).bookmarkPost(testMemberPostBookmarkRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.bookmarkCommunicationPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("cancelCommunicationPostBookmark로 응답 반환")
    void testCancelCommunicationPostBookmark_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.cancelCommunicationPostBookmark(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("likeCommunicationComment로 응답 반환")
    void testLikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).likeComment(testMemberCommentLikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationComment(TEST_POST_ULID, TEST_COMMENT_PATH, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationComment로 응답 반환")
    void testUnlikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).unlikeComment(testMemberCommentUnlikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationComment(TEST_POST_ULID, TEST_COMMENT_PATH, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("reportProposalOrBug V1으로 응답 반환")
    void testReportProposalOrBug_v1_givenValidRequest_willReturnResponse() throws IOException {
        // given
        willDoNothing().given(memberController).reportProposalOrBug(testProposalOrBugReportRecord_v1);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.reportProposalOrBug_v1(TEST_REPORT_TITLE, TEST_REPORT_CONTENT, TEST_REPORT_IMAGES, TEST_REPORT_IMAGE_NUMBER_3, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("reportProposalOrBug V2로 응답 반환")
    void testReportProposalOrBugV2_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).reportProposalOrBug(testProposalOrBugReportRecord_v2);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity =
                memberRestController.reportProposalOrBug_v2(TEST_REPORT_TITLE, TEST_REPORT_CONTENT, TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATHS, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("prepareProposalOrBugReportImage V2로 응답 반환")
    void testPrepareProposalOrBugReportImageV2_givenValidParameters_willReturnResponse() {
        // given
        given(memberController.prepareProposalOrBugReportImage(testProposalOrBugReportImagePrepareRecord_V2)).willReturn(testProposalOrBugReportPrepareResponse);

        // when
        ResponseEntity<DataResponse<ProposalOrBugReportPrepareResponse>> memberResponseEntity =
                memberRestController.prepareProposalOrBugReportImage_v2(TEST_REPORT_IMAGE_FILE_NAMES, TEST_REPORT_IMAGE_CONTENT_TYPES, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(memberResponseEntity.getBody()).toString()).isEqualTo(DataResponse.ok(testProposalOrBugReportPrepareResponse).toString());
    }

    @Test
    @DisplayName("게시글 ID를 포함하지 않는 요청으로 reportPostAbuse로 응답 반환")
    void testReportPostAbuse_givenValidRequestWithoutPostId_willReturnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.reportPostAbuse(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.of(MemberErrorCode.NOT_FOUND_ACTIVITY_SUBJECT_POST_ID).toString());
    }

    @Test
    @DisplayName("게시글 ID를 포함하는 요청으로 reportPostAbuse로 응답 반환")
    void testReportPostAbuse_givenValidRequestWithPostId_willReturnResponse() {
        // given
        willDoNothing().given(memberController).reportPostAbuse(testPostAbuseReportRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.reportPostAbuse(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("댓글 ID를 포함하지 않는 요청으로 reportCommentAbuse로 응답 반환")
    void testReportCommentAbuse_givenValidRequestWithoutCommentId_willReturnResponse() {
        // given & when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.reportCommentAbuse(TEST_COMMENT_PATH, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.of(MemberErrorCode.NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID).toString());
    }

    @Test
    @DisplayName("댓글 ID를 포함하는 요청으로 reportCommentAbuse로 응답 반환")
    void testReportCommentAbuse_givenValidRequestWithCommentId_willReturnResponse() {
        // given
        willDoNothing().given(memberController).reportCommentAbuse(testCommentAbuseReportRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.reportCommentAbuse(TEST_POST_ULID, TEST_COMMENT_PATH, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("withdrawMember로 응답 반환")
    void testWithdrawMember_givenValidParameters_willReturnResponse() {
        // given
        willDoNothing().given(memberController).withdraw(testKakaoMemberWithdrawalRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.withdrawMember(testBasicMemberWithdrawRequest, MEMBER_AUTH_BASIC_USER_AUTHORIZATION);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).toString()).isEqualTo(DataResponse.ok().toString());
    }
}