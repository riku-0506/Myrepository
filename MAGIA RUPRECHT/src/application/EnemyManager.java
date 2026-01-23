package application;

import java.util.Map;

public class EnemyManager {

    // ------------------------------------------------------------
    // ★ 敵を倒したときに呼ぶ
    // ------------------------------------------------------------
    public static void addKill(int enemyId) {

        QuestController qc = QuestController.getInstance();
        if (qc == null) return;

        Map<String, QuestData> map = qc.getQuestMap();

        boolean progressChanged = false;

        for (QuestData quest : map.values()) {

            if (!quest.isAccepted()) continue;  // ★ 受注中のみ対象

            if (quest.getCondition() instanceof KillCondition kc) {

                int before = kc.getCurrentCount();
                kc.onEnemyKilled(enemyId);

                // カウントが増えていなければ何もしない
                if (kc.getCurrentCount() == before) continue;

                progressChanged = true;
            }
        }

        // 進行が変わっていないなら無駄な処理をさせない
        if (!progressChanged) return;

        // ★ 重要：ここで達成判定 & 報酬付与
        qc.updateQuestProgress();
    }
}
