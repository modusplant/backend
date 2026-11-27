package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import org.springframework.stereotype.Component;

@Component
public class NormalIdentityTermJpaMapper {

    public MemberTermEntity toSiteMemberTermEntity(MemberEntity savedMember, SignUpData sign) {
        return MemberTermEntity.builder()
                .member(savedMember)
                .agreedTermsOfUseVersion(sign.getAgreedTermsOfUseVersion().getValue())
                .agreedPrivacyPolicyVersion(sign.getAgreedPrivacyPolicyVersion().getValue())
                .agreedCommunityPolicyVersion(sign.getAgreedCommunityPolicyVersion().getValue())
                .build();
    }
}
