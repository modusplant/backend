package kr.modusplant.global.error;

import kr.modusplant.domain.exception.enums.ErrorCode;
import lombok.Getter;

/**
 * {@code BusinessException}은 모든 커스텀 예외의 상위 클래스입니다.
 *
 * <p>커스텀 예외를 {@link ErrorCode}와 대응시키는 역할을 합니다.
 * 모든 커스텀 예외는 반드시 {@code BusinessException}을 상속받거나
 * 해당 클래스의 서브 클래스를 상속받아야 합니다.</p>
 *
 * <p>해당 클래스의 서브 클래스들은 {@link kr.modusplant.global.advice.GlobalExceptionHandler}에
 * 예외를 처리하는 메서드를 구현할 필요가 없습니다.</p>
 *
 * @author Jun Hee
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
