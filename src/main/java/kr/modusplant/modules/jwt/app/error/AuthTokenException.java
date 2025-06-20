package kr.modusplant.modules.jwt.error;

import kr.modusplant.global.error.DomainException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthTokenException extends DomainException {
    protected AuthTokenException(HttpStatus status, String code, String message) {
        super(status, code, message);
    }
}
