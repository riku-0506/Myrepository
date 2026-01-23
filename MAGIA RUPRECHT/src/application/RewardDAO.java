package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RewardDAO {

    private Connection conn;
    private final int CHAR_ID = 1; // 固定キャラクター
    public static final int INITIAL_DEBT = 1_000_000;

    public RewardDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    /**
     * 累計返済額に応じて、まだ受け取っていない reward.id のリストを返す
     */
    public List<Integer> getNextRewardsByPaidAmount(int remainingDebt) throws SQLException {
        int paidAmount = INITIAL_DEBT - remainingDebt;
        if (paidAmount < 0) paidAmount = 0;

        List<Integer> rewards = new ArrayList<>();

        String sql = """
            SELECT id
            FROM reward
            WHERE threshold <= ?
            ORDER BY threshold ASC
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paidAmount);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int rewardId = rs.getInt("id");
                if (!isRewardAlreadyGiven(rewardId)) {
                    rewards.add(rewardId);
                }
            }
        }
        return rewards;
    }

    /**
     * reward.id に対応する取得しきい値を取得
     */
    public int getRewardThreshold(int rewardId) throws SQLException {
        String sql = "SELECT threshold FROM reward WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rewardId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("threshold") : 0;
        }
    }
    
    /**
     * reward テーブルにあるすべての報酬IDを取得
     */
    public List<Integer> getAllRewards() throws SQLException {
        List<Integer> rewards = new ArrayList<>();
        String sql = "SELECT id FROM reward ORDER BY threshold ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rewards.add(rs.getInt("id"));
            }
        }
        return rewards;
    }


    /**
     * 次に取得可能な報酬の threshold（取得済みも含む）を取得
     */
    public int getNextRewardThreshold(int remainingDebt) throws SQLException {
        int paidAmount = INITIAL_DEBT - remainingDebt;
        String sql = """
            SELECT threshold
            FROM reward
            WHERE threshold > ?
            ORDER BY threshold ASC
            LIMIT 1
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paidAmount);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("threshold") : 0;
        }
    }

    /**
     * 既に報酬を受け取ったか判定
     */
    public boolean isRewardAlreadyGiven(int rewardId) throws SQLException {
        String sql = "SELECT 1 FROM RewardLog WHERE char_id=? AND reward_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, CHAR_ID);
            stmt.setInt(2, rewardId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void markRewardGiven(int rewardId) throws SQLException {
        String sql = "INSERT INTO RewardLog(char_id, reward_id) VALUES(?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, CHAR_ID);
            stmt.setInt(2, rewardId);
            stmt.executeUpdate();
        }
    }

    public String getRewardName(int rewardId) throws SQLException {
        String sql = "SELECT item_name FROM reward WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rewardId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("item_name") : "なし";
        }
    }
}
