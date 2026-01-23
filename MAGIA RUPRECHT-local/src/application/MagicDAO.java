package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MagicDAO {
    
    private Connection conn;
    public MagicDAO(Connection conn) {
        try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static List<Magic> getAll() {
        List<Magic> list = new ArrayList<>();
        String sql = "SELECT magic_id, name, costMP, description, Price FROM magics";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Magic(
                    rs.getInt("magic_id"),
                    rs.getString("name"),
                    rs.getInt("costMP"),
                    rs.getString("description"),
                    rs.getInt("Price")   // ★ 価格もセット
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Magic getById(int id) {
        String sql = "SELECT magic_id, name, costMP, description, Price FROM magics WHERE magic_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Magic(
                        rs.getInt("magic_id"),
                        rs.getString("name"),
                        rs.getInt("costMP"),
                        rs.getString("description"),
                        rs.getInt("Price")   // ★ 価格もセット
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAllMagicNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM magics";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public static Integer getIdByName(String name) {
        String sql = "SELECT magic_id FROM magics WHERE name = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("magic_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Magic findByName(String name) {
        String sql = "SELECT magic_id, name, costMP, description, Price FROM magics WHERE name = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Magic(
                        rs.getInt("magic_id"),
                        rs.getString("name"),
                        rs.getInt("costMP"),
                        rs.getString("description"),
                        rs.getInt("Price")   // ★ 価格もセット
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

 // プレイヤーが所持している魔法のうち、player_magic カラムが 6 以下の名前を取得
    public static List<String> getTop6MagicNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT m.name " +
                     "FROM magics AS m " +
                     "INNER JOIN player_magic AS pm ON m.magic_id = pm.player_magic " +
                     "WHERE pm.player_magic <= 6 " +        // ★ 6以下の値に限定
                     "ORDER BY m.magic_id ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    
 // プレイヤーが所持している魔法の名前一覧を取得
    public static List<String> getPlayerMagicNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT m.name " +
                "FROM magics AS m " +
                "INNER JOIN player_magic AS pm ON m.magic_id = pm.player_magic " +
                "WHERE pm.player_magic >= 7 " +   // ★ 7以上に変更
                "ORDER BY m.magic_id ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }




    // ★ 新規追加メソッド
    public static int getPrice(int magicId) {
        String sql = "SELECT purchasePrice FROM magics WHERE magic_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, magicId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 取得失敗時は0を返す
    }
}