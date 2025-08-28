package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.common.app.service.supers.UuidCrudApplicationService;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberAppInfraMapper;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberAppInfraMapperImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SiteMemberApplicationService implements UuidCrudApplicationService<SiteMemberResponse, SiteMemberInsertRequest, SiteMemberUpdateRequest> {

    private final SiteMemberValidationService validationService;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberAppInfraMapper memberAppInfraMapper = new SiteMemberAppInfraMapperImpl();

    @Override
    public List<SiteMemberResponse> getAll() {
        return memberRepository.findAll().stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByBirthDate(LocalDate birthDate) {
        return memberRepository.findByBirthDate(birthDate).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByIsActive(Boolean isActive) {
        return memberRepository.findByIsActive(isActive).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByIsDisabledByLinking(Boolean isDisabledByLinking) {
        return memberRepository.findByIsDisabledByLinking(isDisabledByLinking).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByIsBanned(Boolean isBanned) {
        return memberRepository.findByIsBanned(isBanned).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByIsDeleted(Boolean isDeleted) {
        return memberRepository.findByIsDeleted(isDeleted).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    public List<SiteMemberResponse> getByLoggedInAt(LocalDateTime loggedInAt) {
        return memberRepository.findByLoggedInAt(loggedInAt).stream().map(memberAppInfraMapper::toMemberResponse).toList();
    }

    @Override
    public Optional<SiteMemberResponse> getByUuid(UUID uuid) {
        Optional<SiteMemberEntity> memberOrEmpty = memberRepository.findByUuid(uuid);
        return memberOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberAppInfraMapper.toMemberResponse(memberOrEmpty.orElseThrow()));
    }

    @Transactional
    @Override
    public SiteMemberResponse insert(SiteMemberInsertRequest memberInsertRequest) {
        return memberAppInfraMapper.toMemberResponse(memberRepository.save(memberAppInfraMapper.toMemberEntity(memberInsertRequest)));
    }

    @Transactional
    @Override
    public SiteMemberResponse update(SiteMemberUpdateRequest memberUpdateRequest) {
        UUID uuid = memberUpdateRequest.uuid();
        validationService.validateNotFoundUuid(uuid);
        SiteMemberEntity memberEntity = memberRepository.findByUuid(uuid).orElseThrow();
        memberEntity.updateNickname(memberUpdateRequest.nickname());
        memberEntity.updateBirthDate(memberUpdateRequest.birthDate());
        return memberAppInfraMapper.toMemberResponse(memberRepository.save(memberEntity));
    }

    @Transactional
    @Override
    public void removeByUuid(UUID uuid) {
        validationService.validateNotFoundUuid(uuid);
        memberRepository.deleteByUuid(uuid);
    }
}
