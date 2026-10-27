package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentChooseCacheRemoveEvent {
    private final String postUlid;
    private final String commentPath;

    public static CommentChooseCacheRemoveEvent create(String postUlid, String commentPath) {
        if (StringUtils.isBlank(postUlid)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        }
        if (StringUtils.isBlank(commentPath)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "commentPath");
        }
        return new CommentChooseCacheRemoveEvent(postUlid, commentPath);
    }
}
