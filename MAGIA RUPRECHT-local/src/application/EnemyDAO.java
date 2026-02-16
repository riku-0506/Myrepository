package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnemyDAO {

    /**
     * 指定ステージに登場する敵一覧を取得
     */
    public static List<Enemy> getEnemiesByStageId(int stageId) {
        List<Enemy> enemies = new ArrayList<>();

        try (Connection conn = DBManager.getConnection()) {
            String sql = """
                    SELECT e.*
                    FROM enemies e
                    JOIN stage_enemies se ON e.enemy_id = se.enemy_id
                    WHERE se.stage_id = ?
                """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, stageId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                enemies.add(mapResultSetToEnemy(rs));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return enemies;
    }

    /**
     * 敵IDから1体の敵を取得（ボス用など）
     */
    public static Enemy getEnemyById(int enemyId) {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "SELECT * FROM enemies WHERE enemy_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, enemyId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Enemy e = mapResultSetToEnemy(rs);
                rs.close();
                stmt.close();
                return e;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ResultSet → Enemy オブジェクトへの変換
     */
    private static Enemy mapResultSetToEnemy(ResultSet rs) throws SQLException {
        return new Enemy(
            rs.getInt("enemy_id"),
            rs.getString("name"),
            rs.getInt("level"),
            rs.getInt("hp"),
            rs.getInt("atk"),
            rs.getDouble("normal"),
            rs.getDouble("flame"),
            rs.getDouble("thunder"),
            rs.getDouble("ice"),
            rs.getDouble("holy"),
            rs.getInt("item_id"),
            rs.getString("imagePath"),
            rs.getDouble("dropRate"),
            rs.getInt("exp")   // ← 追加
        );
    }
}