package kr.modusplant.domains.member.domain.entity;

import kr.modusplant.domains.member.domain.vo.ReportImageBytes;
import kr.modusplant.domains.member.domain.vo.ReportImageFileName;
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
public class ReportImage {
    private final ReportImagePath reportImagePath;
    private ReportImageFileName reportImageFileName;
    private ReportImageBytes reportImageBytes;

    public static ReportImage create(ReportImagePath reportImagePath,
                                     ReportImageFileName reportImageFileName,
                                     ReportImageBytes reportImageBytes) {
        if (reportImagePath == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_PATH, "reportImagePath");
        } else if (reportImageFileName == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_FILE_NAME, "reportImageFileName");
        } else if (reportImageBytes == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_BYTES, "reportImageBytes");
        }
        return new ReportImage(reportImagePath, reportImageFileName, reportImageBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportImage reportImage)) return false;

        return new EqualsBuilder().append(getReportImagePath(), reportImage.getReportImagePath()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getReportImagePath()).toHashCode();
    }
}
