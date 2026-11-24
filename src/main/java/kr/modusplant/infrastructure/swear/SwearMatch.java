package kr.modusplant.infrastructure.swear;

import kr.modusplant.infrastructure.normalization.TextNormalizationType;

public record SwearMatch(SwearWord swearWord, TextNormalizationType textNormalizationType, int originalStart, int originalEnd) {
}
