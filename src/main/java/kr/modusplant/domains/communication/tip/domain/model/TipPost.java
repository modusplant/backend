package kr.modusplant.domains.communication.tip.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class TipPost {
    private final String ulid;

    private final Integer groupOrder;

    private final UUID authMemberUuid;

    private final UUID createMemberUuid;

    private final Integer recommendationNumber;

    private final Integer viewCount;

    private final String title;

    private final JsonNode content;

    private final Boolean isDeleted;

    public static class TipPostBuilder {
        private String ulid;
        private Integer groupOrder;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private Integer recommendationNumber;
        private Integer viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public TipPostBuilder tipPost(TipPost tipPost) {
            this.ulid = tipPost.ulid;
            this.groupOrder = tipPost.groupOrder;
            this.authMemberUuid = tipPost.authMemberUuid;
            this.createMemberUuid = tipPost.createMemberUuid;
            this.recommendationNumber = tipPost.recommendationNumber;
            this.viewCount = tipPost.viewCount;
            this.title = tipPost.title;
            this.content = tipPost.content;
            this.isDeleted = tipPost.isDeleted;
            return this;
        }

        public TipPost build() {
            return new TipPost(this.ulid,this.groupOrder,this.authMemberUuid,this.createMemberUuid,this.recommendationNumber,this.viewCount,this.title,this.content,this.isDeleted);
        }
    }

}
