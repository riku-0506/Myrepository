package application;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MagicEffectRegistry {

    private static final Map<Integer, Consumer<PrimitiveMagic>> registry = new HashMap<>();

    static {

        // ============================
        // 1〜6：攻撃魔法
        // ============================

        // アロー（単体 威力7）
        registry.put(1, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(7);
            magic.setAllTarget(false);
        });

        // ショット（単体 威力30）
        registry.put(2, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(30);
            magic.setAllTarget(false);
        });

        // カノン（単体 威力60）
        registry.put(3, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(60);
            magic.setAllTarget(false);
        });

        // アローレイン（全体 威力2）
        registry.put(4, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(2);
            magic.setAllTarget(true);
        });

        // サイクロン（全体 威力10）
        registry.put(5, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(10);
            magic.setAllTarget(true);
        });

        // テンペスト（全体 威力20）
        registry.put(6, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(20);
            magic.setAllTarget(true);
        });


        // ============================
        // 7〜10：属性付与
        // ============================
        registry.put(7, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.ATTRIBUTE);
            magic.addElement("火");
        });

        registry.put(8, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.ATTRIBUTE);
            magic.addElement("雷");
        });

        registry.put(9, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.ATTRIBUTE);
            magic.addElement("氷");
        });

        registry.put(10, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.ATTRIBUTE);
            magic.addElement("聖");
        });


        // ============================
        // 11〜15：特定状態異常確率UP
        // ============================
        registry.put(11, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "やけど");
        });

        registry.put(12, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "感電");
        });

        registry.put(13, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "凍傷");
        });

        registry.put(14, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "睡眠");
        });

        registry.put(15, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 0.5, 1, true, "毒");
        });


        // ============================
        // 16〜18：攻撃UP
        // ============================
        registry.put(16, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ATTACK, 1.1, 5, false, null);
        });

        registry.put(17, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ATTACK, 1.2, 5, false, null);
        });

        registry.put(18, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ATTACK, 1.3, 5, false, null);
        });


        // ============================
        // 19〜21：防御UP（軽減率）
        // ============================
        registry.put(19, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.DEFENSE, 0.10, 5, false, null);
        });

        registry.put(20, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.DEFENSE, 0.14, 5, false, null);
        });

        registry.put(21, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.DEFENSE, 0.18, 5, false, null);
        });


        // ============================
        // 22〜24：威力UP（単発）
        // ============================
        registry.put(22, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.POWER, 1.1, 1, true, null);
        });

        registry.put(23, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.POWER, 1.2, 1, true, null);
        });

        registry.put(24, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.POWER, 1.3, 1, true, null);
        });


        // ============================
        // 25：状態異常確率UP（全体）
        // ============================
        registry.put(25, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.BUFF);
            magic.setBuffEffect(UnifiedBuff.Type.STATUS_CHANCE_UP, 0.3, 1, true, null);
        });


        // ============================
        // 26〜30：デバフ
        // ============================
        registry.put(26, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DEBUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ATTACK, 0.9, 5, false, null);
        });

        registry.put(27, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DEBUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "火");
        });

        registry.put(28, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DEBUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "雷");
        });

        registry.put(29, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DEBUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "氷");
        });

        registry.put(30, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DEBUFF);
            magic.setBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.1, 5, false, "聖");
        });


        // ============================
        // 31〜33：HP回復
        // ============================
        registry.put(31, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.HEAL);
            magic.setPower(20);
        });

        registry.put(32, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.HEAL);
            magic.setPower(70);
        });

        registry.put(33, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.HEAL);
            magic.setPower(150);
        });


        // ============================
        // 34：状態異常回復
        // ============================
        registry.put(34, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.STATUS);
            magic.setStatusEffect(null);
        });


	     // ============================
	     // 35：バーサク
	     // 自身：攻撃力1.5倍、防御力20%低下（被ダメ1.2倍）
	     // ============================
	     registry.put(35, magic -> {
	         magic.setEffectType(PrimitiveMagic.EffectType.SPECIAL);
	
	         // 攻撃UP
	         magic.addBuffEffect(UnifiedBuff.Type.ATTACK, 1.5, 3, false, null);
	
	         // 防御DOWN（被ダメUP）
	         magic.addBuffEffect(UnifiedBuff.Type.DEFENSE, -0.20, 3, false, null);
	     });
	
	
	     // ============================
	     // 36：フォトレス
	     // 自身：攻撃力0.8倍、防御力30%上昇（被ダメ0.7倍）
	     // ============================
	     registry.put(36, magic -> {
	         magic.setEffectType(PrimitiveMagic.EffectType.SPECIAL);
	
	         // 攻撃DOWN
	         magic.addBuffEffect(UnifiedBuff.Type.ATTACK, 0.8, 3, false, null);
	
	         // 防御UP（被ダメ軽減）
	         magic.addBuffEffect(UnifiedBuff.Type.DEFENSE, 0.30, 3, false, null);
	     });
	
	
	     // ============================
	     // 37：ケイオスフィルド
	     // 敵味方：状態異常確率 +50%
	     // ============================
	     registry.put(37, magic -> {
	         magic.setEffectType(PrimitiveMagic.EffectType.SPECIAL);
	
	         // 状態異常確率UP（敵味方両方）
	         magic.addBuffEffect(UnifiedBuff.Type.STATUS_CHANCE_UP, 0.5, 3, false, null);
	     });
	
	
	     // ============================
	     // 38：サバイバー
	     // 自身：攻撃力1.5倍、防御力30%低下
	     // 敵：全属性耐性30%低下
	     // ============================
	     registry.put(38, magic -> {
	         magic.setEffectType(PrimitiveMagic.EffectType.SPECIAL);
	
	         // 自身：攻撃UP
	         magic.addBuffEffect(UnifiedBuff.Type.ATTACK, 1.5, 3, false, null);
	
	         // 自身：防御DOWN（被ダメUP）
	         magic.addBuffEffect(UnifiedBuff.Type.DEFENSE, -0.30, 3, false, null);
	
	         // 敵：全属性耐性DOWN
	         magic.addBuffEffect(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 0.30, 3, false, null);
	     });

    }

    public static void apply(int id, PrimitiveMagic magic) {
        Consumer<PrimitiveMagic> effect = registry.get(id);
        if (effect != null) {
            effect.accept(magic);
        }
    }
}