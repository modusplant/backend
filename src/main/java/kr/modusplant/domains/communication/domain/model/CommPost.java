package kr.modusplant.domains.communication.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class CommPost {
    private final String ulid;

    private final UUID categoryUuid;

    private final UUID authMemberUuid;

    private final UUID createMemberUuid;

    private final Integer likeCount;

    private final Long viewCount;

    private final String title;

    private final JsonNode content;

    private final Boolean isDeleted;

    public static class CommPostBuilder {
        private String ulid;
        private UUID categoryUuid;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public CommPostBuilder commPost(CommPost commPost) {
            this.ulid = commPost.ulid;
            this.categoryUuid = commPost.categoryUuid;
            this.authMemberUuid = commPost.authMemberUuid;
            this.createMemberUuid = commPost.createMemberUuid;
            this.likeCount = commPost.likeCount;
            this.viewCount = commPost.viewCount;
            this.title = commPost.title;
            this.content = commPost.content;
            this.isDeleted = commPost.isDeleted;
            return this;
        }

        public CommPost build() {
            return new CommPost(this.ulid, this.categoryUuid, this.authMemberUuid, this.createMemberUuid, this.likeCount, this.viewCount, this.title, this.content, this.isDeleted);
        }
    }

}
