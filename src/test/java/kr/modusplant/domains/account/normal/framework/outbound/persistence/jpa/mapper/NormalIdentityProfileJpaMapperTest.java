package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityProfileJpaMapperTest implements MemberEntityTestUtils {
    private final NormalIdentityProfileJpaMapper mapper = new NormalIdentityProfileJpaMapper();

    @Test
    @DisplayName("유효한 회원 엔티티로 MemberProfileEntity 반환")
    public void testToSiteMemberProfileEntity_givenValidMember_willReturnMemberProfileEntity() {
        // given
        MemberEntity savedMember = createMemberBasicUserEntityWithUuid();

        // when
        MemberProfileEntity result = mapper.toSiteMemberProfileEntity(savedMember);

        // then
        assertThat(result.getMember()).isEqualTo(savedMember);
        assertThat(result.getImagePath()).isNull();
        assertThat(result.getIntroduction()).isNull();
    }
}
