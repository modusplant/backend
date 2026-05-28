package kr.modusplant.domains.comment.framework.inbound.web.cache.model;

import java.time.LocalDateTime;

public record CommentCacheData(
        String entityTag,
        LocalDateTime lastModifiedAt,
        boolean isCacheable
) {
}
