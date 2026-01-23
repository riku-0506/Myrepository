package application;

public enum StatusEffect {

    BURN(
        "やけど",
        Element.FIRE,
        0,
        -1,
        5,
        StatusCategory.DOT
    ),

    PARALYSIS(
        "感電",
        Element.THUNDER,
        0,
        2,
        2,
        StatusCategory.DEBUFF
    ),

    FREEZE(
        "凍傷",
        Element.ICE,
        0,
        2,
        2,
        StatusCategory.DEBUFF
    ),

    SLEEP(
        "睡眠",
        Element.NONE,
        0,
        2,
        2,
        StatusCategory.CROWD_CONTROL
    ),

    POISON(
        "毒",
        Element.NONE,
        0,
        5,
        5,
        StatusCategory.DOT
    );

    private final String label;
    private final Element element;
    private final double baseChance;
    private final int playerDuration;
    private final int enemyDuration;
    private final StatusCategory category;

    StatusEffect(
        String label,
        Element element,
        double baseChance,
        int playerDuration,
        int enemyDuration,
        StatusCategory category
    ) {
        this.label = label;
        this.element = element;
        this.baseChance = baseChance;
        this.playerDuration = playerDuration;
        this.enemyDuration = enemyDuration;
        this.category = category;
    }
    
    public String getLabel() { return label; }
    public Element getElement() { return element; }
    public double getBaseChance() { return baseChance; }
    public int getPlayerDuration() { return playerDuration; }
    public int getEnemyDuration() { return enemyDuration; }
    public StatusCategory getCategory() { return category; }

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