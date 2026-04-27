package kr.modusplant.domains.member.domain.aggregate;

import kr.modusplant.domains.member.domain.entity.ReportImage;
import kr.modusplant.domains.member.domain.vo.ReportContent;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.domain.vo.ReportImageNumber;
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
public class Report {
    private final ReportId reportId;
    private ReportTitle reportTitle;
    private ReportContent reportContent;
    private List<ReportImage> reportImages;
    private ReportImageNumber reportImageNumber;

    public static Report create(ReportId id,
                                ReportTitle title,
                                ReportContent content,
                                List<ReportImage> images,
                                ReportImageNumber imageNumber) {
        if (id == null) {
            throw new EmptyValueException(EMPTY_REPORT_ID, "reportId");
        } else if (title == null) {
            throw new EmptyValueException(EMPTY_REPORT_TITLE, "reportTitle");
        } else if (content == null) {
            throw new EmptyValueException(EMPTY_REPORT_CONTENT, "reportContent");
        } else if (images == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE, "reportImages");
        } else if (imageNumber == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_NUMBER, "reportImageNumber");
        } else if (images.size() != imageNumber.getValue()) {
            throw new InvalidValueException(MISMATCHED_REPORT_IMAGE_SIZE, List.of("reportImages", "reportImageNumber"));
        }
        return new Report(id, title, content, images, imageNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Report report)) return false;

        return new EqualsBuilder().append(getReportId(), report.getReportId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getReportId()).toHashCode();
    }
}
