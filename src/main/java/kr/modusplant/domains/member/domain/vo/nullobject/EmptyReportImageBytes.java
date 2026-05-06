package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.ReportImageBytes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyReportImageBytes extends ReportImageBytes {
    public static EmptyReportImageBytes create() {
        return instance;
    }
    private static final EmptyReportImageBytes instance = new EmptyReportImageBytes();

    @Override
    public byte[] getValue() {
        return null;
    }
}
