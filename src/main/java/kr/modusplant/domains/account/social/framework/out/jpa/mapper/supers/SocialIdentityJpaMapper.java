package kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;

public interface SocialIdentityJpaMapper {

    SiteMemberEntity toMemberEntity(Nickname nickname);

    SiteMemberAuthEntity toMemberAuthEntity(SiteMemberEntity memberEntity, SocialAccountProfile profile);

    SocialMemberProfile toSocialMemberProfile(SiteMemberEntity memberEntity, SiteMemberAuthEntity memberAuthEntity);
}
