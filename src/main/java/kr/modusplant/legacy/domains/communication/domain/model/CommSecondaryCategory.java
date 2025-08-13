package kr.modusplant.legacy.domains.communication.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class CommSecondaryCategory {
    private final UUID uuid;

    private final String category;

    private final Integer order;

    public static class CommCategoryBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public CommCategoryBuilder commCategory(CommSecondaryCategory commSecondaryCategory) {
            this.uuid = commSecondaryCategory.getUuid();
            this.category = commSecondaryCategory.getCategory();
            this.order = commSecondaryCategory.getOrder();
            return this;
        }

        public CommSecondaryCategory build() {
            return new CommSecondaryCategory(this.uuid, this.category, this.order);
        }
    }
}
