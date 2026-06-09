package kr.modusplant.domains.search.usecase.model.read;

import org.jspecify.annotations.NonNull;

public record SearchPlantKoreanNameReadModel(String koreanName, Double similarity)
        implements Comparable<SearchPlantKoreanNameReadModel> {

    @Override
    public int compareTo(@NonNull SearchPlantKoreanNameReadModel o) {
        return Double.compare(this.similarity, o.similarity);
    }
}
