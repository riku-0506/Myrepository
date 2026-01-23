package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SellItemDAO {

    private Connection conn = null;
    private final InventoryDAO inventoryDAO;

    public SellItemDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        this.inventoryDAO = new InventoryDAO(conn);
    }

    /**
     * 売却処理
     * @param itemId 売却するアイテムID
     * @param quantity 売却数
     * @param price 売却価格（1個あたり）
     * @throws SQLException
     */
    public void sellItem(int itemId, int quantity, int price) throws SQLException {

        conn.setAutoCommit(false);

        try {
            // ① 現在の所持数を取得
            int current = inventoryDAO.getItemCount(itemId);
            if (current < quantity) {
                throw new SQLException("所持数が不足しています");
            }

            // ② 所持数を減算
            inventoryDAO.updateItemCount(itemId, current - quantity);

            // ③ 売却額をプレイヤーに加算
            addMoney(price * quantity);

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // プレイヤーの所持金を増やす（characterテーブル）
    private void addMoney(int amount) throws SQLException {
        String sql = "UPDATE character SET money = money + ? WHERE id = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.executeUpdate();
        }
    }

}
