package kr.modusplant.domains.post.common.util.usecase.response;

import kr.modusplant.domains.post.usecase.response.PostFileUploadUrlResponse;

import static kr.modusplant.domains.post.common.constant.FileConstant.*;

public interface PostFileUploadUrlResponseTestUtils {
    PostFileUploadUrlResponse testImageJpgFileUploadUrlResponse = new PostFileUploadUrlResponse(TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_PRESIGNED_URL, TEST_IMAGE_JPG_FILE_KEY);
    PostFileUploadUrlResponse testImagePngFileUploadUrlResponse = new PostFileUploadUrlResponse(TEST_IMAGE_PNG_FILENAME, TEST_IMAGE_PNG_PRESIGNED_URL, TEST_IMAGE_PNG_FILE_KEY);
    PostFileUploadUrlResponse testVideoMp4FileUploadUrlResponse = new PostFileUploadUrlResponse(TEST_VIDEO_MP4_FILENAME, TEST_VIDEO_MP4_PRESIGNED_URL, TEST_VIDEO_MP4_FILE_KEY);
    PostFileUploadUrlResponse testVideoAviFileUploadUrlResponse = new PostFileUploadUrlResponse(TEST_VIDEO_AVI_FILENAME, TEST_VIDEO_AVI_PRESIGNED_URL, TEST_VIDEO_AVI_FILE_KEY);
}
