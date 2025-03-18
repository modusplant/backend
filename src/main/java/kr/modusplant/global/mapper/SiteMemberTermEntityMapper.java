package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;

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

    @Mapping(source = MEMBER, target = "uuid", qualifiedByName = "toUuid")
    SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity memberTermEntity);

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity member) {
        return member.getUuid();
    }
}
