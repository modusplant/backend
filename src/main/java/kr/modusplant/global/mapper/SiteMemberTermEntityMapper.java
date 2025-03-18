package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface SiteMemberTermEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity createSiteMemberTermEntity(SiteMemberTerm memberTerm, @Context SiteMemberJpaRepository memberRepository) {
        return SiteMemberTermEntity.builder()
                .member(memberRepository.findByUuid(memberTerm.getUuid()).orElseThrow())
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity updateSiteMemberTermEntity(SiteMemberTerm memberTerm, @Context SiteMemberJpaRepository memberRepository) {
        return SiteMemberTermEntity.builder()
                .member(memberRepository.findByUuid(memberTerm.getUuid()).orElseThrow())
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity memberTermEntity);
}
