package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 討伐型クエスト条件
 * - 指定モンスターを所定数討伐する
 */
public class KillCondition extends QuestCondition {

    private final int enemyId;        // 対象の敵ID
    private String enemyName;         // 表示用敵名
    private final int requireCount;   // 必要討伐数
    private int currentCount = 0;     // 現在討伐数（個別進捗）

    public KillCondition(int enemyId, int requireCount) {
        this.enemyId = enemyId;
        this.requireCount = requireCount;

        // --- 敵名をDBから取得 ---
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name FROM enemies WHERE enemy_id = ?"
             )) {
            stmt.setInt(1, enemyId);
            try (ResultSet rs = stmt.executeQuery()) {
                enemyName = rs.next() ? rs.getString("name") : "???";
            }
        } catch (Exception e) {
            e.printStackTrace();
            enemyName = "???";
        }
    }

    // =====================================================
    // DBから進捗復元
    // =====================================================
    public void loadProgress(int progress) {
        this.currentCount = progress;
    }

    // =====================================================
    // 敵撃破時に EnemyManager などから呼ばれる
    // =====================================================
    public void onEnemyKilled(int killedEnemyId) {
        if (killedEnemyId == enemyId && currentCount < requireCount) {
            currentCount++;
        }
    }

    // =====================================================
    // 条件達成判定
    // =====================================================
    @Override
    public boolean isSatisfied() {
        return currentCount >= requireCount;
    }

    // =====================================================
    // 表示用文言
    // =====================================================
    @Override
    public String getConditionText() {
        return enemyName + " を " + currentCount + " / " + requireCount + " 体倒す";
    }

    // =====================================================
    // ゲッター / セッター
    // =====================================================
    public int getEnemyId() { return enemyId; }
    public String getEnemyName() { return enemyName; }
    public int getCurrentCount() { return currentCount; }
    public void setCurrentCount(int count) { this.currentCount = count; }
    public int getRequireCount() { return requireCount; }
}
