package application;

public class EnemySkillExecutor {
	
	static int dmg;

    public static void execute(int skillId, Character enemy, Character player) {
    	
    	System.out.println("[EXEC] スキル実行: " + skillId + " (" + SkillRegistry.getName(skillId) + ")");

        // 1. PrimitiveMagic を ID から生成（効果はコンストラクタ内で Registry により適用済み）
        PrimitiveMagic magic = new PrimitiveMagic(skillId);

        // 2. ログ表示（SkillRegistry から名前を取得）
        String skillName = SkillRegistry.getName(skillId);
        System.out.println(enemy.getName() + " の " + skillName + "！");
        System.out.println("[EXEC] 効果タイプ: " + magic.getEffectType());
        System.out.println("[EXEC] 威力: " + magic.getPower());
        System.out.println("[EXEC] バフ数: " + (magic.getBuffList() == null ? 0 : magic.getBuffList().size()));


        // 3. 効果タイプに応じて処理を分岐
        switch (magic.getEffectType()) {

            case DAMAGE -> {

                // 通常攻撃（ID101）は敵の攻撃力を使う
                if (skillId == 101) {
                    dmg = enemy.getATK();
                } else {
                    dmg = magic.getPower();
                }

                player.applyDamage(dmg);
            }

            case STATUS -> {
                if (magic.getStatusEffectEnum() != null) {
                    player.applyStatusEffect(magic.getStatusEffectEnum());
                }
            }

            case BUFF, DEBUFF -> {
                if (magic.getBuffList() != null) {
                    // 敵自身にバフ/デバフを適用
                    for (var buff : magic.getBuffList()) {
                        enemy.applyBuff(buff);
                    }
                }
            }
		default -> System.out.println("Unexpected value: " + magic.getEffectType());
        }

        // 4. 追加効果（UnifiedBuff）を適用
        if (magic.getBuffList() != null) {
            for (var buff : magic.getBuffList()) {

            	// SPECIFIC_STATUS_CHANCE_UP → player に状態異常付与
            	if (buff.getType() == UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP) {

            	    String statusName = buff.getElement(); // "毒" "凍傷" "睡眠" など

            	    if (statusName != null) {

            	        // 名前から StatusEffect enum を取得
            	        StatusEffect effect = StatusEffect.fromLabel(statusName);

            	        if (effect != null) {
            	            // ★ 確定で状態異常を付与（attacker = enemy）
            	            if(player.applyStatus(effect, enemy)){
	            	            System.out.println("[EXEC] 状態異常付与: " + statusName + "");
	
	            	            enemy.log("ループは" + effect.getLabel() + " 状態になった!");
            	            }
            	            updateStatusUIForTarget(player);
            	        }
            	    }
            	}
            }
        }
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
    
    public static int getdmg() {
    	return dmg;
    }
}