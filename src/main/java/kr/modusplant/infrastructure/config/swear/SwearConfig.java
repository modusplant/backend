package kr.modusplant.infrastructure.config.swear;

import kr.modusplant.infrastructure.normalization.TextNormalizer;
import kr.modusplant.infrastructure.swear.SwearEngine;
import kr.modusplant.infrastructure.swear.SwearScript;
import kr.modusplant.infrastructure.swear.SwearWord;
import kr.modusplant.shared.enums.SwearType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.NOT_SWEAR;
import static kr.modusplant.shared.persistence.constant.TableName.SWEAR;

/***
 * 비속어 사전 전용 구성입니다.
 */
@Configuration
public class SwearConfig {
    @Bean
    public SwearEngine swearEngine(JdbcTemplate jdbcTemplate, TextNormalizer textNormalizer) {
        List<SwearWord> swearWords = jdbcTemplate.query(
                "SELECT " + WORD + ", " + TYPE + ", " + SEVERITY + ", " + SCRIPT + " FROM " + SWEAR,
                (resultSet, rowNumber) -> new SwearWord(
                        resultSet.getString(WORD),
                        SwearType.valueOf(resultSet.getString(TYPE)),
                        resultSet.getInt(SEVERITY),
                        SwearScript.valueOf(resultSet.getString(SCRIPT))));
        List<String> notSwearWords = jdbcTemplate.queryForList("SELECT " + WORD + " FROM " + NOT_SWEAR, String.class);
        return SwearEngine.create(textNormalizer, swearWords, notSwearWords);
    }
}
