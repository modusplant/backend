package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReportEvent {
    private final UUID memberId;
    private final String reportId;
    private final String title;
    private final String content;
    private final String imagePath;

    public static ProposalOrBugReportEvent create(UUID memberId, String reportId, String title, String content, String imagePath) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (StringUtils.isBlank(reportId)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "reportId");
        } else if (StringUtils.isBlank(title)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "title");
        } else if (StringUtils.isBlank(content)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "content");
        } else {
            return new ProposalOrBugReportEvent(memberId, reportId, title, content, imagePath);
        }
    }
}
