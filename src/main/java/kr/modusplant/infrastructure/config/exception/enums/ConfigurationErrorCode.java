package kr.modusplant.infrastructure.config.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConfigurationErrorCode implements ErrorCode {

    INCORRECT_RELATIONSHIP_BETWEEN_CONNECTION_SIZE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "incorrect_relationship_between_connection_size", "커넥션 크기 간 연관관계가 올바르지 않습니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
