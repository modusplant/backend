package kr.modusplant.api.crud.member.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.api.crud.member.enums.AuthProvider;
import kr.modusplant.api.crud.member.mapper.SiteMemberAuthEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberAuthEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberAuthCrudJpaRepository;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.ACTIVE_MEMBER_UUID;
import static kr.modusplant.global.vo.CamelCaseWord.ORIGINAL_MEMBER_UUID;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;

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
        validateNotFoundMemberUuid(ACTIVE_MEMBER_UUID, memberAuth.getActiveMemberUuid());
        validateNotFoundMemberUuid(ORIGINAL_MEMBER_UUID, memberAuth.getOriginalMemberUuid());
        validateExistedMemberAuthUuid(memberAuth.getUuid());
        validateExistedMemberAuthOriginalMemberUuid(memberAuth.getOriginalMemberUuid());
        return memberAuthEntityMapper.toSiteMemberAuth(memberAuthRepository.save(memberAuthEntityMapper.createSiteMemberAuthEntity(memberAuth, memberRepository)));
    }

    @Override
    @Transactional
    public SiteMemberAuth update(SiteMemberAuth memberAuth) {
        validateNotFoundMemberUuid(ACTIVE_MEMBER_UUID, memberAuth.getActiveMemberUuid());
        validateNotFoundMemberUuid(ORIGINAL_MEMBER_UUID, memberAuth.getOriginalMemberUuid());
        validateNotFoundMemberAuthUuid(memberAuth.getUuid());
        return memberAuthEntityMapper.toSiteMemberAuth(memberAuthRepository.save(memberAuthEntityMapper.updateSiteMemberAuthEntity(memberAuth, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundMemberAuthUuid(uuid);
        memberAuthRepository.deleteByUuid(uuid);
    }

    private void validateNotFoundMemberUuid(String name, UUID uuid) {
        if (uuid == null || memberRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY, name, uuid, SiteMemberEntity.class));
        }
    }

    private void validateExistedMemberAuthUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberAuthRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }

    private void validateExistedMemberAuthOriginalMemberUuid(UUID uuid) {
        if (memberAuthRepository.findByOriginalMember(memberRepository.findByUuid(uuid).orElseThrow()).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, ORIGINAL_MEMBER_UUID, uuid, SiteMemberAuthEntity.class));
        }
    }

    private void validateNotFoundMemberAuthUuid(UUID uuid) {
        if (uuid == null || memberAuthRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberAuthEntity.class);
        }
    }
}
