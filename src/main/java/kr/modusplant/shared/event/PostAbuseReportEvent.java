package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostAbuseReportEvent {
    private final String postUlid;
    private final LocalDateTime createdAt;

    public static PostAbuseReportEvent create(String postUlid, LocalDateTime createdAt) {
        if (StringUtils.isBlank(postUlid)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postUlid");
        } else if (createdAt == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postUlid");
        }
        return new PostAbuseReportEvent(postUlid, createdAt);
    }
}
