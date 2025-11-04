package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.infrastructure.security.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityRoleJpaMapperTest {
    private final NormalIdentityRoleJpaMapper mapper = new NormalIdentityRoleJpaMapperImpl();

    @Test
    @DisplayName("유효한 사용자 엔티티를 사용자 역할 엔티티로 전환")
    public void testToSiteMemberRoleEntity_givenValidSiteMemberEntity_willReturnSiteMemberRoleEntity() {
        // given
        SiteMemberEntity testMember = SiteMemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME).build();

        // when
        SiteMemberRoleEntity result = mapper.toSiteMemberRoleEntity(testMember);

        // then
        assertThat(result.getMember()).isEqualTo(testMember);
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }
}
