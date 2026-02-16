package application;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveMagic {

    private final int magicId;
    private final String name;
    private final String description;
    private final int costMP;

    private EffectType effectType = EffectType.UNKNOWN;
    private boolean isAllTarget = false;
    private int power = 0;
    private List<String> elements = new ArrayList<>();
    private StatusEffect statusEffectEnum;

    // バフ定義
    private UnifiedBuff.Type buffType;
    private double buffModifier;
    private int buffDuration;
    private boolean isSingleUse;
    private String buffElement;
    private final List<UnifiedBuff> buffList = new ArrayList<>();

    public List<UnifiedBuff> getBuffList() {
        return buffList;
    }


    public enum EffectType {
        DAMAGE, HEAL, BUFF, DEBUFF, STATUS, ATTRIBUTE, SPECIAL, UNKNOWN;

        public boolean isFriendly() {
            return this == HEAL || this == BUFF;
        }
    }
    
    //プレイヤー用
    public PrimitiveMagic(int magicId, String name, int costMP, String description) {
        this.magicId = magicId;
        this.name = name;
        this.costMP = costMP;
        this.description = description;

        parseById(magicId);
    }
    
    
    //敵スキル用
    public PrimitiveMagic(int magicId) {
        this.magicId = magicId;
        this.name = "enemy_skill_" + magicId; // 適当でOK
        this.costMP = 0;
        this.description = "";

        parseById(magicId);
    }


    /**
     * ★IDベースで魔法効果を適用する新メソッド
     */
    private void parseById(int id) {
        if (id <= 100) {
            MagicEffectRegistry.apply(id, this); // プレイヤー用
        } else {
            EnemySkillEffectRegistry.apply(id, this); // 敵専用
        }
    }

    
  //全体攻撃かどうかを返すgetter
    public boolean isAoE() {
        return isAllTarget;
    }


    // ====== 既存の setter 群はそのまま利用 ======

    public void addElement(String element) {
        if (element != null && !element.isBlank() && !elements.contains(element)) {
            elements.add(element);
        }
    }

    public boolean hasBuffEffect() {
        return buffType != null;
    }

    public UnifiedBuff toUnifiedBuff(boolean isBuff) {
        return new UnifiedBuff(buffType, buffModifier, buffDuration, isBuff, isSingleUse, buffElement);
    }

    public void setBuffEffect(UnifiedBuff.Type type, double modifier, int duration, boolean isSingleUse, String element) {
    	addBuffEffect(type, modifier, duration, isSingleUse, element);
    }
    
    public void addBuffEffect(UnifiedBuff.Type type, double modifier, int duration, boolean isSingleUse, String element) {
        UnifiedBuff buff = new UnifiedBuff(type, modifier, duration, true, isSingleUse, element);
        buffList.add(buff);
    }


    // ====== Getters ======

    public int getMagicId() { return magicId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCostMP() { return costMP; }
    public EffectType getEffectType() { return effectType; }
    public boolean isAllTarget() { return isAllTarget; }
    public int getPower() { return power; }
    public List<String> getElements() { return elements; }
    public StatusEffect getStatusEffectEnum() { return statusEffectEnum; }
    public UnifiedBuff.Type getBuffType() { return buffType; }
    public double getBuffModifier() { return buffModifier; }
    public int getBuffDuration() { return buffDuration; }
    public boolean isBuffSingleUse() { return isSingleUse; }
    public String getBuffElement() { return buffElement; }

    // ====== IDベース適用用 Setter ======

    public void setEffectType(EffectType type) { this.effectType = type; }
    public void setPower(int power) { this.power = power; }
    public void setAllTarget(boolean all) { this.isAllTarget = all; }
    public void setStatusEffect(StatusEffect se) { this.statusEffectEnum = se; }
}