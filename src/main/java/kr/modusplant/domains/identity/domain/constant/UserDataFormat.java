package kr.modusplant.domains.identity.domain.constant;

public final class UserDataFormat {
    public static final String EMAIL_FORMAT = "^(?=.{2,255}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD_FORMAT = "^(?=\\S*[a-zA-Z])(?=\\S*[!@#$%^&*+\\-=_])(?=\\S*[0-9])\\S{8,64}$";
    public static final String NICKNAME_FORMAT = "^[0-9a-zA-Z가-힣]{2,10}$";
}
