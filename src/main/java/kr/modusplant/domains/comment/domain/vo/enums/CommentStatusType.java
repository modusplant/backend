package kr.modusplant.domains.comment.domain.vo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatusType {
    VALID("valid"),
    DELETED("deleted");

    private final String statusType;

    public static boolean isValidStatus(String input) {
        for (CommentStatusType type : CommentStatusType.values()) {
            if(type.getStatusType().equals(input)) {
                return true;
            }
        }
        return false;
    }
}
