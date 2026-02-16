package application;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnemyAIDataLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public EnemyAIData load(int enemyId) {

        String resourcePath = "/enemyAction/enemy_" + enemyId + ".json";

        try (var stream = getClass().getResourceAsStream(resourcePath)) {

            if (stream == null) {
                throw new IllegalArgumentException("敵AI JSON が見つかりません: " + resourcePath);
            }

            EnemyAIData data = mapper.readValue(stream, EnemyAIData.class);

            // null 安全性の確保
            if (data.skills == null) {
                throw new IllegalArgumentException("skills が JSON に存在しません: " + resourcePath);
            }
            if (data.actions == null) {
                data.actions = java.util.Collections.emptyList();
            }

            return data;

        } catch (Exception e) {
            throw new RuntimeException("敵AI JSON の読み込みに失敗しました: " + resourcePath, e);
        }
    }
}