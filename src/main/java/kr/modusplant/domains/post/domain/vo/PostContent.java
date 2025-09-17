package kr.modusplant.domains.post.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.exception.EmptyPostContentException;
import kr.modusplant.domains.post.domain.exception.InvalidPostContentException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostContent {
    private static final int MAX_TITLE_LENGTH = 150;        // TODO:따로 빼기

    private final String title;
    private final JsonNode content;

    public static PostContent create(String title, JsonNode content) {
        if (title == null || title.trim().isEmpty()) {
            throw new EmptyPostContentException("게시글 제목이 비어 있습니다. ");
        }
        if (title.length() > 150) {
            throw new InvalidPostContentException("게시글 제목이 유효하지 않습니다. ");
        }
        if (content == null) {
            throw new EmptyPostContentException("게시글 내용이 비어 있습니다. ");
        }
        return new PostContent(title, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PostContent postContent)) return false;

        return new EqualsBuilder()
                .append(getTitle(),postContent.getTitle())
                .append(getContent(),postContent.getContent())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getTitle()).append(getContent()).toHashCode();
    }

}
