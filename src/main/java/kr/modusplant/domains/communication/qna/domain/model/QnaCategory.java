package kr.modusplant.domains.communication.qna.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class QnaCategory {
    private final UUID uuid;

    private final String category;

    private final Integer order;

    public static class QnaCategoryBuilder {
        private UUID uuid;
        private String category;
        private Integer order;

        public QnaCategoryBuilder qnaCategory(QnaCategory qnaCategory) {
            this.uuid = qnaCategory.getUuid();
            this.category = qnaCategory.getCategory();
            this.order = qnaCategory.getOrder();
            return this;
        }

        public QnaCategory build() {
            return new QnaCategory(this.uuid, this.category, this.order);
        }
    }
}
