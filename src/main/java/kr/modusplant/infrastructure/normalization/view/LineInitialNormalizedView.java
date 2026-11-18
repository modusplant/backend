package kr.modusplant.infrastructure.normalization.view;

import kr.modusplant.infrastructure.normalization.view.supers.TextNormalizedView;

// TextNormalizer.normalizeWithLineInitial이 만든 행 머리 정규화 뷰 (TextNormalizationType.LINE_INITIAL)
public final class LineInitialNormalizedView extends TextNormalizedView {
    public LineInitialNormalizedView(TextNormalizedView source) {
        super(source);
    }
}
