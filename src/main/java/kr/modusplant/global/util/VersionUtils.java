package kr.modusplant.global.util;

public abstract class VersionUtils {
    public static String createVersion(int major, int minor, int patch) {
        return "v" + major + "." + minor + "." + patch;
    }
}
