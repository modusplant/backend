package kr.modusplant.infrastructure.swear.service;

import jakarta.annotation.PostConstruct;
import kr.modusplant.infrastructure.swear.persistence.jpa.entity.SwearEntity;
import kr.modusplant.infrastructure.swear.persistence.jpa.repository.SwearJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwearService {

    private final SwearJpaRepository repository;
    private List<String> swearWords;

    @PostConstruct
    public void init() {
        List<String> swears = repository.findAll()
                .stream()
                .map(SwearEntity::getWord)
                .collect(Collectors.toList());
        this.swearWords = addModifiedSwears(swears);
    }

    private List<String> addModifiedSwears(List<String> swears) {

        List<String> swearsWithWhiteSpaceAndNumber = new ArrayList<>();

        for (String version : List.of(" ", "1", "2")) {
            List<String> modifiedSwears = swears.stream()
                    .map(swear -> String.join(version, swear.split("")))
                    .toList();
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
