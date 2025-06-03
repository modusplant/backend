package kr.modusplant.domains.communication.conversation.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class ConvCategory {
    private final UUID uuid;

    private final String category;

    private final Integer order;

    public static class ConvCategoryBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public ConvCategoryBuilder convCategory(ConvCategory convCategory) {
            this.uuid = convCategory.getUuid();
            this.category = convCategory.getCategory();
            this.order = convCategory.getOrder();
            return this;
        }

        public ConvCategory build() {
            return new ConvCategory(this.uuid, this.category, this.order);
        }
    }
}
