package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostAbuseReportDismissEvent {
    private final String postUlid;

    public static PostAbuseReportDismissEvent create(String postUlid) {
        if (StringUtils.isBlank(postUlid)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postUlid");
        }
        return new PostAbuseReportDismissEvent(postUlid);
    }
}
