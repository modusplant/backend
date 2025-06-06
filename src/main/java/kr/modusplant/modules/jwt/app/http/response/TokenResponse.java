package kr.modusplant.modules.jwt.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_ACCESS_TOKEN;

public record TokenResponse (@JsonProperty(SNAKE_ACCESS_TOKEN) String accessToken){
}
