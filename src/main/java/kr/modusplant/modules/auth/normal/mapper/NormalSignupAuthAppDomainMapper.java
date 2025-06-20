package kr.modusplant.modules.auth.normal.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.modules.auth.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.Mapper;

@Mapper
public interface NormalSignupAuthAppDomainMapper {

    default SiteMemberAuthInsertRequest toSiteMemberAuthInsertRequest(NormalSignUpRequest request, SiteMemberResponse memberResponse) {
        return new SiteMemberAuthInsertRequest(memberResponse.uuid(), request.email(), request.pw(), AuthProvider.BASIC, null);
    }
}
