package kr.modusplant.domains.member.adapter.util;

import java.util.UUID;

public abstract class MemberProfileImageUtils {
    public static String generateMemberProfileImagePath(UUID id, String filename) {
        return "member/" + id + "/profile/" + filename;
    }
}
