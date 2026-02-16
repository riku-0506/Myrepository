package application;

public enum StatusEffect {

    BURN(
        "やけど",
        Element.FIRE,
        0.1,
        -1,
        5,
        StatusCategory.DOT,
        "status_burn.png"
    ),

    PARALYZE(
        "感電",
        Element.THUNDER,
        0.1,
        2,
        2,
        StatusCategory.DEBUFF,
        "status_paralysis.png"
    ),

    FREEZE(
        "凍傷",
        Element.ICE,
        0.1,
        2,
        2,
        StatusCategory.DEBUFF,
        "status_freeze.png"
    ),

    SLEEP(
        "睡眠",
        Element.NONE,
        0,
        2,
        2,
        StatusCategory.CROWD_CONTROL,
        "status_sleep.png"
    ),

    POISON(
        "毒",
        Element.NONE,
        0,
        5,
        5,
        StatusCategory.DOT,
        "status_poison.png"
    );

    private final String label;
    private final Element element;
    private final double baseChance;
    private final int playerDuration;
    private final int enemyDuration;
    private final StatusCategory category;
    private final String iconName;   // ★ 追加

    StatusEffect(
        String label,
        Element element,
        double baseChance,
        int playerDuration,
        int enemyDuration,
        StatusCategory category,
        String iconName
    ) {
        this.label = label;
        this.element = element;
        this.baseChance = baseChance;
        this.playerDuration = playerDuration;
        this.enemyDuration = enemyDuration;
        this.category = category;
        this.iconName = iconName;
    }

    public String getLabel() { return label; }
    public Element getElement() { return element; }
    public double getBaseChance() { return baseChance; }
    public int getPlayerDuration() { return playerDuration; }
    public int getEnemyDuration() { return enemyDuration; }
    public StatusCategory getCategory() { return category; }

    // ★ 追加：アイコンパスを返す
    public String getIconPath() {
    	System.out.println("/application/images/Buff_icon/" + iconName);
        return "/application/images/Buff_icon/" + iconName;
    }

    /**
     * 説明文から状態異常を抽出する
     * 例：「対象に毒を付与する」→ POISON
     */
    public static StatusEffect fromDescription(String desc) {
        if (desc == null || desc.isBlank()) return null;

        for (StatusEffect effect : values()) {
            if (desc.contains(effect.label)) {
                return effect;
            }
        }
        return null;
    }

    /**
     * ラベルから状態異常を抽出する（部分一致）
     */
    public static StatusEffect fromLabel(String label) {
        if (label == null || label.isBlank()) return null;

        for (StatusEffect effect : values()) {
            if (label.contains(effect.label)) {
                return effect;
            }
        }
        return null;
    }
}