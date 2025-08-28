package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRepository;
import kr.modusplant.framework.out.persistence.repository.SiteMemberTermRepository;
import kr.modusplant.legacy.domains.common.app.service.supers.UuidCrudApplicationService;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberTermValidationService;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberTermAppInfraMapper;
import kr.modusplant.legacy.domains.member.mapper.SiteMemberTermAppInfraMapperImpl;
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
public class SiteMemberTermApplicationService implements UuidCrudApplicationService<SiteMemberTermResponse, SiteMemberTermInsertRequest, SiteMemberTermUpdateRequest> {

    private final SiteMemberTermValidationService memberTermValidationService;
    private final SiteMemberValidationService memberValidationService;
    private final SiteMemberTermRepository memberTermRepository;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberTermAppInfraMapper memberTermAppInfraMapper = new SiteMemberTermAppInfraMapperImpl();

    @Override
    public List<SiteMemberTermResponse> getAll() {
        return memberTermRepository.findAll().stream().map(memberTermAppInfraMapper::toMemberTermResponse).toList();
    }

    public List<SiteMemberTermResponse> getByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion) {
        return memberTermRepository.findByAgreedTermsOfUseVersion(agreedTermsOfUseVersion).stream().map(memberTermAppInfraMapper::toMemberTermResponse).toList();
    }

    public List<SiteMemberTermResponse> getByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion) {
        return memberTermRepository.findByAgreedPrivacyPolicyVersion(agreedPrivacyPolicyVersion).stream().map(memberTermAppInfraMapper::toMemberTermResponse).toList();
    }

    public List<SiteMemberTermResponse> getByAgreedAdInfoReceivingVersion(String agreedAdInfoReceivingVersion) {
        return memberTermRepository.findByAgreedAdInfoReceivingVersion(agreedAdInfoReceivingVersion).stream().map(memberTermAppInfraMapper::toMemberTermResponse).toList();
    }

    @Override
    public Optional<SiteMemberTermResponse> getByUuid(UUID uuid) {
        Optional<SiteMemberTermEntity> memberTermOrEmpty = memberTermRepository.findByUuid(uuid);
        return memberTermOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberTermAppInfraMapper.toMemberTermResponse(memberTermOrEmpty.orElseThrow()));
    }

    public Optional<SiteMemberTermResponse> getByMember(SiteMemberEntity member) {
        Optional<SiteMemberTermEntity> memberTermOrEmpty = memberTermRepository.findByUuid(member.getUuid());
        return memberTermOrEmpty.isEmpty() ? Optional.empty() : Optional.of(memberTermAppInfraMapper.toMemberTermResponse(memberTermOrEmpty.orElseThrow()));
    }

    @Transactional
    @Override
    public SiteMemberTermResponse insert(SiteMemberTermInsertRequest memberTermInsertRequest) {
        UUID uuid = memberTermInsertRequest.uuid();
        memberValidationService.validateNotFoundUuid(uuid);
        memberTermValidationService.validateExistedUuid(uuid);
        return memberTermAppInfraMapper.toMemberTermResponse(memberTermRepository.save(memberTermAppInfraMapper.toMemberTermEntity(memberTermInsertRequest, memberRepository)));
    }

    @Transactional
    @Override
    public SiteMemberTermResponse update(SiteMemberTermUpdateRequest memberTermUpdateRequest) {
        UUID uuid = memberTermUpdateRequest.uuid();
        memberValidationService.validateNotFoundUuid(uuid);
        memberTermValidationService.validateNotFoundUuid(uuid);
        SiteMemberTermEntity memberTermEntity = memberTermRepository.findByUuid(uuid).orElseThrow();
        memberTermEntity.updateAgreedTermsOfUseVersion(memberTermUpdateRequest.agreedTermsOfUseVersion());
        memberTermEntity.updateAgreedPrivacyPolicyVersion(memberTermUpdateRequest.agreedPrivacyPolicyVersion());
        memberTermEntity.updateAgreedAdInfoReceivingVersion(memberTermUpdateRequest.agreedAdInfoReceivingVersion());
        return memberTermAppInfraMapper.toMemberTermResponse(memberTermRepository.save(memberTermEntity));
    }

    @Transactional
    @Override
    public void removeByUuid(UUID uuid) {
        memberTermValidationService.validateNotFoundUuid(uuid);
        memberTermRepository.deleteByUuid(uuid);
    }
}
