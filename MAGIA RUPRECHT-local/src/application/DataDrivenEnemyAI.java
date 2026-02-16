package application;

import java.util.Random;

public class DataDrivenEnemyAI {

    private final EnemyAIData data;
    private int turn = 0;
    private final Random random = new Random();

    public DataDrivenEnemyAI(EnemyAIData data) {
        this.data = data;
    }

    /** このターンに使うスキルIDを返す */
    public int decideSkillId(Character self, Character player) {
        turn++;

        // 1. actions が存在する場合は条件を評価
        if (data.actions != null && !data.actions.isEmpty()) {
            for (var action : data.actions) {
                if (evaluateCondition(action.condition, self, player)) {
                	System.out.println("[AI] 選択されたスキルID: " + action.skillId + " (" + SkillRegistry.getName(action.skillId) + ")");

                    return action.skillId;
                }
            }
        }

        // 2. 条件に一致しなかった場合は skills の重みづけで選ぶ
        return chooseWeightedRandomSkill();
    }

    /** 重みづけランダム選択 */
    private int chooseWeightedRandomSkill() {
        int totalWeight = 0;
        for (var s : data.skills) {
            totalWeight += s.weight;
        }

        int r = random.nextInt(totalWeight);
        int sum = 0;

        for (var s : data.skills) {
            sum += s.weight;
            if (r < sum) {
                return s.id;
            }
        }

        // ここに来ることはほぼないが保険
        return data.skills.get(0).id;
    }

    /** 条件式の評価（必要に応じて拡張） */
    private boolean evaluateCondition(String expr, Character self, Character player) {

        if ("true".equals(expr)) return true;

        if (expr.startsWith("self.hp <")) {
            double threshold = Double.parseDouble(expr.replace("self.hp <", "").trim());
            return self.getHP() < self.getMaxHP() * threshold;
        }

        if (expr.startsWith("player.hp <")) {
            double threshold = Double.parseDouble(expr.replace("player.hp <", "").trim());
            return player.getHP() < player.getMaxHP() * threshold;
        }

        if (expr.startsWith("turn >")) {
            int value = Integer.parseInt(expr.replace("turn >", "").trim());
            return turn > value;
        }

        return false;
    }
}