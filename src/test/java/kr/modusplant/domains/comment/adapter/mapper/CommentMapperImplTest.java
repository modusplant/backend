package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.common.util.usecase.model.CommentOfAuthorPageModelTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.model.CommentOfPostReadModelTestUtils;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.shared.framework.aws.service.AmazonS3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

public class CommentMapperImplTest implements
        CommentOfPostReadModelTestUtils, CommentOfAuthorPageModelTestUtils {

    private final AmazonS3Service fileService = Mockito.mock(AmazonS3Service.class);
    private final CommentMapperImpl mapper = new CommentMapperImpl(fileService);

    @Test
    @DisplayName("프로필 이미지가 있는 읽기 모델을 응답으로 변환")
    public void testToCommentOfPostResponse_givenProfileImagePresent_willReturnResponseWithS3Url() {
        // given
        given(fileService.generateS3SrcUrl(testCommentOfPostReadModel.profileImage())).willReturn("https://s3/profile.png");

        // when
        CommentOfPostResponse result = mapper.toCommentOfPostResponse(testCommentOfPostReadModel);

        // then
        assertThat(result.profileImagePath()).isEqualTo("https://s3/profile.png");
        assertThat(result.nickname()).isEqualTo(testCommentOfPostReadModel.nickname());
        assertThat(result.path()).isEqualTo(testCommentOfPostReadModel.path());
        assertThat(result.content()).isEqualTo(testCommentOfPostReadModel.content());
        assertThat(result.likeCount()).isEqualTo(testCommentOfPostReadModel.likeCount());
        assertThat(result.isLiked()).isEqualTo(testCommentOfPostReadModel.isLiked());
        assertThat(result.createdAt()).isEqualTo(testCommentOfPostReadModel.createdAt());
        assertThat(result.editedAt()).isEqualTo(testCommentOfPostReadModel.updatedAt());
        assertThat(result.isDeleted()).isEqualTo(testCommentOfPostReadModel.isDeleted());
    }

    @Test
    @DisplayName("프로필 이미지가 없는 읽기 모델을 응답으로 변환")
    public void testToCommentOfPostResponse_givenNullProfileImage_willReturnResponseWithNullProfileImagePath() {
        // given
        CommentOfPostReadModel readModel = new CommentOfPostReadModel(
                null, testCommentOfPostReadModel.nickname(), testCommentOfPostReadModel.path(),
                testCommentOfPostReadModel.content(), testCommentOfPostReadModel.likeCount(),
                testCommentOfPostReadModel.isLiked(), testCommentOfPostReadModel.createdAt(),
                testCommentOfPostReadModel.updatedAt(), testCommentOfPostReadModel.isDeleted());

        // when
        CommentOfPostResponse result = mapper.toCommentOfPostResponse(readModel);

        // then
        assertThat(result.profileImagePath()).isNull();
        then(fileService).should(times(0)).generateS3SrcUrl(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("페이지네이션 된 읽기 모델을 1-based 페이지 응답으로 변환")
    public void testToCommentPageResponseWithOnePlusPage_givenPaginatedReadModel_willReturnOneIndexedPageResponse() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        PageImpl<CommentOfAuthorReadModel> page = new PageImpl<>(List.of(testCommentOfAuthorReadModel), pageable, 1L);

        // when
        CommentPageResponse<CommentOfAuthorReadModel> result = mapper.toCommentPageResponseWithOnePlusPage(page);

        // then
        assertThat(result.commentList()).isEqualTo(List.of(testCommentOfAuthorReadModel));
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(page.getSize());
        assertThat(result.totalElements()).isEqualTo(page.getTotalElements());
        assertThat(result.totalPages()).isEqualTo(page.getTotalPages());
        assertThat(result.hasNext()).isEqualTo(page.hasNext());
        assertThat(result.hasPrevious()).isEqualTo(page.hasPrevious());
    }
}
