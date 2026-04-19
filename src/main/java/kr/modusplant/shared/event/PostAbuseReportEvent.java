package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostAbuseReportEvent {
    private final UUID memberId;
    private final String postUlid;

    public static PostAbuseReportEvent create(UUID memberId, String postUlid) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (postUlid.isEmpty()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST, "postUlid");
        } else {
            return new PostAbuseReportEvent(memberId, postUlid);
        }
    }
}
