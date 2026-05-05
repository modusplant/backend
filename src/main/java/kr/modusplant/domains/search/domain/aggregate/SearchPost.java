package kr.modusplant.domains.search.domain.aggregate;

import kr.modusplant.domains.search.domain.entity.SearchPostOption;
import kr.modusplant.domains.search.domain.enums.SearchPostSortCondition;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.SearchKeyword;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPost {
    private final SearchPostOption searchPostOption;
    private SearchKeyword searchKeyword;
    private SearchPostTarget searchPostTarget;
    private SearchPostSortCondition searchPostSortCondition;

    public static SearchPost create(SearchPostOption searchPostOption,
                                    SearchKeyword searchKeyword,
                                    SearchPostTarget searchPostTarget,
                                    SearchPostSortCondition searchPostSortCondition) {
        if (searchPostOption == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_OPTION, "searchPostOption");
        } else if (searchKeyword == null) {
            throw new EmptyValueException(EMPTY_SEARCH_KEYWORD, "searchKeyword");
        } else if (searchPostTarget == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_TARGET, "searchPostTarget");
        } else if (searchPostSortCondition == null) {
            throw new EmptyValueException(EMPTY_SEARCH_KEYWORD_SIMILARITY, "searchPostSortCondition");
        } else if (searchPostSortCondition.equals(SearchPostSortCondition.LATEST)) {
            if (!searchPostOption.getSearchPostImportance().isEmpty() ||
                    !searchPostOption.getSearchKeywordSimilarity().isEmpty()) {
                throw new InvalidValueException(INCORRECT_SEARCH_POST_OPTION,
                        List.of("searchPostImportance", "searchKeywordSimilarity"));
            }
        } else if (searchPostSortCondition.equals(SearchPostSortCondition.RELEVANCE)) {
            if (!(
                    (searchPostOption.getSearchPostId().getValue() == null &&
                            searchPostOption.getSearchPostPublishedAt().getValue() == null &&
                            searchPostOption.getSearchPostImportance().isEmpty() &&
                            searchPostOption.getSearchKeywordSimilarity().isEmpty()) ||
                            (searchPostOption.getSearchPostId().getValue() != null &&
                                    searchPostOption.getSearchPostPublishedAt().getValue() != null &&
                                    !searchPostOption.getSearchPostImportance().isEmpty() &&
                                    !searchPostOption.getSearchKeywordSimilarity().isEmpty())
            )) {
                throw new InvalidValueException(INCORRECT_SEARCH_POST_OPTION,
                        List.of("searchPostId", "searchPostPublishedAt",
                                "searchPostImportance", "searchKeywordSimilarity"));
            }
        }
        return new SearchPost(searchPostOption, searchKeyword, searchPostTarget, searchPostSortCondition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPost searchPost)) return false;

        return new EqualsBuilder().append(getSearchPostOption().getSearchPostId().getValue(), searchPost.getSearchPostOption().getSearchPostId().getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getSearchPostOption().getSearchPostId().getValue()).toHashCode();
    }
}
