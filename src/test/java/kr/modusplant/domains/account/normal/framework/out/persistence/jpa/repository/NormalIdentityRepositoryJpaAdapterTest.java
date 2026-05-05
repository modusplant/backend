package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityAuthJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityProfileJpaMapper;
import kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper.NormalIdentityTermJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberAuthEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberProfileJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberTermJpaRepository;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

public class NormalIdentityRepositoryJpaAdapterTest implements SiteMemberEntityTestUtils,
        SiteMemberAuthEntityTestUtils, SiteMemberTermEntityTestUtils {
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final SiteMemberAuthJpaRepository authJpaRepository = Mockito.mock(SiteMemberAuthJpaRepository.class);
    private final SiteMemberTermJpaRepository termJpaRepository = Mockito.mock(SiteMemberTermJpaRepository.class);
    private final SiteMemberProfileJpaRepository profileJpaRepository = Mockito.mock(SiteMemberProfileJpaRepository.class);

    private final NormalIdentityJpaMapper identityMapper = Mockito.mock(NormalIdentityJpaMapper.class);
    private final NormalIdentityAuthJpaMapper authMapper = Mockito.mock(NormalIdentityAuthJpaMapper.class);
    private final NormalIdentityTermJpaMapper termMapper = Mockito.mock(NormalIdentityTermJpaMapper.class);
    private final NormalIdentityProfileJpaMapper profileMapper = Mockito.mock(NormalIdentityProfileJpaMapper.class);

    PasswordEncoder encoder = Mockito.mock(BCryptPasswordEncoder.class);

    private final NormalIdentityRepositoryJpaAdapter adapter = new NormalIdentityRepositoryJpaAdapter(
            memberJpaRepository, authJpaRepository, termJpaRepository, profileJpaRepository,
            identityMapper, authMapper, termMapper, profileMapper, encoder);

    private SiteMemberEntity memberToBeSaved;
    private SiteMemberEntity savedMember;
    private SiteMemberAuthEntity authEntityToBeSaved;
    private SiteMemberTermEntity termEntityToBeSaved;
    private SignUpData sign;

    @BeforeEach
    void setUp() {
        sign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);

        memberToBeSaved = SiteMemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME).build();

        savedMember = createMemberBasicUserEntityWithUuid();

        authEntityToBeSaved = createMemberAuthBasicUserEntityBuilder()
                .member(createMemberBasicUserEntityWithUuid())
                .email(sign.getNormalCredentials().getEmail().getValue())
                .pw(sign.getNormalCredentials().getPassword().getValue())
                .provider(AuthProvider.BASIC).build();

        termEntityToBeSaved = createMemberTermUserEntityWithUuid();
    }

    @Test
    @DisplayName("유효한 회원가입 데이터를 받아 리포지토리와 매퍼 클래스들을 실행")
    void testSave_givenValidSignUpData_willRunRepositoriesAndMappers() {
        // given
        given(identityMapper.toSiteMemberEntity(sign.getNickname())).willReturn(memberToBeSaved);
        given(memberJpaRepository.save(memberToBeSaved)).willReturn(savedMember);

        given(authMapper.toSiteMemberAuthEntity(savedMember, sign)).willReturn(authEntityToBeSaved);
        given(authJpaRepository.save(authEntityToBeSaved)).willReturn(null);

        given(termMapper.toSiteMemberTermEntity(savedMember, sign)).willReturn(termEntityToBeSaved);
        given(termJpaRepository.save(termEntityToBeSaved)).willReturn(null);

        // when
        adapter.save(sign);

        // then
        Mockito.verify(identityMapper, times(1)).toSiteMemberEntity(sign.getNickname());
        Mockito.verify(memberJpaRepository, times(1)).save(memberToBeSaved);

        Mockito.verify(authMapper, times(1)).toSiteMemberAuthEntity(savedMember, sign);
        Mockito.verify(authJpaRepository, times(1)).save(authEntityToBeSaved);

        Mockito.verify(termMapper, times(1)).toSiteMemberTermEntity(savedMember, sign);
        Mockito.verify(termJpaRepository, times(1)).save(termEntityToBeSaved);
    }
}
