package kr.modusplant.infrastructure.config.prohibitedword;

import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWord;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordEngine;
import kr.modusplant.infrastructure.prohibitedword.ProhibitedWordScript;
import kr.modusplant.shared.enums.ProhibitedWordType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.ALLOWED_WORD;
import static kr.modusplant.shared.persistence.constant.TableName.PROHIBITED_WORD;

/***
 * 금지된 말 사전 전용 구성입니다.
 */
@Configuration
public class ProhibitedWordConfig {
    @Bean
    public ProhibitedWordEngine prohibitedWordEngine(JdbcTemplate jdbcTemplate, TextNormalizer textNormalizer) {
        List<ProhibitedWord> prohibitedWords = jdbcTemplate.query(
                "SELECT " + WORD + ", " + TYPE + ", " + SEVERITY + ", " + SCRIPT + " FROM " + PROHIBITED_WORD,
                (resultSet, rowNumber) -> new ProhibitedWord(
                        resultSet.getString(WORD),
                        ProhibitedWordType.valueOf(resultSet.getString(TYPE)),
                        resultSet.getInt(SEVERITY),
                        ProhibitedWordScript.valueOf(resultSet.getString(SCRIPT))));
        List<String> allowedWords = jdbcTemplate.queryForList("SELECT " + WORD + " FROM " + ALLOWED_WORD, String.class);
        return ProhibitedWordEngine.create(textNormalizer, prohibitedWords, allowedWords);
    }
}
