package kr.modusplant.domains.member.framework.outbound.persistence.jpa.repository;

import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.repository.supers.MemberJpaRepository;
import kr.modusplant.domains.member.test.utils.framework.MemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class MemberRepositoryImplTest implements MemberEntityTestUtils {
    private final MemberJpaMapperImpl memberJpaMapper = new MemberJpaMapperImpl();
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberRepositoryImpl memberRepositoryImpl = new MemberRepositoryImpl(memberJpaMapper, memberJpaRepository);

    @Test
    @DisplayName("save로 Member 반환")
    void callSave_withValidMember_returnsMember() {
        // given
        Member member = createMember();
        MemberEntity memberEntity = createMemberEntity();
        given(memberJpaRepository.save(memberEntity)).willReturn(memberEntity);

        // when & then
        assertThat(memberRepositoryImpl.save(member)).isEqualTo(member);
    }

}