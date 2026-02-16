package application;

import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {

    // スキルID → スキル名
    private static final Map<Integer, String> skillNames = new HashMap<>();

    // スキルID → 重み（AIが参照する）
    private static final Map<Integer, Integer> skillWeights = new HashMap<>();

    /** JSON の SkillEntry を登録する */
    public static void register(EnemyAIData.SkillEntry entry) {
        skillNames.put(entry.id, entry.name);
        skillWeights.put(entry.id, entry.weight);
    }

    /** スキル名を取得（ログ用） */
    public static String getName(int skillId) {
        return skillNames.getOrDefault(skillId, "Unknown Skill");
    }

    /** 重みを取得（AI用） */
    public static int getWeight(int skillId) {
        return skillWeights.getOrDefault(skillId, 0);
    }

    /** スキルが登録されているか */
    public static boolean contains(int skillId) {
        return skillNames.containsKey(skillId);
    }
}