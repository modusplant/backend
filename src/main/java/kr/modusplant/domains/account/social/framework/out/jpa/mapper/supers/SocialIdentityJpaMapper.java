package kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;

public interface SocialIdentityJpaMapper {

    SiteMemberEntity toMemberEntity(Nickname nickname, Role role);

    SiteMemberAuthEntity toMemberAuthEntity(SiteMemberEntity memberEntity, SocialCredentials socialCredentials, Email email);

    SiteMemberProfileEntity toMemberProfileEntity(SiteMemberEntity memberEntity, String introduction);

    SiteMemberTermEntity toMemberTermEntity(SiteMemberEntity memberEntity, AgreedTerms agreedTerms);

    SocialMemberProfile toSocialMemberProfile(SiteMemberEntity memberEntity, SiteMemberAuthEntity memberAuthEntity);
}
