package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentAbuseReportDismissEvent {
    private final String postUlid;
    private final String path;

    public static CommentAbuseReportDismissEvent create(String postUlid, String path) {
        if (StringUtils.isBlank(postUlid)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "postUlid");
        } else if (StringUtils.isBlank(path)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_COMMENT, "path");
        }
        return new CommentAbuseReportDismissEvent(postUlid, path);
    }
}
