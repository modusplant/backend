package kr.modusplant.infrastructure.config.prohibitedword;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWord;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordEngine;
import kr.modusplant.infrastructure.prohibitedword.common.util.ProhibitedWordMatchTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class ProhibitedWordConfigTest implements ProhibitedWordMatchTestUtils {
    private static final Icu4jConfig icu4jConfig = new Icu4jConfig();
    private static final TextNormalizer textNormalizer =
            new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                    icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());

    private final JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);
    private final ProhibitedWordConfig prohibitedWordConfig = new ProhibitedWordConfig();

    @Test
    @DisplayName("금지된 말 사전 행으로 ProhibitedWordEngine 반환")
    void testProhibitedWordEngine_givenDictionaryRows_willReturnProhibitedWordEngine() throws Exception {
        // given
        given(resultSet.getString(WORD)).willReturn(testProhibitedWord.word());
        given(resultSet.getString(TYPE)).willReturn(testProhibitedWord.type().name());
        given(resultSet.getInt(SEVERITY)).willReturn(testProhibitedWord.severity());
        given(resultSet.getString(SCRIPT)).willReturn(testProhibitedWord.script().name());
        given(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<ProhibitedWord>>any()))
                .willAnswer(invocation -> List.of(
                        invocation.<RowMapper<ProhibitedWord>>getArgument(1).mapRow(resultSet, 0)));
        given(jdbcTemplate.queryForList(anyString(), eq(String.class))).willReturn(List.of("시발점"));

        // when
        ProhibitedWordEngine prohibitedWordEngine = prohibitedWordConfig.prohibitedWordEngine(jdbcTemplate, textNormalizer);

        // then
        assertThat(prohibitedWordEngine.match("시발")).containsExactly(testProhibitedWordMatch);
        assertThat(prohibitedWordEngine.match("시발점")).isEmpty();
    }
}
