package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.event.ImageRemoveEvent;

import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_CONTENT_IMAGE_FILE_KEYS;

public interface ImageRemoveEventTestUtils {
    ImageRemoveEvent testImageRemoveEvent = ImageRemoveEvent.create(TEST_COMM_POST_CONTENT_IMAGE_FILE_KEYS);
}
