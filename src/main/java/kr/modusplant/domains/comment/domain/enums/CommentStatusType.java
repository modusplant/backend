package kr.modusplant.domains.comment.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatusType {
    ACTIVE("active"),
    DELETED("deleted");

    private final String value;

    public static boolean contains(String input) {
        for (CommentStatusType type : CommentStatusType.values()) {
            if(type.getValue().equals(input)) {
                return true;
            }
        }
        return false;
    }
}
