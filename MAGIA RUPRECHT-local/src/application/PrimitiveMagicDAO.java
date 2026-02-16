package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrimitiveMagicDAO {
	
	private Connection conn;

	public PrimitiveMagicDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static List<PrimitiveMagic> getAll() {
        List<PrimitiveMagic> list = new ArrayList<>();
        String sql = "SELECT magic_id, name, costMP, description FROM magics";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PrimitiveMagic magic = new PrimitiveMagic(
                    rs.getInt("magic_id"),
                    rs.getString("name"),
                    rs.getInt("costMP"),
                    rs.getString("description")
                );
                list.add(magic);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static PrimitiveMagic getById(int id) {
        String sql = "SELECT magic_id, name, costMP, description FROM magics WHERE magic_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PrimitiveMagic(
                    rs.getInt("magic_id"),
                    rs.getString("name"),
                    rs.getInt("costMP"),
                    rs.getString("description")
                );
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
    
    public static PrimitiveMagic findByName(String name) {
        String sql = "SELECT magic_id, name, costMP, description FROM magics WHERE name = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PrimitiveMagic(
                        rs.getInt("magic_id"),
                        rs.getString("name"),
                        rs.getInt("costMP"),
                        rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static List<String> getTop6MagicNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM magics ORDER BY magic_id ASC LIMIT 6";

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
}