package kr.modusplant.infrastructure.swear.service;

import jakarta.annotation.PostConstruct;
import kr.modusplant.infrastructure.swear.persistence.jpa.entity.SwearEntity;
import kr.modusplant.infrastructure.swear.persistence.jpa.repository.SwearJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwearService {

    private final SwearJpaRepository repository;
    private Set<String> swearWords;

    @PostConstruct
    public void init() {
        Set<String> swears = repository.findAll()
                .stream()
                .map(SwearEntity::getWord)
                .collect(Collectors.toSet());
        this.swearWords = addModifiedSwears(swears);
    }

    private Set<String> addModifiedSwears(Set<String> swears) {

        Set<String> swearsWithWhiteSpaceAndNumber = new HashSet<>();

        for (String version : List.of(" ", "1", "2")) {
            Set<String> modifiedSwears = swears.stream()
                    .map(swear -> String.join(version, swear.split("")))
                    .collect(Collectors.toSet());
            swearsWithWhiteSpaceAndNumber.addAll(modifiedSwears);
        }
        swears.addAll(swearsWithWhiteSpaceAndNumber);

        return swears;
    }

    public String filterSwear(String text) {
        if(text == null || text.isBlank()) {
            return text;
        }

        for (String swear : swearWords) {
            if(text.contains(swear)) {
                String replacePart = "*".repeat(swear.length());
                text = text.replace(swear, replacePart);
            }

        }
        return text;
    }

    public boolean isSwearContained(String text) {
        if(text == null || text.isBlank()) {
            return false;
        }
        return swearWords.stream().anyMatch(text::contains);
    }

}
