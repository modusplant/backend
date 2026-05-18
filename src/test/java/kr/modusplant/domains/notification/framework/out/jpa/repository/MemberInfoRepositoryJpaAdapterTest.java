package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberInfoRepositoryJpaAdapterTest {
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberInfoRepositoryJpaAdapter memberInfoRepositoryJpaAdapter = new MemberInfoRepositoryJpaAdapter(memberJpaRepository);

    @Test
    @DisplayName("회원 UUID로 닉네임을 조회한다")
    void testGetNicknameByUuid_givenValidUuid_willReturnNickname() {
        // given
        MemberEntity memberEntity = Mockito.mock(MemberEntity.class);
        String nickname = "테스트유저";

        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(memberEntity.getNickname()).willReturn(nickname);

        // when
        String result = memberInfoRepositoryJpaAdapter.getNicknameByUuid(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEqualTo(nickname);
    }

    @Test
    @DisplayName("회원이 존재하지 않으면 NotFoundEntityException이 발생한다")
    void testGetNicknameByUuid_givenNonExistentMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(any())).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundEntityException.class, () ->
                memberInfoRepositoryJpaAdapter.getNicknameByUuid(UUID.randomUUID()));
    }

}