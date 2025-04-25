package kr.modusplant.modules.signup.normal.mapper.domain;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.modules.signup.normal.app.http.request.NormalSignUpRequest;
import org.mapstruct.*;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SiteMemberDomainMapper {

    SiteMember toSiteMember(NormalSignUpRequest request);

}

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
//    default SiteMember toSiteMember(NormalSignUpRequest request) {
//        return SiteMember.builder()
//                .nickname(request.nickname())
//                .build();
//    }