package kr.modusplant.domains.post.framework.out.processor.enums;

import lombok.Getter;

import java.util.Set;

@Getter
public enum FileType {
    TEXT("text",false, Set.of("txt")),
    IMAGE("image",true, Set.of("jpeg", "jpg", "png", "heif", "gif")),
    VIDEO("video",true, Set.of("mp4", "mov", "wmv", "avi")),
    AUDIO("audio",true, Set.of()),
    FILE("file",true, Set.of()),
    UNKNOWN("unknown",false, Set.of());

    private final String value;
    private final Boolean uploadable;
    private final Set<String> allowedExtensions;

    FileType(String value, Boolean uploadable, Set<String> allowedExtensions) {
        this.value = value;
        this.uploadable = uploadable;
        this.allowedExtensions = allowedExtensions;
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

    public static FileType fromExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            return UNKNOWN;
        }
        String normalizedExtension = extension.toLowerCase();
        for (FileType ft : values()) {
            if (ft.allowedExtensions.contains(normalizedExtension)) {
                return ft;
            }
        }
        return UNKNOWN;
    }

    public boolean isAllowedExtension(String extension) {
        if (extension == null || allowedExtensions.isEmpty()) {
            return false;
        }
        return allowedExtensions.contains(extension.toLowerCase());
    }

    public boolean hasExtensionRestriction() {
        return !allowedExtensions.isEmpty();
    }
}
