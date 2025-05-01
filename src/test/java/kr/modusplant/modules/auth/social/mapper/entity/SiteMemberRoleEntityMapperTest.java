package kr.modusplant.modules.auth.social.mapper.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SiteMemberRoleEntityMapperTest {
    private final SiteMemberRoleEntityMapper mapper = new SiteMemberRoleEntityMapperImpl();
    private SiteMemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(SiteMemberRepository.class);
    }

    @Test
    @DisplayName("UUID를 기반으로 SiteMemberRoleEntity 생성")
    void toSiteMemberRoleEntityTest() {
        // given
        UUID memberUuid = UUID.randomUUID();
        SiteMemberEntity mockMember = SiteMemberEntity.builder()
                .uuid(memberUuid)
                .nickname("test-user")
                .loggedInAt(LocalDateTime.now())
                .build();

        Mockito.when(memberRepository.findByUuid(memberUuid))
                .thenReturn(Optional.of(mockMember));

        // when
        SiteMemberRoleEntity roleEntity = mapper.toSiteMemberRoleEntity(memberUuid, memberRepository);

        // then
        assertThat(roleEntity.getMember().getUuid()).isEqualTo(memberUuid);
        assertThat(roleEntity.getRole()).isEqualTo(Role.ROLE_USER);
    }
}