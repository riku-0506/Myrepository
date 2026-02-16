package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShopMagicDAO {
    private Connection conn = null;

    public ShopMagicDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    // ショップで販売されている魔法一覧を取得（magic_id順）
    public List<Integer> getAllShopMagics() throws SQLException {
        List<Integer> magicIds = new ArrayList<>();
        String sql = "SELECT magic_id FROM shop_magic ORDER BY magic_id ASC"; // ★ 並び替え追加
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                magicIds.add(rs.getInt("magic_id"));
            }
        }
        return magicIds;
    }

    // 魔法をショップに追加（新しい魔法を販売開始）
    public void addShopMagic(int magicId) throws SQLException {
        String sql = "INSERT INTO shop_magic (magic_id) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, magicId);
            ps.executeUpdate();
        }
    }

    // 魔法をショップから削除（販売終了）
    public void removeShopMagic(int magicId) throws SQLException {
        String sql = "DELETE FROM shop_magic WHERE magic_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, magicId);
            ps.executeUpdate();
        }
    }

    // 魔法がショップにあるか確認
    public boolean hasShopMagic(int magicId) throws SQLException {
        String sql = "SELECT 1 FROM shop_magic WHERE magic_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, magicId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}