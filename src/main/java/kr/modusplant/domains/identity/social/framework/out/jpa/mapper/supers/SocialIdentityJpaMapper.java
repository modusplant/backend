package kr.modusplant.domains.identity.social.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.identity.social.domain.vo.SocialUserProfile;
import kr.modusplant.domains.identity.social.domain.vo.UserPayload;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;

public interface SocialIdentityJpaMapper {

    SiteMemberEntity toMemberEntity(Nickname nickname);

    SiteMemberAuthEntity toMemberAuthEntity(SiteMemberEntity memberEntity, SocialUserProfile profile);

    SiteMemberRoleEntity toMemberRoleEntity(SiteMemberEntity memberEntity, Role role);

    UserPayload toUserPayload(SiteMemberEntity memberEntity, SiteMemberAuthEntity memberAuthEntity, SiteMemberRoleEntity memberRoleEntity);

    UserPayload toUserPayload(SiteMemberEntity memberEntity, Nickname nickname, Email email, Role role);
}
