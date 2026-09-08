package kr.modusplant.infrastructure.cache.service;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberProfileEntityTestUtils;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.framework.inbound.web.cache.record.MemberCacheValidationResult;
import kr.modusplant.domains.member.framework.inbound.web.cache.service.MemberCacheValidationService;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberProfileJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
class MemberCacheValidationServiceTest implements MemberEntityTestUtils, MemberProfileEntityTestUtils {
    private final MemberProfileJpaRepository memberProfileJpaRepository = Mockito.mock(MemberProfileJpaRepository.class);
    private final PasswordEncoder passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    private final MemberCacheValidationService memberCacheValidationService = new MemberCacheValidationService(memberProfileJpaRepository, passwordEncoder);

    private final UUID id = UUID.randomUUID();

    private MemberProfileEntity givenMemberProfileEntity(LocalDateTime lastModifiedAt) {
        MemberProfileEntity memberProfileEntity = createMemberProfileBasicUserEntityBuilder()
                .member(createMemberBasicUserEntityWithUuid()).build();
        ReflectionTestUtils.setField(memberProfileEntity, "lastModifiedAt", lastModifiedAt);
        ReflectionTestUtils.setField(memberProfileEntity, "versionNumber", 0L);
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.of(memberProfileEntity));
        return memberProfileEntity;
    }

    @Test
    @DisplayName("비어 있는 프로필로 예외 반환")
    void testGetMemberCacheValidationResult_givenEmptyProfile_willThrowException() {
        // given
        given(memberProfileJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // when
        NotFoundEntityException exception = assertThrows(
                NotFoundEntityException.class,
                () -> memberCacheValidationService.getMemberCacheValidationResult(
                        String.format("\"%s\"", passwordEncoder.encode(UUID.randomUUID() + "-0")),
                        ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                        id));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(MemberErrorCode.NOT_FOUND_MEMBER_PROFILE);
    }

    @Test
    @DisplayName("ifNoneMatch가 null일 때 MemberCacheValidationResult 반환")
    void testGetMemberCacheValidationResult_givenNullIfNoneMatch_willReturnMemberCacheValidationResult() {
        // given
        givenMemberProfileEntity(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // when
        MemberCacheValidationResult result = memberCacheValidationService.getMemberCacheValidationResult(
                null, ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME), id);

        // then
        assertThat(result.isCacheUsable()).isEqualTo(false);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 없을 때 MemberCacheValidationResult 반환")
    void testGetMemberCacheValidationResult_givenNotMatchedEntityTag_willReturnMemberCacheValidationResult() {
        // given
        MemberProfileEntity memberProfileEntity = givenMemberProfileEntity(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // when
        MemberCacheValidationResult result = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getUuid() + "-99")),
                ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(result.isCacheUsable()).isEqualTo(false);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 있고 ifModifiedSince가 null일 때 MemberCacheValidationResult 반환")
    void testGetMemberCacheValidationResult_givenMatchedEntityTagAndNullIfModifiedSince_willReturnMemberCacheValidationResult() {
        // given
        MemberProfileEntity memberProfileEntity = givenMemberProfileEntity(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // when
        MemberCacheValidationResult result = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                null,
                id);

        // then
        assertThat(result.isCacheUsable()).isEqualTo(true);
    }

    @Test
    @DisplayName("ifModifiedSince가 lastModifiedAt과 같을 때 MemberCacheValidationResult 반환")
    void testGetMemberCacheValidationResult_givenModifiedSinceEqualToLastModified_willReturnMemberCacheValidationResult() {
        // given
        MemberProfileEntity memberProfileEntity = givenMemberProfileEntity(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // when
        MemberCacheValidationResult result = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                ZonedDateTime.of(memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds(),
                        ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(result.isCacheUsable()).isEqualTo(true);
    }

    @Test
    @DisplayName("ifModifiedSince가 lastModifiedAt보다 이후일 때 MemberCacheValidationResult 반환")
    void testGetMemberCacheValidationResult_givenModifiedSinceAfterLastModified_willReturnMemberCacheValidationResult() {
        // given
        MemberProfileEntity memberProfileEntity = givenMemberProfileEntity(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        // when
        MemberCacheValidationResult result = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                ZonedDateTime.of(memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds().plusMinutes(5),
                        ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(result.isCacheUsable()).isEqualTo(true);
    }

    @Test
    @DisplayName("매칭되는 엔터티 태그가 있고 ifModifiedSince가 조건을 만족하지 않을 때 MemberCacheValidationResult 반환")
    void testGetMemberCacheValidationResult_givenMatchedEntityTagAndNotRangedIfModifiedSince_willReturnMemberCacheValidationResult() {
        // given
        MemberProfileEntity memberProfileEntity = givenMemberProfileEntity(LocalDateTime.now());

        // when
        MemberCacheValidationResult result = memberCacheValidationService.getMemberCacheValidationResult(
                String.format("\"%s\"", passwordEncoder.encode(memberProfileEntity.getETagSource())),
                ZonedDateTime.of(memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds().minusMinutes(5),
                        ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.RFC_1123_DATE_TIME),
                id);

        // then
        assertThat(result.isCacheUsable()).isEqualTo(false);
    }
}
