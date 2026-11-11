package kr.modusplant.infrastructure.swear;

import kr.modusplant.shared.enums.SwearType;

// 비속어 사전 단어, 유형, 심각도, 문자 체계
public record SwearWord(String word, SwearType type, int severity, SwearScript script) {
}
