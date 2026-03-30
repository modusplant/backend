package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.domains.notification.domain.exception.EmptyValueException;
import kr.modusplant.domains.notification.domain.exception.enums.NotificationErrorCode;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
public class ContentPreview {
    private static final int MAX_LENGTH = 100;

    private final String content;

    private ContentPreview(String content) {
        if (content != null && content.length() > MAX_LENGTH) {
            this.content = content.substring(0, MAX_LENGTH-3) + "...";
        } else {
            this.content = content;
        }
    }

    public static ContentPreview create(String content) {
        if (content == null || content.isBlank()) {
            throw new EmptyValueException(NotificationErrorCode.EMPTY_NOTIFICATION_CONTENT);
        }
        return new ContentPreview(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentPreview contentPreview)) return false;
        return new EqualsBuilder().append(getContent(), contentPreview.getContent()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getContent()).toHashCode();
    }
}
