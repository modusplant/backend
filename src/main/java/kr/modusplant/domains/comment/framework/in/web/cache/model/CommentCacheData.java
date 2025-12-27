package kr.modusplant.domains.comment.framework.in.web.cache.model;

import java.time.LocalDateTime;

public record CommentCacheData(
        String entityTag,
        LocalDateTime lastModifiedAt,
        boolean result
) {
}
