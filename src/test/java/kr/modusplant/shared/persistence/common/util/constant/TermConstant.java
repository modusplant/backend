package kr.modusplant.shared.persistence.common.util.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static kr.modusplant.shared.util.VersionUtils.createVersion;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TermConstant {
    public static final UUID TERMS_OF_USE_UUID = UUID.fromString("848f747c-72c2-4a67-9266-71afc893b070");
    public static final String TERMS_OF_USE_NAME = "이용약관";
    public static final String TERMS_OF_USE_CONTENT = "이용약관 내용";
    public static final String TERMS_OF_USE_VERSION = createVersion(1, 0, 0);
}
