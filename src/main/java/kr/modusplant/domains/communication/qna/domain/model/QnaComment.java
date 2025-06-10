package kr.modusplant.domains.communication.qna.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class QnaComment {
    private final String postUlid;
    private final String path;
    private final UUID authMemberUuid;
    private final UUID createMemberUuid;
    private final String content;
    private final Boolean isDeleted;

    public static class QnaCommentBuilder {
        private String postUlid;
        private String path;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private String content;
        private Boolean isDeleted;

        public QnaCommentBuilder qnaComment(QnaComment qnaComment) {
            this.postUlid = qnaComment.getPostUlid();
            this.path = qnaComment.getPath();
            this.authMemberUuid = qnaComment.getAuthMemberUuid();
            this.createMemberUuid = qnaComment.getCreateMemberUuid();
            this.content = qnaComment.getContent();
            this.isDeleted = qnaComment.getIsDeleted();
            return this;
        }

        public QnaComment build() {
            return new QnaComment(this.postUlid, this.path, this.authMemberUuid, this.createMemberUuid, this.content, this.isDeleted);
        }
    }
}
