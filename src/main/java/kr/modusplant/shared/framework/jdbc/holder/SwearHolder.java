package kr.modusplant.shared.framework.jdbc.holder;

import jakarta.annotation.PostConstruct;
import kr.modusplant.shared.exception.NotInitializedException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.HOLDER_NOT_INITIALIZED;
import static kr.modusplant.shared.persistence.constant.TableColumnName.WORD;
import static kr.modusplant.shared.persistence.constant.TableName.SWEAR;

@Component
@RequiredArgsConstructor
public class SwearHolder {
    private final JdbcTemplate jdbcTemplate;

    private List<String> swearWords;
    private static List<String> staticSwearWords;

    @PostConstruct
    public void init() {
        List<String> swearWords = Set.copyOf(setModifiedSwears()).stream()
                .sorted(Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()))
                .toList();
        this.swearWords = swearWords;
        SwearHolder.staticSwearWords = swearWords;
    }

    // 주의: 최소한으로만 사용할 것.
    public static List<String> getStaticSwearWords() {
        if (staticSwearWords == null) {
            throw new NotInitializedException(HOLDER_NOT_INITIALIZED, "staticSwearWords");
        }
        return staticSwearWords;
    }

    public String maskSwear(String text) {
        if(text == null || text.isBlank()) {
            return text;
        }
        for (String swear : swearWords) {
            if (text.contains(swear)) {
                String replacePart = "*".repeat(swear.length());
                text = text.replace(swear, replacePart);
            }
        }
        return text;
    }

    private Set<String> setModifiedSwears() {
        Set<String> swears = new HashSet<>(
                jdbcTemplate.queryForList("SELECT " + WORD + " FROM " + SWEAR, String.class));

        Set<String> swearsWithWhiteSpaceAndNumber = new HashSet<>();
        for (String version : List.of(" ", "1", "2")) {
            Set<String> modifiedSwears = swears.stream()
                    .filter(swear -> swear.length() == 2)
                    .map(swear -> String.join(version, swear.split("")))
                    .collect(Collectors.toSet());
            swearsWithWhiteSpaceAndNumber.addAll(modifiedSwears);
        }
        swears.addAll(swearsWithWhiteSpaceAndNumber);

        return swears;
    }
}
