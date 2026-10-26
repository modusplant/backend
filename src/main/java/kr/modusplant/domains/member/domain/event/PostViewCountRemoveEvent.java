package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostViewCountRemoveEvent {
    private final String[] postIds;

    public static PostViewCountRemoveEvent create(String[] postIds) {
        if (ArrayUtils.isEmpty(postIds)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postIds");
        } else {
            return new PostViewCountRemoveEvent(postIds);
        }
    }
}
