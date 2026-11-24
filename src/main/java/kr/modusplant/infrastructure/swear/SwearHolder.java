package kr.modusplant.infrastructure.swear;

import kr.modusplant.infrastructure.normalization.TextNormalizationType;
import kr.modusplant.shared.kernel.inspector.SwearInspector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class SwearHolder implements SwearInspector {
    private final SwearEngine swearEngine;

    public String maskSwear(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        boolean[] maskedCodeUnits = new boolean[text.length()];
        for (SwearMatch swearMatch : swearEngine.match(text)) {
            if (swearMatch.textNormalizationType() == TextNormalizationType.LINE_INITIAL) {
                continue;
            }
            Arrays.fill(maskedCodeUnits, swearMatch.originalStart(), swearMatch.originalEnd(), true);
        }
        StringBuilder maskedText = new StringBuilder(text.length());
        for (int codeUnitIndex = 0; codeUnitIndex < text.length(); codeUnitIndex++) {
            if (!maskedCodeUnits[codeUnitIndex]) {
                maskedText.append(text.charAt(codeUnitIndex));
            } else if (codeUnitIndex == 0 || !maskedCodeUnits[codeUnitIndex - 1]) {
                maskedText.append("***");
            }
        }
        return maskedText.toString();
    }

    @Override
    public boolean containsSwear(String text) {
        return !swearEngine.match(text).isEmpty();
    }
}
