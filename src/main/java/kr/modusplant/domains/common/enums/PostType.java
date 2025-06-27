package kr.modusplant.domains.common.enums;

import lombok.Getter;

@Getter
public enum PostType {
    TIP_POST("tip-post"),
    CONV_POST("conv-post"),
    QNA_POST("qna-post");

    private final String value;

    PostType(String value) {
        this.value = value;
    }
}
