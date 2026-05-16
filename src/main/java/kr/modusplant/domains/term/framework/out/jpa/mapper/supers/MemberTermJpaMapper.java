package kr.modusplant.domains.term.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.framework.jpa.entity.MemberTermEntity;

public interface MemberTermJpaMapper {
    MemberTermEntity toMemberTermNewEntity(SiteMemberTerm siteMemberTerm);

    SiteMemberTerm toSiteMemberTerm(MemberTermEntity entity);
}
