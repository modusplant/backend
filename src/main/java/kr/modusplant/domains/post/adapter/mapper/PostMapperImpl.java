package kr.modusplant.domains.post.adapter.mapper;

import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.*;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {
    private final MultipartDataProcessorPort multipartDataProcessorPort;

    @Override
    public PostContent toPostContent(PostInsertRequest request) throws IOException {
        return PostContent.create(request.title(), multipartDataProcessorPort.saveFilesAndGenerateContentJson(request.content()));
    }

    @Override
    public PostContent toPostContent(PostUpdateRequest request) throws IOException {
        return PostContent.create(request.title(),multipartDataProcessorPort.saveFilesAndGenerateContentJson(request.content()));
    }

    @Override
    public PostContent toContentJson(Post post) throws IOException{
        return PostContent.create(
                post.getPostContent().getTitle(),
                multipartDataProcessorPort.convertFileSrcToBinaryData(post.getPostContent().getContent())
        );
    }

    @Override
    public PostResponse toPostResponse(Post post,Long viewCount) {
        return new PostResponse(
                post.getPostId().getValue(),
                post.getPrimaryCategoryId().getValue(),
                post.getSecondaryCategoryId().getValue(),
                post.getAuthorId().getValue(),
                post.getLikeCount().getValue(),
                viewCount==null ? 0 : viewCount,
                post.getPostContent().getTitle(),
                post.getPostContent().getContent(),
                post.getStatus().isPublished()
        );
    }
}
