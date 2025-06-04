package kr.modusplant.global.middleware.security.common.util;

import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.global.middleware.security.models.SiteMemberUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

public interface SiteMemberUserDetailsTestUtils {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    SiteMemberUserDetails testSiteMemberUserDetails = SiteMemberUserDetails.builder()
            .email("akdnjs0308@gmail.com")
            .password(passwordEncoder.encode("userPw2!"))
            .activeUuid(UUID.fromString("f56aca35-d225-4ac2-8c0a-e927cf88dc6e"))
            .nickname("테스트닉네임")
            .provider(AuthProvider.BASIC)
            .isActive(true)
            .isDisabledByLinking(false)
            .isBanned(false)
            .isDeleted(false)
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
            .build();
}
