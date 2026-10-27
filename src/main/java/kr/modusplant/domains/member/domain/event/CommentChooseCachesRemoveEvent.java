package kr.modusplant.domains.member.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentChooseCachesRemoveEvent {
    private final Map<String, String> chosenCommentPathsByPostUlid;

    public static CommentChooseCachesRemoveEvent create(Map<String, String> chosenPostUlidCommentPathMap) {
        if (CollectionUtils.isEmpty(chosenPostUlidCommentPathMap)) {
            throw new EmptyValueException(GeneralErrorCode.EMPTY_VALUE, "chosenPostUlidCommentPathMap");
        }
        return new CommentChooseCachesRemoveEvent(chosenPostUlidCommentPathMap);
    }
}
