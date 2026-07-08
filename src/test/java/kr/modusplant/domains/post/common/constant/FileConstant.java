package kr.modusplant.domains.post.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileConstant {
    // File Constant
    public static final String TEST_IMAGE_JPG_FILENAME = "image_0.jpg";
    public static final String TEST_IMAGE_PNG_FILENAME = "image_0.png";
    public static final String TEST_VIDEO_MP4_FILENAME = "video_0.mp4";
    public static final String TEST_VIDEO_AVI_FILENAME = "video_0.avi";
    public static final String TEST_IMAGE_JPG_CONTENT_TYPE = "image/jpeg";
    public static final String TEST_IMAGE_PNG_CONTENT_TYPE = "image/png";
    public static final String TEST_VIDEO_MP4_CONTENT_TYPE = "video/mp4";
    public static final String TEST_VIDEO_AVI_CONTENT_TYPE = "video/x-msvideo";

    // Post File Constant
    public static final String TEST_IMAGE_JPG_FILE_KEY = "post/01KM6B54J6G4DSGHT22NJ8Z1WC/image/image_0_1.jpg";
    public static final String TEST_IMAGE_PNG_FILE_KEY = "post/01KVZ95D1ZW6G7BEF1JCA7CNYS/image/image_0_1.png";
    public static final String TEST_VIDEO_MP4_FILE_KEY = "post/01KM6B54J6G4DSGHT22NJ8Z1WC/video/video_0_2.mp4";
    public static final String TEST_VIDEO_AVI_FILE_KEY = "post/01KVZ95D1ZW6G7BEF1JCA7CNYS/video/video_0_2.avi";
    public static final String TEST_IMAGE_JPG_PRESIGNED_URL = "https://s3.ap-northeast-1.wasabisys.com/modusplant-test/post/01KM6B54J6G4DSGHT22NJ8Z1WC/image/image_0_1.jpg" +
            "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=U0EJ2FA12MYM9OPEVF8Q%2F20260626%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20260626T083606Z&" +
            "X-Amz-Expires=604800&X-Amz-Signature=8e6f1ff35d7cbe420bac2a09996db297a35910219a4488017f2670c4a7b573f3&X-Amz-SignedHeaders=host";
    public static final String TEST_IMAGE_PNG_PRESIGNED_URL = "https://s3.ap-northeast-1.wasabisys.com/modusplant-test/post/01KVZ95D1ZW6G7BEF1JCA7CNYS/image/image_0_1.png" +
            "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=U0EJ2FA12MYM9OPEVF8Q%2F20260626%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20260626T083606Z&" +
            "X-Amz-Expires=604800&X-Amz-Signature=8e6f1ff35d7cbe420bac2a09996db297a35910219a4488017f2670c4a7b573f3&X-Amz-SignedHeaders=host";
    public static final String TEST_VIDEO_MP4_PRESIGNED_URL = "https://s3.ap-northeast-1.wasabisys.com/modusplant-test/post/01KM6B54J6G4DSGHT22NJ8Z1WC/video/video_0_2.mp4" +
            "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=U0EJ2FA12MYM9OPEVF8Q%2F20260626%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20260626T083606Z&" +
            "X-Amz-Expires=604800&X-Amz-Signature=8e6f1ff35d7cbe420bac2a09996db297a35910219a4488017f2670c4a7b573f3&X-Amz-SignedHeaders=host";
    public static final String TEST_VIDEO_AVI_PRESIGNED_URL = "https://s3.ap-northeast-1.wasabisys.com/modusplant-test/post/01KVZ95D1ZW6G7BEF1JCA7CNYS/video/video_0_2.avi" +
            "?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=U0EJ2FA12MYM9OPEVF8Q%2F20260626%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20260626T083606Z&" +
            "X-Amz-Expires=604800&X-Amz-Signature=8e6f1ff35d7cbe420bac2a09996db297a35910219a4488017f2670c4a7b573f3&X-Amz-SignedHeaders=host";
}
