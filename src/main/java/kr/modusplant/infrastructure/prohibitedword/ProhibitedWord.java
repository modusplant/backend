package kr.modusplant.infrastructure.prohibitedword;

import kr.modusplant.shared.enums.ProhibitedWordType;

public record ProhibitedWord(String word, ProhibitedWordType type, int severity, ProhibitedWordScript script) {
}
