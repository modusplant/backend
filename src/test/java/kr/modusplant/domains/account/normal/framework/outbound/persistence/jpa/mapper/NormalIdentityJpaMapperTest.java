package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityJpaMapperTest implements NicknameTestUtils {
    private final NormalIdentityJpaMapper mapper = new NormalIdentityJpaMapper();

    @Test
    @DisplayName("유효한 닉네임으로 MemberEntity 반환")
    public void testToMemberEntity_givenValidNickname_willReturnMemberEntity() {
        // given & when
        MemberEntity result = mapper.toMemberEntity(testNormalUserNickname);

        // then
        assertThat(result.getNickname()).isEqualTo(testNormalUserNickname.getValue());
    }
}
