package kr.modusplant.infrastructure.cache.service;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.shared.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.exception.enums.ErrorCode.MEMBER_PROFILE_NOT_FOUND;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;

@Service
@Transactional
@Slf4j
public class CacheValidationService {
    private final SiteMemberProfileJpaRepository memberProfileJpaRepository;
    private final PasswordEncoder passwordEncoder;

    private final String ENTITY_TAG = "entityTag";
    private final String LAST_MODIFIED_DATE_TIME = "lastModifiedDateTime";
    private final String RESULT = "result";

    public CacheValidationService(SiteMemberProfileJpaRepository memberProfileJpaRepository,
                                  @Qualifier("pbkdf2PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.memberProfileJpaRepository = memberProfileJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> isCacheUsableForSiteMemberProfile(@Nullable String ifNoneMatch,
                                                 @Nullable String ifModifiedSince,
                                                 @Nonnull UUID id) {
        Optional<SiteMemberProfileEntity> optionalMemberProfile = memberProfileJpaRepository.findByUuid(id);
        if (optionalMemberProfile.isEmpty()) {
            throw new EntityNotFoundException(MEMBER_PROFILE_NOT_FOUND, "memberProfile");
        }
        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfile.orElseThrow();
        Map<String, Object> returnedMap = new HashMap<>(){{
            put(ENTITY_TAG, passwordEncoder.encode(memberProfileEntity.getETagSource()));
            put(LAST_MODIFIED_DATE_TIME, memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds());
        }};
        if (ifNoneMatch == null) {                  // ETag를 통한 검증 강제
            returnedMap.put(RESULT, false);
            return returnedMap;
        }
        String comparedEntityTagSource = memberProfileEntity.getETagSource();
        Optional<String> foundEntityTag = parseIfNoneMatch(ifNoneMatch).stream()
                .filter(element -> passwordEncoder.matches(comparedEntityTagSource, element))
                .findFirst();
        if (foundEntityTag.isEmpty()) {
            returnedMap.put(RESULT, false);
        } else {
            if (ifModifiedSince == null) {
                returnedMap.put(RESULT, true);
            } else {
                returnedMap.put(RESULT,
                        !memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds().isAfter(parseIfModifiedSince(ifModifiedSince)));
            }
        }
        return returnedMap;
    }
}
