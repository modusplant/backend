package kr.modusplant.shared.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Regex {
    public static final String REGEX_EMAIL = "^[A-Za-z0-9]+@[A-Za-z0-9]+\\.[a-z]{2,4}$";
    public static final String REGEX_JWT = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?\\[A-Za-z0-9-_.+/=]*$";
    public static final String REGEX_MATERIALIZED_PATH = "^\\d+(?:\\.\\d+)*$";
    public static final String REGEX_NICKNAME = "^[가-힣A-Za-z0-9]{2,16}$";
    public static final String REGEX_PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&()_\\-+=\\[\\]{}|\\\\;:'\",.<>/?]).{8,64}$";
    public static final String REGEX_ULID = "^[0-9A-HJKMNP-TV-Z]{26}$";
    public static final String REGEX_UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String REGEX_VERSION = "^v\\d+.\\d+.\\d+$";

    public static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);
    public static final Pattern PATTERN_JWT = Pattern.compile(REGEX_JWT);
    public static final Pattern PATTERN_MATERIALIZED_PATH = Pattern.compile(REGEX_MATERIALIZED_PATH);
    public static final Pattern PATTERN_NICKNAME = Pattern.compile(REGEX_NICKNAME);
    public static final Pattern PATTERN_PASSWORD = Pattern.compile(REGEX_PASSWORD);
    public static final Pattern PATTERN_ULID = Pattern.compile(REGEX_ULID);
    public static final Pattern PATTERN_UUID = Pattern.compile(REGEX_UUID);
    public static final Pattern PATTERN_VERSION = Pattern.compile(REGEX_VERSION);
}
