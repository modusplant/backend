package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ImagesRemoveEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CONTENT_IMAGE_FILE_KEYS;

public interface ImagesRemoveEventTestUtils {
    ImagesRemoveEvent testImagesRemoveEvent = ImagesRemoveEvent.create(TEST_POST_CONTENT_IMAGE_FILE_KEYS);
}
