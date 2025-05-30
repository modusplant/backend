package kr.modusplant.global.util;

import java.util.UUID;

public abstract class GenerationUtils {
    public static UUID generateDeviceId() {
        return UUID.randomUUID();
    }
}