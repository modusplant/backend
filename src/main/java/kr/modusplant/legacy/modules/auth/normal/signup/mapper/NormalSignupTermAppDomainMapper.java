package kr.modusplant.legacy.modules.auth.normal.signup.mapper;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalSignupTermAppDomainMapper {

    @Mapping(target = "uuid", source = "memberResponse.uuid")
    SiteMemberTermInsertRequest toSiteMemberTermInsertRequest(NormalSignUpRequest request, SiteMemberResponse memberResponse);
}
