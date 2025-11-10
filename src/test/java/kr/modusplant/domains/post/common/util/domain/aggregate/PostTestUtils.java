package kr.modusplant.domains.post.common.util.domain.aggregate;

import kr.modusplant.domains.post.common.util.domain.vo.*;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostStatus;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostTestUtils extends PostIdTestUtils, AuthorIdTestUtils, PrimaryCategoryIdTestUtils, SecondaryCategoryIdTestUtils, PostContentTestUtils, LikeCountTestUtils {
    default Post createDraftPost() {
        return Post.create(testPostId,testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.draft());
    }

    default Post createDraftPost2() {
        return Post.create(testPostId,AuthorId.fromUuid(MEMBER_BASIC_USER_UUID), testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.draft());
    }

    default Post createPublishedPost() {
        return Post.create(testPostId,testAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.published());
    }

    default Post createPublishedPost2() {
        return Post.create(testPostId, AuthorId.fromUuid(MEMBER_BASIC_USER_UUID), testPrimaryCategoryId, testSecondaryCategoryId, testPostContent,testLikeCount, PostStatus.published());
    }
}
