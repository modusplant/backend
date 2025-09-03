package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentPath {
    String path;

    // TODO: 대댓글이 최대 얼마의 깊이까지 달릴 수 있는지를 create 에서 확인할 것.

    /**
     * @param path 의 형식은 반드시 숫자와 점(.)의 연속물이어야 합니다.
     */
    public static CommentPath create(String path) {
        if (path.isBlank()) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_PATH);
        }
        if (!path.matches("^\\d+(\\.\\d+)*$")) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_PATH);
        }

        return new CommentPath(path);
    }
}
