package kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.identity.social.domain.vo.Nickname;
import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.identity.social.framework.out.jpa.entity.MemberRoleEntity;
import kr.modusplant.infrastructure.security.enums.Role;

public interface SocialIdentityJpaMapper {

    MemberEntity toMemberEntity(Nickname nickname);

    MemberAuthEntity toMemberAuthEntity(MemberEntity memberEntity, SocialUserProfile profile);

    MemberRoleEntity toMemberRoleEntity(MemberEntity memberEntity, Role role);

    UserPayload toUserPayload(MemberEntity memberEntity, MemberRoleEntity memberRoleEntity);

    UserPayload toUserPayload(MemberEntity memberEntity, Nickname nickname, Role role);
}
