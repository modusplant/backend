package kr.modusplant.domains.term.adaptor.mapper;

import kr.modusplant.domains.term.common.util.domain.aggregate.SiteMemberTermTestUtils;
import kr.modusplant.domains.term.usecase.port.mapper.SiteMemberTermMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.term.common.util.usecase.response.SiteMemberTermResponseTestUtils.testSiteMemberTermResponse;
import static org.assertj.core.api.Assertions.assertThat;

class SiteMemberTermMapperImplTest implements SiteMemberTermTestUtils {
    private final SiteMemberTermMapper siteMemberTermMapper = new SiteMemberTermMapperImpl();

    @Test
    @DisplayName("toSiteMemberTermResponse로 응답 반환")
    void testToSiteMemberTermResponse_givenValidSiteMemberTerm_willReturnResponse() {
        assertThat(siteMemberTermMapper.toSiteMemberTermResponse(createSiteMemberTerm())).isEqualTo(testSiteMemberTermResponse);
    }

    @Test
    @DisplayName("toSiteMemberTermListResponse로 응답 리스트 반환")
    void testToSiteMemberTermListResponse_givenValidSiteMemberTermList_willReturnResponseList() {
        assertThat(siteMemberTermMapper.toSiteMemberTermListResponse(java.util.List.of(createSiteMemberTerm())))
                .containsExactly(testSiteMemberTermResponse);
    }
}
