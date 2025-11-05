package kr.modusplant.domains.post.common.util.domain.vo;

import kr.modusplant.domains.post.domain.vo.PostContent;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT;
import static kr.modusplant.domains.post.common.constant.PostStringConstant.TEST_POST_TITLE;

public interface PostContentTestUtils {
    PostContent testPostContent = PostContent.create(TEST_POST_TITLE, TEST_POST_CONTENT);
}
