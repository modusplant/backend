package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReportEvent {
    private final UUID memberId;
    private final String title;
    private final String content;
    private final String imagePath;

    public static ProposalOrBugReportEvent create(UUID memberId, String title, String content, String imagePath) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (title.isEmpty()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "title");
        } else if (content.isEmpty()) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "content");
        } else {
            return new ProposalOrBugReportEvent(memberId, title, content, imagePath);
        }
    }
}
