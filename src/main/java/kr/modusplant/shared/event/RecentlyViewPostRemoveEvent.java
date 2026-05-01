package kr.modusplant.shared.event;

import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RecentlyViewPostRemoveEvent {
    private final String[] postIds;

    public static RecentlyViewPostRemoveEvent create(String[] postIds) {
        if (ArrayUtils.isEmpty(postIds)) {
            throw new InvalidValueException(EntityErrorCode.NOT_FOUND_POST_ID, "postIds");
        } else {
            return new RecentlyViewPostRemoveEvent(postIds);
        }
    }
}
