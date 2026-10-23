package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
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
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        } else if (StringUtils.isBlank(path)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "path");
        }
        return new CommentAbuseReportDismissEvent(postUlid, path);
    }
}
