package kr.modusplant.modules.signup.normal.service;

import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NormalSignUpApplicationService {

    private final TermCrudService termCrudService;

    public List<Term> getAllTerms() {
        return termCrudService.getAll();
    }

    public List<Map<String, Object>> createTermMapList(List<Term> terms) {

        return terms.stream()
                .filter(term -> {
                    String termKey = term.getName();

                    return termKey.equals("이용약관") ||
                            termKey.equals("개인정보처리방침") ||
                            termKey.equals("광고성 정보 수신");
                })
                .map(this::createTermMap)
                .toList();
    }

    private Map<String, Object> createTermMap(Term term) {
        String mapKey = switch (term.getName()) {
            case ("개인정보처리방침") -> "privacyPolicy";
            case ("이용약관") -> "termsOfUse";
            case ("광고성 정보 수신") -> "adInfoReceiving";
            default -> "unKnownTerm";
        };

        return Map.of(mapKey, term);
    }
}
