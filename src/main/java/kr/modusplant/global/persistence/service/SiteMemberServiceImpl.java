package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.SiteMemberEntityMapper;
import kr.modusplant.global.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
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
        validateExistedEntity(member.getUuid());
        return memberEntityMapper.toSiteMember(memberRepository.save(memberEntityMapper.createSiteMemberEntity(member)));
    }

    @Override
    @Transactional
    public SiteMember update(SiteMember member) {
        validateNotFoundEntity(member.getUuid());
        return memberEntityMapper.toSiteMember(memberRepository.save(memberEntityMapper.updateSiteMemberEntity(member)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundEntity(uuid);
        memberRepository.deleteByUuid(uuid);
    }

    private void validateExistedEntity(UUID uuid) {
        if (memberRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, SiteMemberEntity.class);
        }
    }

    private void validateNotFoundEntity(UUID uuid) {
        if (memberRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, SiteMemberEntity.class);
        }
    }
}
