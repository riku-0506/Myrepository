package application;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EnemySkillEffectRegistry {

    private static final Map<Integer, Consumer<PrimitiveMagic>> registry = new HashMap<>();

    static {
        // ============================
        // 101: 通常攻撃（全敵共通）
        // ============================
        registry.put(101, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            // 威力は EnemySkillExecutor 側で enemy.getAttack() を使う
            magic.setPower(0); // Executor 側で上書きするため 0 でOK
        });

        // ============================
        // 102:スライム/酸攻撃
        // ============================
        registry.put(102, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(15);
            magic.addBuffEffect(UnifiedBuff.Type.DEFENSE, -0.10, 3, false, null);
        });

        // ============================
        // 103:ゴブリン/こん棒攻撃
        // ============================
        registry.put(103, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(14);
        });
        
        // ============================
        // 104:ワイルドボア/突進
        // ============================
        registry.put(104, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(18);
        });
        
        // ============================
        // 105:ジャイアントアンツ/毒針
        // ============================
        registry.put(105, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(5);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "毒");
        });
        
        // ============================
        // 106:サイクロプス/冷たい一撃
        // ============================
        registry.put(106, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(30);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "凍傷");
            
        });
        
        // ============================
        // 107:マーメイド/破滅への誘い
        // ============================
        registry.put(107, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(1);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "睡眠");
        });

        // ============================
        // 108:マーマン/トライデント
        // ============================
        registry.put(108, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(25);
        });
        
        // ============================
        // 109:ゴーストシップクルー/サーベルスラッシュ
        // ============================
        registry.put(109, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(18);
        });
        
        // ============================
        // 110:ゴーストシップキャプテン/乱れ撃ち
        // ============================
        registry.put(110, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(33);
        });
        
        // ============================
        // 111:クラーケン/コキュートス
        // ============================
        registry.put(111, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(80);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "凍傷");
        });
        
        // ============================
        // 112:オーガ/こん棒振り回し
        // ============================
        registry.put(112, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(50);
        });
        
        // ============================
        // 113:ワーウルフ/宵闇の一爪
        // ============================
        registry.put(113, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(44);
        });
        
        // ============================
        // 114:ホーンラビット/雷電槍撃
        // ============================
        registry.put(114, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(48);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "感電");
        });
        
        // ============================
        // 115:サンダーバード/サンダーストライク
        // ============================
        registry.put(115, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(54);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "感電");
        });
        
        // ============================
        // 116:レッサードラゴン/ドラゴニック・ノヴァ
        // ============================
        registry.put(116, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(180);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "やけど");
        });
        
        // ============================
        // 117:ゴーストソルジャー/精密射撃
        // ============================
        registry.put(117, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(65);
        });
        
        // ============================
        // 118:ゾンビ/コラプトバイト
        // ============================
        registry.put(118, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(50);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "毒");
        });
        
        // ============================
        // 119:バーゲスト/黒焔
        // ============================
        registry.put(119, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(80);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "やけど");
        });
        
        // ============================
        // 120:鬼/無慈悲ナ一撃
        // ============================
        registry.put(120, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(100);
        });
        
        // ============================
        // 121:デュラハン/帝国流剣術・シュネルシュベールト
        // ============================
        registry.put(121, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(150);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "感電");
        });
        
        // ============================
        // 122:アークデーモン/絶望の魔眼
        // ============================
        registry.put(122, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(50);
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "やけど");
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "凍傷");
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "感電");
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "毒");
            magic.setBuffEffect(UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP, 1.0, 1, true, "睡眠");
        });
        
        // ============================
        // 123:トゥルー・アークデーモン/ヘル・インフェルノ
        // ============================
        registry.put(121, magic -> {
            magic.setEffectType(PrimitiveMagic.EffectType.DAMAGE);
            magic.setPower(700);
        });
    }

    /** ID に対応する効果を PrimitiveMagic に適用する */
    public static void apply(int id, PrimitiveMagic magic) {
        Consumer<PrimitiveMagic> effect = registry.get(id);
        if (effect != null) {
            effect.accept(magic);
        }
    }
}