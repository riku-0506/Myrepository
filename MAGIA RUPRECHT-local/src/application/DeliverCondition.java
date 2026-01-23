package application;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 納品型クエスト条件
 * - 指定アイテムを所定数納品する
 */
public class DeliverCondition extends QuestCondition {

    private final int itemId;        // 必要アイテムID
    private final int requireCount;  // 必要個数
    private int deliveredCount = 0;  // 現在納品数
    private boolean delivered = false; // 納品完了フラグ

    public DeliverCondition(int itemId, int requireCount) {
        this.itemId = itemId;
        this.requireCount = requireCount;
    }

    // =====================================================
    // 条件達成判定
    // =====================================================
    @Override
    public boolean isSatisfied() {
        return deliveredCount >= requireCount;
    }

    // =====================================================
    // 表示用文言
    // =====================================================
    @Override
    public String getConditionText() {
        try (Connection conn = DBManager.getConnection()) {
            ItemDAO dao = new ItemDAO(conn);
            String itemName = dao.getItemNameById(itemId);
            return itemName + " を " + requireCount + " 個納品";
        } catch (SQLException e) {
            e.printStackTrace();
            return "不明なアイテム を " + requireCount + " 個納品";
        }
    }

    // =====================================================
    // ゲッター / セッター
    // =====================================================
    public int getItemId() { return itemId; }
    public int getRequireCount() { return requireCount; }
    public int getDeliveredCount() { return deliveredCount; }
    public void setDeliveredCount(int count) { this.deliveredCount = count; }
    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
}
