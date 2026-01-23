package application;

import application.Item.ItemCategory;
import application.Item.ItemEffectType;
import application.Item.TargetType;

public class ItemFactory {

    public static Item parse(int id, String name, String effectText, int purchasePrice, int sellingPrice) {
        ItemCategory category = getCategoryById(id);
        TargetType target = getTargetById(id);
        ItemEffectType effectType = getEffectTypeById(id); // 任意：分類用に残すなら

        return new Item(id, name, effectText, category, effectType, target, purchasePrice, sellingPrice);
    }

    private static ItemCategory getCategoryById(int id) {
        return switch (id) {
            case 1, 2, 3 -> ItemCategory.HEAL_HP;
            case 4, 5 -> ItemCategory.FULL_RECOVERY;
            case 6, 7, 8 -> ItemCategory.HEAL_MP;
            case 9, 10, 11, 12, 13 -> ItemCategory.STATUS_RECOVERY;
            case 14, 15, 16, 17 -> ItemCategory.BUFF;
            case 18, 19, 20, 21 -> ItemCategory.MAGIC_CAST;
            case 22, 23, 24, 25, 26, 27, 28 -> ItemCategory.DEBUFF;
            case 29, 30, 31, 32, 33 -> ItemCategory.STATUS_INFLICT;
            case 34, 35, 36 -> ItemCategory.DAMAGE_SINGLE;
            case 37, 38, 39, 40 -> ItemCategory.DAMAGE_ALL;
            default -> ItemCategory.NONE;
        };
    }

    private static TargetType getTargetById(int id) {
        return switch (id) {
            case 1, 2, 3, 4, 5, 6, 7, 8,
                 9, 10, 11, 12, 13,
                 14, 15, 16, 17,
                 18, 19, 20, 21 -> TargetType.SELF;
            case 22, 23, 24, 25, 26, 27, 28,
                 29, 30, 31, 32, 33,
                 34, 35, 36 -> TargetType.ENEMY_SINGLE;
            case 37, 38, 39, 40 -> TargetType.ENEMY_ALL;
            default -> TargetType.SELF;
        };
    }

    private static ItemEffectType getEffectTypeById(int id) {
        // 任意：UI分類やフィルタ用に使うならここで定義
        return switch (getCategoryById(id)) {
            case HEAL_HP -> ItemEffectType.HEAL_HP;
            case HEAL_MP -> ItemEffectType.HEAL_MP;
            case FULL_RECOVERY -> ItemEffectType.HEAL_ALL;
            case STATUS_RECOVERY -> ItemEffectType.REMOVE_STATUS;
            case BUFF -> ItemEffectType.BUFF;
            case DEBUFF -> ItemEffectType.DEBUFF;
            case STATUS_INFLICT -> ItemEffectType.APPLY_STATUS;
            case DAMAGE_SINGLE -> ItemEffectType.DAMAGE_SINGLE;
            case DAMAGE_ALL -> ItemEffectType.DAMAGE_ALL;
            case MAGIC_CAST -> ItemEffectType.SPECIAL_EFFECT;
            default -> null;
        };
    }
}