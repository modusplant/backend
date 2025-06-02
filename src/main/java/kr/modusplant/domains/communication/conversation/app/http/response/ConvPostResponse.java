package kr.modusplant.domains.communication.conversation.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Getter
@Setter
public class ConvPostResponse {
    private String ulid;
    @JsonProperty(SNAKE_GROUP_ORDER)
    private Integer groupOrder;
    private String category;
    private String nickname;
    @JsonProperty(SNAKE_LIKE_COUNT)
    private Integer likeCount;
    @JsonProperty(SNAKE_VIEW_COUNT)
    private Long viewCount;
    private String title;
    private JsonNode content;
    @JsonProperty(SNAKE_CREATED_AT)
    private LocalDateTime createdAt;
    @JsonProperty(SNAKE_UPDATED_AT)
    private LocalDateTime updatedAt;
}
