package kr.modusplant.domains.communication.tip.domain.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class TipCategory {
    private final Integer order;

    private final String category;
    
    public static class TipCategoryBuilder {
        private Integer order;
        private String category;

        public TipCategoryBuilder tipCategory(TipCategory tipCategory) {
            this.order = tipCategory.getOrder();
            this.category = tipCategory.getCategory();
            return this;
        }

        public TipCategory build() {
            return new TipCategory(this.order, this.category);
        }
    }
}
