package application;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PrimitiveMagic {

    

    private final int magicId;
    private final String name;
    private final String description;
    private final int costMP;

    private EffectType effectType = EffectType.UNKNOWN;
    private boolean isAllTarget = false;
    private int power = 0;
    private String element = null;
    private List<String> elements = new ArrayList<>();
    private StatusEffect statusEffectEnum;
 // バフ定義
    private UnifiedBuff.Type buffType;
    private double buffModifier;
    private int buffDuration;
    private boolean isSingleUse;
    private String buffElement;


    
    
    public enum EffectType {
        DAMAGE, HEAL, BUFF, DEBUFF, STATUS, ATTRIBUTE, SPECIAL, UNKNOWN;
        
        public boolean isFriendly() {
            return this == HEAL || this == BUFF;
        }

    }

    public PrimitiveMagic(int magicId, String name, int costMP, String description) {
        this.magicId = magicId;
        this.name = name;
        this.costMP = costMP;
        this.description = description;
        parseDescription(description);
    }

    private void parseDescription(String desc) {

        // 効果タイプの判定（優先度順）
        if (desc.contains("威力UP") || desc.contains("威力UP+") || desc.contains("威力UP++")) {
            effectType = EffectType.BUFF;

        } else if (desc.contains("上げる") || desc.contains("下げる") || desc.contains("倍") || desc.contains("攻撃力") || desc.contains("防御力")) {
            effectType = desc.contains("自身") || desc.contains("魔法") || desc.contains("魔法") || desc.contains("確率") || desc.contains("威力") || desc.contains("防御力")
                ? EffectType.BUFF
                : EffectType.DEBUFF;

        } else if (desc.contains("全体攻撃")) {
            isAllTarget = true;
            effectType = EffectType.DAMAGE;

        } else if (desc.contains("威力")) {
            effectType = EffectType.DAMAGE;

        } else if (desc.contains("回復")) {
            effectType = EffectType.HEAL;

        } else if (desc.contains("属性を付与")) {
            effectType = EffectType.ATTRIBUTE;

        } else {
            effectType = EffectType.SPECIAL;
        }


        // 威力・回復量抽出
        Pattern pattern = Pattern.compile("(?:威力|回復)[^0-9]*([0-9]+)|([0-9]+)[^0-9]*(?:回復|威力)");
        Matcher matcher = pattern.matcher(desc);
        if (matcher.find()) {
            try {
                power = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(2));
            } catch (Exception e) {
                power = 0;
            }
        } else {
            power = 0;
        }


        // 属性抽出
        Pattern attrPattern = Pattern.compile("(火|雷|氷|聖)属性");
        Matcher attrMatcher = attrPattern.matcher(desc);
        while (attrMatcher.find()) {
            addElement(attrMatcher.group(1));
        }


        // 状態異常抽出（確率UP系は除外）
        boolean isChanceUp = desc.contains("確率") || desc.contains("確率UP") || desc.contains("上げる");

        if (!isChanceUp) {
            statusEffectEnum = StatusEffect.fromDescription(desc);
        }


        String lowerDesc = desc.toLowerCase();


        // ============================
        // 威力UP（ランク制）
        // ============================
        if (lowerDesc.contains("魔法の威力を1.3倍する")) {
            setBuffEffect(UnifiedBuff.Type.POWER, 1.3, 1, true, null);

        } else if (lowerDesc.contains("魔法の威力を1.2倍する")) {
            setBuffEffect(UnifiedBuff.Type.POWER, 1.2, 1, true, null);

        } else if (lowerDesc.contains("魔法の威力を1.1倍する")) {
            setBuffEffect(UnifiedBuff.Type.POWER, 1.1, 1, true, null);
        }


        // ============================
        // 通常バフ・デバフ（buffType未設定時のみ）
        // ============================
        if ((effectType == EffectType.BUFF || effectType == EffectType.DEBUFF) && buffType == null) {

            // 攻撃力UP（ランク制）
            if (lowerDesc.contains("5ターンの間自身の攻撃力を1.3倍する")) {
                setBuffEffect(UnifiedBuff.Type.ATTACK, 1.3, 5, false, null);

            } else if (lowerDesc.contains("5ターンの間自身の攻撃力を1.2倍する")) {
                setBuffEffect(UnifiedBuff.Type.ATTACK, 1.2, 5, false, null);

            } else if (lowerDesc.contains("5ターンの間自身の攻撃力を1.1倍する")) {
                setBuffEffect(UnifiedBuff.Type.ATTACK, 1.1, 5, false, null);
            }

            // 防御力UP（ランク制）
            else if (lowerDesc.contains("5ターンの間自身の防御力を1.3倍する")) {
                setBuffEffect(UnifiedBuff.Type.DEFENSE, 0.18, 5, false, null);

            } else if (lowerDesc.contains("5ターンの間自身の防御力を1.2倍する")) {
                setBuffEffect(UnifiedBuff.Type.DEFENSE, 0.14, 5, false, null);

            } else if (lowerDesc.contains("5ターンの間自身の防御力を1.1倍する")) {
                setBuffEffect(UnifiedBuff.Type.DEFENSE, 0.1, 5, false, null);
            }

            // 全状態異常確率UP
            else if (lowerDesc.contains("魔法の状態異常付与の確率を上げる")) {
                setBuffEffect(UnifiedBuff.Type.STATUS_CHANCE_UP, 0.3, 1, true, null);
            }

            // 特定状態異常確率UP
            else if (lowerDesc.contains("敵を毒状態にする確率を上げる")) {
                setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "毒");

            } else if (lowerDesc.contains("敵をやけど状態にする確率を上げる")) {
                setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "やけど");

            } else if (lowerDesc.contains("敵を睡眠状態にする確率を上げる")) {
                setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "睡眠");

            } else if (lowerDesc.contains("敵を感電状態にする確率を上げる")) {
                setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "感電");

            } else if (lowerDesc.contains("敵を凍傷状態にする確率を上げる")) {
                setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "凍傷");
            }
            
            //デバフ関連
            else if (lowerDesc.contains("敵の攻撃力を10%下げる")) {
                setBuffEffect(UnifiedBuff.Type.ATTACK, 0.9, 5, false, null);
                
            }else if (lowerDesc.contains("敵の火耐性を10%下げる")) {
                setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "火");

            }else if (lowerDesc.contains("敵の雷耐性を10%下げる")) {
                setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "雷");

            }else if (lowerDesc.contains("敵の氷耐性を10%下げる")) {
                setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "氷");

            }else if (lowerDesc.contains("敵の聖耐性を10%下げる")) {
                setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "聖");
            }
            
            
         // 特殊魔法系
         //バーサク
         else if (lowerDesc.contains("防御力") && lowerDesc.contains("0.2上げ")) {
             // 被ダメージ倍率 +20% → DAMAGE_TAKEN 1.2
             setBuffEffect(UnifiedBuff.Type.DAMAGE_TAKEN, 1.2, 3, false, null);
         }
         else if (lowerDesc.contains("攻撃力を1.5倍")) {
             // 攻撃力 1.5倍
             setBuffEffect(UnifiedBuff.Type.ATTACK, 1.5, 3, false, null);
         }


         //フォトレス
         else if (lowerDesc.contains("攻撃力を0.8倍")) {
             // 攻撃力 0.8倍（デバフ）
             setBuffEffect(UnifiedBuff.Type.ATTACK, 0.8, 3, false, null);
         }
         else if (lowerDesc.contains("防御力") && lowerDesc.contains("0.3下げ")) {
             // 被ダメージ倍率 -30% → DAMAGE_TAKEN 0.7
             setBuffEffect(UnifiedBuff.Type.DAMAGE_TAKEN, 0.7, 3, false, null);
         }
            
         // ケイオスフィルド
         else if (lowerDesc.contains("状態異常確率") && lowerDesc.contains("50")) {
             // 状態異常確率 +50%
             setBuffEffect(UnifiedBuff.Type.STATUS_CHANCE_UP, 0.5, 3, false, null);
         }


         // サバイバー
         else if (lowerDesc.contains("全属性の耐性") && lowerDesc.contains("0.3上げ")) {
             // 全属性耐性 +30% → ELEMENT_RESIST_UP 1.3
             setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.3, 3, false, null);
         }
         else if (lowerDesc.contains("防御力") && lowerDesc.contains("0.3上げ")) {
             // 被ダメージ倍率 +30% → DAMAGE_TAKEN 1.3
             setBuffEffect(UnifiedBuff.Type.DAMAGE_TAKEN, 1.3, 3, false, null);
         }
         else if (lowerDesc.contains("攻撃力を1.5倍")) {
             // 攻撃力 1.5倍
             setBuffEffect(UnifiedBuff.Type.ATTACK, 1.5, 3, false, null);
         }

        }
    }
    
    
    //魔法に含まれる属性をリストに追加
    public void addElement(String element) {
        if (element != null && !element.isBlank() && !elements.contains(element)) {
            elements.add(element);
        }
    }
    
    
    //バフ関連
    public boolean hasBuffEffect() {
        return buffType != null;
    }

    public UnifiedBuff toUnifiedBuff(boolean isBuff) {
        return new UnifiedBuff(buffType, buffModifier, buffDuration, isBuff, isSingleUse, buffElement);
    }
    
    public void setBuffEffect(UnifiedBuff.Type type, double modifier, int duration, boolean isSingleUse, String element) {
        this.buffType = type;
        this.buffModifier = modifier;
        this.buffDuration = duration;
        this.isSingleUse = isSingleUse;
        this.buffElement = element;
    }
    
    //全体攻撃かどうかを返すgetter
    public boolean isAoE() {
        return isAllTarget;
    }
    
    //属性を返すgetter
    public List<String> getElements() {
        return elements;
    }
    
    //状態異常を返すgetter
    public StatusEffect getStatusEffectEnum() {
        return statusEffectEnum;
    }



    // Getters
    public int getMagicId() { return magicId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCostMP() { return costMP; }
    public EffectType getEffectType() { return effectType; }
    public boolean isAllTarget() { return isAllTarget; }
    public int getPower() { return power; }
    public String getElement() { return element; }
    public UnifiedBuff.Type getBuffType() { return buffType; }
    public double getBuffModifier() { return buffModifier; }
    public int getBuffDuration() { return buffDuration; }
    public boolean isBuffSingleUse() { return isSingleUse; }
    public String getBuffElement() { return buffElement; }

}
