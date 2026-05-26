package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalBugReportEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface ProposalBugReportJpaRepository extends
        CreatedAtRepository<ProposalBugReportEntity>,
        UlidPrimaryRepository<ProposalBugReportEntity>,
        JpaRepository<ProposalBugReportEntity, UUID> {
}