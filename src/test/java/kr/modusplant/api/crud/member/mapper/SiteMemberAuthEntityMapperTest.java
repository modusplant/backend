package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.common.util.domain.SiteMemberAuthTestUtils;
import kr.modusplant.api.crud.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberAuthCrudJpaRepository;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberAuthEntityMapperTest implements SiteMemberAuthTestUtils, SiteMemberAuthEntityTestUtils {

    private final SiteMemberAuthCrudJpaRepository memberAuthRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberAuthEntityMapper memberAuthMapper = new SiteMemberAuthEntityMapperImpl();

    @Autowired
    SiteMemberAuthEntityMapperTest(SiteMemberAuthCrudJpaRepository memberAuthRepository, SiteMemberCrudJpaRepository memberRepository) {
        this.memberAuthRepository = memberAuthRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("매퍼 적용 후 일관된 회원 인증 엔터티 확인")
    @Test
    void checkConsistentEntity() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuthEntity = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().activeMember(memberEntity).originalMember(memberEntity).build());

        // then
        assertThat(memberAuthEntity).isEqualTo(memberAuthMapper.updateSiteMemberAuthEntity(memberAuthMapper.toSiteMemberAuth(memberAuthEntity), memberRepository));
    }

    @DisplayName("매퍼 적용 후 일관된 회원 인증 도메인 확인")
    @Test
    void checkConsistentDomain() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());
        SiteMemberAuthEntity memberAuthEntity = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().activeMember(memberEntity).originalMember(memberEntity).build());

        // when
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        // then
        assertThat(memberAuth).isEqualTo(memberAuthMapper.toSiteMemberAuth(memberAuthMapper.updateSiteMemberAuthEntity(memberAuth, memberRepository)));
    }
}