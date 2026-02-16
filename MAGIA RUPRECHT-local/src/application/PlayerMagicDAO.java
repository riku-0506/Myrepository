package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerMagicDAO {
    private Connection conn;

    public PlayerMagicDAO(Connection conn) {
        try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
    }

    // プレイヤーが所持している魔法一覧を取得
    public List<Integer> getAllPlayerMagics() throws SQLException {
        List<Integer> magicIds = new ArrayList<>();
        String sql = "SELECT player_magic FROM player_magic";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                magicIds.add(rs.getInt("player_magic"));
            }
        }
        return magicIds;
    }

    // 魔法を追加（プレイヤーが新しく習得した魔法）
    public void addPlayerMagic(int magicId) throws SQLException {
        String sql = "INSERT INTO player_magic (player_magic) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, magicId);
            ps.executeUpdate();
        }
    }

    // 魔法を削除（プレイヤーが魔法を忘れるなど）
    public void removePlayerMagic(int magicId) throws SQLException {
        String sql = "DELETE FROM player_magic WHERE player_magic = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, magicId);
            ps.executeUpdate();
        }
    }

    // 魔法を保持しているか確認
    public boolean hasPlayerMagic(int magicId) throws SQLException {
        String sql = "SELECT 1 FROM player_magic WHERE player_magic = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, magicId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
