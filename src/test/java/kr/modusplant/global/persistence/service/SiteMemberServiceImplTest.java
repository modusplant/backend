package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.mapper.SiteMemberEntityMapper;
import kr.modusplant.global.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.support.context.ServiceOnlyContext;
import kr.modusplant.support.util.domain.SiteMemberTestUtils;
import kr.modusplant.support.util.entity.SiteMemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ServiceOnlyContext
class SiteMemberServiceImplTest implements SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberService memberService;
    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberServiceImplTest(SiteMemberService memberService, SiteMemberJpaRepository memberRepository) {
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

        given(memberRepository.findByUuid(member.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findAll()).willReturn(emptyList());
        willDoNothing().given(memberRepository).deleteByUuid(member.getUuid());

        // when
        member = memberService.insert(member);
        memberService.removeByUuid(member.getUuid());

        // then
        assertThat(memberService.getAll()).isEmpty();
    }
}