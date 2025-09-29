package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberTermRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberTermResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberTermAppInfraMapperTest implements SiteMemberTermRequestTestUtils, SiteMemberTermResponseTestUtils, SiteMemberTermEntityTestUtils {

    private final SiteMemberRepository memberRepository;
    private final SiteMemberTermAppInfraMapper memberTermMapper = new SiteMemberTermAppInfraMapperImpl();

    @Autowired
    SiteMemberTermAppInfraMapperTest(SiteMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toSiteMemberTermResponseTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();

        // when
        SiteMemberTermResponse memberTermResponse = memberTermMapper.toMemberTermResponse(memberTermEntity);

        // then
        assertThat(memberTermResponse).isEqualTo(memberTermUserResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toSiteMemberTermEntityTest() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberTermEntity memberTermEntity = memberTermMapper.toMemberTermEntity(new SiteMemberTermInsertRequest(memberEntity.getUuid(), MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION, MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION), memberRepository);

        // then
        assertThat(memberTermEntity.getAgreedTermsOfUseVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
    }
}