package kr.modusplant.domains.communication.tip.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Getter
@Setter
public class TipPostResponse {
    private String ulid;

    @JsonProperty(CATEGORY)
    private String category;

    @JsonProperty(SNAKE_CATE_UUID)
    private UUID categoryUuid;

    @JsonProperty(SNAKE_CATE_ORDER)
    private Integer categoryOrder;

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
