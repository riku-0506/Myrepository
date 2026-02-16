package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StageDAO {

    /**
     * 現在「解放されているステージ番号」を返す
     * 例:
     *  0クリア → 1
     *  1クリア → 2
     *  2クリア → 3
     */
	public static int getCurrentUnlockStage() {
	    int clearedCount = 0;

	    String sql = "SELECT COUNT(*) FROM stages WHERE cleared = 'true'";

	    try (Connection conn = DBManager.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        if (rs.next()) {
	            clearedCount = rs.getInt(1);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return clearedCount + 1; // ★ 1ステージ目は最初から解放
	}
	
	public static boolean isStage4Cleared() {
	    // 例：DBのクリアフラグや進行度で判定
	    return getCurrentUnlockStage() >= 5;
	}


}


