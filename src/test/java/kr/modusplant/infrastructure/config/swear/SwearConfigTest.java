package kr.modusplant.infrastructure.config.swear;

import kr.modusplant.infrastructure.config.icu4j.Icu4jConfig;
import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.swear.SwearEngine;
import kr.modusplant.infrastructure.swear.SwearWord;
import kr.modusplant.infrastructure.swear.common.util.SwearMatchTestUtils;
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

class SwearConfigTest implements SwearMatchTestUtils {
    private static final Icu4jConfig icu4jConfig = new Icu4jConfig();
    private static final TextNormalizer textNormalizer =
            new TextNormalizer(icu4jConfig.nfdNormalizer(), icu4jConfig.nfcNormalizer(),
                    icu4jConfig.nfkdNormalizer(), icu4jConfig.nfkcNormalizer());

    private final JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);
    private final SwearConfig swearConfig = new SwearConfig();

    @Test
    @DisplayName("비속어 사전 행으로 SwearEngine 반환")
    void testSwearEngine_givenDictionaryRows_willReturnSwearEngine() throws Exception {
        // given
        given(resultSet.getString(WORD)).willReturn(testSwearWord.word());
        given(resultSet.getString(TYPE)).willReturn(testSwearWord.type().name());
        given(resultSet.getInt(SEVERITY)).willReturn(testSwearWord.severity());
        given(resultSet.getString(SCRIPT)).willReturn(testSwearWord.script().name());
        given(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<SwearWord>>any()))
                .willAnswer(invocation -> List.of(
                        invocation.<RowMapper<SwearWord>>getArgument(1).mapRow(resultSet, 0)));
        given(jdbcTemplate.queryForList(anyString(), eq(String.class))).willReturn(List.of("시발점"));

        // when
        SwearEngine swearEngine = swearConfig.swearEngine(jdbcTemplate, textNormalizer);

        // then
        assertThat(swearEngine.match("시발")).containsExactly(testSwearMatch);
        assertThat(swearEngine.match("시발점")).isEmpty();
    }
}
