package kr.modusplant.domains.member.framework.in.web.cache.record;

import java.time.LocalDateTime;

public record MemberCacheValidationResult(String entityTag, LocalDateTime lastModifiedDateTime, boolean isCacheable) {
}
