package kr.modusplant.modules.jwt.app.http.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse (@JsonProperty("access_token") String accessToken){
}
