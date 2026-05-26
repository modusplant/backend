package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.domain.vo.ProposalOrBugReportImageFileName;
import kr.modusplant.domains.member.domain.vo.ReportImageBytes;
import kr.modusplant.domains.member.domain.vo.ReportImagePath;
import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProposalOrBugReportImage {
    private final ReportImagePath reportImagePath;
    private ProposalOrBugReportImageFileName proposalOrBugReportImageFileName;
    private ReportImageBytes reportImageBytes;

    public static ProposalOrBugReportImage create(ReportImagePath reportImagePath,
                                                  ProposalOrBugReportImageFileName proposalOrBugReportImageFileName,
                                                  ReportImageBytes reportImageBytes) {
        if (reportImagePath == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_PATH, "reportImagePath");
        } else if (proposalOrBugReportImageFileName == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_FILE_NAME, "reportImageFileName");
        } else if (reportImageBytes == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_BYTES, "reportImageBytes");
        }
        return new ProposalOrBugReportImage(reportImagePath, proposalOrBugReportImageFileName, reportImageBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProposalOrBugReportImage proposalOrBugReportImage)) return false;

        return new EqualsBuilder().append(getReportImagePath(), proposalOrBugReportImage.getReportImagePath()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getReportImagePath()).toHashCode();
    }
}
