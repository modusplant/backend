<<<<<<<< HEAD:src/main/java/kr/modusplant/modules/jwt/app/http/response/TokenResponse.java
package kr.modusplant.modules.jwt.app.http.response;
========
package kr.modusplant.modules.auth.social.model.response;
>>>>>>>> f2cf09f (MP-151 :truck: Rename: 소셜 로그인 파일 이동):src/main/java/kr/modusplant/modules/auth/social/model/response/TokenResponse.java

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
}
