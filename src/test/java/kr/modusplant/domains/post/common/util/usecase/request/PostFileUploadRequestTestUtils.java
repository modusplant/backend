package kr.modusplant.domains.post.common.util.usecase.request;

import kr.modusplant.domains.post.usecase.request.PostFileUploadRequest;

import static kr.modusplant.domains.post.common.constant.FileConstant.*;

public interface PostFileUploadRequestTestUtils {
    PostFileUploadRequest testImageJpgFileUploadRequest= new PostFileUploadRequest(TEST_IMAGE_JPG_FILENAME, TEST_IMAGE_JPG_CONTENT_TYPE);
    PostFileUploadRequest testImagePngFileUploadRequest = new PostFileUploadRequest(TEST_IMAGE_PNG_FILENAME,TEST_IMAGE_PNG_CONTENT_TYPE);
    PostFileUploadRequest testVideoMp4FileUploadRequest = new PostFileUploadRequest(TEST_VIDEO_MP4_FILENAME,TEST_VIDEO_MP4_CONTENT_TYPE);
    PostFileUploadRequest testVideoAviFileUploadRequest = new PostFileUploadRequest(TEST_VIDEO_AVI_FILENAME, TEST_VIDEO_AVI_CONTENT_TYPE);
}
