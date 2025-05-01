package kr.modusplant.modules.auth.social.mapper.entity;

import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryOnlyContext
class SiteMemberAuthEntityMapperTest {
    private final SiteMemberAuthEntityMapper mapper = new SiteMemberAuthEntityMapperImpl();
    private SiteMemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(SiteMemberRepository.class);
    }

    @Test
    @DisplayName("UUID 기반으로 SiteMemberAuthEntity 생성")
    void toSiteMemberAuthEntityTest() {
        // given
        UUID uuid = UUID.randomUUID();
        SiteMemberEntity member = SiteMemberEntity.builder()
                .uuid(uuid)
                .nickname("tester")
                .loggedInAt(LocalDateTime.now())
                .build();

        Mockito.when(memberRepository.findByUuid(uuid)).thenReturn(Optional.of(member));

        // when
        SiteMemberAuthEntity authEntity = mapper.toSiteMemberAuthEntity(
                uuid,
                AuthProvider.GOOGLE,
                "googleId123",
                "test@example.com",
                memberRepository
        );

        // then
        assertThat(authEntity.getActiveMember().getUuid()).isEqualTo(uuid);
        assertThat(authEntity.getOriginalMember().getUuid()).isEqualTo(uuid);
        assertThat(authEntity.getProvider()).isEqualTo(AuthProvider.GOOGLE);
        assertThat(authEntity.getProviderId()).isEqualTo("googleId123");
        assertThat(authEntity.getEmail()).isEqualTo("test@example.com");
    }

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