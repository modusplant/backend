package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.kernel.common.util.NicknameTestUtils.testNormalUserNickname;
import static org.assertj.core.api.Assertions.assertThat;

class MemberJpaMapperImplTest implements MemberTestUtils, MemberEntityTestUtils {
    private final MemberJpaMapper memberJpaMapper = new MemberJpaMapperImpl();

    @Test
    @DisplayName("toMemberEntity(Nickname nickname)로 엔터티 반환")
    void testToMemberEntity_givenValidNickname_willReturnEntity() {
        MemberEntity memberEntity = memberJpaMapper.toMemberEntity(testNormalUserNickname);
        assertThat(memberEntity.getNickname()).isEqualTo(MEMBER_BASIC_USER_NICKNAME);
    }

    @Test
    @DisplayName("toMemberEntity(MemberId memberId, Nickname nickname)로 엔터티 반환")
    void testToMemberEntity_givenValidMemberIdAndNickname_willReturnEntity() {
        MemberEntity memberEntity = memberJpaMapper.toMemberEntity(testMemberId, testNormalUserNickname);
        assertThat(memberEntity.getUuid()).isEqualTo(MEMBER_BASIC_USER_UUID);
        assertThat(memberEntity.getNickname()).isEqualTo(MEMBER_BASIC_USER_NICKNAME);
    }

    @Test
    @DisplayName("toMember로 회원 반환")
    void testToMember_givenValidMemberEntity_willReturnMember() {
        assertThat(memberJpaMapper.toMember(createMemberBasicUserEntityWithUuid())).isEqualTo(createMember());
    }
}