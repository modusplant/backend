package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface IdentityAuthJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "originalMember", source = "savedMember")
    @Mapping(target = "activeMember", source = "savedMember")
    @Mapping(target = "email", source = "sign.credentials.email.email")
    @Mapping(target = "pw", source = "sign.credentials.password.password")
    @Mapping(target = "provider", expression = "java( kr.modusplant.legacy.domains.member.enums.AuthProvider.BASIC )")
    SiteMemberAuthEntity toSiteMemberAuthEntity(SiteMemberEntity savedMember, SignUpData sign);

//    @Named("mapMember")
//    default SiteMemberEntity mapMember(UUID memberUuid) {
//        return SiteMemberEntity.builder()
//                .uuid(memberUuid)
//                .build();
//    }

    // TODO: 현재 사용중이지 않음. 매핑 잘 되면 mapPw와 함게 삭제할 것.
    @Named("mapEmail")
    default String mapEmail(SignUpData sign) {
        return sign.getCredentials().getEmail().getEmail();
    }

    @Named("mapPw")
    default String mapPw(SignUpData sign) {
        return sign.getCredentials().getPassword().getPassword();
    }
}
