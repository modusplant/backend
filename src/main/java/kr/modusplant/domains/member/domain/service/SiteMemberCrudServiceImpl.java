<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/SiteMemberCrudServiceImpl.java
package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
========
package kr.modusplant.api.crud.member.domain.service;

import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.service.supers.SiteMemberService;
import kr.modusplant.api.crud.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
>>>>>>>> 4cc54db (MP-145 :truck: Rename: 서비스 및 서비스 구현체 파일 이동):src/main/java/kr/modusplant/domains/member/domain/service/supers/SiteMemberServiceImpl.java
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
public class SiteMemberCrudServiceImpl implements SiteMemberCrudService {

    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberEntityMapper memberEntityMapper = new SiteMemberEntityMapperImpl();

    @Override
    public List<SiteMember> getAll() {
        return memberRepository.findAll().stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByBirthDate(LocalDate birthDate) {
        return memberRepository.findByBirthDate(birthDate).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByIsActive(Boolean isActive) {
        return memberRepository.findByIsActive(isActive).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByIsDisabledByLinking(Boolean isDisabledByLinking) {
        return memberRepository.findByIsDisabledByLinking(isDisabledByLinking).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByIsBanned(Boolean isBanned) {
        return memberRepository.findByIsBanned(isBanned).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByIsDeleted(Boolean isDeleted) {
        return memberRepository.findByIsDeleted(isDeleted).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public List<SiteMember> getByLoggedInAt(LocalDateTime loggedInAt) {
        return memberRepository.findByLoggedInAt(loggedInAt).stream().map(memberEntityMapper::toSiteMember).toList();
    }

    @Override
    public Optional<SiteMember> getByUuid(UUID uuid) {
        Optional<SiteMemberEntity> memberOrEmpty = memberRepository.findByUuid(uuid);
        return memberOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberEntityMapper.toSiteMember(memberOrEmpty.orElseThrow()));
    }

    @Override
    @Transactional
    public SiteMember insert(SiteMember member) {
        return memberEntityMapper.toSiteMember(memberRepository.save(memberEntityMapper.createSiteMemberEntity(member)));
    }

    @Override
    @Transactional
    public SiteMember update(SiteMember member) {
        return memberEntityMapper.toSiteMember(memberRepository.save(memberEntityMapper.updateSiteMemberEntity(member)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        memberRepository.deleteByUuid(uuid);
    }
}
