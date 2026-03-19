package kr.modusplant.infrastructure.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SecurityLogger {

    public static void logUnknownException(Exception exception) {
        log.error("[Security Error] exceptionName={} | message={}", exception.getClass(), exception.getMessage());
    }
}