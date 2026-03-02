package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PropBugRepEntity;
import kr.modusplant.framework.jpa.entity.common.util.PropBugRepEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class PropBugRepJpaRepositoryTest implements PropBugRepEntityTestUtils {

    private final PropBugRepJpaRepository proposalOrBugReportRepository;

    @Autowired
    PropBugRepJpaRepositoryTest(PropBugRepJpaRepository proposalOrBugReportRepository) {
        this.proposalOrBugReportRepository = proposalOrBugReportRepository;
    }

    @DisplayName("ulid로 보고서 찾기")
    @Test
    void findByUlidTest() {
        // given
        PropBugRepEntity proposalOrBugReport = createPropBugRepEntityBuilder().member(createMemberBasicUserEntity()).build();

        // when
        proposalOrBugReportRepository.save(proposalOrBugReport);

        // then
        assertThat(proposalOrBugReportRepository.findByUlid(proposalOrBugReport.getUlid()).orElseThrow()).isEqualTo(proposalOrBugReport);
    }

    @DisplayName("createdAt으로 보고서 찾기")
    @Test
    void findByCreatedAtTest() {
        // given
        PropBugRepEntity proposalOrBugReport = createPropBugRepEntityBuilder().member(createMemberBasicUserEntity()).build();

        // when
        proposalOrBugReportRepository.save(proposalOrBugReport);

        // then
        assertThat(proposalOrBugReportRepository.findByCreatedAt(proposalOrBugReport.getCreatedAt()).getFirst()).isEqualTo(proposalOrBugReport);
    }

    @DisplayName("lastModifiedAt으로 보고서 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        PropBugRepEntity proposalOrBugReport = createPropBugRepEntityBuilder().member(createMemberBasicUserEntity()).build();

        // when
        proposalOrBugReportRepository.save(proposalOrBugReport);

        // then
        assertThat(proposalOrBugReportRepository.findByLastModifiedAt(proposalOrBugReport.getLastModifiedAt()).getFirst()).isEqualTo(proposalOrBugReport);
    }

    @DisplayName("ulid로 보고서 삭제")
    @Test
    void deleteByUlidTest() {
        // given
        PropBugRepEntity proposalOrBugReport = proposalOrBugReportRepository.save(createPropBugRepEntityBuilder().member(createMemberBasicUserEntity()).build());
        String ulid = proposalOrBugReport.getUlid();

        // when
        proposalOrBugReportRepository.deleteByUlid(ulid);

        // then
        assertThat(proposalOrBugReportRepository.findByUlid(ulid)).isEmpty();
    }

    @DisplayName("ulid로 보고서 확인")
    @Test
    void existsByUlidTest() {
        // given
        PropBugRepEntity proposalOrBugReport = createPropBugRepEntityBuilder().member(createMemberBasicUserEntity()).build();

        // when
        proposalOrBugReportRepository.save(proposalOrBugReport);

        // then
        assertThat(proposalOrBugReportRepository.existsByUlid(proposalOrBugReport.getUlid())).isEqualTo(true);
    }

    @DisplayName("보고서 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenPropBugRepEntity_willReturnRepresentative() {
        // given
        PropBugRepEntity proposalOrBugReport = createPropBugRepEntityBuilder().member(createMemberBasicUserEntity()).build();

        // when
        PropBugRepEntity proposalOrBugReportEntity = proposalOrBugReportRepository.save(proposalOrBugReport);

        // then
        assertDoesNotThrow(proposalOrBugReportEntity::toString);
    }
}