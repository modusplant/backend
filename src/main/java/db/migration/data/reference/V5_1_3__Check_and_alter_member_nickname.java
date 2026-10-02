package db.migration.data.reference;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * 비속어가 포함된 기존 회원 닉네임을 비속어 제약 조건에 맞게 치환한다.
 */
public class V5_1_3__Check_and_alter_member_nickname extends BaseJavaMigration {
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_NICKNAME_LENGTH = 16;
    private static final String MASK_CHARACTER = "0";
    private static final Pattern PATTERN_NICKNAME = Pattern.compile("^[가-힣A-Za-z0-9]{2,16}$");

    private static final String SELECT_FIRST_MEMBER_PAGE =
            "SELECT uuid, nickname FROM site_member ORDER BY uuid LIMIT ?";
    private static final String SELECT_NEXT_MEMBER_PAGE =
            "SELECT uuid, nickname FROM site_member WHERE uuid > ? ORDER BY uuid LIMIT ?";
    private static final String UPDATE_MEMBER_NICKNAME =
            "UPDATE site_member SET nickname = ?, last_modified_at = NOW(), ver_num = ver_num + 1 WHERE uuid = ?";
    private static final String UPDATE_NOTIFICATION_ACTOR_NICKNAME =
            "UPDATE comm_notification SET actor_nickname = ? WHERE actor_id = ?";

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        List<String> swearWords = loadSwearWords(connection);

        // 새 닉네임 후보가 다른 회원의 닉네임과 겹치는지(UNIQUE 충돌) DB 조회 없이 확인하기 위해, 기존 닉네임 전체를 조회용 집합으로 먼저 수집
        Set<String> takenNicknames = new HashSet<>();
        forEachMemberPage(connection, page ->
                page.forEach(member -> takenNicknames.add(member.nickname())));

        // 기존 닉네임 중 비속어의 영향을 받는 닉네임을 마스킹된 것으로 갱신(UPDATE)
        try (PreparedStatement memberStatement = connection.prepareStatement(UPDATE_MEMBER_NICKNAME);
             PreparedStatement notificationStatement = connection.prepareStatement(UPDATE_NOTIFICATION_ACTOR_NICKNAME)) {
            forEachMemberPage(connection, page -> {
                try {
                    int count = 0;
                    for (MemberRow member : page) {
                        String newNickname = resolveNewNickname(member.nickname(), swearWords, takenNicknames);
                        if (newNickname == null) {
                            continue;
                        }
                        takenNicknames.remove(member.nickname());
                        takenNicknames.add(newNickname);

                        memberStatement.setString(1, newNickname);
                        memberStatement.setObject(2, member.uuid());
                        memberStatement.addBatch();
                        notificationStatement.setString(1, newNickname);
                        notificationStatement.setObject(2, member.uuid());
                        notificationStatement.addBatch();
                        count = count + 1;
                    }
                    if (count > 0) {
                        memberStatement.executeBatch();
                        notificationStatement.executeBatch();
                    }
                } catch (SQLException exception) {
                    throw new IllegalStateException(exception);
                }
            });
        }
    }

    private List<String> loadSwearWords(Connection connection) throws SQLException {
        Set<String> swears = new HashSet<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT word FROM swear")) {
            while (resultSet.next()) {
                swears.add(resultSet.getString(1));
            }
        }

        // 공백 및 숫자가 삽입된 변형 비속어 추가 (두 글자 비속어만 해당)
        Set<String> modifiedSwears = new HashSet<>();
        for (String version : List.of(" ", "1", "2")) {
            for (String swear : swears) {
                if (swear.length() != 2) {
                    continue;
                }
                modifiedSwears.add(String.join(version, swear.split("")));
            }
        }
        swears.addAll(modifiedSwears);

        // 긴 비속어부터 마스킹되도록 길이 내림차순 정렬
        List<String> sortedSwears = new ArrayList<>(swears);
        sortedSwears.sort(Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()));
        return sortedSwears;
    }

    private void forEachMemberPage(Connection connection, Consumer<List<MemberRow>> pageConsumer) throws SQLException {
        UUID lastUuid = null;
        while (true) {
            // BATCH_SIZE만큼 Member를 uuid가 작은 것부터 순서대로 조회
            List<MemberRow> page = new ArrayList<>(BATCH_SIZE);
            try (PreparedStatement statement = connection.prepareStatement(
                    lastUuid == null ? SELECT_FIRST_MEMBER_PAGE : SELECT_NEXT_MEMBER_PAGE)) {
                int index = 1;
                if (lastUuid != null) {
                    statement.setObject(index++, lastUuid);
                }
                statement.setInt(index, BATCH_SIZE);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        page.add(new MemberRow(
                                resultSet.getObject("uuid", UUID.class),
                                resultSet.getString("nickname")));
                    }
                }
            }
            if (page.isEmpty()) {
                return;
            }
            pageConsumer.accept(page);
            if (page.size() < BATCH_SIZE) {
                return;
            }
            lastUuid = page.getLast().uuid();
        }
    }

    /**
     * 비속어를 마스킹한 새 닉네임을 반환한다. 변경이 필요 없으면 null을 반환한다.
     */
    private String resolveNewNickname(String nickname, List<String> swearWords, Set<String> takenNicknames) {
        // 기존 형식 위반 닉네임은 이 마이그레이션의 대상이 아니며, 접미사를 붙여도 형식을 만족할 수 없으므로 제외
        if (nickname == null || !PATTERN_NICKNAME.matcher(nickname).matches()) {
            return null;
        }

        String masked = nickname;
        for (String swear : swearWords) {
            if (masked.contains(swear)) {
                masked = masked.replace(swear, MASK_CHARACTER.repeat(swear.length()));
            }
        }
        if (masked.equals(nickname)) {
            return null;
        }

        // 다른 닉네임과 겹치면 맨 뒤에 0부터 증가하는 숫자를 추가
        String candidate = masked;
        for (long suffixNumber = 0; takenNicknames.contains(candidate); suffixNumber++) {
            String suffix = String.valueOf(suffixNumber);
            String base = masked.length() + suffix.length() > MAX_NICKNAME_LENGTH
                    ? masked.substring(0, MAX_NICKNAME_LENGTH - suffix.length())
                    : masked;
            candidate = base + suffix;
        }
        validateNickname(nickname, candidate, swearWords);
        return candidate;
    }

    private void validateNickname(String nickname, String candidate, List<String> swearWords) {
        if (!PATTERN_NICKNAME.matcher(candidate).matches()) {
            throw new IllegalStateException(
                    "치환된 닉네임이 닉네임 형식을 위반합니다: " + nickname + " -> " + candidate);
        }
        if (swearWords.stream().anyMatch(candidate::contains)) {
            throw new IllegalStateException(
                    "치환된 닉네임에 비속어가 포함되어 있습니다: " + nickname + " -> " + candidate);
        }
    }

    private record MemberRow(UUID uuid, String nickname) {
    }
}
