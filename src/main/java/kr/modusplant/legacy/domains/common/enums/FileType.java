package kr.modusplant.legacy.domains.common.enums;

import lombok.Getter;

@Getter
public enum FileType {
    TEXT("text",false),
    IMAGE("image",true),
    VIDEO("video",true),
    AUDIO("audio",true),
    FILE("file",true),
    UNKNOWN("unknown",false);

    private final String value;
    private final Boolean uploadable;

    FileType(String value, Boolean uploadable) {
        this.value = value;
        this.uploadable = uploadable;
    }

    public static FileType from(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return UNKNOWN;
        }
        String type = contentType.contains("/") ? contentType.split("/")[0] : contentType;
        if(type.equals("application"))
            type = "file";

        for (FileType ft : values()) {
            if (ft.value.equals(type)) {
                return ft;
            }
        }
        return UNKNOWN;
    }
}
