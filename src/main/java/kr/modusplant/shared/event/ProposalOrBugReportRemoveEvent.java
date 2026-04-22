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
public class ProposalOrBugReportRemoveEvent {
    private final UUID memberId;
    private final String reportId;

    public static ProposalOrBugReportRemoveEvent create(UUID memberId, String reportId) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (StringUtils.isEmpty(reportId)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "reportId");
        } else {
            return new ProposalOrBugReportRemoveEvent(memberId, reportId);
        }
    }
}
