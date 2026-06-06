package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentAbuseReportEvent {
    private final String postUlid;
    private final String path;
    private final LocalDateTime createdAt;

    public static CommentAbuseReportEvent create(String postUlid, String path, LocalDateTime createdAt) {
        if (StringUtils.isBlank(postUlid)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "postUlid");
        } else if (StringUtils.isBlank(path)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "path");
        } else if (createdAt == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "createdAt");
        }
        return new CommentAbuseReportEvent(postUlid, path, createdAt);
    }
}
