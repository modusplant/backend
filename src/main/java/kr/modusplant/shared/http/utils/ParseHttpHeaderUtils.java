package kr.modusplant.shared.http.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public abstract class ParseHttpHeaderUtils {
    public static List<String> parseIfNoneMatch(String ifNoneMatch) {
        return Arrays.stream(ifNoneMatch.split(","))
                .map(String::trim)
                .map(element -> element.substring(1, element.length() - 1))
                .toList();
    }

    public static LocalDateTime parseIfModifiedSince(String ifModifiedSince) {
        LocalDateTime returnDateTime;
        try {
            returnDateTime = ZonedDateTime.parse(
                            ifModifiedSince, DateTimeFormatter.RFC_1123_DATE_TIME)
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime()
                    .truncatedTo(ChronoUnit.SECONDS);
        } catch (RuntimeException exception) {
            returnDateTime = ZonedDateTime.parse(
                            ifModifiedSince, DateTimeFormatter.ofPattern("EEE,dd MMM yyyy HH:mm:ss z", Locale.ENGLISH))
                    .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                    .toLocalDateTime()
                    .truncatedTo(ChronoUnit.SECONDS);
        }
        return returnDateTime;
    }
}
