package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.domain.vo.SiteMemberTermId;
import kr.modusplant.domains.term.framework.out.jpa.mapper.SiteMemberTermJpaMapperImpl;
import kr.modusplant.domains.term.usecase.port.repository.SiteMemberTermRepository;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.repository.SiteMemberTermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SiteMemberTermRepositoryJpaAdapter implements SiteMemberTermRepository {
    private final SiteMemberTermJpaMapperImpl siteMemberTermJpaMapper;
    private final SiteMemberTermJpaRepository siteMemberTermJpaRepository;

    @Override
    public SiteMemberTerm save(SiteMemberTerm siteMemberTerm) {
        boolean exists = siteMemberTermJpaRepository.existsById(siteMemberTerm.getSiteMemberTermId().getValue());
        if (exists) {
            SiteMemberTermEntity entity = siteMemberTermJpaRepository.getReferenceById(siteMemberTerm.getSiteMemberTermId().getValue());
            entity.updateAgreedTermsOfUseVersion(siteMemberTerm.getAgreedTermsOfUseVersion());
            entity.updateAgreedPrivacyPolicyVersion(siteMemberTerm.getAgreedPrivacyPolicyVersion());
            entity.updateAgreedAdInfoReceivingVersion(siteMemberTerm.getAgreedAdInfoReceivingVersion());
            return siteMemberTermJpaMapper.toSiteMemberTerm(entity);
        } else {
            return siteMemberTermJpaMapper.toSiteMemberTerm(siteMemberTermJpaRepository.save(siteMemberTermJpaMapper.toSiteMemberTermNewEntity(siteMemberTerm)));
        }
    }

    @Override
    public Optional<SiteMemberTerm> findById(SiteMemberTermId siteMemberTermId) {
        return siteMemberTermJpaRepository.findById(siteMemberTermId.getValue()).map(siteMemberTermJpaMapper::toSiteMemberTerm);
    }

    @Override
    public List<SiteMemberTerm> findAll() {
        return siteMemberTermJpaRepository.findAll().stream().map(siteMemberTermJpaMapper::toSiteMemberTerm).toList();
    }

    @Override
    public boolean isIdExist(SiteMemberTermId siteMemberTermId) {
        return siteMemberTermJpaRepository.existsById(siteMemberTermId.getValue());
    }

    @Override
    public void deleteById(SiteMemberTermId siteMemberTermId) {
        siteMemberTermJpaRepository.deleteById(siteMemberTermId.getValue());
    }

}
