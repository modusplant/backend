package kr.modusplant.domains.communication.tip.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class TipCategory {
    private final UUID uuid;

    private final String category;

    private final Integer order;

    public static class TipCategoryBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public TipCategoryBuilder tipCategory(TipCategory tipCategory) {
            this.uuid = tipCategory.getUuid();
            this.category = tipCategory.getCategory();
            this.order = tipCategory.getOrder();
            return this;
        }

        public TipCategory build() {
            return new TipCategory(this.uuid, this.category, this.order);
        }
    }
}
