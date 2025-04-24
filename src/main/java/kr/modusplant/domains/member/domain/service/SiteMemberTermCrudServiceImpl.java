package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberTermCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberTermEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberTermEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermRepository;
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
public class SiteMemberTermCrudServiceImpl implements SiteMemberTermCrudService {
    private final SiteMemberTermRepository memberTermRepository;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermEntityMapper = new SiteMemberTermEntityMapperImpl();

    @Override
    public List<SiteMemberTerm> getAll() {
        return memberTermRepository.findAll().stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public List<SiteMemberTerm> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion) {
        return memberTermRepository.findByAgreedTermsOfUseVersion(agreedTermsOfUseVersion).stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public List<SiteMemberTerm> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion) {
        return memberTermRepository.findByAgreedPrivacyPolicyVersion(agreedPrivacyPolicyVersion).stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public List<SiteMemberTerm> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion) {
        return memberTermRepository.findByAgreedAdInfoReceivingVersion(agreedAdInfoReceivingVersion).stream().map(memberTermEntityMapper::toSiteMemberTerm).toList();
    }

    @Override
    public Optional<SiteMemberTerm> getByUuid(UUID uuid) {
        Optional<SiteMemberTermEntity> memberTermOrEmpty = memberTermRepository.findByUuid(uuid);
        return memberTermOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberTermEntityMapper.toSiteMemberTerm(memberTermOrEmpty.orElseThrow()));
    }

    @Override
    public Optional<SiteMemberTerm> getByMember(SiteMember member) {
        Optional<SiteMemberTermEntity> memberTermOrEmpty = memberTermRepository.findByUuid(member.getUuid());
        return memberTermOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberTermEntityMapper.toSiteMemberTerm(memberTermOrEmpty.orElseThrow()));
    }

    @Override
    @Transactional
    public SiteMemberTerm insert(SiteMemberTerm memberTerm) {
        return memberTermEntityMapper.toSiteMemberTerm(memberTermRepository.save(memberTermEntityMapper.createSiteMemberTermEntity(memberTerm, memberRepository)));
    }

    @Override
    @Transactional
    public SiteMemberTerm update(SiteMemberTerm memberTerm) {
        return memberTermEntityMapper.toSiteMemberTerm(memberTermRepository.save(memberTermEntityMapper.updateSiteMemberTermEntity(memberTerm, memberRepository)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        memberTermRepository.deleteByUuid(uuid);
    }
}
