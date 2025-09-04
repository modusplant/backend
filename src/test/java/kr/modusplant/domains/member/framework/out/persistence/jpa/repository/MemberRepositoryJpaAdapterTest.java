package kr.modusplant.domains.member.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.framework.out.persistence.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.persistence.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.framework.out.persistence.jpa.repository.supers.MemberJpaRepository;
import kr.modusplant.domains.member.common.utils.framework.MemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MemberRepositoryJpaAdapterTest implements MemberEntityTestUtils {
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberRepositoryJpaAdapter memberRepositoryJpaAdapter = new MemberRepositoryJpaAdapter(memberJpaMapper, memberJpaRepository);

    @Test
    @DisplayName("updateNickname로 Member 반환")
    void callUpdateNickname_withValidMember_returnsMember() {
        // given
        Member member = createMember();
        MemberEntity memberEntity = createMemberEntityWithUuid();
        given(memberJpaRepository.save(memberEntity)).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryJpaAdapter.updateNickname(member)).isEqualTo(member);
    }

    @Test
    @DisplayName("save로 Member 반환")
    void callSave_withValidMember_returns() {
        // given
        Member member = createMember();
        MemberEntity memberEntity = createMemberEntityWithUuid();
        given(memberJpaRepository.save(memberEntity)).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryJpaAdapter.save(member)).isEqualTo(member);
    }
}