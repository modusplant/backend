package kr.modusplant.domains.communication.qna.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class QnaPost {
    private final String ulid;

    private final UUID categoryUuid;

    private final UUID authMemberUuid;

    private final UUID createMemberUuid;

    private final Integer likeCount;

    private final Long viewCount;

    private final String title;

    private final JsonNode content;

    private final Boolean isDeleted;

    public static class QnaPostBuilder {
        private String ulid;
        private UUID categoryUuid;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public QnaPostBuilder qnaPost(QnaPost qnaPost) {
            this.ulid = qnaPost.ulid;
            this.categoryUuid = qnaPost.categoryUuid;
            this.authMemberUuid = qnaPost.authMemberUuid;
            this.createMemberUuid = qnaPost.createMemberUuid;
            this.likeCount = qnaPost.likeCount;
            this.viewCount = qnaPost.viewCount;
            this.title = qnaPost.title;
            this.content = qnaPost.content;
            this.isDeleted = qnaPost.isDeleted;
            return this;
        }

        public QnaPost build() {
            return new QnaPost(this.ulid, this.categoryUuid, this.authMemberUuid, this.createMemberUuid, this.likeCount, this.viewCount, this.title, this.content, this.isDeleted);
        }
    }

}
