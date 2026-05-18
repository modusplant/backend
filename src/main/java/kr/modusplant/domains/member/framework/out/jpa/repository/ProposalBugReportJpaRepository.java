package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.ProposalBugReportEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface ProposalBugReportJpaRepository extends
        CreatedAtAndLastModifiedAtRepository<ProposalBugReportEntity>,
        UlidPrimaryRepository<ProposalBugReportEntity>,
        JpaRepository<ProposalBugReportEntity, UUID> {
}