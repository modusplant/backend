package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.ProposalOrBugReportEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface ProposalOrBugReportJpaRepository extends
        CreatedAtRepository<ProposalOrBugReportEntity>,
        UlidPrimaryRepository<ProposalOrBugReportEntity>,
        JpaRepository<ProposalOrBugReportEntity, UUID> {
}