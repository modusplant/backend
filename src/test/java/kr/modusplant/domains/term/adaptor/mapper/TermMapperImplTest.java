package kr.modusplant.domains.term.adaptor.mapper;

import kr.modusplant.domains.term.common.util.domain.aggregate.TermTestUtils;
import kr.modusplant.domains.term.usecase.port.mapper.TermMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.term.common.util.usecase.response.TermResponseTestUtils.testTermResponse;
import static org.assertj.core.api.Assertions.assertThat;

class TermMapperImplTest implements TermTestUtils {
    private final TermMapper termMapper = new TermMapperImpl();

    @Test
    @DisplayName("toTermResponse로 응답 반환")
    void testToTermResponse_givenValidTerm_willReturnResponse() {
        assertThat(termMapper.toTermResponse(createTerm())).isEqualTo(testTermResponse);
    }

    @Test
    @DisplayName("toTermListResponse로 응답 리스트 반환")
    void testToTermListResponse_givenValidTermList_willReturnResponseList() {
        assertThat(termMapper.toTermListResponse(java.util.List.of(createTerm())))
                .containsExactly(testTermResponse);
    }
}
