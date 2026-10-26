package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
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
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        } else if (StringUtils.isBlank(path)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "path");
        } else if (createdAt == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "createdAt");
        }
        return new CommentAbuseReportEvent(postUlid, path, createdAt);
    }
}
