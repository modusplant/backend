package kr.modusplant.infrastructure.normalization.view;

import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;

// TextNormalizer.normalizeAggressively가 만든 강화 정규화 뷰 (TextNormalizationType.AGGRESSIVE)
public final class AggressivelyNormalizedView extends TextNormalizedView {
    public AggressivelyNormalizedView(TextNormalizedView source) {
        super(source);
    }
}
