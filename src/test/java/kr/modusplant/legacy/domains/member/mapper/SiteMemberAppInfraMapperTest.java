package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberAppInfraMapperTest implements SiteMemberRequestTestUtils, SiteMemberResponseTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberAppInfraMapper memberMapper =  new SiteMemberAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toMemberResponseTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        // when
        SiteMemberResponse memberResponse = memberMapper.toMemberResponse(memberEntity);

        // then
        assertThat(memberResponse).isEqualTo(memberBasicUserResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toSiteMemberEntityTest() {
        // given & when
        SiteMemberEntity memberEntity = memberMapper.toMemberEntity(memberBasicUserInsertRequest);

        // then
        assertThat(memberEntity.getNickname()).isEqualTo(memberBasicUser.getNickname());
    }
}