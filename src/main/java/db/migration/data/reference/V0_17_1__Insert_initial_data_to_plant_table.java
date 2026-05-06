package db.migration.data.reference;

import kr.modusplant.shared.exception.FileLoadFailureException;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class V0_17_1__Insert_initial_data_to_plant_table extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        try (
                InputStream is = getClass().getResourceAsStream("/text/plant_korean_name.txt");
                BufferedReader reader = (is != null) ?
                        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)) : null;
                PreparedStatement statement = context.getConnection().prepareStatement(
                        "INSERT INTO plant (kor_name, created_at) VALUES (?, ?)")
        ) {
            if (reader == null) {
                throw new FileLoadFailureException();
            }

            // 모든 레코드에 동일한 생성 시간 부여
            LocalDateTime now = LocalDateTime.now();

            String line;
            while ((line = reader.readLine()) != null) {
                String korName = line.trim();
                if (!korName.isEmpty()) {
                    statement.setString(1, korName);
                    statement.setObject(2, now);
                    statement.addBatch();
                }
            }

            statement.executeBatch();
        }
    }
}