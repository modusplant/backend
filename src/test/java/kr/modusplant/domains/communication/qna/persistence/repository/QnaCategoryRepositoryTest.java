package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class QnaCategoryRepositoryTest implements QnaCategoryEntityTestUtils {

    private final QnaCategoryRepository qnaCategoryRepository;

    @Autowired
    QnaCategoryRepositoryTest(QnaCategoryRepository qnaCategoryRepository) {
        this.qnaCategoryRepository = qnaCategoryRepository;
    }

    @DisplayName("UUID로 Q&A 항목 찾기")
    @Test
    void findByUuidTest() {
        // given & when
        QnaCategoryEntity entity = qnaCategoryRepository.save(createTestQnaCategoryEntity());

        // then
        assertThat(qnaCategoryRepository.findByUuid(entity.getUuid()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("category로 Q&A 항목 찾기")
    @Test
    void findByCategoryTest() {
        // given & when
        QnaCategoryEntity entity = qnaCategoryRepository.save(createTestQnaCategoryEntity());

        // then
        assertThat(qnaCategoryRepository.findByCategory(entity.getCategory()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("order로 Q&A 항목 찾기")
    @Test
    void findByOrderTest() {
        // given & when
        QnaCategoryEntity entity = qnaCategoryRepository.save(createTestQnaCategoryEntity());

        // then
        assertThat(qnaCategoryRepository.findByOrder(entity.getOrder()).orElseThrow()).isEqualTo(entity);
    }

    @DisplayName("createdAt으로 Q&A 항목 찾기")
    @Test
    void findByCreatedAtTest() {
        // given & when
        QnaCategoryEntity entity = qnaCategoryRepository.save(createTestQnaCategoryEntity());

        // then
        assertThat(qnaCategoryRepository.findByCreatedAt(entity.getCreatedAt()).getFirst()).isEqualTo(entity);
    }

    @DisplayName("UUID로 Q&A 항목 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        QnaCategoryEntity entity = qnaCategoryRepository.save(createTestQnaCategoryEntity());
        UUID uuid = entity.getUuid();

        // when
        qnaCategoryRepository.deleteByUuid(uuid);

        // then
        assertThat(qnaCategoryRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("UUID로 Q&A 항목 확인")
    @Test
    void existsByUuidTest() {
        // given & when
        QnaCategoryEntity entity = qnaCategoryRepository.save(createTestQnaCategoryEntity());

        // then
        assertThat(qnaCategoryRepository.existsByUuid(entity.getUuid())).isEqualTo(true);
    }
}