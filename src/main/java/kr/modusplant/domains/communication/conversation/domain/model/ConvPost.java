package kr.modusplant.domains.communication.conversation.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class ConvPost {
    private final String ulid;

    private final Integer groupOrder;

    private final UUID authMemberUuid;

    private final UUID createMemberUuid;

    private final Integer likeCount;

    private final Long viewCount;

    private final String title;

    private final JsonNode content;

    private final Boolean isDeleted;

    public static class ConvPostBuilder {
        private String ulid;
        private Integer groupOrder;
        private UUID authMemberUuid;
        private UUID createMemberUuid;
        private Integer likeCount;
        private Long viewCount;
        private String title;
        private JsonNode content;
        private Boolean isDeleted;

        public ConvPostBuilder convPost(ConvPost convPost) {
            this.ulid = convPost.ulid;
            this.groupOrder = convPost.groupOrder;
            this.authMemberUuid = convPost.authMemberUuid;
            this.createMemberUuid = convPost.createMemberUuid;
            this.likeCount = convPost.likeCount;
            this.viewCount = convPost.viewCount;
            this.title = convPost.title;
            this.content = convPost.content;
            this.isDeleted = convPost.isDeleted;
            return this;
        }

        public ConvPost build() {
            return new ConvPost(this.ulid,this.groupOrder,this.authMemberUuid,this.createMemberUuid,this.likeCount,this.viewCount,this.title,this.content,this.isDeleted);
        }
    }

}
