package kr.modusplant.modules.auth.social.app.service;

import kr.modusplant.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.mapper.SiteMemberAuthDomainInfraMapper;
import kr.modusplant.domains.member.mapper.SiteMemberAuthDomainInfraMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.auth.social.app.dto.JwtUserPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class SocialAuthApplicationServiceTest implements SiteMemberEntityTestUtils, SiteMemberAuthEntityTestUtils {
    @Autowired
    private SocialAuthApplicationService socialAuthApplicationService;
    @Autowired
    private SiteMemberRepository memberRepository;
    @Autowired
    private SiteMemberAuthRepository memberAuthRepository;
    @Autowired
    private SiteMemberRoleRepository memberRoleRepository;
    @Autowired
    private final SiteMemberAuthDomainInfraMapper memberAuthEntityMapper = new SiteMemberAuthDomainInfraMapperImpl();

    private final AuthProvider provider = AuthProvider.GOOGLE;
    private final String id = "639796866968871286823";
    private final String email = "Test3gOogleUsser@gmail.com";
    private final String nickname = "구글 유저";

    @Test
    @DisplayName("이미 존재하는 사용자라면, 사용자 정보를 조회한다")
    void findOrCreateMemberWhenMemberExists() {
        // Given
        SiteMemberEntity savedMemberEntity = memberRepository.save(createMemberGoogleUserEntity());

        SiteMemberAuthEntity memberAuthEntity = createMemberAuthGoogleUserEntityBuilder()
                .activeMember(savedMemberEntity)
                .originalMember(savedMemberEntity)
                .build();
        SiteMemberAuthEntity savedMemberAuthEntity = memberAuthRepository.save(memberAuthEntity);

        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder()
                .member(savedMemberEntity)
                .role(Role.USER).build();
        SiteMemberRoleEntity savedMemberRoleEntity = memberRoleRepository.save(memberRoleEntity);

        // when
        JwtUserPayload result = socialAuthApplicationService.findOrCreateMember(provider, id, email, nickname);

        // Then
        System.out.println(result.nickname());

        assertNotNull(result);
        assertEquals(savedMemberEntity.getUuid(), result.memberUuid());
        assertEquals(savedMemberAuthEntity.getOriginalMember().getUuid(), result.memberUuid());
        assertEquals(savedMemberRoleEntity.getUuid(), result.memberUuid());
    }

    @Test
    @DisplayName("존재하지 않는 사용자라면, 사용자를 생성한다")
    void findOrCreateMemberWhenMemberDoesNotExists() {
        // When
        JwtUserPayload result = socialAuthApplicationService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(nickname, result.nickname());
        assertNotNull(result.memberUuid());

        SiteMemberAuthEntity memberAuthEntity = memberAuthRepository.findByProviderAndProviderId(provider,id)
                .orElseThrow(() -> new AssertionError("SiteMemberAuth not found"));
        assertEquals(email, memberAuthEntity.getEmail());
        assertEquals(result.memberUuid(),memberAuthEntity.getActiveMember().getUuid());

        SiteMemberRoleEntity memberRoleEntity = memberRoleRepository.findByUuid(result.memberUuid())
                .orElseThrow(() -> new AssertionError("SiteMemberRole not found"));
        assertEquals(result.role(), memberRoleEntity.getRole());
    }
}