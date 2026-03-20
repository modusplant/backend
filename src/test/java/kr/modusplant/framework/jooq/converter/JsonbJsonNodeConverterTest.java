package kr.modusplant.framework.jooq.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.infrastructure.config.jackson.JacksonConfig;
import org.jooq.JSONB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonbJsonNodeConverterTest {
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();
    private final ObjectMapper objectMapper = JacksonConfig.objectMapper();
    private final String string = """
            {"test": "test"}
            """;
    private final JsonNode jsonNode = objectMapper.readTree(string);
    private final JSONB jsonb = JSONB.jsonb(string);

    JsonbJsonNodeConverterTest() throws JsonProcessingException {
    }

    @Test
    @DisplayName("JSONB가 null일 때 null 반환")
    void testFrom_givenNull_willReturnNull() {
        assertThat(jsonbJsonNodeConverter.from(null)).isEqualTo(null);
    }

    @Test
    @DisplayName("from으로 JsonNode 반환")
    void testFrom_givenValidJsonb_willReturnJsonNode() {
        assertThat(jsonbJsonNodeConverter.from(jsonb)).isEqualTo(jsonNode);
    }

    @Test
    @DisplayName("JsonNode가 null일 때 null 반환")
    void testTo_givenNull_willReturnNull() {
        assertThat(jsonbJsonNodeConverter.to(null)).isEqualTo(null);
    }

    @Test
    @DisplayName("to로 JSONB 반환")
    void testTo_givenValidJsonNode_willReturnJsonb() {
        assertThat(jsonbJsonNodeConverter.to(jsonNode)).isEqualTo(jsonb);
    }

    @Test
    @DisplayName("fromType으로 JSONB 클래스 반환")
    void testFromType_givenNothing_willReturnJsonbClass() {
        assertThat(jsonbJsonNodeConverter.fromType()).isEqualTo(JSONB.class);
    }

    @Test
    @DisplayName("toType으로 JsonNode 클래스 반환")
    void testToType_givenNothing_willReturnJsonNodeClass() {
        assertThat(jsonbJsonNodeConverter.toType()).isEqualTo(JsonNode.class);
    }
}