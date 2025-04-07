package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.api.crud.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberEntityMapperTest implements SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberEntityMapper memberMapper =  new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberEntityMapperTest(SiteMemberJpaRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("매퍼 적용 후 일관된 회원 엔터티 확인")
    @Test
    void checkConsistentEntity() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();

        // when
        memberEntity = memberRepository.save(memberEntity);

        // then
        assertThat(memberEntity).isEqualTo(memberMapper.updateSiteMemberEntity(memberMapper.toSiteMember(memberEntity)));
    }

    @DisplayName("매퍼 적용 후 일관된 회원 도메인 확인")
    @Test
    void checkConsistentDomain() {
        assertThat(memberBasicUser).isEqualTo(memberMapper.toSiteMember(memberMapper.createSiteMemberEntity(memberBasicUser)));
    }
}