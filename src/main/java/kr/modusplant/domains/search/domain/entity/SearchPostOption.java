package kr.modusplant.domains.search.domain.entity;

import kr.modusplant.domains.search.domain.vo.SearchKeywordSimilarity;
import kr.modusplant.domains.search.domain.vo.SearchPostId;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static kr.modusplant.domains.search.domain.exception.enums.SearchErrorCode.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchPostOption {
    private final SearchPostId searchPostId;
    private SearchPostPublishedAt searchPostPublishedAt;
    private SearchPostImportance searchPostImportance;
    private SearchKeywordSimilarity searchKeywordSimilarity;

    public static SearchPostOption createRelevanceOption(SearchPostId searchPostId,
                                                         SearchPostPublishedAt searchPostPublishedAt,
                                                         SearchPostImportance searchPostImportance,
                                                         SearchKeywordSimilarity searchKeywordSimilarity) {
        if (searchPostId == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_ID, "searchPostId");
        } else if (searchPostPublishedAt == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_PUBLISHED_AT, "searchPostPublishedAt");
        } else if (searchPostImportance == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_IMPORTANCE, "searchPostImportance");
        } else if (searchKeywordSimilarity == null) {
            throw new EmptyValueException(EMPTY_SEARCH_KEYWORD_SIMILARITY, "searchKeywordSimilarity");
        } else if ((searchPostId.getValue() == null && searchPostPublishedAt.getValue() != null) ||
                (searchPostId.getValue() != null && searchPostPublishedAt.getValue() == null)){
            throw new InvalidValueException(
                    GeneralErrorCode.INVALID_INPUT, List.of("searchPostId", "searchPostPublishedAt"));
        } else if ((searchPostImportance.isEmpty() && !searchKeywordSimilarity.isEmpty()) ||
                (!searchPostImportance.isEmpty() && searchKeywordSimilarity.isEmpty())) {
            throw new InvalidValueException(
                    GeneralErrorCode.INVALID_INPUT, List.of("searchPostImportance", "searchKeywordSimilarity"));
        }
        return new SearchPostOption(searchPostId, searchPostPublishedAt, searchPostImportance, searchKeywordSimilarity);
    }

    public static SearchPostOption createLatestOption(SearchPostId searchPostId,
                                                      SearchPostPublishedAt searchPostPublishedAt) {
        if (searchPostId == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_ID, "searchPostId");
        } else if (searchPostPublishedAt == null) {
            throw new EmptyValueException(EMPTY_SEARCH_POST_PUBLISHED_AT, "searchPostPublishedAt");
        } else if ((searchPostId.getValue() == null && searchPostPublishedAt.getValue() != null) ||
                (searchPostId.getValue() != null && searchPostPublishedAt.getValue() == null)){
            throw new InvalidValueException(
                    GeneralErrorCode.INVALID_INPUT, List.of("searchPostId", "searchPostPublishedAt"));
        }
        return new SearchPostOption(
                searchPostId, searchPostPublishedAt,
                SearchPostImportance.empty(), SearchKeywordSimilarity.createEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPostOption searchPostOption)) return false;

        return new EqualsBuilder().append(getSearchPostId().getValue(), searchPostOption.getSearchPostId().getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getSearchPostId().getValue()).toHashCode();
    }
}
