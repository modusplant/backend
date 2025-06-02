package kr.modusplant.domains.communication.conversation.domain.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class ConvCategory {
    private final Integer order;

    private final String category;
    
    public static class ConvCategoryBuilder {
        private Integer order;
        private String category;

        public ConvCategoryBuilder convCategory(ConvCategory convCategory) {
            this.order = convCategory.getOrder();
            this.category = convCategory.getCategory();
            return this;
        }

        public ConvCategory build() {
            return new ConvCategory(this.order, this.category);
        }
    }
}
