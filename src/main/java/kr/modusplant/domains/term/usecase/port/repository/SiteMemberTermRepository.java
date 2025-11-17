package kr.modusplant.domains.term.usecase.port.repository;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;

import java.util.List;
import java.util.Optional;

public interface SiteMemberTermRepository {
    SiteMemberTerm save(SiteMemberTerm siteMemberTerm);

    Optional<SiteMemberTerm> findById(SiteMemberTermId siteMemberTermId);

    List<SiteMemberTerm> findAll();

    boolean isIdExist(SiteMemberTermId siteMemberTermId);

    void deleteById(SiteMemberTermId siteMemberTermId);
}
