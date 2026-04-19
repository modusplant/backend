package kr.modusplant.domains.post.common.util.usecase.request;

import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;

import java.util.List;

public interface PostCategoryRequestTestUtils {
    PostCategoryRequest testPostCategoryRequest = new PostCategoryRequest(1, List.of(1));
}
