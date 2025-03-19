package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberTermJpaRepository;
import kr.modusplant.support.context.RepositoryOnlyContext;
import kr.modusplant.support.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.support.util.entity.SiteMemberTermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberTermEntityMapperTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils {

    private final SiteMemberTermJpaRepository memberTermRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermMapper = new SiteMemberTermEntityMapperImpl();

    @Autowired
    SiteMemberTermEntityMapperTest(SiteMemberTermJpaRepository memberTermRepository, SiteMemberJpaRepository memberRepository) {
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