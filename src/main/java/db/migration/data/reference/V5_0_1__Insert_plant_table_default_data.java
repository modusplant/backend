package db.migration.data.reference;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.InputStream;
import java.sql.PreparedStatement;

public class V5_0_1__Insert_plant_table_default_data extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();

        InputStream is = getClass().getResourceAsStream("/csv/plant_classification_system_name_001.csv");

        // CSV를 Map 데이터로 변환 (반복적인 String.split 파싱 필요 없음)
        MappingIterator<PlantRow> iterator = csvMapper.readerFor(PlantRow.class)
                .with(csvSchema)
                .readValues(is);

        // Flyway가 제공하는 Connection을 통해 DB에 벌크 인서트
        String sql = """
                INSERT INTO plant (\
                id,
                scientific_name_id,
                scientific_name,
                korean_name,
                family_scientific_name,
                family_korean_name,
                genus_scientific_name,
                genus_korean_name,
                species_name,
                subspecies_name,
                resource_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        int count = 0;
        try (PreparedStatement statement = context.getConnection().prepareStatement(sql)) {
            while (iterator.hasNext()) {
                PlantRow row = iterator.next();
                statement.setLong(1, count);
                statement.setLong(2, row.scientific_name_id);
                statement.setString(3, row.scientific_name);
                statement.setString(4, row.korean_name);
                statement.setString(5, row.family_scientific_name);
                statement.setString(6, row.family_korean_name);
                statement.setString(7, row.genus_scientific_name);
                statement.setString(8, row.genus_korean_name);
                statement.setString(9, row.species_name);
                statement.setString(10, row.subspecies_name);
                statement.setString(11, row.resource_type);
                statement.addBatch();
                count = count + 1;
            }
            statement.executeBatch();
        }
    }

    private static class PlantRow {
        public int scientific_name_id;
        public String scientific_name;
        public String korean_name;
        public String family_scientific_name;
        public String family_korean_name;
        public String genus_scientific_name;
        public String genus_korean_name;
        public String species_name;
        public String subspecies_name;
        public String resource_type;
    }
}