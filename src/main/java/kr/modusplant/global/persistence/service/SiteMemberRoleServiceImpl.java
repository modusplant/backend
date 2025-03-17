package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.domain.service.crud.SiteMemberRoleService;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.SiteMemberRoleEntityMapper;
import kr.modusplant.global.mapper.SiteMemberRoleEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberRoleJpaRepository;
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
public class SiteMemberRoleServiceImpl implements SiteMemberRoleService {

    private final SiteMemberRoleJpaRepository memberRoleRepository;
    private final SiteMemberJpaRepository memberRepository;
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
    @Transactional
    public SiteMemberRole insert(SiteMemberRole memberRole) {
        validateExistedEntity(memberRole.getUuid());
        return memberRoleEntityMapper.toSiteMemberRole(memberRoleRepository.save(memberRoleEntityMapper.createSiteMemberRoleEntity(memberRole)));
    }

    @Override
    @Transactional
    public SiteMemberRole update(SiteMemberRole memberRole) {
        validateNotFoundEntity(memberRole.getUuid());
        return memberRoleEntityMapper.toSiteMemberRole(memberRoleRepository.save(memberRoleEntityMapper.updateSiteMemberRoleEntity(memberRole, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundEntity(uuid);
        memberRoleRepository.deleteByUuid(uuid);
    }

    private void validateExistedEntity(UUID uuid) {
        if (memberRoleRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberRoleEntity.class);
        }
    }

    private void validateNotFoundEntity(UUID uuid) {
        if (memberRoleRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberRoleEntity.class);
        }
    }
}
