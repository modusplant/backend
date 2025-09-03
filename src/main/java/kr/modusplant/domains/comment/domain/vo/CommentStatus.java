package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.exception.enums.CommentStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentStatus {
    private String status;

    public static CommentStatus create(String status) {

        if(!CommentStatusType.isValidStatus(status)) {
            throw new InvalidValueException(CommentErrorCode.INVALID_COMMENT_STATUS);
        }
        return new CommentStatus(status);
    }

    public static CommentStatus setAsValid() { return new CommentStatus("valid"); }
    public static CommentStatus setAsDeleted() { return new CommentStatus("deleted"); }
}
