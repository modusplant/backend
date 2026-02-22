package kr.modusplant.domains.member.adapter.util;

import java.util.UUID;

public abstract class MemberProfileImageUtils {
    public static String generateMemberProfileImagePath(UUID id, String filename) {
        return String.format("member/%s/profile/%s", id, filename);
    }

    public static String generateReportImagePath(UUID id, String filename) {
        return String.format("member/%s/report/%s", id, filename);
    }
}
