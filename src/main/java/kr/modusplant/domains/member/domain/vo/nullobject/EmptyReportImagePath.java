package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.ReportImagePath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyReportImagePath extends ReportImagePath {
    public static EmptyReportImagePath create() {
        return instance;
    }
    private static final EmptyReportImagePath instance = new EmptyReportImagePath();

    @Override
    public String getValue() {
        return null;
    }
}
