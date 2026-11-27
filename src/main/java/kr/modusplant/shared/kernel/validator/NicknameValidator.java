package kr.modusplant.shared.kernel.validator;

import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.inspector.ProhibitedWordInspector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NicknameValidator {
    private final ProhibitedWordInspector prohibitedWordInspector;

    public void validate(Nickname nickname) {
        if (prohibitedWordInspector.containsProhibitedWord(nickname.getValue())) {
            throw new InvalidValueException(GeneralErrorCode.PROHIBITED_WORD_CONTAINED_NICKNAME, "nickname");
        }
    }
}
