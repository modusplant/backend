package kr.modusplant.domains.post.usecase.port.mapper;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.domain.vo.PostContent;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.PostResponse;

import java.io.IOException;

public interface PostMapper {
    PostContent toPostContent(PostInsertRequest request) throws IOException;

    PostContent toPostContent(PostUpdateRequest request) throws IOException;

    PostContent toContentJson(Post post) throws IOException;

    PostResponse toPostResponse(Post post,Long viewCount);
}
