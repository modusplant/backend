package kr.modusplant.global.util;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;

public abstract class MapperUtils {
    public static SiteMemberEntity map(SiteMember siteMember, SiteMemberEntity siteMemberEntity) {
        siteMemberEntity.setNickname(siteMember.getNickname());
        siteMemberEntity.setBirthDate(siteMember.getBirthDate());
        siteMemberEntity.setIsActive(siteMember.getIsActive());
        siteMemberEntity.setIsDisabledByLinking(siteMember.getIsDisabledByLinking());
        siteMemberEntity.setIsBanned(siteMember.getIsBanned());
        siteMemberEntity.setIsDeleted(siteMember.getIsDeleted());
        siteMemberEntity.setLoggedInAt(siteMember.getLoggedInAt());
        return siteMemberEntity;
    }

    public static SiteMemberAuthEntity map(SiteMemberAuth memberTerm, SiteMemberAuthEntity memberAuthEntity, SiteMemberJpaRepository memberRepository) {
        memberAuthEntity.setActiveMember(memberRepository.findByUuid(memberTerm.getActiveMemberUuid())
                .orElseThrow(() -> new EntityNotFoundWithUuidException(memberTerm.getUuid(), SiteMemberEntity.class)));
        memberAuthEntity.setOriginalMember(memberRepository.findByUuid(memberTerm.getOriginalMemberUuid())
                .orElseThrow(() -> new EntityNotFoundWithUuidException(memberTerm.getUuid(), SiteMemberEntity.class)));
        memberAuthEntity.setEmail(memberTerm.getEmail());
        memberAuthEntity.setPw(memberTerm.getPw());
        memberAuthEntity.setProvider(memberTerm.getProvider());
        memberAuthEntity.setProviderId(memberTerm.getProviderId());
        memberAuthEntity.setFailedAttempt(memberTerm.getFailedAttempt());
        memberAuthEntity.setLockoutRefreshAt(memberTerm.getLockoutRefreshAt());
        memberAuthEntity.setLockoutUntil(memberTerm.getLockoutUntil());
        return memberAuthEntity;
    }

    public static SiteMemberTermEntity map(SiteMemberTerm memberAuth, SiteMemberTermEntity memberAuthEntity) {
        memberAuthEntity.setAgreedTermsOfUseVersion(memberAuth.getAgreedTermsOfUseVersion());
        memberAuthEntity.setAgreedPrivacyPolicyVersion(memberAuth.getAgreedPrivacyPolicyVersion());
        memberAuthEntity.setAgreedAdInfoReceivingVersion(memberAuth.getAgreedAdInfoReceivingVersion());
        return memberAuthEntity;
    }

    public static TermEntity map(Term term, TermEntity termEntity) {
        termEntity.setName(term.getName());
        termEntity.setContent(term.getContent());
        termEntity.setVersion(term.getVersion());
        return termEntity;
    }
}
