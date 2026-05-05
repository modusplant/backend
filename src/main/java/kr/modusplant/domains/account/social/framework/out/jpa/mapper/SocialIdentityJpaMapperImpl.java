package kr.modusplant.domains.account.social.framework.out.jpa.mapper;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SocialIdentityJpaMapperImpl implements SocialIdentityJpaMapper {

    @Override
    public SiteMemberEntity toMemberEntity(Nickname nickname, Role role) {
        return SiteMemberEntity.builder()
                .nickname(nickname.getValue())
                .loggedInAt(LocalDateTime.now())
                .role(role)
                .build();
    }

    @Override
    public SiteMemberAuthEntity toMemberAuthEntity(SiteMemberEntity memberEntity, SocialCredentials socialCredentials, Email email) {
        return SiteMemberAuthEntity.builder()
                .member(memberEntity)
                .email(email.getValue())
                .provider(socialCredentials.getProvider())
                .providerId(socialCredentials.getProviderId())
                .build();
    }

    @Override
    public SiteMemberTermEntity toMemberTermEntity(SiteMemberEntity memberEntity, AgreedTerms agreedTerms) {
        return SiteMemberTermEntity.builder()
                .member(memberEntity)
                .agreedTermsOfUseVersion(agreedTerms.getAgreedTermsOfUseVersion().getValue())
                .agreedPrivacyPolicyVersion(agreedTerms.getAgreedPrivacyPolicyVersion().getValue())
                .agreedCommunityPolicyVersion(agreedTerms.getAgreedCommunityPolicyVersion().getValue())
                .build();
    }

    @Override
    public SiteMemberProfileEntity toMemberProfileEntity(SiteMemberEntity memberEntity, String introduction) {
        return SiteMemberProfileEntity.builder()
                .member(memberEntity)
                .introduction(introduction)
                .build();
    }

    @Override
    public SocialMemberProfile toSocialMemberProfile(SiteMemberEntity memberEntity, SiteMemberAuthEntity memberAuthEntity) {
        return SocialMemberProfile.create(
                AccountId.fromUuid(memberEntity.getUuid()),
                SocialCredentials.create(memberAuthEntity.getProvider(), memberAuthEntity.getProviderId()),
                Email.create(memberAuthEntity.getEmail()),
                Nickname.create(memberEntity.getNickname()),
                memberEntity.getRole()
        );
    }
}
