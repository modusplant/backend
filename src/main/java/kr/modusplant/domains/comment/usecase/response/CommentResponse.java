package kr.modusplant.domains.comment.usecase.response;

// TODO: 현재 쓰임새가 없으므로 테스트 코드를 수정한 뒤에 삭제할 예정
public record CommentResponse(
        String postId,
        String path,
        String nickname,
        String content,
        boolean isDeleted,
        String createdAt
) {
}
