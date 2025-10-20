package kr.modusplant.legacy.domains.communication.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder
public class CommComment {
    private final String postUlid;
    private final String path;
    private final UUID authMemberUuid;
    private final UUID createMemberUuid;
    private final String content;
    private final Boolean isDeleted;

    public static class CommCommentBuilder {
        private String postUlid;
        private String path;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private String content;
        private Boolean isDeleted;

        public CommCommentBuilder commComment(CommComment commComment) {
            this.postUlid = commComment.getPostUlid();
            this.path = commComment.getPath();
            this.authMemberUuid = commComment.getAuthMemberUuid();
            this.createMemberUuid = commComment.getCreateMemberUuid();
            this.content = commComment.getContent();
            this.isDeleted = commComment.getIsDeleted();
            return this;
        }

        public CommComment build() {
            return new CommComment(this.postUlid, this.path, this.authMemberUuid, this.createMemberUuid, this.content, this.isDeleted);
        }
    }
}
