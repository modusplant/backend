package kr.modusplant.infrastructure.normalization.view;

import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;

// TextNormalizer.normalizeWithKeystroke가 만든 키 입력 정규화 뷰 (TextNormalizationType.KEYSTROKE)
public final class KeystrokeNormalizedView extends TextNormalizedView {
    public KeystrokeNormalizedView(TextNormalizedView source) {
        super(source);
    }
}
