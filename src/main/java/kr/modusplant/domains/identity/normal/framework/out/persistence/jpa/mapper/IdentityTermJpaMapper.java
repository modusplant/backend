package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface IdentityTermJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "member", source = "savedMember")
    @Mapping(target = "agreedTermsOfUseVersion", source = "sign.agreedTermsOfUseVersion.version")
    @Mapping(target = "agreedPrivacyPolicyVersion", source = "sign.agreedPrivacyPolicyVersion.version")
    @Mapping(target = "agreedAdInfoReceivingVersion", source = "sign.agreedAdInfoReceivingVersion.version")
    SiteMemberTermEntity toSiteMemberTermEntity(SiteMemberEntity savedMember, SignUpData sign);

    @Named("mapMember")
    default SiteMemberEntity mapMember(UUID memberUuid) {
        return SiteMemberEntity.builder()
                .uuid(memberUuid)
                .build();
    }
}
