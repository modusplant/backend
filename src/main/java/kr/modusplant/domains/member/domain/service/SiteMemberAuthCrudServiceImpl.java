<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/SiteMemberAuthCrudServiceImpl.java
package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.mapper.SiteMemberAuthEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberAuthEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
========
package kr.modusplant.api.crud.member.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.domain.service.supers.SiteMemberAuthService;
import kr.modusplant.api.crud.member.enums.AuthProvider;
import kr.modusplant.api.crud.member.mapper.SiteMemberAuthEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberAuthEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
>>>>>>>> 4cc54db (MP-145 :truck: Rename: 서비스 및 서비스 구현체 파일 이동):src/main/java/kr/modusplant/domains/member/domain/service/supers/SiteMemberAuthServiceImpl.java
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
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
public class SiteMemberAuthCrudServiceImpl implements SiteMemberAuthCrudService {

    private final SiteMemberAuthCrudJpaRepository memberAuthRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
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
        return memberAuthEntityMapper.toSiteMemberAuth(memberAuthRepository.save(memberAuthEntityMapper.createSiteMemberAuthEntity(memberAuth, memberRepository)));
    }

    @Override
    @Transactional
    public SiteMemberAuth update(SiteMemberAuth memberAuth) {
        return memberAuthEntityMapper.toSiteMemberAuth(memberAuthRepository.save(memberAuthEntityMapper.updateSiteMemberAuthEntity(memberAuth, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        memberAuthRepository.deleteByUuid(uuid);
    }
}
