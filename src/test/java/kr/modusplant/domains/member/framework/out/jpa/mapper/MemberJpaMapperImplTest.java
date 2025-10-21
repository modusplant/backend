package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.common.util.framework.out.persistence.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;
import static org.assertj.core.api.Assertions.assertThat;

class MemberJpaMapperImplTest implements MemberEntityTestUtils {
    private final MemberJpaMapper memberJpaMapper = new MemberJpaMapperImpl();

    @Test
    @DisplayName("toMemberEntity(MemberNickname memberNickname)로 엔터티 반환")
    void testToMemberEntity_givenValidMemberNickname_willReturnEntity() {
        SiteMemberEntity memberEntity = memberJpaMapper.toMemberEntity(testMemberNickname);
        assertThat(memberEntity.getNickname()).isEqualTo(TEST_MEMBER_NICKNAME_STRING);
    }

    @Test
    @DisplayName("toMemberEntity(MemberId memberId, MemberNickname memberNickname)로 엔터티 반환")
    void testToMemberEntity_givenValidMemberIdAndNickname_willReturnEntity() {
        SiteMemberEntity memberEntity = memberJpaMapper.toMemberEntity(testMemberId, testMemberNickname);
        assertThat(memberEntity.getUuid()).isEqualTo(TEST_MEMBER_ID_UUID);
        assertThat(memberEntity.getNickname()).isEqualTo(TEST_MEMBER_NICKNAME_STRING);
    }

    @Test
    @DisplayName("toMember로 회원 반환")
    void testToMember_givenValidMemberEntity_willReturnMember() {
        assertThat(memberJpaMapper.toMember(createMemberEntityWithUuid())).isEqualTo(createMember());
    }
}