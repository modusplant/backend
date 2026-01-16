package kr.modusplant.infrastructure.cache.service;

import kr.modusplant.domains.member.framework.in.web.cache.record.MemberCacheValidationResult;
import kr.modusplant.domains.member.framework.in.web.cache.service.MemberCacheValidationService;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberProfileEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.shared.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.exception.enums.ErrorCode.MEMBER_PROFILE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
class MemberCacheValidationServiceTest implements SiteMemberEntityTestUtils, SiteMemberProfileEntityTestUtils {
    private final SiteMemberProfileJpaRepository memberProfileJpaRepository = Mockito.mock(SiteMemberProfileJpaRepository.class);
    private final PasswordEncoder passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private final MemberCacheValidationService memberCacheValidationService = new MemberCacheValidationService(memberProfileJpaRepository, passwordEncoder);

    private final UUID id = UUID.randomUUID();

    @Test
    @DisplayName("비어 있는 프로필로 인해 예외 발생")
    void testGetMemberCacheValidationResult_willThrowException() {
        // given
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> memberCacheValidationService.getMemberCacheValidationResult(
                        String.format("\"%s\"", passwordEncoder.encode(UUID.randomUUID() + "-0")),
                        ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                        id));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MEMBER_PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("ifNoneMatch가 null일 때 응답 반환")
    void testGetMemberCacheValidationResult_givenNullIfNoneMatch_willReturnResponse() {
        // given
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = Optional.of(
                createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build());
        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
        ReflectionTestUtils.setField(memberProfileEntity, "lastModifiedAt", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ReflectionTestUtils.setField(memberProfileEntity, "versionNumber", 0L);
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(optionalMemberProfileEntity);

        // when
        MemberCacheValidationResult returnedMap = memberCacheValidationService.getMemberCacheValidationResult(
                null, ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME), id);

        // then
        assertThat(returnedMap.isCacheUsable()).isEqualTo(false);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 없을 때 응답 반환")
    void testGetMemberCacheValidationResult_givenNotMatchedEntityTag_willReturnResponse() {
        // given
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = Optional.of(
                createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build());
        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
        ReflectionTestUtils.setField(memberProfileEntity, "lastModifiedAt", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ReflectionTestUtils.setField(memberProfileEntity, "versionNumber", 0L);
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(optionalMemberProfileEntity);

        // when
        MemberCacheValidationResult returnedMap = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getUuid() + "-99")),
                ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(returnedMap.isCacheUsable()).isEqualTo(false);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 있고 ifModifiedSince가 null일 때 응답 반환")
    void testGetMemberCacheValidationResult_givenMatchedEntityTagAndNullIfModifiedSince_willReturnResponse() {
        // given
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = Optional.of(
                createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build());
        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
        ReflectionTestUtils.setField(memberProfileEntity, "lastModifiedAt", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ReflectionTestUtils.setField(memberProfileEntity, "versionNumber", 0L);
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(optionalMemberProfileEntity);

        // when
        MemberCacheValidationResult returnedMap = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                null,
                id);

        // then
        assertThat(returnedMap.isCacheUsable()).isEqualTo(true);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 있고 ifModifiedSince가 조건을 만족할 때 응답 반환")
    void testGetMemberCacheValidationResult_givenMatchedEntityTagAndRangedIfModifiedSince_willReturnResponse() {
        // given
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = Optional.of(
                createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build());
        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
        ReflectionTestUtils.setField(memberProfileEntity, "lastModifiedAt", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        ReflectionTestUtils.setField(memberProfileEntity, "versionNumber", 0L);
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(optionalMemberProfileEntity);

        // 엔터티의 lastModifiedAt 값이 ifModifiedSince 값과 같을 때
        // when
        MemberCacheValidationResult returnedMapEqual = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                ZonedDateTime.of(memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds(),
                        ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(returnedMapEqual.isCacheUsable()).isEqualTo(true);

        // 엔터티의 lastModifiedAt 값이 ifModifiedSince 값보다 과거일 때
        // when
        MemberCacheValidationResult returnedMapPast = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                ZonedDateTime.of(memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds().plusMinutes(5),
                        ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(returnedMapPast.isCacheUsable()).isEqualTo(true);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 있고 ifModifiedSince가 조건을 만족하지 않을 때 응답 반환")
    void testGetMemberCacheValidationResult_givenMatchedEntityTagAndNotRangedIfModifiedSince_willReturnResponse() {
        // given
        Optional<SiteMemberProfileEntity> optionalMemberProfileEntity = Optional.of(
                createMemberProfileBasicUserEntityBuilder().member(createMemberBasicUserEntityWithUuid()).build());
        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfileEntity.orElseThrow();
        ReflectionTestUtils.setField(memberProfileEntity, "lastModifiedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(memberProfileEntity, "versionNumber", 0L);
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(optionalMemberProfileEntity);

        // when
        MemberCacheValidationResult returnedMapEqual = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                ZonedDateTime.of(memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds().minusMinutes(5),
                        ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(returnedMapEqual.isCacheUsable()).isEqualTo(false);
    }
}