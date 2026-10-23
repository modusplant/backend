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
public class PostAbuseReportEvent {
    private final String postUlid;
    private final LocalDateTime createdAt;

    public static PostAbuseReportEvent create(String postUlid, LocalDateTime createdAt) {
        if (StringUtils.isBlank(postUlid)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        } else if (createdAt == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "createdAt");
        }
        return new PostAbuseReportEvent(postUlid, createdAt);
    }
}
