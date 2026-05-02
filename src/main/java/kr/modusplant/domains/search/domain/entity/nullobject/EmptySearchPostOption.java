package kr.modusplant.domains.search.domain.entity.nullobject;

import kr.modusplant.domains.search.domain.entity.SearchPostOption;
import kr.modusplant.domains.search.domain.vo.SearchKeywordSimilarity;
import kr.modusplant.domains.search.domain.vo.SearchPostImportance;
import kr.modusplant.domains.search.domain.vo.nullobject.EmptySearchPostId;
import kr.modusplant.domains.search.domain.vo.nullobject.EmptySearchPostPublishedAt;

public class EmptySearchPostOption extends SearchPostOption {
    private EmptySearchPostOption() {
        super(EmptySearchPostId.create(),
                EmptySearchPostPublishedAt.create(),
                SearchPostImportance.createEmpty(),
                SearchKeywordSimilarity.createEmpty());
    }

    public static EmptySearchPostOption create() {
        return new EmptySearchPostOption();
    }
}
