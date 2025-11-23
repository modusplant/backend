package kr.modusplant.domains.term.framework.out.jpa.mapper;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.domain.exception.SiteMemberNotFoundException;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import kr.modusplant.domains.term.framework.out.jpa.mapper.supers.SiteMemberTermJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SiteMemberTermJpaMapperImpl implements SiteMemberTermJpaMapper {

    private final SiteMemberJpaRepository siteMemberJpaRepository;

    @Override
    public SiteMemberTermEntity toSiteMemberTermNewEntity(SiteMemberTerm siteMemberTerm) {
        SiteMemberEntity entity = siteMemberJpaRepository.findById(siteMemberTerm.getSiteMemberTermId().getValue())
                .orElseThrow(SiteMemberNotFoundException::new);

        return SiteMemberTermEntity.builder()
                .member(entity)
                .agreedTermsOfUseVersion(siteMemberTerm.getAgreedTermsOfUseVersion())
                .agreedPrivacyPolicyVersion(siteMemberTerm.getAgreedPrivacyPolicyVersion())
                .agreedAdInfoReceivingVersion(siteMemberTerm.getAgreedAdInfoReceivingVersion())
                .build();
    }

    @Override
    public SiteMemberTerm toSiteMemberTerm(SiteMemberTermEntity entity) {
        return SiteMemberTerm.create(
                SiteMemberTermId.fromUuid(entity.getUuid()),
                entity.getAgreedTermsOfUseVersion(),
                entity.getAgreedPrivacyPolicyVersion(),
                entity.getAgreedAdInfoReceivingVersion()
        );
    }
}
