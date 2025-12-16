package kr.modusplant.shared.http.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public abstract class ParseHttpHeaderUtils {
    public static List<String> parseIfNoneMatch(String ifNoneMatch) {
        return Arrays.stream(ifNoneMatch.split(","))
                .map(String::trim)
                .map(element -> element.substring(1, element.length() - 1))
                .toList();
    }

    public static LocalDateTime parseIfModifiedSince(String ifModifiedSince) {
        return LocalDateTime.parse(ifModifiedSince, DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}
