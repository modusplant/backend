package kr.modusplant.domains.comment.framework.inbound.web.cache;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.domain.vo.ParentPostId;
import kr.modusplant.domains.comment.framework.inbound.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfModifiedSince;
import static kr.modusplant.shared.http.utils.ParseHttpHeaderUtils.parseIfNoneMatch;

@Service
@Transactional
@Slf4j
public class CommentCacheService {

    private static final String ETAG_SOURCE_DELIMITER = "|";

    private final PostJpaRepository postJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final CommentQueryRepository commentQueryRepository;

    private final PasswordEncoder passwordEncoder;

    public CommentCacheService(PostJpaRepository postJpaRepository,
                               MemberJpaRepository memberJpaRepository,
                               CommentQueryRepository commentQueryRepository,
                               @Qualifier("pbkdf2PasswordEncoder") PasswordEncoder passwordEncoder) {
        this.postJpaRepository = postJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.commentQueryRepository = commentQueryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CommentCacheData getCacheData(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull ParentPostId postUlid,
            @Nullable String lastPath,
            @Nullable Integer size
    ) {
        PostEntity postEntity = postJpaRepository.findByUlid(postUlid.getValue())
                .orElseThrow( () -> new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post"));

        // 댓글의 최신 변경 시각 조회
        LocalDateTime latestCommentUpdatedAt = commentQueryRepository.findLatestUpdatedAtByPost(postUlid)
                .orElse(LocalDateTime.MIN);

        // post와 댓글 변경 시각 중 더 최신 것을 기준으로 함
        LocalDateTime lastModifiedAt = Stream.of(
                        postEntity.getUpdatedAtAsTruncatedToSeconds(),
                        latestCommentUpdatedAt.truncatedTo(ChronoUnit.SECONDS))
                .max(Comparator.naturalOrder())
                .get();

        // ETag 소스도 두 값을 합산하고, 페이지 단위로 구분되도록 페이지네이션 값을 포함
        String ETagSource = postEntity.getETagSource() + latestCommentUpdatedAt
                + ETAG_SOURCE_DELIMITER + Objects.requireNonNullElse(lastPath, "null")
                + ETAG_SOURCE_DELIMITER + Objects.requireNonNullElse(size, "null");

        return determineCacheData(ifNoneMatch, ifModifiedSince, ETagSource, lastModifiedAt);
    }

    public CommentCacheData getCacheData(
            @Nullable String ifNoneMatch,
            @Nullable String ifModifiedSince,
            @Nonnull MemberId memberId,
            int page,
            int size
    ) {
        MemberEntity memberEntity = memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(MemberErrorCode.NOT_FOUND_MEMBER, "member"));

        // 작성자 댓글의 최신 변경 시각 조회
        LocalDateTime latestCommentUpdatedAt = commentQueryRepository.findLatestUpdatedAtByAuthor(
                        CommentAuthorId.fromUuid(memberId.getValue()))
                .orElse(LocalDateTime.MIN);

        // member와 댓글 변경 시각 중 더 최신 것을 기준으로 함
        LocalDateTime lastModifiedAt = Stream.of(
                        memberEntity.getLastModifiedAtAsTruncatedToSeconds(),
                        latestCommentUpdatedAt.truncatedTo(ChronoUnit.SECONDS))
                .max(Comparator.naturalOrder())
                .get();

        // ETag 소스도 두 값을 합산하고, 페이지 단위로 구분되도록 페이지네이션 값을 포함
        String ETagSource = memberEntity.getETagSource() + latestCommentUpdatedAt
                + ETAG_SOURCE_DELIMITER + page
                + ETAG_SOURCE_DELIMITER + size;

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
