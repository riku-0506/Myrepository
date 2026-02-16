package application;

public class Magic {
    public final String name;
    public final String description;

    public int magicId;
    public int costMP;
    public String code;

    // ★ 追加フィールド
    private int Price;

    // 拡張用フィールド（分類・効果タイプ・属性など）
    public enum EffectType {
        DAMAGE, HEAL, BUFF, DEBUFF, STATUS, ATTRIBUTE, SPECIAL, UNKNOWN
    }

    public EffectType effectType;
    public boolean isAllTarget;
    public int power; // 威力 or 回復量
    public String element; // "火", "雷", "氷", "聖" など
    public String statusEffect; // "やけど", "感電" など

    // 基本コンストラクタ（図鑑用）
    public Magic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Magic(String name, String description, String code) {
        this.name = name;
        this.description = description;
        this.code = code;
    }

    // 拡張コンストラクタ（DB連携用）
    public Magic(int magicId, String name, int costMP, String description, String code, int Price) {
        this.magicId = magicId;
        this.name = name;
        this.costMP = costMP;
        this.description = description;
        this.code = code;
        this.Price = Price;
        parseDescription(description);
    }

    // 拡張コンストラクタ（コードなし）
    public Magic(int magicId, String name, int costMP, String description, int Price) {
        this.magicId = magicId;
        this.name = name;
        this.costMP = costMP;
        this.description = description;
        this.code = "";
        this.Price = Price;
        parseDescription(description);
    }

    // 効果説明から分類・威力・属性などを抽出
    private void parseDescription(String desc) {
        if (desc.contains("全体攻撃")) {
            isAllTarget = true;
            effectType = EffectType.DAMAGE;
        } else if (desc.contains("威力")) {
            isAllTarget = false;
            effectType = EffectType.DAMAGE;
        } else if (desc.contains("回復")) {
            effectType = EffectType.HEAL;
        } else if (desc.contains("付与")) {
            if (desc.contains("属性")) {
                effectType = EffectType.ATTRIBUTE;
            } else {
                effectType = EffectType.STATUS;
            }
        } else if (desc.contains("UP") || desc.contains("DOWN")) {
            effectType = desc.contains("自身") ? EffectType.BUFF : EffectType.DEBUFF;
        } else {
            effectType = EffectType.SPECIAL;
        }

        // 威力抽出
        if (desc.contains("威力")) {
            try {
                String[] parts = desc.split("威力");
                power = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                power = 0;
            }
        }

        // 属性抽出
        if (desc.contains("火")) element = "火";
        else if (desc.contains("雷")) element = "雷";
        else if (desc.contains("氷")) element = "氷";
        else if (desc.contains("聖")) element = "聖";

        // 状態異常抽出
        if (desc.contains("やけど")) statusEffect = "やけど";
        else if (desc.contains("感電")) statusEffect = "感電";
        else if (desc.contains("凍傷")) statusEffect = "凍傷";
        else if (desc.contains("睡眠")) statusEffect = "睡眠";
        else if (desc.contains("毒")) statusEffect = "毒";
    }

    // Getter
    public int getMagicId() { return magicId; }
    public String getName() { return name; }
    public int getCostMP() { return costMP; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
    public EffectType getEffectType() { return effectType; }
    public boolean isAllTarget() { return isAllTarget; }
    public int getPower() { return power; }
    public String getElement() { return element; }
    public String getStatusEffect() { return statusEffect; }

    // ★ purchasePrice Getter/Setter
    public int getPrice() { return Price; }
}