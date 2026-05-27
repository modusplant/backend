package kr.modusplant.domains.term.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;

public interface MemberTermJpaMapper {
    MemberTermEntity toMemberTermNewEntity(SiteMemberTerm siteMemberTerm);

    SiteMemberTerm toSiteMemberTerm(MemberTermEntity entity);
}
