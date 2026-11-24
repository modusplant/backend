package kr.modusplant.shared.kernel.validator;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.inspector.SwearInspector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameValidator {
    private final SwearInspector swearInspector;

    public void validate(Nickname nickname) {
        if (swearInspector.containsSwear(nickname.getValue())) {
            throw new InvalidValueException(GeneralErrorCode.SWEAR_CONTAINED_NICKNAME, "nickname");
        }
    }
}
