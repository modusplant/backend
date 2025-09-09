package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRoleRepository;
import kr.modusplant.legacy.domains.common.app.service.supers.UuidCrudApplicationService;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberRoleValidationService;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberRoleAppInfraMapper;
import kr.modusplant.legacy.modules.security.enums.Role;
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
public class SiteMemberRoleApplicationService implements UuidCrudApplicationService<SiteMemberRoleResponse, SiteMemberRoleInsertRequest, SiteMemberRoleUpdateRequest> {

    private final SiteMemberValidationService memberValidationService;
    private final SiteMemberRoleValidationService memberRoleValidationService;
    private final SiteMemberRoleRepository memberRoleRepository;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberRoleAppInfraMapper memberRoleEntityMapper;

    @Override
    public List<SiteMemberRoleResponse> getAll() {
        return memberRoleRepository.findAll().stream().map(memberRoleEntityMapper::toMemberRoleResponse).toList();
    }

    public List<SiteMemberRoleResponse> getByRole(Role role) {
        return memberRoleRepository.findByRole(role).stream().map(memberRoleEntityMapper::toMemberRoleResponse).toList();
    }

    @Override
    public Optional<SiteMemberRoleResponse> getByUuid(UUID uuid) {
        Optional<SiteMemberRoleEntity> memberRoleOrEmpty = memberRoleRepository.findByUuid(uuid);
        return memberRoleOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberRoleEntityMapper.toMemberRoleResponse(memberRoleOrEmpty.orElseThrow()));
    }

    public Optional<SiteMemberRoleResponse> getByMember(SiteMemberEntity memberEntity) {
        Optional<SiteMemberRoleEntity> memberRoleOrEmpty = memberRoleRepository.findByUuid(memberEntity.getUuid());
        return memberRoleOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberRoleEntityMapper.toMemberRoleResponse(memberRoleOrEmpty.orElseThrow()));
    }

    @Transactional
    @Override
    public SiteMemberRoleResponse insert(SiteMemberRoleInsertRequest memberRoleInsertRequest) {
        UUID uuid = memberRoleInsertRequest.uuid();
        memberValidationService.validateNotFoundUuid(uuid);
        memberRoleValidationService.validateExistedUuid(uuid);
        return memberRoleEntityMapper.toMemberRoleResponse(memberRoleRepository.save(memberRoleEntityMapper.toMemberRoleEntity(memberRoleInsertRequest, memberRepository)));
    }

    @Transactional
    @Override
    public SiteMemberRoleResponse update(SiteMemberRoleUpdateRequest memberRoleUpdateRequest) {
        UUID uuid = memberRoleUpdateRequest.uuid();
        memberValidationService.validateNotFoundUuid(uuid);
        memberRoleValidationService.validateNotFoundUuid(uuid);
        SiteMemberRoleEntity memberRoleEntity = memberRoleRepository.findByUuid(uuid).orElseThrow();
        memberRoleEntity.updateRole(memberRoleUpdateRequest.role());
        return memberRoleEntityMapper.toMemberRoleResponse(memberRoleRepository.save(memberRoleEntity));
    }

    @Transactional
    @Override
    public void removeByUuid(UUID uuid) {
        memberRoleValidationService.validateNotFoundUuid(uuid);
        memberRoleRepository.deleteByUuid(uuid);
    }
}
