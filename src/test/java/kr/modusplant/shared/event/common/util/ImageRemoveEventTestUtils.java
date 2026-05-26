package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ImageRemoveEvent;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CONTENT_IMAGE_FILE_KEY;

public interface ImageRemoveEventTestUtils {
    ImageRemoveEvent testImageRemoveEvent = ImageRemoveEvent.create(TEST_POST_CONTENT_IMAGE_FILE_KEY);
}
