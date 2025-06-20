package kr.modusplant.modules.auth.normal.login.app.http.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record NormalLoginRequest(
        String email,
        String password) {
    @JsonIgnore
    public boolean isEmailOrPasswordNull() {
        return email == null || password == null;
    }
}
