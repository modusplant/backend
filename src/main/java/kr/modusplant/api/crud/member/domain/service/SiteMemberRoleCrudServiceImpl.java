package kr.modusplant.api.crud.member.domain.service;

import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.api.crud.member.domain.service.supers.SiteMemberRoleCrudService;
import kr.modusplant.api.crud.member.mapper.SiteMemberRoleEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberRoleEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberRoleCrudJpaRepository;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.error.EntityExistsWithUuidException;
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
public class SiteMemberRoleCrudServiceImpl implements SiteMemberRoleCrudService {

    private final SiteMemberRoleCrudJpaRepository memberRoleRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberRoleEntityMapper memberRoleEntityMapper = new SiteMemberRoleEntityMapperImpl();

    @Override
    public List<SiteMemberRole> getAll() {
        return memberRoleRepository.findAll().stream().map(memberRoleEntityMapper::toSiteMemberRole).toList();
    }

    @Override
    public List<SiteMemberRole> getByRole(Role role) {
        return memberRoleRepository.findByRole(role).stream().map(memberRoleEntityMapper::toSiteMemberRole).toList();
    }

    @Override
    public Optional<SiteMemberRole> getByUuid(UUID uuid) {
        Optional<SiteMemberRoleEntity> memberRoleOrEmpty = memberRoleRepository.findByUuid(uuid);
        return memberRoleOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberRoleEntityMapper.toSiteMemberRole(memberRoleOrEmpty.orElseThrow()));
    }

    @Override
    public Optional<SiteMemberRole> getByMember(SiteMember member) {
        Optional<SiteMemberRoleEntity> memberRoleOrEmpty = memberRoleRepository.findByUuid(member.getUuid());
        return memberRoleOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberRoleEntityMapper.toSiteMemberRole(memberRoleOrEmpty.orElseThrow()));
    }

    @Override
    @Transactional
    public SiteMemberRole insert(SiteMemberRole memberRole) {
        validateNotFoundMemberUuid(memberRole.getUuid());
        validateExistedMemberRoleUuid(memberRole.getUuid());
        return memberRoleEntityMapper.toSiteMemberRole(memberRoleRepository.save(memberRoleEntityMapper.createSiteMemberRoleEntity(memberRole, memberRepository)));
    }

    @Override
    @Transactional
    public SiteMemberRole update(SiteMemberRole memberRole) {
        validateNotFoundMemberUuid(memberRole.getUuid());
        validateNotFoundMemberRoleUuid(memberRole.getUuid());
        return memberRoleEntityMapper.toSiteMemberRole(memberRoleRepository.save(memberRoleEntityMapper.updateSiteMemberRoleEntity(memberRole, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundMemberRoleUuid(uuid);
        memberRoleRepository.deleteByUuid(uuid);
    }

    private void validateNotFoundMemberUuid(UUID uuid) {
        if (uuid == null || memberRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberEntity.class);
        }
    }

    private void validateExistedMemberRoleUuid(UUID uuid) {
        if (memberRoleRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberRoleEntity.class);
        }
    }

    private void validateNotFoundMemberRoleUuid(UUID uuid) {
        if (uuid == null || memberRoleRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberRoleEntity.class);
        }
    }
}
