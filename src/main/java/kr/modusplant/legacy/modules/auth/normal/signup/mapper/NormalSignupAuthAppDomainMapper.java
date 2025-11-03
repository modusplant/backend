package kr.modusplant.legacy.modules.auth.normal.signup.mapper;

import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import kr.modusplant.shared.enums.AuthProvider;
import org.mapstruct.Mapper;

@Mapper
public interface NormalSignupAuthAppDomainMapper {

    default SiteMemberAuthInsertRequest toSiteMemberAuthInsertRequest(NormalSignUpRequest request, SiteMemberResponse memberResponse) {
        return new SiteMemberAuthInsertRequest(memberResponse.uuid(), request.email(), request.pw(), AuthProvider.BASIC, null);
    }
}
