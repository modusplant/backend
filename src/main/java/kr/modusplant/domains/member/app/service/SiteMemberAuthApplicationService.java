package kr.modusplant.domains.member.app.service;

import kr.modusplant.domains.common.app.service.supers.UuidCrudApplicationService;
import kr.modusplant.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.domains.member.app.http.request.SiteMemberAuthUpdateRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.domains.member.domain.service.SiteMemberAuthValidationService;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.mapper.SiteMemberAuthAppInfraMapper;
import kr.modusplant.domains.member.mapper.SiteMemberAuthAppInfraMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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
public class SiteMemberAuthApplicationService implements UuidCrudApplicationService<SiteMemberAuthResponse, SiteMemberAuthInsertRequest, SiteMemberAuthUpdateRequest> {

    private final SiteMemberValidationService memberValidationService;
    private final SiteMemberAuthValidationService memberAuthValidationService;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthAppInfraMapper memberAuthAppInfraMapper = new SiteMemberAuthAppInfraMapperImpl();

    @Override
    public List<SiteMemberAuthResponse> getAll() {
        return memberAuthRepository.findAll().stream().map(memberAuthAppInfraMapper::toMemberAuthResponse).toList();
    }

    public List<SiteMemberAuthResponse> getByActiveMember(SiteMemberEntity activeMemberEntity) {
        return memberAuthRepository.findByActiveMember(memberRepository.findByUuid(activeMemberEntity.getUuid())
                .orElseThrow()).stream().map(memberAuthAppInfraMapper::toMemberAuthResponse).toList();
    }

    public List<SiteMemberAuthResponse> getByEmail(String email) {
        return memberAuthRepository.findByEmail(email).stream().map(memberAuthAppInfraMapper::toMemberAuthResponse).toList();
    }

    public List<SiteMemberAuthResponse> getByProvider(AuthProvider provider) {
        return memberAuthRepository.findByProvider(provider).stream().map(memberAuthAppInfraMapper::toMemberAuthResponse).toList();
    }

    public List<SiteMemberAuthResponse> getByProviderId(String providerId) {
        return memberAuthRepository.findByProviderId(providerId).stream().map(memberAuthAppInfraMapper::toMemberAuthResponse).toList();
    }

    public List<SiteMemberAuthResponse> getByFailedAttempt(Integer failedAttempt) {
        return memberAuthRepository.findByFailedAttempt(failedAttempt).stream().map(memberAuthAppInfraMapper::toMemberAuthResponse).toList();
    }

    public Optional<SiteMemberAuthResponse> getByUuid(UUID uuid) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByUuid(uuid);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthAppInfraMapper.toMemberAuthResponse(memberAuthOrEmpty.orElseThrow()));
    }

    public Optional<SiteMemberAuthResponse> getByOriginalMember(SiteMemberEntity memberEntity) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByOriginalMember(memberEntity);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthAppInfraMapper.toMemberAuthResponse(memberAuthOrEmpty.orElseThrow()));
    }


    public Optional<SiteMemberAuthResponse> getByEmailAndProvider(String email, AuthProvider provider) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByEmailAndProvider(email, provider);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthAppInfraMapper.toMemberAuthResponse(memberAuthOrEmpty.orElseThrow()));
    }

    public Optional<SiteMemberAuthResponse> getByProviderAndProviderId(AuthProvider provider, String providerId) {
        Optional<SiteMemberAuthEntity> memberAuthOrEmpty = memberAuthRepository.findByProviderAndProviderId(provider, providerId);
        return memberAuthOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAuthAppInfraMapper.toMemberAuthResponse(memberAuthOrEmpty.orElseThrow()));
    }

    @Transactional
    @Override
    public SiteMemberAuthResponse insert(SiteMemberAuthInsertRequest memberAuthInsertRequest) {
        UUID originalMemberUuid = memberAuthInsertRequest.originalMemberUuid();
        memberValidationService.validateNotFoundUuid(originalMemberUuid);
        memberAuthValidationService.validateExistedOriginalMemberUuid(originalMemberUuid);
        return memberAuthAppInfraMapper.toMemberAuthResponse(memberAuthRepository.save(memberAuthAppInfraMapper.toMemberAuthEntity(memberAuthInsertRequest, memberRepository)));
    }

    @Transactional
    @Override
    public SiteMemberAuthResponse update(SiteMemberAuthUpdateRequest memberAuthUpdateRequest) {
        memberValidationService.validateNotFoundUuid(memberAuthUpdateRequest.originalMemberUuid());
        memberValidationService.validateNotFoundUuid(memberAuthUpdateRequest.activeMemberUuid());
        SiteMemberAuthEntity memberAuthEntity = memberAuthRepository.findByOriginalMember(memberRepository.findByUuid(memberAuthUpdateRequest.originalMemberUuid()).orElseThrow()).orElseThrow();
        memberAuthEntity.updateEmail(memberAuthUpdateRequest.email());
        memberAuthEntity.updatePw(memberAuthUpdateRequest.pw());
        return memberAuthAppInfraMapper.toMemberAuthResponse(memberAuthRepository.save(memberAuthEntity));
    }

    @Transactional
    @Override
    public void removeByUuid(UUID uuid) {
        memberAuthValidationService.validateNotFoundOriginalMemberUuid(uuid);
        memberAuthRepository.deleteByUuid(uuid);
    }
}
