package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.service.crud.SiteMemberAuthService;
import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.SiteMemberAuthEntityMapper;
import kr.modusplant.global.mapper.SiteMemberAuthEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
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
public class SiteMemberAuthServiceImpl implements SiteMemberAuthService {

    private final SiteMemberAuthJpaRepository memberAuthRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberAuthEntityMapper memberAuthEntityMapper = new SiteMemberAuthEntityMapperImpl();

    @Override
    public List<SiteMemberAuth> getAll() {
        return memberAuthRepository.findAll().stream().map(memberAuthEntityMapper::toSiteMemberAuth).toList();
    }

    @Override
    public List<SiteMemberAuth> getByActiveMember(SiteMember activeMember) {
        return memberAuthRepository.findByActiveMember(memberRepository.findByUuid(activeMember.getUuid())
                        .orElseThrow(() -> new EntityNotFoundWithUuidException(activeMember.getUuid(), SiteMemberEntity.class)))
                .stream().map(memberAuthEntityMapper::toSiteMemberAuth).toList();
    }

    @Override
    public List<SiteMemberAuth> getByEmail(String email) {
        return memberAuthRepository.findByEmail(email).stream().map(memberAuthEntityMapper::toSiteMemberAuth).toList();
    }

    @Override
    public List<SiteMemberAuth> getByProvider(AuthProvider provider) {
        return memberAuthRepository.findByProvider(provider).stream().map(memberAuthEntityMapper::toSiteMemberAuth).toList();
    }

    @Override
    public List<SiteMemberAuth> getByProviderId(String providerId) {
        return memberAuthRepository.findByProviderId(providerId).stream().map(memberAuthEntityMapper::toSiteMemberAuth).toList();
    }

    @Override
    public List<SiteMemberAuth> getByFailedAttempt(Integer failedAttempt) {
        return memberAuthRepository.findByFailedAttempt(failedAttempt).stream().map(memberAuthEntityMapper::toSiteMemberAuth).toList();
    }

    @Override
    public Optional<SiteMemberAuth> getByUuid(UUID uuid) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByUuid(uuid);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthEntityMapper.toSiteMemberAuth(memberAuthOrEmpty.orElseThrow()));
    }

    @Override
    public Optional<SiteMemberAuth> getByOriginalMember(SiteMember originalMember) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByOriginalMember(
                memberRepository.findByUuid(originalMember.getUuid())
                        .orElseThrow(() -> new EntityNotFoundWithUuidException(originalMember.getUuid(), SiteMemberEntity.class)));
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthEntityMapper.toSiteMemberAuth(memberAuthOrEmpty.orElseThrow()));
    }

    @Override
    public Optional<SiteMemberAuth> getByEmailAndProvider(String email, AuthProvider provider) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByEmailAndProvider(email, provider);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthEntityMapper.toSiteMemberAuth(memberAuthOrEmpty.orElseThrow()));
    }

    @Override
    public Optional<SiteMemberAuth> getByProviderAndProviderId(AuthProvider provider, String providerId) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByProviderAndProviderId(provider, providerId);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthEntityMapper.toSiteMemberAuth(memberAuthOrEmpty.orElseThrow()));
    }

    @Override
    @Transactional
    public SiteMemberAuth insert(SiteMemberAuth memberAuth) {
        UUID uuid = memberAuth.getUuid();
        validateExistedEntity(uuid);
        return memberAuthEntityMapper.toSiteMemberAuth(memberAuthRepository.save(memberAuthEntityMapper.createSiteMemberAuthEntity(memberAuth, memberRepository)));
    }

    @Override
    @Transactional
    public SiteMemberAuth update(SiteMemberAuth memberAuth) {
        UUID uuid = memberAuth.getUuid();
        validateNotFoundEntity(uuid);
        return memberAuthEntityMapper.toSiteMemberAuth(memberAuthRepository.save(memberAuthEntityMapper.updateSiteMemberAuthEntity(memberAuth, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundEntity(uuid);
        memberAuthRepository.deleteByUuid(uuid);
    }

    private void validateExistedEntity(UUID uuid) {
        if (memberAuthRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }

    private void validateNotFoundEntity(UUID uuid) {
        if (memberAuthRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }
}
