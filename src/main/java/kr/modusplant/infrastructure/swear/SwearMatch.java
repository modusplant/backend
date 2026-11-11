package kr.modusplant.infrastructure.swear;

import kr.modusplant.infrastructure.normalization.TextNormalizationType;

// 비속어로서 매칭된 사전 단어, 매칭된 정규화 뷰, 원본 텍스트에서의 반열린 구간 [originalStart, originalEnd)
public record SwearMatch(SwearWord swearWord, TextNormalizationType textNormalizationType, int originalStart, int originalEnd) {
}
