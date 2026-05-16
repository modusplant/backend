package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;

import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_PUBLISHED_AT;

public interface SearchPostPublishedAtTestUtils {
    SearchPostPublishedAt testSearchPostPublishedAt = SearchPostPublishedAt.create(TEST_COMM_POST_PUBLISHED_AT);
}
