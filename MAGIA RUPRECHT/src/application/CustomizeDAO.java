package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomizeDAO {

	public static class Customize {
	    public int customizeId;
	    public String name;
	    public Integer magic1, magic2, magic3, magic4, magic5, magic6;
	    public int costMP;

	    // 🔽 追加：部品IDリスト（CustomMagic構築用）
	    public List<Integer> componentIds;

	    public Customize(int customizeId, String name,
	                     Integer magic1, Integer magic2, Integer magic3,
	                     Integer magic4, Integer magic5, Integer magic6,
	                     int costMP) {
	        this.customizeId = customizeId;
	        this.name = name;
	        this.magic1 = magic1;
	        this.magic2 = magic2;
	        this.magic3 = magic3;
	        this.magic4 = magic4;
	        this.magic5 = magic5;
	        this.magic6 = magic6;
	        this.costMP = costMP;

	        // 🔽 追加：nullを除いた部品IDリストを生成
	        componentIds = new ArrayList<>();
	        if (magic1 != null) componentIds.add(magic1);
	        if (magic2 != null) componentIds.add(magic2);
	        if (magic3 != null) componentIds.add(magic3);
	        if (magic4 != null) componentIds.add(magic4);
	        if (magic5 != null) componentIds.add(magic5);
	        if (magic6 != null) componentIds.add(magic6);
	    }
	}


    public static List<Customize> getAll() {
        List<Customize> list = new ArrayList<>();
        String sql = "SELECT customize_id, name, magic1, magic2, magic3, magic4, magic5, magic6, costMP FROM customizes";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Customize(
                    rs.getInt("customize_id"),
                    rs.getString("name"),
                    getNullableInt(rs, "magic1"),
                    getNullableInt(rs, "magic2"),
                    getNullableInt(rs, "magic3"),
                    getNullableInt(rs, "magic4"),
                    getNullableInt(rs, "magic5"),
                    getNullableInt(rs, "magic6"),
                    rs.getInt("costMP")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Customize getById(int id) {
        String sql = "SELECT customize_id, name, magic1, magic2, magic3, magic4, magic5, magic6, costMP FROM customizes WHERE customize_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Customize(
                        rs.getInt("customize_id"),
                        rs.getString("name"),
                        getNullableInt(rs, "magic1"),
                        getNullableInt(rs, "magic2"),
                        getNullableInt(rs, "magic3"),
                        getNullableInt(rs, "magic4"),
                        getNullableInt(rs, "magic5"),
                        getNullableInt(rs, "magic6"),
                        rs.getInt("costMP")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateCostMP(Customize customize) {
        int totalMP = 0;
        Integer[] magicIds = {
            customize.magic1, customize.magic2, customize.magic3,
            customize.magic4, customize.magic5, customize.magic6
        };

        String sql = "SELECT costMP FROM magics WHERE magic_id = ?";
        try (Connection conn = DBManager.getConnection()) {
            for (Integer id : magicIds) {
                if (id != null) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, id);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                totalMP += rs.getInt("costMP");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalMP;
    }
    
    //カスタマイズ新規登録
    public static boolean insert(Customize customize) {
        // MP合計を事前に計算してセット
        customize.costMP = calculateCostMP(customize);

        String sql = "INSERT INTO customizes (name, magic1, magic2, magic3, magic4, magic5, magic6, costMP) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customize.name);
            setNullableInt(stmt, 2, customize.magic1);
            setNullableInt(stmt, 3, customize.magic2);
            setNullableInt(stmt, 4, customize.magic3);
            setNullableInt(stmt, 5, customize.magic4);
            setNullableInt(stmt, 6, customize.magic5);
            setNullableInt(stmt, 7, customize.magic6);
            stmt.setInt(8, customize.costMP);

            int result = stmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    //カスタマイズ更新
    public static boolean update(Customize customize) {
        customize.costMP = calculateCostMP(customize);

        String sql = "UPDATE customizes SET name = ?, magic1 = ?, magic2 = ?, magic3 = ?, magic4 = ?, magic5 = ?, magic6 = ?, costMP = ? WHERE customize_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customize.name);
            setNullableInt(stmt, 2, customize.magic1);
            setNullableInt(stmt, 3, customize.magic2);
            setNullableInt(stmt, 4, customize.magic3);
            setNullableInt(stmt, 5, customize.magic4);
            setNullableInt(stmt, 6, customize.magic5);
            setNullableInt(stmt, 7, customize.magic6);
            stmt.setInt(8, customize.costMP);
            stmt.setInt(9, customize.customizeId);

            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasDuplicateMagic(Customize customize) {
        Integer[] magicIds = {
            customize.magic1, customize.magic2, customize.magic3,
            customize.magic4, customize.magic5, customize.magic6
        };

        Set<Integer> seen = new HashSet<>();
        for (Integer id : magicIds) {
            if (id != null) {
                if (!seen.add(id)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //削除機能
    public static boolean deleteById(int customizeId) {
        String sql = "DELETE FROM customizes WHERE customize_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customizeId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNameById(int id) {
        String sql = "SELECT name FROM customizes WHERE customize_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveMagicAssignment(String customizeName, List<String> magicNames) {
        String sql = "UPDATE customizes SET magic1 = ?, magic2 = ?, magic3 = ?, magic4 = ?, magic5 = ?, magic6 = ? WHERE name = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < 6; i++) {
                Integer magicId = MagicDAO.getIdByName(magicNames.get(i));
                if (magicId != null) {
                    stmt.setInt(i + 1, magicId);
                } else {
                    stmt.setNull(i + 1, Types.INTEGER);
                }
            }
            stmt.setString(7, customizeName);
            stmt.executeUpdate();
            System.out.println("✅ 魔法構成を保存しました: " + customizeName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Integer getNullableInt(ResultSet rs, String columnLabel) throws SQLException {
        int value = rs.getInt(columnLabel);
        return rs.wasNull() ? null : value;
    }

    private static void setNullableInt(PreparedStatement stmt, int index, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(index, value);
        } else {
            stmt.setNull(index, Types.INTEGER);
        }
    }
    
    
}