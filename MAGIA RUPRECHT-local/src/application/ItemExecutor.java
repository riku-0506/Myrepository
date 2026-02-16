package application;

import java.sql.SQLException;
import java.util.List;

import application.Item.ItemType;

public class ItemExecutor {

    public static boolean use(Item item, Character user, List<Character> targets, Inventory inventory) {
        if (item.getType() != ItemType.CONSUMABLE) {
            user.log("「" + item.getName() + "」は使用できません（素材アイテム）");
            return false;
        }

        if (inventory.getCount(item) <= 0) {
            user.log("「" + item.getName() + "」は所持していません");
            return false;
        }

        boolean success = false;

        for (Character target : targets) {
            switch (item.getCategory()) {
            case HEAL_HP -> {
                switch (item.getId()) {
                    case 1 -> {
                        target.heal(70);
                        user.log("「" + item.getName() + "」で " + target.getName() + " のHPを70回復！");
                    }
                    case 2 -> {
                        target.heal(150);
                        user.log("「" + item.getName() + "」で " + target.getName() + " のHPを150回復！");
                    }
                    case 3 -> {
                        target.heal(300);
                        user.log("「" + item.getName() + "」で " + target.getName() + " のHPを300回復！");
                    }
                    case 4 -> {
                        target.setHPToMax();
                        user.log("「" + item.getName() + "」で " + target.getName() + " のHPを全回復！");
                    }
                    default -> {
                        user.log("「" + item.getName() + "」の回復量が未定義です");
                        return false;
                    }
                }
                success = true;
            }


                case HEAL_MP -> {
                    int amount = switch (item.getId()) {
                        case 6 -> 30;
                        case 7 -> 100;
                        case 8 -> 200;
                        default -> 0;
                    };
                    target.recoverMP(amount);
                    user.log("「" + item.getName() + "」で " + target.getName() + " のMPを " + amount + " 回復！");
                    success = true;
                }

                case FULL_RECOVERY -> {
                    target.setHPToMax();
                    target.setMPToMax();
                    user.log("「" + item.getName() + "」で " + target.getName() + " のHPとMPを全回復！");
                    success = true;
                }

                case STATUS_RECOVERY -> {
                    switch (item.getId()) {
                        case 9  -> target.getStatusTurns().remove(StatusEffect.BURN);
                        case 10 -> target.getStatusTurns().remove(StatusEffect.PARALYZE);
                        case 11 -> target.getStatusTurns().remove(StatusEffect.FREEZE);
                        case 12 -> target.getStatusTurns().remove(StatusEffect.SLEEP);
                        case 13 -> target.getStatusTurns().remove(StatusEffect.POISON);
                    }

                    user.log("「" + item.getName() + "」で " + target.getName() + " の状態異常を解除！");
                    success = true;
                }




                case BUFF -> {
                    UnifiedBuff buff = switch (item.getId()) {
                        case 14 -> new UnifiedBuff(UnifiedBuff.Type.ATTACK, 1.15, 3, true, false, null);
                        case 15 -> new UnifiedBuff(UnifiedBuff.Type.DEFENSE, 0.1, 3, true, false, null);
                        case 16 -> new UnifiedBuff(UnifiedBuff.Type.STATUS_CHANCE_DOWN, 0.9, 3, true, false, null);
                        case 17 -> new UnifiedBuff(UnifiedBuff.Type.POWER, 1.5, 1, true, true, null);
                        default -> null;
                    };
                    if (buff != null) {
                        target.applyBuff(buff);
                        //user.log("「" + item.getName() + "」で " + target.getName() + " に「" + buff.getLabel() + "」を付与！");
                        success = true;
                    }
                }

                case DEBUFF -> {
                    UnifiedBuff debuff = switch (item.getId()) {
                        case 22 -> new UnifiedBuff(UnifiedBuff.Type.ATTACK, 0.85, 3, false, false, null);
                        case 23 -> new UnifiedBuff(UnifiedBuff.Type.ELEMENT_RESIST_DOWN,0.9,3,true,false,"全属性耐性");
                        case 24 -> new UnifiedBuff(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 1.2, 3, true, false, "火耐性");
                        case 25 -> new UnifiedBuff(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 1.2, 3, true, false, "雷耐性");
                        case 26 -> new UnifiedBuff(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 1.2, 3, true, false, "氷耐性");
                        case 27 -> new UnifiedBuff(UnifiedBuff.Type.ELEMENT_RESIST_DOWN, 1.2, 3, true, false, "聖耐性");
                        case 28 -> new UnifiedBuff(UnifiedBuff.Type.STATUS_CHANCE_UP, 1.1, 3, true, false, null);
                        default -> null;
                    };
                    if (debuff != null) {
                        target.applyBuff(debuff);
                        //user.log("「" + item.getName() + "」で " + target.getName() + " に「" + debuff.getLabel() + "」を付与！");
                        success = true;
                    }
                }

                case STATUS_INFLICT -> {
                    StatusEffect status = switch (item.getId()) {
                        case 29 -> StatusEffect.BURN;
                        case 30 -> StatusEffect.PARALYZE;
                        case 31 -> StatusEffect.FREEZE;
                        case 32 -> StatusEffect.SLEEP;
                        case 33 -> StatusEffect.POISON;
                        default -> null;
                    };
                    if (status != null) {
                        target.applyStatusEffect(status);
                        user.log("「" + item.getName() + "」で " + target.getName() + " に状態異常「" + status.getLabel() + "」を付与！");
//                        Stage1Controller.getInstance().updateEnemyStatusUI(target, target.getDisplaySlotId()); // ✅ 追加！
                        success = true;
                    }
                }


                case DAMAGE_SINGLE -> {
                    int damage = switch (item.getId()) {
                        case 34 -> 20;
                        case 35 -> 40;
                        case 36 -> 60;
                        default -> 0;
                    };
                    target.applyDamage(damage);
                    user.log("「" + item.getName() + "」で " + target.getName() + " に " + damage + " ダメージ！");
                    success = true;
                }

                case DAMAGE_ALL -> {
                    int damage = switch (item.getId()) {
                        case 37 -> 15;
                        case 38 -> 25;
                        case 39 -> 40;
                        case 40 -> 250;
                        default -> 0;
                    };
                    target.applyDamage(damage);
                    user.log("「" + item.getName() + "」で " + target.getName() + " に " + damage + " ダメージ！");
                    success = true;
                }

                case MAGIC_CAST -> {
                    String magicName = switch (item.getId()) {
                        case 18 -> "バーサク";
                        case 19 -> "フォトレス";
                        case 20 -> "ケイオスフィルド";
                        case 21 -> "サバイバー";
                        default -> null;
                    };
                    if (magicName != null) {
                        MagicExecutor.castMagic(user, magicName);
                        user.log("「" + item.getName() + "」で魔法「" + magicName + "」を発動！");
                        success = true;
                    }
                }

                default -> user.log("「" + item.getName() + "」の効果は未定義です");
            }
        }

        if (success && item.isConsumable()) {
            try {
                inventory.consume(item);
            } catch (SQLException e) {
                user.log("アイテムの消費処理中にエラーが発生しました：" + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        return success;
    }
}