package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaveEnemySelector {

    private static final int MIN_ENEMY_COUNT = 2;
    private static final int MAX_ENEMY_COUNT = 5;

    /**
     * 通常ウェーブ用の敵をランダムに選出
     */
    public static List<Enemy> selectEnemies(int stageId) {
        List<Enemy> candidates = EnemyDAO.getEnemiesByStageId(stageId);
        List<Enemy> result = new ArrayList<>();

        if (candidates.size() < MIN_ENEMY_COUNT) {
            System.err.println("ステージ " + stageId + " に必要な敵数が足りません（現在: " + candidates.size() + "）");
            return result;
        }

        int count = new Random().nextInt(MAX_ENEMY_COUNT - MIN_ENEMY_COUNT + 1) + MIN_ENEMY_COUNT;
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            Enemy template = candidates.get(rand.nextInt(candidates.size()));
            Enemy e = new Enemy(template); // ★ コピーコンストラクタで新しいインスタンスを生成
            result.add(e);
        }


        return result;
    }

    /**
     * ボスウェーブ用の固定敵を取得
     */
    public static Enemy getBossByStage(int stageId) {
        int bossId;

        switch (stageId) {
            case 1 -> bossId = 5;
            case 2 -> bossId = 10;
            case 3 -> bossId = 15;
            case 4 -> bossId = 20;
            default -> {
                System.out.println("未定義のステージIDです");
                return null;
            }
        }

        return EnemyDAO.getEnemyById(bossId);
    }
}