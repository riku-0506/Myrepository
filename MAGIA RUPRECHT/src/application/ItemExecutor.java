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
                	String StatusName = null;
                    switch (item.getId()) {
                        case 9  -> target.getStatusTurns().remove(StatusEffect.BURN);
                        case 10 -> target.getStatusTurns().remove(StatusEffect.PARALYZE);
                        case 11 -> target.getStatusTurns().remove(StatusEffect.FREEZE);
                        case 12 -> target.getStatusTurns().remove(StatusEffect.SLEEP);
                        case 13 -> target.getStatusTurns().remove(StatusEffect.POISON);
                    }
                    
                    switch (item.getId()) {
                    case 9  -> StatusName = "やけど";
                    case 10 -> StatusName = "感電";
                    case 11 -> StatusName = "凍傷";
                    case 12 -> StatusName = "睡眠";
                    case 13 -> StatusName = "毒";
                }

                    user.log("「" + item.getName() + "」で " + target.getName() + " の" + StatusName + "を解除！");
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
                    String description = switch (item.getId()) {
                    case 14 -> "の攻撃力が上がった！";
                    case 15 -> "の防御力が上がった";
                    case 16 -> "は状態異常にかかりにくくなった！";
                    case 17 -> "の次に与える魔法ダメージが上がっている！";
                    default -> null;
                };
                    if (buff != null) {
                        target.applyBuff(buff);
                        user.log("「" + item.getName() + "」でループ" + description);
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
                    String description = switch(item.getId()) {
	                    case 22 -> "の攻撃力を低下させた！";
	                    case 23 -> "の防御力を低下させた！";
	                    case 24 -> "の火耐性を低下させた！";
	                    case 25 -> "の雷耐性を低下させた！";
	                    case 26 -> "の氷耐性を低下させた！";
	                    case 27 -> "の聖耐性を低下させた！";
	                    case 28 -> "は少し状態異常にかかりやすくなった！";
	                    default -> null;
                    };
                    if (debuff != null) {
                        target.applyBuff(debuff);
                        user.log("「" + item.getName() + "」で " + target.getName() + description);
                        success = true;
                    }
                }

                case STATUS_INFLICT -> {
                	int duration = 0;
                    StatusEffect status = null;
                    switch (item.getId()) {
                        case 29: status = StatusEffect.BURN;
                        		 duration = 5;
                        		 break;
                        case 30: status = StatusEffect.PARALYZE;
			               		 duration = 5;
			               		 break;
                        case 31: status = StatusEffect.FREEZE;
			               		 duration = 5;
			               		 break;
                        case 32: status = StatusEffect.SLEEP;
			               		 duration = 1;
			               		 break;
                        case 33: status = StatusEffect.POISON;
			               		 duration = 5;
			               		 break;
                    };
                    if (status != null) {
                        target.setStatusEffect(status,duration);
                        user.log("「" + item.getName() + "」で " + target.getName() + " に状態異常「" + status.getLabel() + "」を付与！");
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
                    PrimitiveMagic magic = switch (item.getId()) {
                        case 18 -> PrimitiveMagicDAO.getById(35);
                        case 19 -> PrimitiveMagicDAO.getById(36);
                        case 20 -> PrimitiveMagicDAO.getById(37);
                        case 21 -> PrimitiveMagicDAO.getById(38);
                        default -> null;
                    };
                    if (magic != null) {
                        MagicExecutor.castMagic(user, magic);
                        user.log("「" + item.getName() + "」で魔法「" + magic.getName() + "」を発動！");
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