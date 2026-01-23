package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepaymentDAO {

    private Connection conn;

    public RepaymentDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    // 残り借金額を返す
    public int getRemainingDebt() throws SQLException {
        String sql = "SELECT repayment FROM repayment LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("repayment");
            }
        }
        return 0;
    }

    // 残り借金額を更新
    public void updateRemainingDebt(int newDebt) throws SQLException {
        String sql = "UPDATE repayment SET repayment = ? WHERE id = 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newDebt);
            pstmt.executeUpdate();
        }
    }
}
