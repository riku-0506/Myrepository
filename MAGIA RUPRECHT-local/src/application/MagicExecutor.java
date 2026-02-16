package application;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import application.PrimitiveMagic.EffectType;

public class MagicExecutor {
	
	static Object getActiveController() {
		 return StageController.getInstance();
	}
	
	private static List<Character> getCurrentEnemies() {
	    StageController controller = StageController.getInstance();

	    if (controller == null) {
	        System.out.println(">>> getCurrentEnemies: no active controller");
	        return List.of();
	    }

	    return controller.getEnemies();
	}

	
	public static Boolean cast(CustomMagic magic, Character caster,
	        List<Character> selectedTargets, List<Character> allEnemies) {

	    final boolean[] result = { true };
	    
	    cast(magic, caster, selectedTargets, allEnemies, () -> {

	        StageController controller = StageController.getInstance();
	        if (controller == null) return;

	        controller.checkBattleEndAsync(battleEnded -> {
	            if (!battleEnded) {
	                controller.playerActing = false;
	                controller.enableAllActions();
	                controller.nextTurnAsync();
	            }
	        });

	    });

	    return result[0];
	}

	// Runnable対応版（修正版）
	public static void cast(CustomMagic magic, Character caster,
	                        List<Character> selectedTargets,
	                        List<Character> allEnemies,
	                        Runnable onFinished) {

	    int totalMP = magic.getTotalMP();
	    if (caster.getMP() < totalMP) {
	        caster.log("MPが足りません！");
	        if (onFinished != null) onFinished.run(); // MP不足でも進行させる
	        return;
	    }

	    caster.consumeMP(totalMP);

	    // ★ 魔法名ログ（ここではプレフィックスを付けない：log() 側が付ける）
	    caster.log("「" + magic.getName() + "」を唱えた！");

	    List<String> logs = new ArrayList<>();
	    List<String> currentAttributeElements = null;

	    List<String> inheritedAttributes = magic.getComponents().stream()
	            .filter(p -> p != null && p.getEffectType() == EffectType.ATTRIBUTE)
	            .flatMap(p -> p.getElements().stream())
	            .distinct()
	            .toList();

		 // ============================
		 // BUFF（複数バフ対応）
		 // ============================
		 for (PrimitiveMagic part : magic.getComponents()) {
		     if (part == null || part.getEffectType() != EffectType.BUFF) continue;
	
		     for (UnifiedBuff rawBuff : part.getBuffList()) {
	
		         // caster に適用するバフ
		         UnifiedBuff buff = new UnifiedBuff(
		             rawBuff.getType(),
		             rawBuff.getModifier(),
		             rawBuff.getDuration(),
		             true,                       // ★ 味方バフ
		             rawBuff.isSingleUse(),
		             rawBuff.getElement()
		         );
	
		         caster.applyBuff(buff);
	
		         // 敵にもバフが及ぶ特殊ケース（ケイオスフィルドなど）
		         if (magic.affectsEnemiesWithBuff()) {
		             for (Character enemy : allEnemies) {
		                 UnifiedBuff enemyBuff = new UnifiedBuff(
		                     rawBuff.getType(),
		                     rawBuff.getModifier(),
		                     rawBuff.getDuration(),
		                     false,              // ★ 敵にはデバフ扱い
		                     rawBuff.isSingleUse(),
		                     rawBuff.getElement()
		                 );
		                 enemy.applyBuff(enemyBuff);
		             }
		         }
		     }
		 }

		 // ★★★ バフ付与後に UI を更新（ここが最適） ★★★
		 StageController controller = StageController.getInstance();
		 if (controller != null && caster.isPlayer()) {
		     controller.updatePlayerStatsUI();
		 }


	    // ============================
	    // BUFF 以外の効果
	    // ============================
	    for (PrimitiveMagic part : magic.getComponents()) {
	        if (part == null || part.getEffectType() == EffectType.BUFF) continue;

	        if (part.getEffectType() == EffectType.ATTRIBUTE) {
	            currentAttributeElements = part.getElements();
	            continue;
	        }

	        List<String> effectiveElements = !part.getElements().isEmpty()
	                ? part.getElements()
	                : (!inheritedAttributes.isEmpty() ? inheritedAttributes : List.of("物理"));

	        boolean isFriendly = switch (part.getEffectType()) {
	            case HEAL -> true;
	            case DEBUFF, DAMAGE, STATUS, SPECIAL -> false;
	            default -> false;
	        };

	        List<Character> currentEnemies = getCurrentEnemies();

	        List<Character> targets = part.isAoE()
	                ? (isFriendly ? List.of(caster) : currentEnemies)
	                : (isFriendly ? List.of(caster) : selectedTargets);

	        // 重複ターゲット除去
	        targets = new ArrayList<>(new LinkedHashSet<>(targets));

	        switch (part.getEffectType()) {

	        // ---------------- DAMAGE ----------------
	        case DAMAGE -> {
	            for (Character target : targets) {
	                int damage = calculateDamage(part.getPower(), effectiveElements, caster, target);
	                target.applyDamage(damage);
	                logs.add(target.getName() + "に" + damage + "ダメージを与えた!");

	                if (controller != null) {
	                    controller.updateEnemyUIWithDamage(target);
	                }

	                if (controller.effectif()) {
	                    applyElementBasedStatusEffect(part, effectiveElements, caster, target, logs);
	                } else {
	                    StageEXeffectlog(target, logs);
	                }
	            }

	            caster.consumeSingleUseBuffs();
	        }

	        // ---------------- HEAL ----------------
	        case HEAL -> {
	            for (Character target : targets) {
	                target.heal(part.getPower());
	                logs.add(target.getName() + "のHPを" + part.getPower() + "回復");
	            }
	        }
	        
	        // ---------------- DEBUFF（複数バフ対応） ----------------
	        case DEBUFF -> {

	            for (UnifiedBuff rawBuff : part.getBuffList()) {

	                for (Character target : targets) {

	                    UnifiedBuff debuff = new UnifiedBuff(
	                        rawBuff.getType(),
	                        rawBuff.getModifier(),
	                        rawBuff.getDuration(),
	                        false,                  // ★ デバフ
	                        rawBuff.isSingleUse(),
	                        rawBuff.getElement()
	                    );

	                    target.applyBuff(debuff);
	                }
	            }
	        }

	        // ---------------- SPECIAL（複数バフ対応） ----------------
	        case SPECIAL -> {

	            for (UnifiedBuff rawBuff : part.getBuffList()) {

	                // 自分に適用するバフ（攻撃UP、防御DOWNなど）
	                UnifiedBuff selfBuff = new UnifiedBuff(
	                    rawBuff.getType(),
	                    rawBuff.getModifier(),
	                    rawBuff.getDuration(),
	                    true,                       // ★ 自分にはバフ扱い
	                    rawBuff.isSingleUse(),
	                    rawBuff.getElement()
	                );
	                caster.applyBuff(selfBuff);

	                // 敵に適用するデバフ（耐性DOWNなど）
	                for (Character target : targets) {
	                    UnifiedBuff enemyDebuff = new UnifiedBuff(
	                        rawBuff.getType(),
	                        rawBuff.getModifier(),
	                        rawBuff.getDuration(),
	                        false,                  // ★ 敵にはデバフ扱い
	                        rawBuff.isSingleUse(),
	                        rawBuff.getElement()
	                    );
	                    target.applyBuff(enemyDebuff);
	                }
	            }
	        }

	        // ---------------- STATUS ----------------
	        case STATUS -> {
	            System.out.println("case STATUS --スタート--");
	            // ここは後で必要なら復活させる
	        }

	        default -> logs.add("効果不明な魔法「" + part.getName() + "」");
	    }
	        
	    }

	    // ============================
	    // ログ出力（ここでもプレフィックスは付けない）
	    // ============================
	    for (String log : logs) {
	        caster.log("　" + log);
	    }
	}

