package kr.modusplant.domains.member.framework.inbound.web.cache.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.member.framework.inbound.web.cache.record.MemberCacheValidationResult;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberProfileJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MemberCacheValidationService {
    private final MemberProfileJpaRepository memberProfileJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberCacheValidationService(MemberProfileJpaRepository memberProfileJpaRepository,
                                        @Qualifier("pbkdf2PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.memberProfileJpaRepository = memberProfileJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MemberCacheValidationResult getMemberCacheValidationResult(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull UUID id) {
        Optional<MemberProfileEntity> optionalMemberProfile = memberProfileJpaRepository.findByUuid(id);
        if (optionalMemberProfile.isEmpty()) {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
        MemberProfileEntity memberProfileEntity = optionalMemberProfile.orElseThrow();
        String entityTagSource = memberProfileEntity.getETagSource();
        LocalDateTime lastModifiedAt = memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds();
        if (ifNoneMatch == null) {                  // ETag를 통한 검증 강제
            return new MemberCacheValidationResult(
                    passwordEncoder.encode(entityTagSource),
                    lastModifiedAt,
                    false
            );
        }
        Optional<String> foundEntityTag = parseIfNoneMatch(ifNoneMatch).stream()
                .filter(element -> passwordEncoder.matches(entityTagSource, element))
                .findFirst();
        if (foundEntityTag.isEmpty()) {
            return new MemberCacheValidationResult(
                    passwordEncoder.encode(entityTagSource),
                    lastModifiedAt,
                    false
            );
        } else {
            if (ifModifiedSince == null) {
                return new MemberCacheValidationResult(
                        passwordEncoder.encode(entityTagSource),
                        lastModifiedAt,
                        true
                );
            } else {
                return new MemberCacheValidationResult(
                        passwordEncoder.encode(entityTagSource),
                        lastModifiedAt,
                        !lastModifiedAt.isAfter(parseIfModifiedSince(ifModifiedSince))
                );
            }
        }
    }
}
