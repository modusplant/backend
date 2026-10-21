package db.migration.data.reference;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.*;
import java.util.List;

/**
 * 댓글 깊이 제약 조건(최대 Depth 2)에 맞게, 하나의 Depth-1 댓글 하위의 모든 Depth-2 이하 댓글을 created_at 순서의 Depth-2 댓글로 변경한다.
 * 댓글 경로(path)를 참조하는 테이블의 경로도 함께 변경하며, 경로 외의 데이터는 변경하지 않는다.
 */
public class V5_2_3__Limit_comment_depth_to_two_or_fewer extends BaseJavaMigration {
    private static final String TEMPORARY_PATH_PREFIX = "#";

    // 경로가 포함되어 갱신이 필요한 테이블 목록
    private static final List<String> TABLES_TO_UPDATE = List.of(
            "comm_comment",
            "comm_comment_like",
            "comm_comment_abu_rep",
            "comm_comment_abuse_report_dashboard");

    private static final String CREATE_COMMENT_PATH_MAPPING = """
            CREATE TEMPORARY TABLE comment_path_mapping ON COMMIT DROP AS
            SELECT post_ulid, old_path, new_path
            FROM (
                SELECT post_ulid,
                       path AS old_path,
                       split_part(path, '.', 1) || '.' || ROW_NUMBER() OVER (
                           PARTITION BY post_ulid, split_part(path, '.', 1)
                           ORDER BY created_at, string_to_array(path, '.')::int[]) AS new_path
                FROM comm_comment
                WHERE path LIKE '%.%'
            ) path_reordered_comment
            WHERE old_path <> new_path
            """;

    private static final String UPDATE_TO_TEMPORARY_PATH = """
            UPDATE %s target
            SET path = ? || mapping.new_path
            FROM comment_path_mapping mapping
            WHERE target.post_ulid = mapping.post_ulid AND target.path = mapping.old_path
            """;

    private static final String UPDATE_TO_FINAL_PATH =
            "UPDATE %s SET path = substr(path, ?) WHERE path LIKE ?";

    private static final String UPDATE_NOTIFICATION_COMMENT_PATH = """
            UPDATE comm_notification notification
            SET comment_path = mapping.new_path
            FROM comment_path_mapping mapping
            WHERE notification.post_ulid = mapping.post_ulid AND notification.comment_path = mapping.old_path
            """;

    private static final String COUNT_COMMENT_DEEPER_THAN_TWO =
            "SELECT COUNT(*) FROM comm_comment WHERE path LIKE '%.%.%'";

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        try (Statement statement = connection.createStatement()) {
            // 변경이 필요한 댓글의 기존 경로와 새 경로의 매핑 생성
            statement.execute(CREATE_COMMENT_PATH_MAPPING);

            // 갱신 도중 기본 키가 충돌하지 않도록, 임시 접두사가 붙은 새 경로로 먼저 갱신한 뒤 접두사를 제거
            for (String table : TABLES_TO_UPDATE) {
                try (PreparedStatement preparedStatement =
                             connection.prepareStatement(UPDATE_TO_TEMPORARY_PATH.formatted(table))) {
                    preparedStatement.setString(1, TEMPORARY_PATH_PREFIX);
                    preparedStatement.executeUpdate();
                }
            }
            for (String table : TABLES_TO_UPDATE) {
                try (PreparedStatement preparedStatement =
                             connection.prepareStatement(UPDATE_TO_FINAL_PATH.formatted(table))) {
                    preparedStatement.setInt(1, TEMPORARY_PATH_PREFIX.length() + 1);
                    preparedStatement.setString(2, TEMPORARY_PATH_PREFIX + "%");
                    preparedStatement.executeUpdate();
                }
            }

            // 알림의 댓글 경로는 기본 키에 포함되지 않으므로 바로 갱신
            statement.executeUpdate(UPDATE_NOTIFICATION_COMMENT_PATH);

            validateCommentDepth(statement);
        }
    }

    private void validateCommentDepth(Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery(COUNT_COMMENT_DEEPER_THAN_TWO)) {
            resultSet.next();
            long count = resultSet.getLong(1);
            if (count > 0) {
                throw new IllegalStateException("깊이가 3 이상인 댓글이 남아 있습니다: " + count + "건");
            }
        }
    }
}
