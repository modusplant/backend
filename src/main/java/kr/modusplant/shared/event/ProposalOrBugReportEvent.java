package kr.modusplant.shared.event;

import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReportEvent {
    private final UUID memberId;
    private final String reportId;
    private final String title;
    private final String content;
    private final List<String> filenames;
    private final List<String> imagePaths;

    public static ProposalOrBugReportEvent create(UUID memberId,
                                                  String reportId,
                                                  String title,
                                                  String content,
                                                  List<String> filenames,
                                                  List<String> imagePaths) {
        if (memberId == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_MEMBER, "memberId");
        } else if (StringUtils.isBlank(reportId)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "reportId");
        } else if (StringUtils.isBlank(title)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "title");
        } else if (StringUtils.isBlank(content)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "content");
        } else if (filenames == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "filenames");
        } else if (imagePaths == null) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_REPORT, "imagePaths");
        } else {
            return new ProposalOrBugReportEvent(memberId, reportId, title, content, filenames, imagePaths);
        }
    }
}
