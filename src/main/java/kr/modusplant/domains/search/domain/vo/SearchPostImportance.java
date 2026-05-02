package kr.modusplant.domains.search.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPostImportance {
    private final Importance importance;

    public static SearchPostImportance create(Integer value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_IMPORTANCE, "searchPostImportance");
        } else if (value < 1 || value > 4) {
            throw new InvalidValueException(SEARCH_POST_IMPORTANCE_OUT_OF_RANGE, "searchPostImportance");
        }
        if (value.equals(4)) {
            return searchPostImportanceTitle;
        } else if (value.equals(3)) {
            return searchPostImportanceContent;
        } else if (value.equals(2)) {
            return searchPostImportanceCommentContent;
        } else {
            return searchPostImportanceOthers;
        }
    }

    public static SearchPostImportance title() {
        return searchPostImportanceTitle;
    }

    public static SearchPostImportance content() {
        return searchPostImportanceContent;
    }

    public static SearchPostImportance commentContent() {
        return searchPostImportanceCommentContent;
    }

    public static SearchPostImportance others() {
        return searchPostImportanceOthers;
    }

    public static SearchPostImportance empty() {
        return searchPostImportanceEmpty;
    }

    public int getValueIfNotEmpty() {
        if (getImportance().equals(Importance.EMPTY)) {
            throw new InvalidValueException(NOT_FOUND_SEARCH_POST_IMPORTANCE, "searchPostImportance");
        }
        return this.getImportance().getValue();
    }

    public boolean isEmpty() {
        return this.getImportance().isEmpty();
    }

    private static final SearchPostImportance searchPostImportanceTitle = new SearchPostImportance(Importance.TITLE);
    private static final SearchPostImportance searchPostImportanceContent = new SearchPostImportance(Importance.CONTENT);
    private static final SearchPostImportance searchPostImportanceCommentContent = new SearchPostImportance(Importance.COMMENT_CONTENT);
    private static final SearchPostImportance searchPostImportanceOthers = new SearchPostImportance(Importance.OTHERS);
    private static final SearchPostImportance searchPostImportanceEmpty = new SearchPostImportance(Importance.EMPTY);

    @Getter
    private enum Importance {
        TITLE(4, false),
        CONTENT(3, false),
        COMMENT_CONTENT(2, false),
        OTHERS(1, false),
        EMPTY(0, true);

        private final int value;
        private final boolean empty;

        Importance(int value, boolean empty) {
            this.value = value;
            this.empty = empty;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPostImportance searchPostImportance)) return false;

        if (isEmpty() && searchPostImportance.isEmpty()) return true;

        if (isEmpty() || searchPostImportance.isEmpty()) return false;

        return new EqualsBuilder().append(getValueIfNotEmpty(), searchPostImportance.getValueIfNotEmpty()).isEquals();
    }

    @Override
    public int hashCode() {
        if (isEmpty()) {
            return 0;
        }
        return new HashCodeBuilder(17, 37).append(getValueIfNotEmpty()).toHashCode();
    }
}
