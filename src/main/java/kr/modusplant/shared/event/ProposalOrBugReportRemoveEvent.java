package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReportRemoveEvent {
    private final String reportId;

    public static ProposalOrBugReportRemoveEvent create(String reportId) {
        if (StringUtils.isEmpty(reportId)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "reportId");
        } else {
            return new ProposalOrBugReportRemoveEvent(reportId);
        }
    }
}
