package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterDAO {

    private Connection conn = null;

    public CharacterDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
 // キャラクター情報をまとめて取得
    public Character getCharacterStatus() throws SQLException {
        String sql = "SELECT level, exp, next_exp, hp, mp, atk, def, Money, mycustomize_number FROM character LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	System.out.println("防御力:" + rs.getInt("def"));
                return new Character(
                    rs.getInt("level"),
                    rs.getInt("exp"),
                    rs.getInt("next_exp"),
                    rs.getInt("hp"),
                    rs.getInt("mp"),
                    rs.getInt("atk"),
                    rs.getDouble("def"),
                    rs.getInt("Money"),
                    rs.getInt("mycustomize_number")
                );
            }
        }
        return null;
    }

    // レベル・経験値の更新
    public void updateLevelAndExp(int level, int exp, int nextExp) throws SQLException {
        String sql = "UPDATE character SET level=?, exp=?, next_exp=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, level);
            stmt.setInt(2, exp);
            stmt.setInt(3, nextExp);
            stmt.executeUpdate();
        }
    }

    // HP/MP/ATKの更新
    public void updateStats(int hp, int mp, int atk) throws SQLException {
        String sql = "UPDATE character SET hp=?, mp=?, atk=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hp);
            stmt.setInt(2, mp);
            stmt.setInt(3, atk);
            stmt.executeUpdate();
        }
    }

    // 全キャラクター情報の更新（オートセーブ用）
    public void saveCharacter(Character c) throws SQLException {
        String sql = "UPDATE character SET level=?, exp=?, next_exp=?, hp=?, mp=?, atk=?, def=?, Money=? WHERE mycustomize_number=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, c.getLevel());
            stmt.setInt(2, c.getExp());
            stmt.setInt(3, c.getNextExp());
            stmt.setInt(4, c.getHP());
            stmt.setInt(5, c.getMP());
            stmt.setInt(6, c.getModifiedAttack()); // バフ込みでなく基礎値を保存するなら c.baseAttack
            stmt.setDouble(7, c.getModifiedDefense()); // 同上
            stmt.setInt(8, c.getMoney());
            stmt.setInt(9, c.getCustomizeNumber());
            stmt.executeUpdate();
        }
    }



    

    // 主人公（キャラクター）の所持金取得
    public int getMoney() throws SQLException {
        String sql = "SELECT Money FROM character LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Money");
            }
        }
        return 0;
    }

    // 主人公（キャラクター）の所持金更新
    public void updateMoney(int newMoney) throws SQLException {
        String sql = "UPDATE character SET Money = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newMoney);
            stmt.executeUpdate();
        }
    }

    // 主人公のHP取得
    public int getHP() throws SQLException {
        String sql = "SELECT hp FROM character LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("hp");
            }
        }
        return 100; // デフォルト値
    }

    // 主人公のMP取得
    public int getMP() throws SQLException {
        String sql = "SELECT mp FROM character LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("mp");
            }
        }
        return 100; // デフォルト値
    }

    // 主人公の攻撃力取得
    public int getAtk() throws SQLException {
        String sql = "SELECT atk FROM character LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("atk");
            }
        }
        return 10; // デフォルト値
    }

    // 主人公の防御力取得
    public double getDef() throws SQLException {
        String sql = "SELECT def FROM character LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("def");
            }
        }
        return 1.0; // デフォルト値
    }
    
    //mycustomize_number を取得
    public int getMyCustomizeNumber() throws SQLException {
        String sql = "SELECT mycustomize_number FROM character LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("mycustomize_number");
            }
        }
        return 0;
    }
    
    // mycustomize_number からセット名を取得
    public String getMySetName(int mySetId) {
        String sql = "SELECT set_name FROM mycustomize WHERE My_customize = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mySetId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("set_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}