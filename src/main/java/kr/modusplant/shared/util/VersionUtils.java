package kr.modusplant.shared.util;

import kr.modusplant.shared.exception.InvalidValueException;

import java.util.List;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.INVALID_INPUT;

public abstract class VersionUtils {
    public static String createVersion(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new InvalidValueException(INVALID_INPUT, List.of("major", "minor", "patch"));
        }
        return "v" + major + "." + minor + "." + patch;
    }
}
