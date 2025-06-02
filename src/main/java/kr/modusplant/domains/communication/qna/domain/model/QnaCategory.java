package kr.modusplant.domains.communication.qna.domain.model;

import lombok.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class QnaCategory {
    private final Integer order;

    private final String category;
    
    public static class QnaCategoryBuilder {
        private Integer order;
        private String category;

        public QnaCategoryBuilder qnaCategory(QnaCategory qnaCategory) {
            this.order = qnaCategory.getOrder();
            this.category = qnaCategory.getCategory();
            return this;
        }

        public QnaCategory build() {
            return new QnaCategory(this.order, this.category);
        }
    }
}
