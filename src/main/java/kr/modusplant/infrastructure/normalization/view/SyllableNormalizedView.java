package kr.modusplant.infrastructure.normalization.view;

import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;

// TextNormalizer.normalizeWithSyllable이 만든 음절 정규화 뷰 (TextNormalizationType.SYLLABLE)
public final class SyllableNormalizedView extends TextNormalizedView {
    public SyllableNormalizedView(TextNormalizedView source) {
        super(source);
    }
}
