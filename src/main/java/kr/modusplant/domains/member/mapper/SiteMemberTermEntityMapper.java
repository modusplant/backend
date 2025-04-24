package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;
import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_TERM;

@Mapper
public interface SiteMemberTermEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity createSiteMemberTermEntity(SiteMemberTerm memberTerm, @Context SiteMemberRepository memberRepository) {
        return SiteMemberTermEntity.builder()
                .member(memberRepository.findByUuid(memberTerm.getUuid()).orElseThrow())
                .agreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default SiteMemberTermEntity updateSiteMemberTermEntity(SiteMemberTerm memberTerm, @Context SiteMemberRepository memberRepository) {
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
