package kr.modusplant.shared.framework.aws.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageRemoveTask {
    private final String imageFileKey;

    public static ImageRemoveTask create(String imageFileKey) {
        if (StringUtils.isBlank(imageFileKey)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_IMAGE_FILE_KEY, "imageFileKey");
        } else {
            return new ImageRemoveTask(imageFileKey);
        }
    }
}
