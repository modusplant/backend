package kr.modusplant.domains.search.common.util.domain.vo;

import kr.modusplant.domains.search.domain.vo.SearchPostId;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface SearchPostIdTestUtils {
    SearchPostId testSearchPostId = SearchPostId.create(TEST_POST_ULID);
}
