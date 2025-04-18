package kr.modusplant.domains.term.mapper;

import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.common.app.http.request.TermRequestTestUtils;
import kr.modusplant.domains.term.common.app.http.response.TermResponseTestUtils;
import kr.modusplant.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class TermAppInfraMapperTest implements TermRequestTestUtils, TermResponseTestUtils, TermEntityTestUtils {

    private final TermAppInfraMapper termAppInfraMapper = new TermAppInfraMapperImpl();

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toTermResponseTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();

        // when
        TermResponse termResponse = termAppInfraMapper.toTermResponse(termEntity);

        // then
        assertThat(termResponse).isEqualTo(termsOfUseResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toTermEntityTest() {
        // given & when
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);

        // then
        assertThat(termEntity.getName()).isEqualTo(termsOfUse.getName());
    }
}