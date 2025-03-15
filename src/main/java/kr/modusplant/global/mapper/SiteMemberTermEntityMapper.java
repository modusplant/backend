package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper
public interface SiteMemberTermEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity createSiteMemberTermEntity(SiteMemberTerm memberTerm) {
        return SiteMemberTermEntity.builder()
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity updateSiteMemberTermEntity(SiteMemberTerm memberTerm) {
        return SiteMemberTermEntity.builder()
                .uuid(memberTerm.getUuid())
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity memberTermEntity);
}
