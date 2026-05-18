package kr.modusplant.domains.term.framework.out.jpa.mapper;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.domain.exception.SiteMemberNotFoundException;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import kr.modusplant.domains.term.framework.out.jpa.entity.MemberTermEntity;
import kr.modusplant.domains.term.framework.out.jpa.mapper.supers.MemberTermJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberTermJpaMapperImpl implements MemberTermJpaMapper {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public MemberTermEntity toMemberTermNewEntity(SiteMemberTerm siteMemberTerm) {
        MemberEntity entity = memberJpaRepository.findById(siteMemberTerm.getSiteMemberTermId().getValue())
                .orElseThrow(SiteMemberNotFoundException::new);

        return MemberTermEntity.builder()
                .member(entity)
                .agreedTermsOfUseVersion(siteMemberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(siteMemberTerm.getAgreedPrivacyPolicyVersion())
                .agreedCommunityPolicyVersion(siteMemberTerm.getAgreedCommunityPolicyVersion())
                .build();
    }

    @Override
    public SiteMemberTerm toSiteMemberTerm(MemberTermEntity entity) {
        return SiteMemberTerm.create(
                SiteMemberTermId.fromUuid(entity.getUuid()),
                entity.getAgreedTermsOfUseVersion(),
                entity.getAgreedPrivacyPolicyVersion(),
                entity.getAgreedCommunityPolicyVersion()
        );
    }
}
