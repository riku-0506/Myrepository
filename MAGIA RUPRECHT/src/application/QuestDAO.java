package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestDAO {
    private Connection conn;

    public QuestDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    // -----------------------
    // クエスト受注状態の取得
    // -----------------------
    public boolean isAccepted(String questId) throws SQLException {
        String sql = "SELECT accepted FROM quest_status WHERE quest_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, questId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("accepted");
                }
            }
        }
        return false;
    }

    // -----------------------
    // クエストクリア状態の取得
    // -----------------------
    public boolean isCompleted(String questId) throws SQLException {
        String sql = "SELECT completed FROM quest_status WHERE quest_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, questId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("completed");
                }
            }
        }
        return false;
    }

    // -----------------------
    // クエストを受注した
    // -----------------------
    public void acceptQuest(String questId) throws SQLException {
        String sql = """
            INSERT INTO quest_status (quest_id, accepted, completed)
            VALUES (?, TRUE, FALSE)
            ON CONFLICT(quest_id)
            DO UPDATE SET accepted = TRUE
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, questId);
            stmt.executeUpdate();
        }
    }

    // -----------------------
    // クエスト達成した
    // -----------------------
    public void completeQuest(String questId) throws SQLException {
        String sql = """
            UPDATE quest_status
            SET completed = TRUE, accepted = FALSE
            WHERE quest_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, questId);
            stmt.executeUpdate();
        }
    }
}
