package application;

import java.util.Objects;

public class UnifiedBuff {
	
	public enum Type {
		 ATTACK("攻撃"),
		 DEFENSE("防御"),
		 DAMAGE_TAKEN("被ダメージ"),
		 POWER("魔法威力"),
		 STATUS_CHANCE_UP("状態異常確率"),
		 STATUS_CHANCE_DOWN("状態異常確率"),
		 ELEMENT_RESIST_UP("属性耐性上昇"),   // ★追加
		 ELEMENT_RESIST_DOWN("属性耐性低下"), // 既存を明示的に「低下」に変更
		 SPECIFIC_STATUS_CHANCE_UP("特定状態異常確率");


	    private final String displayName;

	    Type(String displayName) {
	        this.displayName = displayName;
	    }

	    public String getDisplayName() {
	        return displayName;
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
    
    //バフ効果説明
    public String getLabel() {
        String turn = isSingleUse ? "1回" : duration + "T";

        switch (type) {

            // ============================
            // 属性耐性 UP / DOWN
            // ============================
            case ELEMENT_RESIST_UP -> {
                String target = (element != null && !element.isBlank()) ? element : "全属性";
                int percent = (int)Math.round((modifier - 1.0) * 100); // ★誤差ゼロ
                return target + "耐性+" + percent + "% (" + turn + ")";
            }

            case ELEMENT_RESIST_DOWN -> {
                String target = (element != null && !element.isBlank()) ? element : "全属性";
                int percent = (int)Math.round((1.0 - modifier) * 100); // ★誤差ゼロ
                return target + "耐性−" + percent + "% (" + turn + ")";
            }

            // ============================
            // 特定状態異常確率 UP
            // ============================
            case SPECIFIC_STATUS_CHANCE_UP -> {
                String status = (element != null && !element.isBlank()) ? element : "状態異常";
                int percent = (int)Math.round(modifier * 100); // ★誤差ゼロ
                return status + "付与率+" + percent + "% (" + turn + ")";
            }

            // ============================
            // 全状態異常確率 UP / DOWN
            // ============================
            case STATUS_CHANCE_UP -> {
                int percent = (int)Math.round(modifier * 100); // ★誤差ゼロ
                return "状態異常付与率+" + percent + "% (" + turn + ")";
            }

            case STATUS_CHANCE_DOWN -> {
                int percent = (int)Math.round(modifier * 100); // ★誤差ゼロ
                return "状態異常付与率−" + percent + "% (" + turn + ")";
            }

            // ============================
            // 被ダメージ倍率（DAMAGE_TAKEN）
            // ============================
            case DAMAGE_TAKEN -> {
                double delta = modifier - 1.0;
                int percent = (int)Math.round(Math.abs(delta) * 100); // ★誤差ゼロ
                String sign = delta < 0 ? "−" : "+";
                return "被ダメージ" + sign + percent + "% (" + turn + ")";
            }

            // ============================
            // 攻撃 / 防御 / 魔法威力
            // ============================
            default -> {
                double delta = modifier - 1.0;
                int percent = (int)Math.round(Math.abs(delta) * 100); // ★誤差ゼロ
                String sign = delta < 0 ? "−" : "+";
                return type.getDisplayName() + sign + percent + "% (" + turn + ")";
            }
        }
    }

    // 状態異常由来の識別
    public void setSourceStatusEffect(StatusEffect effect) {
        this.sourceStatusEffect = effect;
    }

    public 
    
    StatusEffect getSourceStatusEffect() {
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
        int duration = 3; // デフォルト持続ターン
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




    // Getters
    public Type getType() { return type; }
    public double getModifier() { return modifier; }
    public int getDuration() { return duration; }
    public boolean isBuff() { return isBuff; }
    public boolean isSingleUse() { return isSingleUse; }
    public String getElement() { return element; }
}