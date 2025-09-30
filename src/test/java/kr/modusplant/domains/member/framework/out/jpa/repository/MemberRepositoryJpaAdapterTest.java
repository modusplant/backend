package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.common.utils.framework.out.persistence.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberRepositoryJpaAdapterTest implements MemberEntityTestUtils {
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final MemberRepositoryJpaAdapter memberRepositoryJpaAdapter = new MemberRepositoryJpaAdapter(memberJpaMapper, memberJpaRepository);

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용할 때)")
    void testGetByNickname_givenValidMemberNickname_willReturnOptionalAvailableMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.of(createMemberEntityWithUuid()));

        // when & then
        assertThat(memberRepositoryJpaAdapter.getByNickname(testMemberNickname)).isEqualTo(Optional.of(createMember()));
    }

    @Test
    @DisplayName("getByNickname으로 가용한 Member 반환(가용하지 않을 때)")
    void testGetByNickname_givenValidMemberNickname_willReturnOptionalEmptyMember() {
        // given
        given(memberJpaRepository.findByNickname(any())).willReturn(Optional.empty());

        // when & then
        assertThat(memberRepositoryJpaAdapter.getByNickname(testMemberNickname)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("save로 Member 반환")
    void testSave_givenValidMember_willReturn() {
        // given
        Member member = createMember();
        SiteMemberEntity memberEntity = createMemberEntityWithUuid();
        given(memberJpaRepository.save(memberEntity)).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryJpaAdapter.save(member)).isEqualTo(member);
    }
}