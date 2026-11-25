package kr.modusplant.infrastructure.prohibitedword;

import kr.modusplant.infrastructure.normalization.TextNormalizationType;

public record ProhibitedWordMatch(ProhibitedWord prohibitedWord, TextNormalizationType textNormalizationType, int originalStart, int originalEnd) {
}
