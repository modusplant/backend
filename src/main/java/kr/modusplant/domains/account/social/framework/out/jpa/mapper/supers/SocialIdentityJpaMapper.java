package kr.modusplant.domains.account.social.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.account.identity.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.term.framework.out.jpa.entity.MemberTermEntity;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;

public interface SocialIdentityJpaMapper {

    MemberEntity toMemberEntity(Nickname nickname, Role role);

    MemberAuthEntity toMemberAuthEntity(MemberEntity memberEntity, SocialCredentials socialCredentials, Email email);

    MemberProfileEntity toMemberProfileEntity(MemberEntity memberEntity, String introduction);

    MemberTermEntity toMemberTermEntity(MemberEntity memberEntity, AgreedTerms agreedTerms);

    SocialMemberProfile toSocialMemberProfile(MemberEntity memberEntity, MemberAuthEntity memberAuthEntity);
}
