package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * インベントリ操作用DAO
 * inventory_itemsテーブルの操作を行う
 */
public class InventoryDAO {
    private Connection conn;

    public InventoryDAO(Connection conn) {
        try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    /**
     * 全アイテムの所持数を取得
     */
    public Map<Integer, Integer> loadItemCounts() throws SQLException {
        Map<Integer, Integer> counts = new HashMap<>();
        String sql = "SELECT item_id, owned FROM inventory_items";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getInt("item_id"), rs.getInt("owned"));
            }
        }
        return counts;
    }

    /**
     * アイテムの所持数を更新
     */
    public void updateItemCount(int itemId, int newCount) throws SQLException {
        String sql = "UPDATE inventory_items SET owned = ? WHERE item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newCount);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        }
    }

    /**
     * アイテムを追加（既に存在する場合は数量を加算）
     */
 // まず現在の所持数を取得
    public void addItem(int itemId, int count) throws SQLException {
        String sql = """
            INSERT INTO inventory_items (item_id, owned)
            VALUES (?, ?)
            ON CONFLICT(item_id) DO UPDATE SET owned = owned + excluded.owned
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.setInt(2, count);
            stmt.executeUpdate();
        }
    }

    /**
     * アイテムの現在所持数を取得
     */
    public int getItemCount(int itemId) throws SQLException {
        String sql = "SELECT owned FROM inventory_items WHERE item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("owned");
                }
            }
        }
        return 0;
    }

    /**
     * アイテムを所持しているか確認
     */
    public boolean hasItem(int itemId) throws SQLException {
        return getItemCount(itemId) > 0;
    }

    /**
     * 返済報酬用メソッド
     * 返済額に応じたアイテムIDを渡すとインベントリに追加
     */
    public void addRewardForRepayment(int rewardItemId) throws SQLException {
        if (rewardItemId <= 0) return; // 無効なIDは追加しない
        addItem(rewardItemId, 1); // 1個追加
    }
}
