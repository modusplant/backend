package kr.modusplant.api.crud.member.persistence.service;

import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.service.SiteMemberService;
import kr.modusplant.api.crud.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
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
public class SiteMemberServiceImpl implements SiteMemberService {

    private final SiteMemberJpaRepository memberRepository;
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
        validateExistedMemberUuid(member.getUuid());
        return memberEntityMapper.toSiteMember(memberRepository.save(memberEntityMapper.createSiteMemberEntity(member)));
    }

    @Override
    @Transactional
    public SiteMember update(SiteMember member) {
        validateNotFoundMemberUuid(member.getUuid());
        return memberEntityMapper.toSiteMember(memberRepository.save(memberEntityMapper.updateSiteMemberEntity(member)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundMemberUuid(uuid);
        memberRepository.deleteByUuid(uuid);
    }

    private void validateExistedMemberUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (memberRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberEntity.class);
        }
    }

    private void validateNotFoundMemberUuid(UUID uuid) {
        if (uuid == null || memberRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberEntity.class);
        }
    }
}
