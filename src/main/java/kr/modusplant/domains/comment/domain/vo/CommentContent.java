package kr.modusplant.domains.comment.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentContent {
    private String content;

    public static CommentContent create(String content) {
        return new CommentContent(content);
    }
}
