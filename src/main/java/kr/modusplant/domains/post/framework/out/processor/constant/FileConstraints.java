package kr.modusplant.domains.post.framework.out.processor.constant;

public final class FileConstraints {
    private FileConstraints() {}

    // 파일 개수 제한
    public static final int MAX_TOTAL_FILES = 10;
    public static final int MAX_IMAGE_FILES = 10;
    public static final int MAX_VIDEO_FILES = 5;

    // 파일 크기 제한 (바이트)
    public static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024; // 20MB
}
