package kr.modusplant.domains.identity.social.framework.out.jpa.mapper;

import kr.modusplant.domains.identity.social.domain.vo.MemberId;
import kr.modusplant.domains.identity.social.domain.vo.Nickname;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberRoleEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers.SocialIdentityJpaMapper;
import kr.modusplant.infrastructure.security.enums.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SocialIdentityJpaMapperImpl implements SocialIdentityJpaMapper {

    @Override
    public MemberEntity toMemberEntity(Nickname nickname) {
        return MemberEntity.builder()
                .nickname(nickname.getNickname())
                .loggedInAt(LocalDateTime.now())
                .build();
    }

    @Override
    public MemberAuthEntity toMemberAuthEntity(MemberEntity memberEntity, SocialUserProfile profile) {
        return MemberAuthEntity.builder()
                .activeMember(memberEntity)
                .originalMember(memberEntity)
                .email(profile.getEmail().getEmail())
                .provider(profile.getSocialCredentials().getProvider())
                .providerId(profile.getSocialCredentials().getProviderId())
                .build();
    }

    @Override
    public MemberRoleEntity toMemberRoleEntity(MemberEntity memberEntity, Role role) {
        return MemberRoleEntity.builder()
                .member(memberEntity)
                .role(role)
                .build();
    }

    @Override
    public UserPayload toUserPayload(MemberEntity memberEntity, MemberRoleEntity memberRoleEntity) {
        return UserPayload.create(
                MemberId.fromUuid(memberEntity.getUuid()),
                Nickname.create(memberEntity.getNickname()),
                memberRoleEntity.getRole()
        );
    }

    @Override
    public UserPayload toUserPayload(MemberEntity memberEntity, Nickname nickname, Role role) {
        return UserPayload.create(
                MemberId.fromUuid(memberEntity.getUuid()),
                nickname,
                role
        );
    }

}
