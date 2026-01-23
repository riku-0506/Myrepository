package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySetDAO {

    // 現在の装備中セットを取得
    public static EquippedSet getEquippedSet() {
        String sql = "SELECT id, set_name, my_magic1, my_magic2, my_magic3, my_magic4, my_magic5, my_magic6 "
                   + "FROM my_customize WHERE is_equipped = 1 LIMIT 1";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                EquippedSet set = new EquippedSet();
                set.id = rs.getInt("id");
                set.setName = rs.getString("set_name");
                set.magic1 = rs.getInt("my_magic1");
                set.magic2 = rs.getInt("my_magic2");
                set.magic3 = rs.getInt("my_magic3");
                set.magic4 = rs.getInt("my_magic4");
                set.magic5 = rs.getInt("my_magic5");
                set.magic6 = rs.getInt("my_magic6");
                return set;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 装備セットの内部クラス
    public static class EquippedSet {
        public int id;
        public String setName;
        public int magic1;
        public int magic2;
        public int magic3;
        public int magic4;
        public int magic5;
        public int magic6;
    }

    // 魔法ID → 名前変換
    public static String getMagicName(int id) {
        String sql = "SELECT name FROM my_magic WHERE id = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "なし";
    }
}
