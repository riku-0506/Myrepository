package application;

public class Item {
    private final int id;
    private final String name;
    private final String description;
    private final ItemEffectType effectType;
    private final TargetType targetType;
    private final boolean consumable;
    private final ItemType type;
    private final ItemCategory category;
    
    private final int purchasePrice;
    private final int sellingPrice;

    public enum ItemEffectType {
        HEAL_HP, HEAL_MP, HEAL_ALL, REMOVE_STATUS,
        BUFF, DEBUFF, DAMAGE_SINGLE, DAMAGE_ALL, APPLY_STATUS,SPECIAL_EFFECT
    }
    
    public enum ItemCategory {
        HEAL_HP, HEAL_MP, FULL_RECOVERY,
        STATUS_RECOVERY, BUFF, DEBUFF,
        STATUS_INFLICT, DAMAGE_SINGLE, DAMAGE_ALL,
        MAGIC_CAST, NONE
    }


    public enum TargetType {
        SELF, ENEMY_SINGLE, ENEMY_ALL
    }

    public enum ItemType {
        CONSUMABLE, MATERIAL
    }

    // ✅ 消耗品用（価格付き）
    public Item(int id, String name, String description,
            ItemCategory category, ItemEffectType effectType, TargetType targetType,
            int purchasePrice, int sellingPrice){
    this(id, name, description, category, effectType, targetType,
         true, ItemType.CONSUMABLE, purchasePrice, sellingPrice);
}


    // ✅ 素材用（価格付き）
    public Item(int id, String name, int purchasePrice, int sellingPrice) {
        this(id, name, null, null, null, null,
             false, ItemType.MATERIAL, purchasePrice, sellingPrice);
    }

    // ✅ 共通のプライベートコンストラクタ
    private Item(int id, String name, String description,
            ItemCategory category, ItemEffectType effectType, TargetType targetType,
            boolean consumable, ItemType type,
            int purchasePrice, int sellingPrice) {
	   this.id = id;
	   this.name = name;
	   this.description = description;
	   this.category = category;
	   this.effectType = effectType;
	   this.targetType = targetType;
	   this.consumable = consumable;
	   this.type = type;
	   this.purchasePrice = purchasePrice;
	   this.sellingPrice = sellingPrice;
	}


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item other = (Item) obj;
        return this.id == other.id;
    }
    
    
    /** ダメージ系アイテムかどうかを判定 */
    public boolean isDamageSingleItem() {
        return effectType == ItemEffectType.DAMAGE_SINGLE;
    }
    public boolean isDamageAllItem() {
    	return effectType == ItemEffectType.DAMAGE_ALL;
    }

    /** 回復系アイテムかどうかを判定 */
    public boolean isHealItem() {
        return effectType == ItemEffectType.HEAL_HP
            || effectType == ItemEffectType.HEAL_MP
            || effectType == ItemEffectType.HEAL_ALL
            || effectType == ItemEffectType.REMOVE_STATUS
            || effectType == ItemEffectType.SPECIAL_EFFECT; // 全回復など特殊効果
    }

    /** デバフ系アイテムかどうかを判定 */
    public boolean isDebuffItem() {
        return effectType == ItemEffectType.DEBUFF
            || effectType == ItemEffectType.APPLY_STATUS;
    }

    /** バフ系アイテムかどうかを判定 */
    public boolean isBuffItem() {
        return effectType == ItemEffectType.BUFF;
    }


    

    // getter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemEffectType getEffectType() { return effectType; }
    public ItemCategory getCategory() { return category; }
    public TargetType getTargetType() { return targetType; }
    public boolean isConsumable() { return consumable; }
    public ItemType getType() { return type; }
    public int getPurchasePrice() { return purchasePrice; }
    public int getSellingPrice() { return sellingPrice; }
}