package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentChooseEvent {
    private final UUID memberId;
    private final String postUlid;
    private final String commentPath;

    public static CommentChooseEvent create(UUID memberId, String postUlid, String commentPath) {
        if (memberId == null) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "memberId");
        }
        if (StringUtils.isBlank(postUlid)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "postUlid");
        }
        if (StringUtils.isBlank(commentPath)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "commentPath");
        }
        return new CommentChooseEvent(memberId, postUlid, commentPath);
    }
}