	//ダメージ計算メソッド
	private static int calculateDamage(int power, List<String> elements, Character caster, Character target) {
	    // 魔法威力UPバフを反映（1回限りのバフはここで消費される）
	    double powerModifier = caster.getPowerModifier();

	    // 攻撃力（物理）も加算する場合は残す（魔法専用なら除外してもOK）
	    double attackPower = caster.getModifiedAttack();
	    double raw = (attackPower + power) * powerModifier;

	    double resistanceRate;
	    if (target.isPlayer()) {
	        resistanceRate = 1.0 / target.getModifiedDefense();
	    } else {
	        resistanceRate = 1.0;
	        for (String element : elements) {
	            resistanceRate *= target.getElementResistanceRate(element);
	            System.out.println(element + resistanceRate);
	        }
	    }
	    System.out.println("最終倍率：" + resistanceRate);
	    // 状態異常による被ダメージ倍率を反映
	    double damageMultiplier = target.getDamageTakenMultiplier();
	    double finalDamage = raw * resistanceRate * damageMultiplier;
	    
	    if (target.isSleeping()) {   // ← sleepFlag を返すメソッドを作る
	        target.clearSleep();     // ← sleepFlag=false & statusTurns.remove(SLEEP)
	    }

	    return Math.max(0, (int) finalDamage);
	}

	
	public static void castMagic(Character user, String magicName) {
	    PrimitiveMagic magic = PrimitiveMagicDAO.findByName(magicName);
	    if (magic == null) {
	        user.log("魔法「" + magicName + "」が見つかりませんでした");
	        return;
	    }

	    user.log("「" + magic.getName() + "」を発動！");
	}


