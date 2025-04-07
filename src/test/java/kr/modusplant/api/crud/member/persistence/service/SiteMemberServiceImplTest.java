package kr.modusplant.api.crud.member.persistence.service;

import kr.modusplant.api.crud.common.context.CrudServiceOnlyContext;
import kr.modusplant.api.crud.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.api.crud.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.service.SiteMemberService;
import kr.modusplant.api.crud.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.api.crud.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@CrudServiceOnlyContext
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

    @DisplayName("회원 삽입 간 검증")
    @Test
    void validateDuringInsertTest() {
        // given & when
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));

        // then
        EntityExistsWithUuidException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberService.insert(member));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));
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

    @DisplayName("회원 갱신 간 검증")
    @Test
    void validateDuringUpdateTest() {
        // given & when
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundWithUuidException existsException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberService.update(member));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));
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

    @DisplayName("uuid로 회원 제거 간 검증")
    @Test
    void validateDuringRemoveByUuidTest() {
        // given & when
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundWithUuidException existsException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberService.removeByUuid(memberEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));
    }
}