package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CONTENT_IMAGE_FILE_KEYS;

public interface ImagesRemoveEventTestUtils {
    ImagesRemoveTask testImagesRemoveTask = ImagesRemoveTask.create(TEST_POST_CONTENT_IMAGE_FILE_KEYS);
}
