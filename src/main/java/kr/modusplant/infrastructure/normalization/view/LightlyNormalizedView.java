package kr.modusplant.infrastructure.normalization.view;

import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;

// TextNormalizer.normalizeLightly가 만든 경량 정규화 뷰 (TextNormalizationType.LIGHT)
public final class LightlyNormalizedView extends TextNormalizedView {
    public LightlyNormalizedView(TextNormalizedView source) {
        super(source);
    }
}
