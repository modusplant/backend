package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.exception.IncorrectMemberIdException;
import kr.modusplant.domains.member.framework.in.web.cache.record.MemberCacheValidationResult;
import kr.modusplant.domains.member.framework.in.web.cache.service.MemberCacheValidationService;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
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
import java.util.UUID;

import static kr.modusplant.domains.member.common.util.usecase.record.MemberCancelPostBookmarkRecordTestUtils.testMemberPostBookmarkCancelRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentLikeRecordTestUtils.testMemberCommentLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentUnlikeRecordTestUtils.testMemberCommentUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberNicknameCheckRecordTestUtils.testMemberNicknameCheckRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostBookmarkRecordTestUtils.testMemberPostBookmarkRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostLikeRecordTestUtils.testMemberPostLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostUnlikeRecordTestUtils.testMemberPostUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileGetRecordTestUtils.testMemberProfileGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecord;
import static kr.modusplant.domains.member.common.util.usecase.request.MemberRegisterRequestTestUtils.testMemberRegisterRequest;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberResponseTestUtils.testMemberResponse;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INCORRECT_MEMBER_ID;
import static kr.modusplant.infrastructure.config.jackson.TestJacksonConfig.objectMapper;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class MemberRestControllerTest implements MemberTestUtils {
    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final PasswordEncoder passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private final MemberController memberController = Mockito.mock(MemberController.class);
    private final JwtTokenProvider jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final MemberCacheValidationService memberCacheValidationService = Mockito.mock(MemberCacheValidationService.class);
    private final MemberRestController memberRestController = new MemberRestController(memberController, jwtTokenProvider, memberCacheValidationService);

    private final String auth = "Bearer a.b.c";
    private final String accessToken = "a.b.c";

    @Test
    @DisplayName("register로 응답 반환")
    void testRegister_givenValidRequest_willReturnResponse() {
        // given
        given(memberController.register(testMemberRegisterRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<DataResponse<MemberResponse>> memberResponseEntity = memberRestController.registerMember(testMemberRegisterRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberResponse).toString());
    }

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
        assertThat(isExistedNickname.getBody().getData().toString()).isEqualTo(Map.of("isNicknameExisted", true).toString());
    }

    @Test
    @DisplayName("캐시를 그대로 사용할 수 있을 때 getMemberProfile로 응답 반환")
    void testGetMemberProfile_givenValidIdAndUsableCache_willReturnResponse() throws IOException {
        // given
        String entityTag = passwordEncoder.encode(UUID.randomUUID() + "-0");
        LocalDateTime now = LocalDateTime.now();
        String ifNoneMatch = String.format("\"%s\"", entityTag);
        String ifModifiedSince = now.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME);

        
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        MemberCacheValidationResult cacheValidationResult = new MemberCacheValidationResult(entityTag, now, true);
        given(memberCacheValidationService.isCacheable(
                ifNoneMatch,
                ifModifiedSince,
                MEMBER_BASIC_USER_UUID
        )).willReturn(cacheValidationResult);

        // when
        ResponseEntity<DataResponse<MemberProfileResponse>> memberResponseEntity = memberRestController.getMemberProfile(MEMBER_BASIC_USER_UUID, auth, ifNoneMatch, ifModifiedSince);

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

        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        MemberCacheValidationResult cacheValidationResult = new MemberCacheValidationResult(entityTag, now, false);
        given(memberCacheValidationService.isCacheable(
                ifNoneMatch,
                ifModifiedSince,
                MEMBER_BASIC_USER_UUID
        )).willReturn(cacheValidationResult);
        given(memberController.getProfile(testMemberProfileGetRecord)).willReturn(testMemberProfileResponse);

        // when
        ResponseEntity<DataResponse<MemberProfileResponse>> memberResponseEntity = memberRestController.getMemberProfile(MEMBER_BASIC_USER_UUID, auth, ifNoneMatch, ifModifiedSince);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.maxAge(Duration.ofDays(1)).cachePrivate().getHeaderValue());
        assertThat(memberResponseEntity.getHeaders().getETag()).isEqualTo(String.format("W/\"%s\"", entityTag));
        assertThat(memberResponseEntity.getHeaders().getLastModified()).isEqualTo(now.atZone(ZoneId.of("Asia/Seoul")).toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli());
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberProfileResponse).toString());
        verify(memberController, only()).getProfile(testMemberProfileGetRecord);
    }

    @Test
    @DisplayName("overrideMemberProfile로 응답 반환")
    void testOverrideMemberProfile_givenValidParameters_willReturnResponse() throws IOException {
        // given
        
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        given(memberController.overrideProfile(testMemberProfileOverrideRecord)).willReturn(testMemberProfileResponse);

        // when
        ResponseEntity<DataResponse<MemberProfileResponse>> memberResponseEntity = memberRestController.overrideMemberProfile(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_IMAGE, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME, auth);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getHeaders().getCacheControl()).isEqualTo(CacheControl.noStore().mustRevalidate().cachePrivate().getHeaderValue());
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberProfileResponse).toString());
    }

    @Test
    @DisplayName("likeCommunicationPost로 응답 반환")
    void testLikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberController).likePost(testMemberPostLikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationPost(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, auth);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationPost로 응답 반환")
    void testUnlikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberController).unlikePost(testMemberPostUnlikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationPost(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, auth);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("bookmarkCommunicationPost로 응답 반환")
    void testBookmarkCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberController).bookmarkPost(testMemberPostBookmarkRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.bookmarkCommunicationPost(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, auth);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("cancelCommunicationPostBookmark로 응답 반환")
    void testCancelCommunicationPostBookmark_givenValidRequest_willReturnResponse() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberController).cancelPostBookmark(testMemberPostBookmarkCancelRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.cancelCommunicationPostBookmark(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, auth);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("likeCommunicationComment로 응답 반환")
    void testLikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberController).likeComment(testMemberCommentLikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationComment(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH, auth);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationComment로 응답 반환")
    void testUnlikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(MEMBER_BASIC_USER_UUID);
        willDoNothing().given(memberController).unlikeComment(testMemberCommentUnlikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationComment(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH, auth);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("자신과 다른 UUID가 저장된 Authorization 사용으로 응답 반환 실패")
    void testValidateTokenAndAccessToId_givenTokenWithoutMyUuid_willThrowExceptionFromToken() {
        // given
        given(jwtTokenProvider.getMemberUuidFromToken(accessToken)).willReturn(UUID.randomUUID());

        // when
        IncorrectMemberIdException incorrectMemberIdException = assertThrows(IncorrectMemberIdException.class, () -> memberRestController.getMemberProfile(MEMBER_BASIC_USER_UUID, auth, null, null));

        // then
        assertThat(incorrectMemberIdException.getMessage()).isEqualTo(INCORRECT_MEMBER_ID.getMessage());
    }
}