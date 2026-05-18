package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import kr.modusplant.domains.term.framework.out.jpa.entity.MemberTermEntity;
import kr.modusplant.domains.term.framework.out.jpa.mapper.MemberTermJpaMapperImpl;
import kr.modusplant.domains.term.usecase.port.repository.SiteMemberTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SiteMemberTermRepositoryJpaAdapter implements SiteMemberTermRepository {
    private final MemberTermJpaMapperImpl siteMemberTermJpaMapper;
    private final MemberTermJpaRepository memberTermJpaRepository;

    @Override
    public SiteMemberTerm save(SiteMemberTerm siteMemberTerm) {
        boolean exists = memberTermJpaRepository.existsById(siteMemberTerm.getSiteMemberTermId().getValue());
        if (exists) {
            MemberTermEntity entity = memberTermJpaRepository.getReferenceById(siteMemberTerm.getSiteMemberTermId().getValue());
            entity.updateAgreedTermsOfUseVersion(siteMemberTerm.getAgreedTermsOfUseVersion());
            entity.updateAgreedPrivacyPolicyVersion(siteMemberTerm.getAgreedPrivacyPolicyVersion());
            entity.updateAgreedCommunityPolicyVersion(siteMemberTerm.getAgreedCommunityPolicyVersion());
            return siteMemberTermJpaMapper.toSiteMemberTerm(entity);
        } else {
            return siteMemberTermJpaMapper.toSiteMemberTerm(memberTermJpaRepository.save(siteMemberTermJpaMapper.toMemberTermNewEntity(siteMemberTerm)));
        }
    }

    @Override
    public Optional<SiteMemberTerm> findById(SiteMemberTermId siteMemberTermId) {
        return memberTermJpaRepository.findById(siteMemberTermId.getValue()).map(siteMemberTermJpaMapper::toSiteMemberTerm);
    }

    @Override
    public List<SiteMemberTerm> findAll() {
        return memberTermJpaRepository.findAll().stream().map(siteMemberTermJpaMapper::toSiteMemberTerm).toList();
    }

    @Override
    public boolean isIdExist(SiteMemberTermId siteMemberTermId) {
        return memberTermJpaRepository.existsById(siteMemberTermId.getValue());
    }

    @Override
    public void deleteById(SiteMemberTermId siteMemberTermId) {
        memberTermJpaRepository.deleteById(siteMemberTermId.getValue());
    }

}