	private static UnifiedBuff createBuffFromMagic(PrimitiveMagic magic, boolean isBuff) {

	    // PrimitiveMagic がバフ情報を持っている場合はこちらを使う
	    if (magic.hasBuffEffect()) {
	        return magic.toUnifiedBuff(isBuff);
	    }

	    // バフ情報が無い場合は何もしない
	    return null;
	}

	
 // 属性に応じて状態異常を判定・付与する補助メソッド
	private static void applyElementBasedStatusEffect(
	        PrimitiveMagic part,
	        List<String> effectiveElements,
	        Character caster,
	        Character target,
	        List<String> logs) {

	    // ============================
	    // ① 説明文由来（今まで通り）
	    // ============================
	    StatusEffect directEffect = part.getStatusEffectEnum();
	    if (directEffect != null) {
	        boolean applied = target.tryApplyStatus(directEffect, caster, null);
	        if (applied) {
	            logs.add(target.getName() + "は" + directEffect.getLabel() + "状態になった!");
	            updateStatusUIForTarget(target);
	        }
	    }

	    // ============================
	    // ② バフ由来：確率が 0% を超える状態異常は全部判定する
	    // ============================
	    for (StatusEffect effect : StatusEffect.values()) {

	        double base = effect.getBaseChance();
	        double specific = caster.getSpecificStatusUp(effect);
	        double all = caster.getStatusChanceUp();
	        double resistDown = target.getStatusResistDown();

	        // 属性ボーナスは tryApplyStatus 内で処理されるのでここでは不要
	        double total = base + specific + all + resistDown;

	        if (total > 0) {
	            boolean applied = target.tryApplyStatus(effect, caster, null);
	            if (applied) {
	                logs.add(target.getName() + "は" + effect.getLabel() + "状態になった!");
	                updateStatusUIForTarget(target);
	            }
	        }
	    }
	}
	
	private static void StageEXeffectlog(Character target, List<String> logs) {
		logs.add(target.getName() + "に状態異常は効かない！");
	}
	
	private static void updateStatusUIForTarget(Character target) {

	    StageController controller = StageController.getInstance();
	    if (controller == null) return;

	    if (!target.isPlayer()) {
	        controller.updateEnemyStatusUI(target, target.getDisplaySlotId());
	    } else {
	        controller.updatePlayerStatsUI();
	    }
	}

}