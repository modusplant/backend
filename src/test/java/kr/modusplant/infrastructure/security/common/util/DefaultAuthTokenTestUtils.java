package kr.modusplant.infrastructure.security.common.util;

import kr.modusplant.infrastructure.security.models.DefaultAuthToken;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.AuthProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;

public interface DefaultAuthTokenTestUtils {

    DefaultAuthToken testUnauthenticatedDefaultAuthToken =
            new DefaultAuthToken("test123@example.com", "userPw2!");

    DefaultAuthToken testAuthenticatedDefaultAuthToken =
            new DefaultAuthToken(
                    DefaultUserDetails.builder()
                            .email("test123@example.com")
                            .uuid(MEMBER_BASIC_USER_UUID)
                            .nickname(MEMBER_BASIC_USER_NICKNAME)
                            .provider(AuthProvider.BASIC)
                            .isActive(true)
                            .isBanned(false)
                            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                            .build(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
}
