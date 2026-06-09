package kr.modusplant.domains.search.usecase.port.cache;

import java.util.List;

public interface SearchPlantCache {
    List<String> getTransliteratedKoreanNames();
}
