package kr.modusplant.modules.auth.social.app.service;

import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import kr.modusplant.modules.auth.social.app.dto.JwtUserPayload;
import kr.modusplant.modules.auth.social.mapper.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SocialAuthApplicationServiceTest {
    @Autowired
    private SocialAuthApplicationService socialAuthApplicationService;
    @Autowired
    private SiteMemberRepository memberRepository;
    @Autowired
    private SiteMemberAuthRepository memberAuthRepository;
    @Autowired
    private SiteMemberRoleRepository memberRoleRepository;
    @Autowired
    private final SiteMemberEntityMapper memberEntityMapper = new SiteMemberEntityMapperImpl();
    @Autowired
    private final SiteMemberAuthEntityMapper memberAuthEntityMapper = new SiteMemberAuthEntityMapperImpl();
    @Autowired
    private final SiteMemberRoleEntityMapper memberRoleEntityMapper = new SiteMemberRoleEntityMapperImpl();

    private final AuthProvider provider = AuthProvider.GOOGLE;
    private final String id = "968788539145693243421";
    private final String email = "test@example.com";
    private final String nickname = "test";

    @Test
    @DisplayName("이미 존재하는 사용자라면, 사용자 정보를 조회한다")
    void findOrCreateMemberWhenMemberExists() {
        // Given
        SiteMemberEntity memberEntity = memberRepository.save(memberEntityMapper.toSiteMemberEntity(nickname));
        SiteMemberAuthEntity memberAuthEntity = memberAuthRepository.save(
                memberAuthEntityMapper.toSiteMemberAuthEntity(memberEntity.getUuid(),provider,id,email,memberRepository)
        );
        SiteMemberRoleEntity memberRoleEntity = memberRoleRepository.save(
                memberRoleEntityMapper.toSiteMemberRoleEntity(memberEntity.getUuid(), memberRepository)
        );

        // when
        JwtUserPayload result = socialAuthApplicationService.findOrCreateMember(provider, id, email, nickname);

        // Then
        assertNotNull(result);
        assertEquals(memberEntity.getUuid(), result.memberUuid());
        assertEquals(memberAuthEntity.getActiveMember().getUuid(), result.memberUuid());
        assertEquals(memberRoleEntity.getUuid(), result.memberUuid());
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