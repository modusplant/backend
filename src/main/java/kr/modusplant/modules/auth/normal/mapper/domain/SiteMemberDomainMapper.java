package kr.modusplant.modules.auth.normal.mapper.domain;

import kr.modusplant.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.*;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SiteMemberDomainMapper {

    SiteMember toSiteMember(NormalSignUpRequest request);

    SiteMemberInsertRequest toSiteMemberInsertRequest(NormalSignUpRequest request);

}