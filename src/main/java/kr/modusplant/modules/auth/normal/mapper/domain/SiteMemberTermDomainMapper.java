package kr.modusplant.modules.auth.normal.mapper.domain;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SiteMemberTermDomainMapper {

    @Mapping(target = "uuid", source = "siteMember.uuid")
    SiteMemberTerm toSiteMemberTerm(NormalSignUpRequest request, SiteMember siteMember);
}
