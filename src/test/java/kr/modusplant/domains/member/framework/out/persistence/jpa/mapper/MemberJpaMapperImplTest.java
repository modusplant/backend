package kr.modusplant.domains.member.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.member.framework.out.persistence.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.domains.member.common.utils.framework.MemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberJpaMapperImplTest implements MemberEntityTestUtils {
    private final MemberJpaMapper memberJpaMapper = new MemberJpaMapperImpl();

    @Test
    @DisplayName("toMemberEntity로 엔터티 반환")
    void callToMemberEntity_withValidMember_returnsEntity() {
        assertThat(memberJpaMapper.toMemberEntity(createMember())).isEqualTo(createMemberEntity());
    }

    @Test
    @DisplayName("toMember로 회원 반환")
    void callToMember_withValidMemberEntity_returnsMember() {
        assertThat(memberJpaMapper.toMember(createMemberEntity())).isEqualTo(createMember());
    }
}