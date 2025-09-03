package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommentStatus {
    private String status;

    // TODO: 댓글의 상태가 구체적으로 무엇이 있어야 하는지 확정한 후 list에 반영할 것.
    // valid, deleted 는 내가 임시로 넣었다.
    // TODO: Null, 공백 검사 할 것
    public static CommentStatus create(String status) {
        if(!List.of("valid", "deleted").contains(status)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_STATUS);
        }
        return new CommentStatus(status);
    }

    public static CommentStatus setAsValid() { return new CommentStatus("valid"); }
    public static CommentStatus setAsDeleted() { return new CommentStatus("deleted"); }
}
