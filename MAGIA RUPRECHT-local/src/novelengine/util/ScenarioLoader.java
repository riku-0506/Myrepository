package novelengine.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import novelengine.model.Scenario;

public class ScenarioLoader {

    /**
     * JSONファイルを読み込み、Scenarioオブジェクトに変換する
     * @param resourcePath 例: "/scenario/chapter1.json"
     */
    public Scenario load(String resourcePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // クラスパス上（resourcesやdataフォルダ）から読み込む
        try (var stream = getClass().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalArgumentException("リソースが見つかりません(テスト文言の追加): " + resourcePath);
            }
            return mapper.readValue(stream, Scenario.class);
        }
    }
}
