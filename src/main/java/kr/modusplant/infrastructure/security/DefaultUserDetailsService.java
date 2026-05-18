package kr.modusplant.infrastructure.security;

import jakarta.transaction.Transactional;
import kr.modusplant.domains.account.identity.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.identity.framework.out.jpa.repository.MemberAuthJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.infrastructure.security.exception.AccountStateException;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultUserDetailsService implements UserDetailsService {

    private final MemberJpaRepository memberRepository;
    private final MemberAuthJpaRepository memberAuthRepository;

    @Override
    public DefaultUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        MemberAuthEntity auth = memberAuthRepository
                .findByEmail(email).orElseThrow(
                        () -> new AccountStateException(EntityErrorCode.NOT_FOUND_MEMBER_AUTH));
        MemberEntity member = memberRepository
                .findByUuid(auth.getMember().getUuid()).orElseThrow(
                        () -> new AccountStateException(EntityErrorCode.NOT_FOUND_MEMBER));

        return DefaultUserDetails.builder()
                .email(auth.getEmail())
                .password(auth.getPw())
                .uuid(auth.getMember().getUuid())
                .nickname(member.getNickname())
                .provider(auth.getProvider())
                .isActive(member.getIsActive())
                .isBanned(member.getIsBanned())
                .authorities(List.of(new SimpleGrantedAuthority(member.getRole().getValue())))
                .build();
    }
}