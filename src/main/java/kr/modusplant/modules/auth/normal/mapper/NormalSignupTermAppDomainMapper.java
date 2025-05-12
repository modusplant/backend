package kr.modusplant.modules.auth.normal.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalSignupTermAppDomainMapper {

    @Mapping(target = "uuid", source = "memberResponse.uuid")
    SiteMemberTermInsertRequest toSiteMemberTermInsertRequest(NormalSignUpRequest request, SiteMemberResponse memberResponse);
}
