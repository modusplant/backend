package kr.modusplant.domains.group.domain.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class PlantGroup {
    private final Integer order;

    private final String category;

    public static class PlantGroupBuilder {
        private Integer order;
        private String category;

        public PlantGroupBuilder plantGroup(PlantGroup plantGroup) {
            this.order = plantGroup.getOrder();
            this.category = plantGroup.getCategory();
            return this;
        }

        public PlantGroup build() {
            return new PlantGroup(this.order, this.category);
        }
    }
}
