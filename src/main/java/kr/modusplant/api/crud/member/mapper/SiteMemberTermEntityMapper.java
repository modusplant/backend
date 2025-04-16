package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.domain.model.SiteMemberTerm;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;
import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_TERM;

@Mapper
public interface SiteMemberTermEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity createSiteMemberTermEntity(SiteMemberTerm memberTerm, @Context SiteMemberCrudJpaRepository memberRepository) {
        return SiteMemberTermEntity.builder()
                .member(memberRepository.findByUuid(memberTerm.getUuid()).orElseThrow())
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity updateSiteMemberTermEntity(SiteMemberTerm memberTerm, @Context SiteMemberCrudJpaRepository memberRepository) {
        return SiteMemberTermEntity.builder()
                .member(memberRepository.findByUuid(memberTerm.getUuid()).orElseThrow())
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    @Mapping(source = MEMBER, target = "uuid", qualifiedByName = "toUuid")
    @Mapping(target = MEMBER_TERM, ignore = true)
    SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity memberTermEntity);

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity member) {
        return member.getUuid();
    }
}
