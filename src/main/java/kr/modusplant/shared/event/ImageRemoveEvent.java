package kr.modusplant.shared.event;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageRemoveEvent {
    private final List<String> imageFileKeys;

    public static ImageRemoveEvent create(List<String> imageFileKeys) {
        if (CollectionUtils.isEmpty(imageFileKeys)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_IMAGE_FILE_KEYS, "imageFileKeys");
        } else {
            return new ImageRemoveEvent(imageFileKeys);
        }
    }
}
