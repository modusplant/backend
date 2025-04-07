<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/SiteMemberRoleCrudServiceImpl.java
package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberRoleCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberRoleEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberRoleEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleCrudJpaRepository;
import kr.modusplant.global.enums.Role;
========
package kr.modusplant.api.crud.member.persistence.service;

import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.api.crud.member.domain.service.SiteMemberRoleService;
import kr.modusplant.api.crud.member.mapper.SiteMemberRoleEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberRoleEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/persistence/service/SiteMemberRoleServiceImpl.java
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
        return memberRoleEntityMapper.toSiteMemberRole(memberRoleRepository.save(memberRoleEntityMapper.createSiteMemberRoleEntity(memberRole, memberRepository)));
    }

    @Override
    @Transactional
    public SiteMemberRole update(SiteMemberRole memberRole) {
        return memberRoleEntityMapper.toSiteMemberRole(memberRoleRepository.save(memberRoleEntityMapper.updateSiteMemberRoleEntity(memberRole, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        memberRoleRepository.deleteByUuid(uuid);
    }
}
