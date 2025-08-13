package kr.modusplant.legacy.domains.communication.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class CommPrimaryCategory {
    private final UUID uuid;

    private final String category;

    private final Integer order;

    public static class CommCategoryBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public CommCategoryBuilder commCategory(CommPrimaryCategory commSecondCategory) {
            this.uuid = commSecondCategory.getUuid();
            this.category = commSecondCategory.getCategory();
            this.order = commSecondCategory.getOrder();
            return this;
        }

        public CommPrimaryCategory build() {
            return new CommPrimaryCategory(this.uuid, this.category, this.order);
        }
    }
}
