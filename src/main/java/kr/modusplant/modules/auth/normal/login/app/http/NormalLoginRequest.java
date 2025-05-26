package kr.modusplant.modules.auth.normal.login.app.http;

public record NormalLoginRequest(
        String email,
        String password,
        String deviceID
) {
    public boolean checkFieldValidation() {
        return email != null && password != null && deviceID != null;
    }
}
