package kr.modusplant.domains.comment.framework.in.web.cache;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;

@Service
@Transactional
@Slf4j
public class CommentCacheService {

    private final PostJpaRepository postJpaRepository;
    private final MemberJpaRepository memberJpaRepository;

    private final PasswordEncoder passwordEncoder;

    public CommentCacheService(PostJpaRepository postJpaRepository, MemberJpaRepository memberJpaRepository,
                               @Qualifier("pbkdf2PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.postJpaRepository = postJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CommentCacheData getCacheData(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull PostId postUlid
    ) {
        PostEntity postEntity = postJpaRepository.findByUlid(postUlid.getId())
                .orElseThrow( () -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post"));

        String ETagSource = postEntity.getETagSource();
        LocalDateTime lastModifiedAt = postEntity.getUpdatedAtAsTruncatedToSeconds();

        return determineCacheData(ifNoneMatch, ifModifiedSince, ETagSource, lastModifiedAt);
    }

    public CommentCacheData getCacheData(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull MemberId memberId
    ) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER, "member"));

        String ETagSource = memberEntity.getETagSource();
        LocalDateTime lastModifiedAt = memberEntity.getLastModifiedAtAsTruncatedToSeconds();

        return determineCacheData(ifNoneMatch, ifModifiedSince, ETagSource, lastModifiedAt);
    }

    private CommentCacheData determineCacheData(String ifNoneMatch, String ifModifiedSince,
                                                String ETagSource, LocalDateTime lastModifiedAt) {
        if (ifNoneMatch == null) {
            return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt, false);
        }

        Optional<String> ETag = parseIfNoneMatch(ifNoneMatch).stream()
                .filter(element -> passwordEncoder.matches(ETagSource, element))
                .findFirst();

        if (ETag.isEmpty()) {
            return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt, false);
        }
        if (ifModifiedSince == null) {
            return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt, true);
        }

        return new CommentCacheData(passwordEncoder.encode(ETagSource), lastModifiedAt,
                !lastModifiedAt.isAfter(parseIfModifiedSince(ifModifiedSince)));
    }

}
