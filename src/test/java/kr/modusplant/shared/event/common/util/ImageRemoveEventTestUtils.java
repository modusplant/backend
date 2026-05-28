package kr.modusplant.shared.event.common.util;

import kr.modusplant.shared.framework.aws.event.ImageRemoveTask;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_CONTENT_IMAGE_FILE_KEY;

public interface ImageRemoveEventTestUtils {
    ImageRemoveTask testImageRemoveTask = ImageRemoveTask.create(TEST_POST_CONTENT_IMAGE_FILE_KEY);
}
