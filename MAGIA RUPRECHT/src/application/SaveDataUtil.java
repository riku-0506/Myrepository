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

            // 行が無い → false
            if (!rs.next()) {
                return false;
            }

            int finished = rs.getInt("story_finished");

            // 値が 1 なら true、それ以外（0 など）は false
            return finished == 1;

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

