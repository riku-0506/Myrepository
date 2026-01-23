package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class QuestDBManager {

    // ============================================================
    // ★ クエスト状態を DB に保存する（受注・達成・納品・討伐 すべて）
    // ============================================================
    public static void save(Map<String, QuestData> questMap) {
        try (Connection conn = DBManager.getConnection()) {

            String sql = """
                REPLACE INTO quest_status (
                    quest_id,
                    accepted,
                    completed,
                    delivered,
                    delivered_count,
                    require_count,
                    kill_progress
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (QuestData q : questMap.values()) {

                    // condition の種類を判定
                    DeliverCondition dc = (q.getCondition() instanceof DeliverCondition)
                            ? (DeliverCondition) q.getCondition()
                            : null;

                    KillCondition kc = (q.getCondition() instanceof KillCondition)
                            ? (KillCondition) q.getCondition()
                            : null;

                    // 1. クエストID
                    ps.setString(1, q.getId());

                    // 2. 受注状態
                    ps.setBoolean(2, q.isAccepted());

                    // 3. 達成状態
                    ps.setBoolean(3, q.isCompleted());

                    // 4〜6. DeliverCondition 用
                    ps.setBoolean(4, dc != null && dc.isDelivered());
                    ps.setInt(5, dc != null ? dc.getDeliveredCount() : 0);
                    ps.setInt(6, dc != null ? dc.getRequireCount() : 0);

                    // 7. KillCondition 用
                    ps.setInt(7, kc != null ? kc.getCurrentCount() : 0);

                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // ★ DB → questMap に読み込む
    // ============================================================
    public static void load(Map<String, QuestData> questMap) {
        try (Connection conn = DBManager.getConnection()) {

            String sql = """
                SELECT quest_id,
                       accepted,
                       completed,
                       delivered,
                       delivered_count,
                       require_count,
                       kill_progress
                FROM quest_status
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String id = rs.getString("quest_id");

                    // DB にあるが、ゲーム側に存在しないクエストは無視
                    if (!questMap.containsKey(id)) continue;

                    QuestData q = questMap.get(id);

                    // --- 基本ステータス ---
                    q.setAccepted(rs.getBoolean("accepted"));
                    q.setCompleted(rs.getBoolean("completed"));

                    // =====================================================
                    // DeliverCondition の復元
                    // =====================================================
                    if (q.getCondition() instanceof DeliverCondition dc) {

                        dc.setDelivered(rs.getBoolean("delivered"));
                        dc.setDeliveredCount(rs.getInt("delivered_count"));

                        System.out.println("[QuestDB] Loaded Deliver: " +
                                id + " (" + dc.getDeliveredCount() + "/" + dc.getRequireCount() + ")");
                    }

                    // =====================================================
                    // KillCondition の復元（討伐 progress）
                    // =====================================================
                    if (q.getCondition() instanceof KillCondition kc) {

                        int progress = rs.getInt("kill_progress");
                        kc.loadProgress(progress);

                        System.out.println("[QuestDB] Loaded Kill: " +
                                id + " progress=" + progress + "/" + kc.getRequireCount());
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // ★ 納品クエストだけ個別保存したい場合用
    // ============================================================
    public static void saveSingleDeliver(String questId, DeliverCondition dc) {

        String sql = """
            UPDATE quest_status
            SET delivered = ?,
                delivered_count = ?,
                require_count = ?
            WHERE quest_id = ?
        """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, dc.isDelivered());
            ps.setInt(2, dc.getDeliveredCount());
            ps.setInt(3, dc.getRequireCount());
            ps.setString(4, questId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
