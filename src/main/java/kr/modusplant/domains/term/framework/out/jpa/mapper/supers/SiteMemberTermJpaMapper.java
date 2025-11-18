package kr.modusplant.domains.term.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;

public interface SiteMemberTermJpaMapper {
    SiteMemberTermEntity toSiteMemberTermNewEntity(SiteMemberTerm siteMemberTerm);

    SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity entity);
}
