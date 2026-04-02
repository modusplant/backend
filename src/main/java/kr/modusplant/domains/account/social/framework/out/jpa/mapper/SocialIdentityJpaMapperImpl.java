package kr.modusplant.domains.account.social.framework.out.jpa.mapper;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SocialIdentityJpaMapperImpl implements SocialIdentityJpaMapper {

    @Override
    public SiteMemberEntity toMemberEntity(Nickname nickname) {
        return SiteMemberEntity.builder()
                .nickname(nickname.getValue())
                .loggedInAt(LocalDateTime.now())
                .build();
    }

    @Override
    public SiteMemberAuthEntity toMemberAuthEntity(SiteMemberEntity memberEntity, SocialAccountProfile profile) {
        return SiteMemberAuthEntity.builder()
                .member(memberEntity)
                .email(profile.getEmail().getValue())
                .provider(profile.getSocialCredentials().getProvider())
                .providerId(profile.getSocialCredentials().getProviderId())
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
