package kr.modusplant.domains.post.usecase.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostDetailResponse(
        @Schema(description = "게시글의 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J")
        @JsonProperty("postId")
        String ulid,

        @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a")
        Integer primaryCategoryId,

        @Schema(description = "게시글이 속한 1차 항목", example = "팁")
        String primaryCategory,

        @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "12d0d32d-f907-4605-a9d0-00b5669ea18a")
        Integer secondaryCategoryId,

        @Schema(description = "게시글이 속한 2차 항목", example = "물꽂이 + 잎꽂이")
        String secondaryCategory,

        @Schema(description = "게시글을 작성한 회원의 식별자", example = "4918b2c8-1890-4e27-9703-3795e0a9d6d9")
        @JsonProperty("authorId")
        UUID authorUuid,

        @Schema(description = "게시글을 작성한 회원의 닉네임", example = "제트드랍")
        String nickname,

        @Schema(description = "회원 프로필 이미지 URL")
        String authorImageUrl,

        @Schema(description = "게시글의 조회수", example = "231")
        Integer likeCount,

        @Schema(description = "게시글의 좋아요수", example = "13")
        Long viewCount,

        @Schema(description = "게시글의 제목", example = "이거 과습인가요?")
        String title,

        @Schema(description = "게시글 컨텐츠")
        JsonNode content,

        @Schema(description = "게시글의 게시 유무")
        Boolean isPublished,

        @Schema(description = "게시글이 게시된 날짜 및 시간")
        LocalDateTime publishedAt,

        @Schema(description = "게시글이 마지막으로 수정된 날짜 및 시간")
        LocalDateTime updatedAt,

        @Schema(description = "게시글 좋아요 여부")
        Boolean isLiked,

        @Schema(description = "게시글 북마크 여부")
        Boolean isBookmarked
) {
}