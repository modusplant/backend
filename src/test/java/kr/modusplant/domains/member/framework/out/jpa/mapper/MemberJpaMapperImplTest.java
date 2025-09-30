package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.common.util.framework.out.persistence.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberJpaMapperImplTest implements MemberEntityTestUtils {
    private final MemberJpaMapper memberJpaMapper = new MemberJpaMapperImpl();

    @Test
    @DisplayName("toMemberEntity로 엔터티 반환")
    void testToMemberEntity_givenValidMember_willReturnEntity() {
        assertThat(memberJpaMapper.toMemberEntity(createMember())).isEqualTo(createMemberEntityWithUuid());
    }

    @Test
    @DisplayName("toMember로 회원 반환")
    void testToMember_givenValidMemberEntity_willReturnMember() {
        assertThat(memberJpaMapper.toMember(createMemberEntityWithUuid())).isEqualTo(createMember());
    }
}