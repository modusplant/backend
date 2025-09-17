package kr.modusplant.domains.post.common.utils.domain.aggregate;

import kr.modusplant.domains.post.common.utils.domain.vo.*;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.PostStatus;

public interface PostTestUtils extends PostIdTestUtils, AuthorIdTestUtils, PrimaryCategoryIdTestUtils, SecondaryCategoryIdTestUtils, PostContentTestUtils, LikeCountTestUtils {
    default Post createDraftPost() {
        return Post.create(testPostId,testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.draft());
    }

    default Post createPublishedPost() {
        return Post.create(testPostId,testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.published());
    }
}
