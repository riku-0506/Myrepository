package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SaveDataUtil {

    public static boolean exists() {

        String sql =
            "SELECT story_finished FROM game_state WHERE id = 1";

        try (Statement stmt = DBManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.next()) {
                return false; // 行が無い = 未プレイ
            }

            return rs.getInt("story_finished") == 1;

        } catch (SQLException e) {
            return false;
        }
    }
    
    public static void markStoryFinished() {

        String sql =
            "INSERT INTO game_state (id, story_finished) " +
            "VALUES (1, 1) " +
            "ON CONFLICT(id) DO UPDATE SET story_finished = 1";

        try (Statement stmt = DBManager.getConnection().createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("ストーリー完了を保存しました");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

