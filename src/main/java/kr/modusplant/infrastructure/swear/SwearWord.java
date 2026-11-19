package kr.modusplant.infrastructure.swear;

import kr.modusplant.shared.enums.SwearType;

public record SwearWord(String word, SwearType type, int severity, SwearScript script) {
}
