package kr.modusplant.domains.post.domain.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostStatus {
    private final Status status;

    public static PostStatus draft() {
        return new PostStatus(Status.DRAFT);
    }

    public static PostStatus published() {
        return new PostStatus(Status.PUBLISHED);
    }

    public boolean isDraft() {
        return this.status == Status.DRAFT;
    }

    public boolean isPublished() {
        return this.status == Status.PUBLISHED;
    }

    @Getter
    private enum Status {
        DRAFT("임시 저장됨"),
        PUBLISHED("게시됨");

        private final String value;

        Status(String value) {
            this.value = value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PostStatus postStatus)) return false;

        return new EqualsBuilder().append(getStatus(),postStatus.getStatus()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getStatus()).toHashCode();
    }
}
