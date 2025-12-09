package kr.modusplant.infrastructure.swear.service;

import kr.modusplant.infrastructure.swear.persistence.jpa.entity.SwearEntity;
import kr.modusplant.infrastructure.swear.persistence.jpa.repository.SwearJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwearService {

    private final List<String> swearWords;

    @Autowired
    public SwearService(SwearJpaRepository repository) {
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

    public String filterText(String text) {
        if(text == null || text.isBlank()) {
            return text;
        }

        for (String swear : swearWords) {
            if(text.contains(swear)) {
                String replacePart = "*".repeat(swear.length());
                text = text.replaceAll(swear, replacePart);
            }

        }
        return text;
    }

    public boolean containSwear(String text) {
        if(text == null || text.isBlank()) {
            return false;
        }

        return swearWords.stream().anyMatch(text::contains);
    }

}
