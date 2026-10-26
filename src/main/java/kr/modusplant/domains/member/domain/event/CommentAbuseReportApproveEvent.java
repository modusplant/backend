package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentAbuseReportApproveEvent {
    private final String postUlid;
    private final String path;
    private final UUID memberId;

    public static CommentAbuseReportApproveEvent create(String postUlid, String path, UUID memberId) {
        if (StringUtils.isBlank(postUlid)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        } else if (StringUtils.isBlank(path)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "path");
        } else if (memberId == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "memberId");
        }
        return new CommentAbuseReportApproveEvent(postUlid, path, memberId);
    }
}
