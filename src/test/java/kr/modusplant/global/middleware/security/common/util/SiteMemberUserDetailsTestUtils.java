package kr.modusplant.global.middleware.security.common.util;

import kr.modusplant.global.middleware.security.models.DefaultUserDetails;
import kr.modusplant.global.middleware.security.models.DefaultUserDetails.DefaultUserDetailsBuilder;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public interface SiteMemberUserDetailsTestUtils extends SiteMemberTestUtils {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    DefaultUserDetailsBuilder testDefaultMemberUserDetailsBuilder = DefaultUserDetails.builder()
            .email("test123@example.com")
            .password(passwordEncoder.encode("userPw2!"))
            .activeUuid(memberBasicUserWithUuid.getUuid())
            .nickname(memberBasicUserWithUuid.getNickname())
            .provider(AuthProvider.BASIC)
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")));
}
