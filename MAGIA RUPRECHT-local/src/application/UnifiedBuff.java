package application;

import java.util.Objects;

public class UnifiedBuff {
	
	public enum Type {
	    ATTACK("攻撃", "buff_attack"),//
	    DEFENSE("防御", "buff_defense"),//
	    POWER("魔法威力", "buff_power"),//
	    DAMAGE_TAKEN("被ダメージ", "buff_damage"),
	    STATUS_CHANCE_UP("状態異常↑", "buff_status_up"),//
	    STATUS_CHANCE_DOWN("状態異常↓", "buff_status_down"),
	    ELEMENT_RESIST_UP("属性耐性↑", "buff_resist_up"),
	    ELEMENT_RESIST_DOWN("属性耐性↓", "buff_resist_down"),
	    SPECIFIC_STATUS_CHANCE_UP("特定状態異常↑", "buff_specific_status");//

	    private final String displayName;
	    private final String iconName;

	    Type(String displayName, String iconName) {
	        this.displayName = displayName;
	        this.iconName = iconName;
	    }

	    public String getIconName() { return iconName; }

		String getDisplayName() {
			return null;
		}
	}


    private final Type type;
    private final double modifier;
    private int duration;
    private final boolean isBuff;
    private final boolean isSingleUse; // 1回限りかどうか（威力UP・状態異常確率UPなど）
    private final String element; // "火", "雷" など（属性耐性DOWN用）
    private StatusEffect sourceStatusEffect = null;

    // 通常バフ・デバフ（ターン制）
    public UnifiedBuff(Type type, double modifier, int duration, boolean isBuff) {
        this(type, modifier, duration, isBuff, false, null);
    }

    // 属性付き or 1回限りバフ
    public UnifiedBuff(Type type, double modifier, int duration, boolean isBuff, boolean isSingleUse, String element) {
        this.type = type;
        this.modifier = modifier;
        this.duration = duration;
        this.isBuff = isBuff;
        this.isSingleUse = isSingleUse;
        this.element = element;
    }

    public void tick() {
        if (!isSingleUse) {
            duration = Math.max(0, duration - 1);
        }
    }

    public boolean isExpired() {
        return duration <= 0;
    }
    
    // 状態異常由来の識別
    public void setSourceStatusEffect(StatusEffect effect) {
        this.sourceStatusEffect = effect;
    }

    public StatusEffect getSourceStatusEffect() {
        return sourceStatusEffect;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    
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
    
    
    //文章から効果を抽出
    public static UnifiedBuff fromString(String str) {
        if (str == null || str.isEmpty()) return null;

        str = str.trim().toUpperCase();

        UnifiedBuff.Type type = null;
        double modifier = 1.0;
        int duration = 5; // デフォルト持続ターン
        boolean isBuff = true;

        if (str.startsWith("ATK")) {
            type = UnifiedBuff.Type.ATTACK;
        } else if (str.startsWith("DEF")) {
            type = UnifiedBuff.Type.DEFENSE;
        } else if (str.startsWith("POWER")) {
            type = UnifiedBuff.Type.POWER;
        } else if (str.startsWith("STATUS")) {
            type = UnifiedBuff.Type.STATUS_CHANCE_UP;
        } else if (str.startsWith("RESIST")) {
            type = UnifiedBuff.Type.ELEMENT_RESIST_DOWN;
            isBuff = false;
        } else {
            return null; // 未対応
        }

        // 数値部分を抽出
        String numPart = str.replaceAll("[^\\d\\-+]", "");
        try {
            int value = Integer.parseInt(numPart);
            if (type == UnifiedBuff.Type.STATUS_CHANCE_UP || type == UnifiedBuff.Type.ELEMENT_RESIST_DOWN) {
                modifier = value / 100.0; // 例: 40 → 0.4
            } else {
                modifier = 1.0 + (value / 100.0); // 例: +30 → 1.3, -20 → 0.8
            }
            if (value < 0) isBuff = false;
        } catch (NumberFormatException e) {
            return null;
        }

        return new UnifiedBuff(type, modifier, duration, isBuff);
    }
    

    /**
     * バフごとのランクを返す（1〜3、または -1〜-3）
     * ※ ランク計算の中身は後で調整する
     */
    public int getRank() {
    	
    	System.out.println("getRack()| " + type + "：" + modifier);

        switch (type) {

            // --- 攻撃UP ---
            case ATTACK -> {
                if (modifier >= 1.3) return 3;
                if (modifier >= 1.2) return 2;
                return 1;
            }

            // --- 威力UP ---
            case POWER -> {
                if (modifier >= 1.3) return 3;
                if (modifier >= 1.2) return 2;
                return 1;
            }

            // --- 防御UP（被ダメ倍率が小さいほど強い） ---
            case DEFENSE -> {
                double m = modifier; // 0.10, 0.14, 0.18 が入る

                if (m >= 0.18) return 3;  // 最も強い軽減
                if (m >= 0.14) return 2;
                return 1;                 // 0.10
            }
            
         // --- 状態異常確率UP（全体 +30%） ---
            case DAMAGE_TAKEN -> {
                return 1;
            }

            // --- 状態異常確率UP（全体 +30%） ---
            case STATUS_CHANCE_UP -> {
                return 1;
            }

            // --- 状態異常確率UP（特定のみ +50%） ---
            case SPECIFIC_STATUS_CHANCE_UP -> {
                return 2;
            }

            // --- その他（今後追加用） ---
            default -> {
                return 1;
            }
            
        }
    }
	
    
	 // アイコンパス生成
    public String getIconPath() {
        int rank = Math.abs(getRank());
        return "/application/images/Buff_icon/" + type.getIconName() + "_" + rank + ".png";
    }

	

    // Getters
    public Type getType() { return type; }
    public double getModifier() { return modifier; }
    public int getDuration() { return duration; }
    public boolean isBuff() { return isBuff; }
    public boolean isSingleUse() { return isSingleUse; }
    public String getElement() { return element; }
    public String getIconName() {return type.getIconName();}
}