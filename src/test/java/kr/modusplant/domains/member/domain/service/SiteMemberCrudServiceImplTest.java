package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainServiceOnlyContext
class SiteMemberCrudServiceImplTest implements SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberCrudService memberService;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberCrudServiceImplTest(SiteMemberCrudService memberService, SiteMemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 얻기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByUuid(member.getUuid()).orElseThrow()).isEqualTo(member);
    }

    @DisplayName("nickname으로 회원 얻기")
    @Test
    void getByNicknameTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByNickname(memberEntity.getNickname())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByNickname(memberEntity.getNickname()).getFirst()).isEqualTo(member);
    }

    @DisplayName("birthDate으로 회원 얻기")
    @Test
    void getByBirthDateTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByBirthDate(memberEntity.getBirthDate())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByBirthDate(memberEntity.getBirthDate()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isActive으로 회원 얻기")
    @Test
    void getByIsActiveTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByIsActive(memberEntity.getIsActive())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByIsActive(memberEntity.getIsActive()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isDisabledByLinking으로 회원 얻기")
    @Test
    void getByIsDisabledByLinkingTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByIsDisabledByLinking(memberEntity.getIsDisabledByLinking())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByIsDisabledByLinking(memberEntity.getIsDisabledByLinking()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isBanned로 회원 얻기")
    @Test
    void getByIsBannedTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByIsBanned(memberEntity.getIsBanned())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByIsBanned(memberEntity.getIsBanned()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isDeleted로 회원 얻기")
    @Test
    void getByIsDeletedTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByIsDeleted(memberEntity.getIsDeleted())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByIsDeleted(memberEntity.getIsDeleted()).getFirst()).isEqualTo(member);
    }

    @DisplayName("loggedInAt으로 회원 얻기")
    @Test
    void getByLoggedInAtTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty());
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRepository.findByLoggedInAt(memberEntity.getLoggedInAt())).willReturn(List.of(memberEntity));

        // when
        member = memberService.insert(member);

        // then
        assertThat(memberService.getByLoggedInAt(memberEntity.getLoggedInAt()).getFirst()).isEqualTo(member);
    }

    @DisplayName("빈 회원 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        UUID uuid = memberBasicUserWithUuid.getUuid();

        // getByUuid
        // given & when
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberService.getByUuid(uuid)).isEmpty();
    }

    @DisplayName("회원 갱신")
    @Test
    void updateTest() {
        // given
        String updatedNickname = "갱신된 닉네임";
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberEntity updatedMemberEntity = SiteMemberEntity.builder().memberEntity(memberEntity).nickname(updatedNickname).build();
        SiteMember updatedMember = memberMapper.toSiteMember(updatedMemberEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity).willReturn(updatedMemberEntity);
        given(memberRepository.findByNickname(updatedNickname)).willReturn(List.of(updatedMemberEntity));

        // when
        memberService.insert(member);
        memberService.update(updatedMember);

        // then
        assertThat(memberService.getByNickname(updatedNickname).getFirst()).isEqualTo(updatedMember);
    }

    @DisplayName("uuid로 회원 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        UUID uuid = member.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity)).willReturn(Optional.empty());
        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        willDoNothing().given(memberRepository).deleteByUuid(uuid);

        // when
        memberService.insert(member);
        memberService.removeByUuid(uuid);

        // then
        assertThat(memberService.getByUuid(uuid)).isEmpty();
    }
}