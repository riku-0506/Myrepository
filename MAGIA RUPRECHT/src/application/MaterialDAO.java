package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {
    private Connection conn;

    public MaterialDAO(Connection conn) {
        try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public List<Item> getAllMaterials() throws SQLException {
        List<Item> materials = new ArrayList<>();

        String sql = """
            SELECT 
                i.item_id, i.name,
                i.[purchase price], i.[selling price]
            FROM items i
            INNER JOIN materials m ON i.item_id = m.item_id
            WHERE i.type = 'material'
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                int purchasePrice = rs.getInt("purchase price");
                int sellingPrice = rs.getInt("selling price");

                // ✅ 素材用コンストラクタ（価格付き）で生成
                Item item = new Item(id, name, purchasePrice, sellingPrice);
                materials.add(item);
            }
        }

        return materials;
    }
}