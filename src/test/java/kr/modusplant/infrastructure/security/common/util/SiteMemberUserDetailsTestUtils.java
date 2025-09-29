package kr.modusplant.infrastructure.security.common.util;

import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails.DefaultUserDetailsBuilder;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberConstant;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public interface SiteMemberUserDetailsTestUtils extends SiteMemberConstant {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    DefaultUserDetailsBuilder testDefaultMemberUserDetailsBuilder = DefaultUserDetails.builder()
            .email("test123@example.com")
            .password(passwordEncoder.encode("userPw2!"))
            .activeUuid(MEMBER_BASIC_USER_UUID)
            .nickname(MEMBER_BASIC_USER_NICKNAME)
            .provider(AuthProvider.BASIC)
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")));
}
