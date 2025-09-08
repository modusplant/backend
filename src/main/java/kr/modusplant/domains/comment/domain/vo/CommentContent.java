package kr.modusplant.domains.comment.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentContent {
    private String content;

    public static CommentContent create(String content) {
        CommentContent.checkSource(content);
        return new CommentContent(content);
    }

    public static void checkSource(String source) {
        if(source.isBlank()) {
            throw new EmptyValueException(CommentErrorCode.EMPTY_COMMENT_CONTENT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentContent commentContent)) return false;

        return new EqualsBuilder()
                .append(getContent(), commentContent.getContent())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getContent()).toHashCode();
    }
}
