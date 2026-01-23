package application;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Item.ItemType;

public class Inventory {
    private final InventoryDAO dao;
    private final Map<Item, Integer> itemCounts = new HashMap<>();

    public Inventory(InventoryDAO dao, List<Item> items) throws SQLException {
        this.dao = dao;
        Map<Integer, Integer> counts = dao.loadItemCounts();

        for (Item item : items) {
            int count = counts.getOrDefault(item.getId(), 0);
            itemCounts.put(item, count);
        }
    }


    // 所持数取得
    public int getCount(Item item) {
        return itemCounts.getOrDefault(item, 0);
    }

 // アイテム追加（入手時）
    public void addItem(Item item, int count) throws SQLException {
        int newCount = getCount(item) + count;
        itemCounts.put(item, newCount); // メモリ上は合計値を保持
        dao.addItem(item.getId(), newCount);
    }


    // アイテム消費
    public void consume(Item item) throws SQLException {
        int current = getCount(item);
        if (current <= 0) throw new SQLException("アイテムが不足しています");

        int newCount = current - 1;
        itemCounts.put(item, newCount);
        dao.updateItemCount(item.getId(), newCount); // DB反映
    }

    // 使用可能な消耗品一覧
    public List<Item> getUsableItems() {
        return itemCounts.keySet().stream()
            .filter(item -> item.getType() == ItemType.CONSUMABLE && getCount(item) > 0)
            .sorted(Comparator.comparingInt(Item::getId)) // ★ Item_id順にソート
            .toList();
    }


    // 素材一覧
    public List<Item> getMaterials() {
        return itemCounts.keySet().stream()
            .filter(item -> item.getType() == ItemType.MATERIAL && getCount(item) > 0)
            .sorted(Comparator.comparingInt(Item::getId)) // ★ Item_id順にソート
            .toList();
    }

    
   
}