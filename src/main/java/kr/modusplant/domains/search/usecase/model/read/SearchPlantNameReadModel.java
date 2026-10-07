package kr.modusplant.domains.search.usecase.model.read;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

public record SearchPlantNameReadModel(@JsonProperty("koreanName") String plantName, Double similarity)
        implements Comparable<SearchPlantNameReadModel> {

    @Override
    public int compareTo(@NonNull SearchPlantNameReadModel o) {
        return Double.compare(this.similarity, o.similarity);
    }
}
