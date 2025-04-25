package kr.modusplant.modules.signup.normal.mapper.domain;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.modules.signup.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.*;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SiteMemberAuthDomainMapper {

    default SiteMemberAuth toSiteMemberAuth(NormalSignUpRequest request, SiteMember siteMember) {
        return SiteMemberAuth.builder()
                .uuid(siteMember.getUuid())
                .activeMemberUuid(siteMember.getUuid())
                .originalMemberUuid(siteMember.getUuid())
                .email(request.email())
                .pw(request.pw())
                .provider(AuthProvider.BASIC)
                .build();
    }
}
