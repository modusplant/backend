package kr.modusplant.domains.account.social.framework.out.jpa.mapper;

import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.infrastructure.security.enums.Role;
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
                .activeMember(memberEntity)
                .originalMember(memberEntity)
                .email(profile.getEmail().getValue())
                .provider(profile.getSocialCredentials().getProvider())
                .providerId(profile.getSocialCredentials().getProviderId())
                .build();
    }

    @Override
    public SiteMemberRoleEntity toMemberRoleEntity(SiteMemberEntity memberEntity, Role role) {
        return SiteMemberRoleEntity.builder()
                .member(memberEntity)
                .role(role)
                .build();
    }

    @Override
    public SocialAccountPayload toUserPayload(SiteMemberEntity memberEntity, SiteMemberAuthEntity memberAuthEntity, SiteMemberRoleEntity memberRoleEntity) {
        return SocialAccountPayload.create(
                AccountId.fromUuid(memberEntity.getUuid()),
                Nickname.create(memberEntity.getNickname()),
                Email.create(memberAuthEntity.getEmail()),
                memberRoleEntity.getRole()
        );
    }

    @Override
    public SocialAccountPayload toUserPayload(SiteMemberEntity memberEntity, Nickname nickname, Email email, Role role) {
        return SocialAccountPayload.create(
                AccountId.fromUuid(memberEntity.getUuid()),
                nickname,
                email,
                role
        );
    }

}
