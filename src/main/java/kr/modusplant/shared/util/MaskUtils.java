package kr.modusplant.shared.util;

/**
 * 민감 정보를 마스킹하기 위한 유틸리티
 */
public abstract class MaskUtils {
    private static final String MASK = "***";

    public static String maskAnyString(String value) {
        return value == null ? null : MASK;
    }

    public static String maskEmail(String email) {
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return MASK;
        }
        return email.charAt(0) + MASK + email.substring(atIndex);
    }
}
