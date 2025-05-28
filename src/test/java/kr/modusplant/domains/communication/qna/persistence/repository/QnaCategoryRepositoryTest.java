package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class QnaCategoryRepositoryTest implements QnaCategoryEntityTestUtils {

    private final QnaCategoryRepository qnaCategoryRepository;

    @Autowired
    QnaCategoryRepositoryTest(QnaCategoryRepository qnaCategoryRepository) {
        this.qnaCategoryRepository = qnaCategoryRepository;
    }

    @DisplayName("order으로 팁 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        qnaCategoryRepository.save(qnaCategoryEntity);

        // then
        assertThat(qnaCategoryRepository.findByOrder(qnaCategory.getOrder()).orElseThrow()).isEqualTo(qnaCategoryEntity);
    }

    @DisplayName("category로 팁 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        qnaCategoryRepository.save(qnaCategoryEntity);

        // then
        assertThat(qnaCategoryRepository.findByCategory(qnaCategory.getCategory()).orElseThrow()).isEqualTo(qnaCategoryEntity);
    }

    @DisplayName("createdAt으로 팁 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        QnaCategoryEntity qnaCategory = qnaCategoryRepository.save(qnaCategoryEntity);

        // then
        assertThat(qnaCategoryRepository.findByCreatedAt(qnaCategory.getCreatedAt()).getFirst()).isEqualTo(qnaCategory);
    }

    @DisplayName("order로 팁 항목 삭제")
    @Test
    void deleteByOrderTest() {
        // given
        QnaCategoryEntity qnaCategory = qnaCategoryRepository.save(qnaCategoryEntity);
        Integer order = qnaCategory.getOrder();

        // when
        qnaCategoryRepository.deleteByOrder(order);

        // then
        assertThat(qnaCategoryRepository.findByOrder(order)).isEmpty();
    }

    @DisplayName("order로 팁 항목 확인")
    @Test
    void existsByOrderTest() {
        // given & when
        QnaCategoryEntity qnaCategory = qnaCategoryRepository.save(qnaCategoryEntity);

        // then
        assertThat(qnaCategoryRepository.existsByOrder(qnaCategory.getOrder())).isEqualTo(true);
    }
}