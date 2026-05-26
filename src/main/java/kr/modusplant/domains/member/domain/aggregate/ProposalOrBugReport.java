package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.domain.entity.ProposalOrBugReportImage;
import kr.modusplant.domains.member.domain.vo.ReportContent;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportTitle;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReport {
    private final ReportId reportId;
    private ReportTitle reportTitle;
    private ReportContent reportContent;
    private List<ProposalOrBugReportImage> proposalOrBugReportImages;

    public static ProposalOrBugReport create(ReportId id,
                                             ReportTitle title,
                                             ReportContent content,
                                             List<ProposalOrBugReportImage> images) {
        if (id == null) {
            throw new EmptyValueException(EMPTY_REPORT_ID, "reportId");
        } else if (title == null) {
            throw new EmptyValueException(EMPTY_REPORT_TITLE, "reportTitle");
        } else if (content == null) {
            throw new EmptyValueException(EMPTY_REPORT_CONTENT, "reportContent");
        } else if (images == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE, "reportImages");
        } else if (images.size() > 3) {
            throw new InvalidValueException(PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE, "reportImages");
        }
        return new ProposalOrBugReport(id, title, content, images);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProposalOrBugReport proposalOrBugReport)) return false;

        return new EqualsBuilder().append(getReportId(), proposalOrBugReport.getReportId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getReportId()).toHashCode();
    }
}
