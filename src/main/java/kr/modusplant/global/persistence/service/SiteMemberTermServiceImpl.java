package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.domain.service.crud.SiteMemberTermService;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.SiteMemberTermEntityMapper;
import kr.modusplant.global.mapper.SiteMemberTermEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberTermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberTermServiceImpl implements SiteMemberTermService {

    private final SiteMemberTermJpaRepository memberTermRepository;
    private final SiteMemberTermEntityMapper memberTermEntityMapper = new SiteMemberTermEntityMapperImpl();

    @Override
    public List<SiteMemberTerm> getAll() {
        return memberTermRepository.findAll().stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion) {
        return memberTermRepository.findByAgreedTermsOfUseVersion(agreedTermsOfUseVersion).stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion) {
        return memberTermRepository.findByAgreedPrivacyPolicyVersion(agreedPrivacyPolicyVersion).stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion) {
        return memberTermRepository.findByAgreedAdInfoReceivingVersion(agreedAdInfoReceivingVersion).stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public Optional<SiteMemberTerm> getByUuid(UUID uuid) {
        Optional<SiteMemberTermEntity> memberTermOrEmpty = memberTermRepository.findByUuid(uuid);
        return memberTermOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberTermEntityMapper.toSiteMemberTerm(memberTermOrEmpty.orElseThrow()));
    }

    @Override
    @Transactional
    public SiteMemberTerm insert(SiteMemberTerm memberTerm) {
        validateExistedEntity(memberTerm.getUuid());
        return memberTermEntityMapper.toSiteMemberTerm(memberTermRepository.save(memberTermEntityMapper.createSiteMemberTermEntity(memberTerm)));
    }

    @Override
    @Transactional
    public SiteMemberTerm update(SiteMemberTerm memberTerm) {
        validateNotFoundEntity(memberTerm.getUuid());
        return memberTermEntityMapper.toSiteMemberTerm(memberTermRepository.save(memberTermEntityMapper.updateSiteMemberTermEntity(memberTerm)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundEntity(uuid);
        memberTermRepository.deleteByUuid(uuid);
    }

    private void validateExistedEntity(UUID uuid) {
        if (memberTermRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberTermEntity.class);
        }
    }

    private void validateNotFoundEntity(UUID uuid) {
        if (memberTermRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberTermEntity.class);
        }
    }
}
