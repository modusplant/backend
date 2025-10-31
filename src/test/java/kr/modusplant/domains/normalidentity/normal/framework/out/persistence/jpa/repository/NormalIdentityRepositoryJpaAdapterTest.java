package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityAuthJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityRoleJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper.NormalIdentityTermJpaMapper;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityAuthJpaRepository;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityJpaRepository;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityRoleJpaRepository;
import kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.repository.supers.NormalIdentityTermJpaRepository;
import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberAuthEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberRoleEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberTermConstant.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

public class NormalIdentityRepositoryJpaAdapterTest implements SiteMemberEntityTestUtils,
        SiteMemberAuthEntityTestUtils, SiteMemberRoleEntityTestUtils, SiteMemberTermEntityTestUtils {
    private final NormalIdentityJpaRepository identityRepository = Mockito.mock(NormalIdentityJpaRepository.class);
    private final NormalIdentityAuthJpaRepository authRepository = Mockito.mock(NormalIdentityAuthJpaRepository.class);
    private final NormalIdentityRoleJpaRepository roleRepository = Mockito.mock(NormalIdentityRoleJpaRepository.class);
    private final NormalIdentityTermJpaRepository termRepository = Mockito.mock(NormalIdentityTermJpaRepository.class);

    private final NormalIdentityJpaMapper identityMapper = Mockito.mock(NormalIdentityJpaMapper.class);
    private final NormalIdentityAuthJpaMapper authMapper = Mockito.mock(NormalIdentityAuthJpaMapper.class);
    private final NormalIdentityRoleJpaMapper roleMapper = Mockito.mock(NormalIdentityRoleJpaMapper.class);
    private final NormalIdentityTermJpaMapper termMapper = Mockito.mock(NormalIdentityTermJpaMapper.class);
    private final NormalIdentityRepositoryJpaAdapter adapter = new NormalIdentityRepositoryJpaAdapter(identityRepository,
            authRepository, roleRepository, termRepository, identityMapper, authMapper, roleMapper, termMapper);

    private SiteMemberEntity memberToBeSaved;
    private SiteMemberEntity savedMember;
    private SiteMemberAuthEntity authEntityToBeSaved;
    private SiteMemberRoleEntity roleEntityToBeSaved;
    private SiteMemberTermEntity termEntityToBeSaved;
    private SignUpData sign;

    @BeforeEach
    void setUp() {
        sign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION);

        memberToBeSaved = SiteMemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME).build();

        savedMember = createMemberBasicUserEntityWithUuid();

        authEntityToBeSaved = createMemberAuthBasicUserEntityBuilder()
                .originalMember(createMemberBasicUserEntityWithUuid())
                .activeMember(createMemberBasicUserEntityWithUuid())
                .email(sign.getCredentials().getEmail().getEmail())
                .pw(sign.getCredentials().getPassword().getPassword())
                .provider(AuthProvider.BASIC).build();

        roleEntityToBeSaved = createMemberRoleUserEntityWithUuid();

        termEntityToBeSaved = createMemberTermUserEntityWithUuid();
    }

    @Test
    @DisplayName("유효한 회원가입 데이터를 받아 리포지토리와 매퍼 클래스들을 실행")
    void testSave_givenValidSignUpData_willRunRepositoriesAndMappers() {
        // given
        given(identityMapper.toSiteMemberEntity(sign.getNickname())).willReturn(memberToBeSaved);
        given(identityRepository.save(memberToBeSaved)).willReturn(savedMember);

        given(authMapper.toSiteMemberAuthEntity(savedMember, sign)).willReturn(authEntityToBeSaved);
        given(authRepository.save(authEntityToBeSaved)).willReturn(null);

        given(roleMapper.toSiteMemberRoleEntity(savedMember)).willReturn(roleEntityToBeSaved);
        given(roleRepository.save(roleEntityToBeSaved)).willReturn(null);

        given(termMapper.toSiteMemberTermEntity(savedMember, sign)).willReturn(termEntityToBeSaved);
        given(termRepository.save(termEntityToBeSaved)).willReturn(null);

        // when
        adapter.save(sign);

        // then
        Mockito.verify(identityMapper, times(1)).toSiteMemberEntity(sign.getNickname());
        Mockito.verify(identityRepository, times(1)).save(memberToBeSaved);

        Mockito.verify(authMapper, times(1)).toSiteMemberAuthEntity(savedMember, sign);
        Mockito.verify(authRepository, times(1)).save(authEntityToBeSaved);

        Mockito.verify(roleMapper, times(1)).toSiteMemberRoleEntity(savedMember);
        Mockito.verify(roleRepository, times(1)).save(roleEntityToBeSaved);

        Mockito.verify(termMapper, times(1)).toSiteMemberTermEntity(savedMember, sign);
        Mockito.verify(termRepository, times(1)).save(termEntityToBeSaved);
    }
}
