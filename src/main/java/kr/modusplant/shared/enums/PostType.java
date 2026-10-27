package kr.modusplant.shared.enums;

import lombok.Getter;

@Getter
public enum PostType {
    LIFE("일상"),
    TIP("팁"),
    QNA("Q&A"),
    ;

    private final String value;

    PostType(String value) {
        this.value = value;
    }
}