package kr.modusplant.domains.comment.framework.in.web.cache;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;

@Service
@Transactional
@Slf4j
public class CommentCacheService {

    private final CommPostJpaRepository postJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public CommentCacheService(CommPostJpaRepository postJpaRepository,
                               @Qualifier("pbkdf2PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.postJpaRepository = postJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CommentCacheData isCacheable(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull PostId postUlid
    ) {
        CommPostEntity postEntity = postJpaRepository.findByUlid(postUlid.getId())
                .orElseThrow( () -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, "post"));
//        Optional<SiteMemberProfileEntity> optionalMemberProfile = memberProfileJpaRepository.findByUuid(id);
//        if (optionalMemberProfile.isEmpty()) {
//            throw new EntityNotFoundException(MEMBER_PROFILE_NOT_FOUND, "memberProfile");
//        }
//        SiteMemberProfileEntity memberProfileEntity = optionalMemberProfile.orElseThrow();

        String ETagSource = postEntity.getETagSource();
        LocalDateTime lastModifiedAt = postEntity.getUpdatedAtAsTruncatedToSeconds();
//        String entityTagSource = memberProfileEntity.getETagSource();
//        LocalDateTime lastModifiedAt = memberProfileEntity.getLastModifiedAtAsTruncatedToSeconds();


//        Map<String, Object> returnedMap = new HashMap<>() {{
//            put(ENTITY_TAG, passwordEncoder.encode(entityTagSource));
//            put(LAST_MODIFIED_DATE_TIME, lastModifiedAt);
//        }};
        if (ifNoneMatch == null) {
            return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt, false);
        }
//        if (ifNoneMatch == null) {                  // ETag를 통한 검증 강제
//            returnedMap.put(RESULT, false);
//            return returnedMap;
//        }


        Optional<String> ETag = parseIfNoneMatch(ifNoneMatch).stream()
                .filter(element -> passwordEncoder.matches(ETagSource, element))
                .findFirst();
//        Optional<String> foundEntityTag = parseIfNoneMatch(ifNoneMatch).stream()
//                .filter(element -> passwordEncoder.matches(entityTagSource, element))
//                .findFirst();

        if (ETag.isEmpty()) {
            return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt, false);
        }
        if (ifModifiedSince == null) {
            return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt, true);
        }

        return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt,
                !lastModifiedAt.isAfter(parseIfModifiedSince(ifModifiedSince)));
//        if (foundEntityTag.isEmpty()) {
//            returnedMap.put(RESULT, false);
//        } else {
//            if (ifModifiedSince == null) {
//                returnedMap.put(RESULT, true);
//            } else {
//                returnedMap.put(RESULT,
//                        !lastModifiedAt.isAfter(parseIfModifiedSince(ifModifiedSince)));
//            }
//        }
//        return returnedMap;
    }

}
