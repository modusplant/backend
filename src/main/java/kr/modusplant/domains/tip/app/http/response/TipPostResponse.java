package kr.modusplant.domains.tip.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TipPostResponse {
    private String ulid;
    @JsonProperty("group_order")
    private Integer groupOrder;
    private String category;
    @JsonProperty("member_uuid")
    private UUID authMemberUuid;
    private String nickname;
    @JsonProperty("recommendation_number")
    private Integer recommendationNumber;
    @JsonProperty("view_count")
    private Long viewCount;
    private String title;
    private JsonNode content;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
