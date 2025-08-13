package kr.modusplant.infrastructure.util;

public abstract class VersionUtils {
    public static String createVersion(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("유효하지 않은 시맨틱 확인됨");
        }
        return "v" + major + "." + minor + "." + patch;
    }
}
