package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.Item.ItemType;

public class ItemDAO {
    private final Connection conn;

    public ItemDAO(Connection conn) {
        this.conn = conn;
    }

 // ✅ 全アイテム取得（MATERIAL を除外）
    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = """
            SELECT 
                item_id, name, type,
                [purchase price], [selling price],
                description
            FROM items
            WHERE type <> 'material'
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                String typeStr = rs.getString("type").toUpperCase();
                ItemType type = ItemType.valueOf(typeStr);

                int purchasePrice = rs.getInt("purchase price");
                int sellingPrice = rs.getInt("selling price");

                // description はここで使える
                Item item = new Item(
                    id, name, description,
                    null, null, null,   // カテゴリ・効果は未実装の場合 null でOK
                    purchasePrice, sellingPrice
                );

                items.add(item);
            }
        }
        return items;
    }


 // ✅ ショップ向け（MATERIAL を除外）
    public List<Item> getItemPrices() throws SQLException {
        List<Item> items = new ArrayList<>();

        String sql = """
            SELECT 
                item_id, name, type,
                [purchase price], [selling price],
                description
            FROM items
            WHERE type <> 'material'
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");

                int purchasePrice = rs.getInt("purchase price");
                int sellingPrice = rs.getInt("selling price");

                Item item = new Item(
                    id, name, description,
                    null, null, null,
                    purchasePrice, sellingPrice
                );

                items.add(item);
            }
        }

        return items;
    }
    
 // ===============================================
//  🔥 素材（material）だけ取得
// ===============================================
	public List<Item> getMaterials() throws SQLException {
	    List<Item> list = new ArrayList<>();
	
	    String sql = """
	        SELECT 
	            item_id, name, type,
	            [purchase price], [selling price],
	            description
	        FROM items
	        WHERE type = 'material'
	        ORDER BY item_id
	    """;
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	
	        while (rs.next()) {
	            Item item = new Item(
	                rs.getInt("item_id"),
	                rs.getString("name"),
	                rs.getString("description"),
	                null, null, null,
	                rs.getInt("purchase price"),
	                rs.getInt("selling price")
	            );
	            list.add(item);
	        }
	    }
	    return list;
	}
	
	
	// ===============================================
	//  🔥 消耗品（consumable）だけ取得
	// ===============================================
	public List<Item> getConsumables() throws SQLException {
	    List<Item> list = new ArrayList<>();
	
	    String sql = """
	        SELECT 
	            item_id, name, type,
	            [purchase price], [selling price],
	            description
	        FROM items
	        WHERE type = 'consumable'
	        ORDER BY item_id
	    """;
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	
	        while (rs.next()) {
	            Item item = new Item(
	                rs.getInt("item_id"),
	                rs.getString("name"),
	                rs.getString("description"),
	                null, null, null,
	                rs.getInt("purchase price"),
	                rs.getInt("selling price")
	            );
	            list.add(item);
	        }
	    }
	    return list;
	}
	
	
	// ===============================================
	//  🔥 item_id を指定して1件取得（売却処理で使用）
	// ===============================================
	public Item findById(int id) throws SQLException {
	    String sql = """
	        SELECT 
	            item_id, name, type,
	            [purchase price], [selling price],
	            description
	        FROM items
	        WHERE item_id = ?
	    """;
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, id);
	
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return new Item(
	                    rs.getInt("item_id"),
	                    rs.getString("name"),
	                    rs.getString("description"),
	                    null, null, null,
	                    rs.getInt("purchase price"),
	                    rs.getInt("selling price")
	                );
	            }
	        }
	    }
	    return null;
	}

	// ===============================================
	//  🔥 item_id からアイテム名を取得（クエスト表示用）
	// ===============================================
	private static final java.util.Map<Integer, String> nameCache = new java.util.HashMap<>();
	
	public String getItemNameById(int itemId) {
	
	    // キャッシュ優先
	    if (nameCache.containsKey(itemId)) {
	        return nameCache.get(itemId);
	    }
	
	    String sql = "SELECT name FROM items WHERE item_id = ?";
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, itemId);
	
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                String name = rs.getString("name");
	                nameCache.put(itemId, name);
	                return name;
	            }
	        }
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	
	    return "不明なアイテム";
	}

}
