package kr.modusplant.domains.record.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecordConstant {
    public static final String TEST_RECORD_ULID = "01JY3PPG5YJ41H7BPD0DSQW2RD";
    public static final String TEST_RECORD_ORIGINAL_FILENAME = "새잎.mp4";
    public static final String TEST_RECORD_MP4_CONTENT_TYPE = "video/mp4";
    public static final String TEST_RECORD_QUICKTIME_CONTENT_TYPE = "video/quicktime";
    public static final Long TEST_RECORD_SIZE_BYTES = 2_400_000L;
    public static final String TEST_RECORD_UPLOAD_TOKEN = "test-upload-token";
    public static final String TEST_RECORD_DESCRIPTION = "오늘 새 잎이 나왔어요";
    public static final String TEST_RECORD_UPLOAD_URL = "https://storage.example.com/presigned-put";
}
