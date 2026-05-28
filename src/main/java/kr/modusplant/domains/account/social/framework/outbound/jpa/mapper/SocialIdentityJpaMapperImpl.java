package kr.modusplant.domains.account.social.framework.outbound.jpa.mapper;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.framework.outbound.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SocialIdentityJpaMapperImpl implements SocialIdentityJpaMapper {

    @Override
    public MemberEntity toMemberEntity(Nickname nickname, Role role) {
        return MemberEntity.builder()
                .nickname(nickname.getValue())
                .loggedInAt(LocalDateTime.now())
                .role(role)
                .build();
    }

    @Override
    public MemberAuthEntity toMemberAuthEntity(MemberEntity memberEntity, SocialCredentials socialCredentials, Email email) {
        return MemberAuthEntity.builder()
                .member(memberEntity)
                .email(email.getValue())
                .provider(socialCredentials.getProvider())
                .providerId(socialCredentials.getProviderId())
                .build();
    }

    @Override
    public MemberTermEntity toMemberTermEntity(MemberEntity memberEntity, AgreedTerms agreedTerms) {
        return MemberTermEntity.builder()
                .member(memberEntity)
                .agreedTermsOfUseVersion(agreedTerms.getAgreedTermsOfUseVersion().getValue())
                .agreedPrivacyPolicyVersion(agreedTerms.getAgreedPrivacyPolicyVersion().getValue())
                .agreedCommunityPolicyVersion(agreedTerms.getAgreedCommunityPolicyVersion().getValue())
                .build();
    }

    @Override
    public MemberProfileEntity toMemberProfileEntity(MemberEntity memberEntity, String introduction) {
        return MemberProfileEntity.builder()
                .member(memberEntity)
                .introduction(introduction)
                .build();
    }

    @Override
    public SocialMemberProfile toSocialMemberProfile(MemberEntity memberEntity, MemberAuthEntity memberAuthEntity) {
        return SocialMemberProfile.create(
                AccountId.fromUuid(memberEntity.getUuid()),
                SocialCredentials.create(memberAuthEntity.getProvider(), memberAuthEntity.getProviderId()),
                Email.create(memberAuthEntity.getEmail()),
                Nickname.create(memberEntity.getNickname()),
                memberEntity.getRole()
        );
    }
}
