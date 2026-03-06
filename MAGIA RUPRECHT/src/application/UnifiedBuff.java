package application;

import java.util.Objects;

public class UnifiedBuff {

    public enum Type {
        ATTACK("攻撃", "buff_attack"),
        DEFENSE("防御", "buff_defense"),
        POWER("魔法威力", "buff_power"),
        DAMAGE_TAKEN("被ダメージ", "buff_damage"),
        STATUS_CHANCE_UP("状態異常↑", "buff_status_up"),
        STATUS_CHANCE_DOWN("状態異常↓", "buff_status_down"),
        ELEMENT_RESIST_UP("属性耐性↑", "buff_resist_up"),
        ELEMENT_RESIST_DOWN("属性耐性↓", "buff_resist_down"),
        SPECIFIC_STATUS_CHANCE_UP("特定状態異常↑", "buff_specific_status"),
        SPECIAL("特殊魔法", "buff_special"); // ← 特殊魔法バフ用

        private final String displayName;
        private final String iconName;

        Type(String displayName, String iconName) {
            this.displayName = displayName;
            this.iconName = iconName;
        }

        public String getIconName() { return iconName; }
        String getDisplayName() { return displayName; }
    }

    private final Type type;
    private final double modifier;
    private int duration;
    private final boolean isBuff;
    private final boolean isSingleUse;
    private final String element;

    private StatusEffect sourceStatusEffect = null;

    // ★ 新規追加：特殊魔法バフかどうか
    private final boolean isSpecialMagicBuff;

    // ============================
    // 既存コンストラクタ（変更なし）
    // ============================
    public UnifiedBuff(Type type, double modifier, int duration, boolean isBuff) {
        this(type, modifier, duration, isBuff, false, null, false);
    }

    public UnifiedBuff(Type type, double modifier, int duration, boolean isBuff,
                       boolean isSingleUse, String element) {
        this(type, modifier, duration, isBuff, isSingleUse, element, false);
    }

    // ============================
    // 内部用コンストラクタ（新規）
    // ============================
    public UnifiedBuff(Type type, double modifier, int duration, boolean isBuff,
                        boolean isSingleUse, String element, boolean isSpecialMagicBuff) {
        this.type = type;
        this.modifier = modifier;
        this.duration = duration;
        this.isBuff = isBuff;
        this.isSingleUse = isSingleUse;
        this.element = element;
        this.isSpecialMagicBuff = isSpecialMagicBuff;
        
        System.out.println("内部バフフラグ" + isSpecialMagicBuff);
    }
    
    public UnifiedBuff(UnifiedBuff other) {
        this.type = other.type;
        this.modifier = other.modifier;
        this.duration = other.duration;
        this.isBuff = other.isBuff;
        this.isSingleUse = other.isSingleUse;
        this.element = other.element;
        this.isSpecialMagicBuff = other.isSpecialMagicBuff; // ★ これが重要
    }

    public boolean isSpecialMagicBuff() {
        return isSpecialMagicBuff;
    }

    // ============================
    // 既存メソッド（変更なし）
    // ============================
    public void tick() {
        if (!isSingleUse) {
            duration = Math.max(0, duration - 1);
        }
    }

    public boolean isExpired() { return duration <= 0; }

    public void setSourceStatusEffect(StatusEffect effect) { this.sourceStatusEffect = effect; }
    public StatusEffect getSourceStatusEffect() { return sourceStatusEffect; }

    public void setDuration(int duration) { this.duration = duration; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UnifiedBuff other = (UnifiedBuff) obj;
        return type == other.type &&
               Double.compare(modifier, other.modifier) == 0 &&
               isBuff == other.isBuff &&
               isSingleUse == other.isSingleUse &&
               Objects.equals(element, other.element);
    }

    // ランク計算（既存）
    public int getRank() {
        switch (type) {
            case ATTACK -> {
            	if (modifier <= 0) return 4;
                if (modifier >= 1.3) return 3;
                if (modifier >= 1.2) return 2;
                return 1;
            }
            case POWER -> {
            	if (modifier <= 0) return 4;
                if (modifier >= 1.3) return 3;
                if (modifier >= 1.2) return 2;
                return 1;
            }
            case DEFENSE -> {
                double m = modifier;
                if(m <= 0) return 4;
                if (m >= 0.18) return 3;
                if (m >= 0.14) return 2;
                return 1;
            }
            case DAMAGE_TAKEN -> { return 99; }
            case STATUS_CHANCE_UP -> { return 99; }
            case SPECIFIC_STATUS_CHANCE_UP -> {
                return switch (element) {
                    case "やけど" -> 2;
                    case "感電" -> 3;
                    case "凍傷" -> 4;
                    case "毒" -> 5;
                    case "睡眠" -> 6;
                    default -> 2;
                };
            }
            case SPECIAL -> {
                return (int) modifier;
            }
            default -> { return 99; }
        }
    }

    // Getters
    public Type getType() { return type; }
    public double getModifier() { return modifier; }
    public int getDuration() { return duration; }
    public boolean isBuff() { return isBuff; }
    public boolean isSingleUse() { return isSingleUse; }
    public String getElement() { return element; }
    public String getIconName() { return type.getIconName(); }
}