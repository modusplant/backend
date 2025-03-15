package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberTermJpaRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import static kr.modusplant.global.util.MapperUtils.map;

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
    default SiteMemberTermEntity updateSiteMemberTermEntity(SiteMemberTerm memberTerm,
                                                            @Context SiteMemberTermJpaRepository memberTermRepository) {
        return map(memberTerm, memberTermRepository.findByUuid(memberTerm.getUuid())
                .orElseThrow(() -> new EntityNotFoundWithUuidException(memberTerm.getUuid(), SiteMemberTermEntity.class)));
    }

    SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity memberTermEntity);
}
