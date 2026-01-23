package novelengine.model;

import java.util.List;
import java.util.Map;

/**
 * SceneData（FeatureManager対応版）
 * 
 * 1つのシーンに関するデータを保持するモデル。
 * 背景・BGM・登場キャラクター・会話データに加えて、
 * featuresマップとしてまとめて演出情報を保持できる。
 */
public class SceneData {

    private BackgroundData background;      // 背景情報
    private BGMData bgm;                    // BGM情報
    private List<CharacterData> characters; // 登場キャラクター情報
    private List<Dialogue> dialogues;       // 会話データ
    private Map<String, Object> features;   // 統一演出情報（背景・BGM・キャラを含む）

    // --- Getter / Setter ---
    public BackgroundData getBackground() { return background; }
    public void setBackground(BackgroundData background) { this.background = background; }

    public BGMData getBgm() { return bgm; }
    public void setBgm(BGMData bgm) { this.bgm = bgm; }

    public List<CharacterData> getCharacters() { return characters; }
    public void setCharacters(List<CharacterData> characters) { this.characters = characters; }

    public List<Dialogue> getDialogues() { return dialogues; }
    public void setDialogues(List<Dialogue> dialogues) { this.dialogues = dialogues; }

    public Map<String, Object> getFeatures() { return features; }
    public void setFeatures(Map<String, Object> features) { this.features = features; }
}