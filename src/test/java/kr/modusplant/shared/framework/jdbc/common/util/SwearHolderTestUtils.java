package kr.modusplant.shared.framework.jdbc.common.util;

import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

public interface SwearHolderTestUtils {
    List<String> testSwearWords = List.of("놈", "애미", "성인");

    static SwearHolder createSwearHolder() {
        JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        given(jdbcTemplate.queryForList(anyString(), eq(String.class))).willReturn(testSwearWords);
        SwearHolder swearHolder = new SwearHolder(jdbcTemplate);
        swearHolder.init();
        return swearHolder;
    }
}
