package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsumableDAO {
    private Connection conn = null;;

    public ConsumableDAO(Connection conn) {
    	try {
			this.conn = DBManager.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public List<Item> getAllConsumables() throws SQLException {
        List<Item> consumables = new ArrayList<>();

        String sql = """
            SELECT i.item_id, i.name, i.[purchase price], i.[selling price], c.effect
            FROM items i
            JOIN consumables c ON i.item_id = c.item_id
            WHERE i.type = 'consumable'
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                int purchasePrice = rs.getInt("purchase price");
                int sellingPrice = rs.getInt("selling price");
                String effectText = rs.getString("effect");

                // ✅ 効果判定をFactoryに委譲
                Item item = ItemFactory.parse(id, name, effectText, purchasePrice, sellingPrice);
                consumables.add(item);
            }
        }

        return consumables;
    }
}