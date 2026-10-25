package kr.modusplant.domains.post.common.util.domain.aggregate;

import kr.modusplant.domains.post.common.util.domain.vo.*;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.PostAuthorId;
import kr.modusplant.domains.post.domain.vo.PostLikeCount;
import kr.modusplant.domains.post.domain.vo.PostStatus;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface PostTestUtils extends PostIdTestUtils, AuthorIdTestUtils, PrimaryCategoryIdTestUtils, SecondaryCategoryIdTestUtils, PostContentTestUtils, LikeCountTestUtils {
    default Post createDraftPost() {
        return Post.create(testPostId, testPostAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent, testPostLikeCount, PostStatus.draft());
    }

    default Post createDraftPost2() {
        return Post.create(testPostId, PostAuthorId.fromUuid(MEMBER_BASIC_USER_UUID), testPrimaryCategoryId, testSecondaryCategoryId, testPostContent, testPostLikeCount, PostStatus.draft());
    }

    default Post createPublishedPost() {
        return Post.create(testPostId, testPostAuthorId, testPrimaryCategoryId, testSecondaryCategoryId, testPostContent, testPostLikeCount, PostStatus.published());
    }

    default Post createPublishedPost2() {
        return Post.create(testPostId, PostAuthorId.fromUuid(MEMBER_BASIC_USER_UUID), testPrimaryCategoryId, testSecondaryCategoryId, testPostContent, testPostLikeCount, PostStatus.published());
    }

    default Post createDraftPostWithEmptyValue() {
        return Post.create(testPostId, testPostAuthorId, testPrimaryCategoryId, null, testDraftPostContent1, PostLikeCount.create(0), PostStatus.draft());
    }

    default Post createDraftPostWithEmptyValue2() {
        return Post.create(testPostId, PostAuthorId.fromUuid(MEMBER_BASIC_USER_UUID), testPrimaryCategoryId, null, testDraftPostContent1, PostLikeCount.create(0), PostStatus.draft());
    }
}
