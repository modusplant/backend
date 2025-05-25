package kr.modusplant.domains.tip.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class TipComment {
    private final String postUlid;
    private final String path;
    private final UUID authMemberUuid;
    private final UUID createMemberUuid;
    private final String content;
    private final Boolean isDeleted;

    public static class ConvCommentBuilder {
        private String postUlid;
        private String path;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private String content;
        private Boolean isDeleted;

        public TipComment.ConvCommentBuilder convComment(TipComment convComment) {
            this.postUlid = convComment.getPostUlid();
            this.path = convComment.getPath();
            this.authMemberUuid = convComment.getAuthMemberUuid();
            this.createMemberUuid = convComment.getCreateMemberUuid();
            this.content = convComment.getContent();
            this.isDeleted = convComment.getIsDeleted();
            return this;
        }

        public TipComment build() {
            return new TipComment(this.postUlid, this.path, this.authMemberUuid, this.createMemberUuid, this.content, this.isDeleted);
        }
    }
}
