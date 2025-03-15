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
    public static SiteMemberEntity map(SiteMember member, SiteMemberEntity memberEntity) {
        memberEntity.setNickname(member.getNickname());
        memberEntity.setBirthDate(member.getBirthDate());
        memberEntity.setIsActive(member.getIsActive());
        memberEntity.setIsDisabledByLinking(member.getIsDisabledByLinking());
        memberEntity.setIsBanned(member.getIsBanned());
        memberEntity.setIsDeleted(member.getIsDeleted());
        memberEntity.setLoggedInAt(member.getLoggedInAt());
        return memberEntity;
    }

    public static SiteMemberAuthEntity map(SiteMemberAuth memberAuth, SiteMemberAuthEntity memberAuthEntity, SiteMemberJpaRepository memberRepository) {
        memberAuthEntity.setActiveMember(memberRepository.findByUuid(memberAuth.getActiveMemberUuid())
                .orElseThrow(() -> new EntityNotFoundWithUuidException(memberAuth.getUuid(), SiteMemberEntity.class)));
        memberAuthEntity.setOriginalMember(memberRepository.findByUuid(memberAuth.getOriginalMemberUuid())
                .orElseThrow(() -> new EntityNotFoundWithUuidException(memberAuth.getUuid(), SiteMemberEntity.class)));
        memberAuthEntity.setEmail(memberAuth.getEmail());
        memberAuthEntity.setPw(memberAuth.getPw());
        memberAuthEntity.setProvider(memberAuth.getProvider());
        memberAuthEntity.setProviderId(memberAuth.getProviderId());
        memberAuthEntity.setFailedAttempt(memberAuth.getFailedAttempt());
        memberAuthEntity.setLockoutRefreshAt(memberAuth.getLockoutRefreshAt());
        memberAuthEntity.setLockoutUntil(memberAuth.getLockoutUntil());
        return memberAuthEntity;
    }

    public static SiteMemberTermEntity map(SiteMemberTerm memberTerm, SiteMemberTermEntity memberTermEntity) {
        memberTermEntity.setAgreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion());
        memberTermEntity.setAgreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion());
        memberTermEntity.setAgreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion());
        return memberTermEntity;
    }

    public static TermEntity map(Term term, TermEntity termEntity) {
        termEntity.setName(term.getName());
        termEntity.setContent(term.getContent());
        termEntity.setVersion(term.getVersion());
        return termEntity;
    }
}
