package kr.modusplant.domains.comment.framework.in.web.cache;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.port.repository.CommentReadRepository;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;

@Service
@Transactional
@Slf4j
public class CommentCacheService {

    private final CommPostJpaRepository postJpaRepository;
    private final CommentReadRepository commentReadRepository;
    private final SiteMemberJpaRepository memberJpaRepository;

    private final PasswordEncoder passwordEncoder;

    public CommentCacheService(CommPostJpaRepository postJpaRepository,
                               CommentReadRepository commentReadRepository,
                               SiteMemberJpaRepository memberJpaRepository,
                               @Qualifier("pbkdf2PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.postJpaRepository = postJpaRepository;
        this.commentReadRepository = commentReadRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CommentCacheData getCacheData(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull PostId postUlid
    ) {
        CommPostEntity postEntity = postJpaRepository.findByUlid(postUlid.getId())
                .orElseThrow(() -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post"));

        // 댓글의 최신 변경 시각 조회
        LocalDateTime latestCommentUpdatedAt = commentReadRepository.findLatestUpdatedAtByPost(postUlid)
                .orElse(LocalDateTime.MIN);

        // post와 댓글 변경 시각 중 더 최신 것을 기준으로 함
        LocalDateTime lastModifiedAt = Stream.of(
                        postEntity.getUpdatedAtAsTruncatedToSeconds(),
                        latestCommentUpdatedAt)
                .max(Comparator.naturalOrder())
                .get();

        // ETag 소스도 두 값을 합산
        String ETagSource = postEntity.getETagSource() + latestCommentUpdatedAt;

        return determineCacheData(ifNoneMatch, ifModifiedSince, ETagSource, lastModifiedAt);
    }

    public CommentCacheData getCacheData(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull MemberId memberId
    ) {
        SiteMemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
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
