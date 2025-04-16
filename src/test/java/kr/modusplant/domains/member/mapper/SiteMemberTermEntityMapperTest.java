package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermCrudJpaRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberTermEntityMapperTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils {

    private final SiteMemberTermCrudJpaRepository memberTermRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermMapper = new SiteMemberTermEntityMapperImpl();

    @Autowired
    SiteMemberTermEntityMapperTest(SiteMemberTermCrudJpaRepository memberTermRepository, SiteMemberCrudJpaRepository memberRepository) {
        this.memberTermRepository = memberTermRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("매퍼 적용 후 일관된 회원 약관 엔터티 확인")
    @Test
    void checkConsistentEntity() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntity();

        // when
        memberTermEntity = memberTermRepository.save(memberTermEntity);

        // then
        assertThat(memberTermEntity).isEqualTo(memberTermMapper.updateSiteMemberTermEntity(memberTermMapper.toSiteMemberTerm(memberTermEntity), memberRepository));
    }

    @DisplayName("매퍼 적용 후 일관된 회원 약관 도메인 확인")
    @Test
    void checkConsistentDomain() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());
        SiteMemberTermEntity memberTermEntity = SiteMemberTermEntity.builder().memberTermEntity(createMemberTermUserEntity()).member(memberEntity).build();

        // when
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        // then
        assertThat(memberTerm).isEqualTo(memberTermMapper.toSiteMemberTerm(memberTermMapper.createSiteMemberTermEntity(memberTerm, memberRepository)));
    }
}