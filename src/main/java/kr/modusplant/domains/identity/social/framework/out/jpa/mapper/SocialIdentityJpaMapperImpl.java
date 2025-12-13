package kr.modusplant.domains.identity.social.framework.out.jpa.mapper;

import kr.modusplant.domains.identity.social.domain.vo.MemberId;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
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
    public SiteMemberAuthEntity toMemberAuthEntity(SiteMemberEntity memberEntity, SocialUserProfile profile) {
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
    public UserPayload toUserPayload(SiteMemberEntity memberEntity, SiteMemberAuthEntity memberAuthEntity, SiteMemberRoleEntity memberRoleEntity) {
        return UserPayload.create(
                MemberId.fromUuid(memberEntity.getUuid()),
                Nickname.create(memberEntity.getNickname()),
                Email.create(memberAuthEntity.getEmail()),
                memberRoleEntity.getRole()
        );
    }

    @Override
    public UserPayload toUserPayload(SiteMemberEntity memberEntity, Nickname nickname, Email email, Role role) {
        return UserPayload.create(
                MemberId.fromUuid(memberEntity.getUuid()),
                nickname,
                email,
                role
        );
    }

}
