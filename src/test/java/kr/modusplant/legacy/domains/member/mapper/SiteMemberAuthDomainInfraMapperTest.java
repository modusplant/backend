package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class SiteMemberAuthDomainInfraMapperTest {
    private final SiteMemberAuthDomainInfraMapper mapper = new SiteMemberAuthDomainInfraMapperImpl();

    @Test
    @DisplayName("SiteMemberAuthEntity → SiteMemberAuth 매핑 테스트")
    void toSiteMemberAuthTest() {
        // given
        UUID uuid = UUID.randomUUID();
        SiteMemberEntity member = SiteMemberEntity.builder()
                .uuid(uuid)
                .nickname("tester")
                .loggedInAt(LocalDateTime.now())
                .build();

        SiteMemberAuthEntity authEntity = SiteMemberAuthEntity.builder()
                .activeMember(member)
                .originalMember(member)
                .email("test@example.com")
                .provider(AuthProvider.GOOGLE)
                .providerId("googleId123")
                .build();

        // when
        SiteMemberAuth domain = mapper.toSiteMemberAuth(authEntity);

        // then
        assertThat(domain.getActiveMemberUuid()).isEqualTo(uuid);
        assertThat(domain.getOriginalMemberUuid()).isEqualTo(uuid);
    }
}