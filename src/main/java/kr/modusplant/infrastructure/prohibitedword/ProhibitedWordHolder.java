package kr.modusplant.infrastructure.prohibitedword;

import kr.modusplant.infrastructure.normalization.TextNormalizationType;
import kr.modusplant.shared.kernel.inspector.ProhibitedWordInspector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ProhibitedWordHolder implements ProhibitedWordInspector {
    private final ProhibitedWordEngine prohibitedWordEngine;

    public String maskProhibitedWord(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        boolean[] maskedCodeUnits = new boolean[text.length()];
        for (ProhibitedWordMatch prohibitedWordMatch : prohibitedWordEngine.match(text)) {
            if (prohibitedWordMatch.textNormalizationType() == TextNormalizationType.LINE_INITIAL) {
                continue;
            }
            Arrays.fill(maskedCodeUnits, prohibitedWordMatch.originalStart(), prohibitedWordMatch.originalEnd(), true);
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
    public boolean containsProhibitedWord(String text) {
        return !prohibitedWordEngine.match(text).isEmpty();
    }
}
