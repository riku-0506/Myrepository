package novelengine.model;

import java.util.Map;

public class Dialogue {

    private String speaker;      // 発話者
    private String text;         // セリフ本文

    // ★ 新規追加: このセリフに紐づく演出情報
    // BGM, SE, キャラクター表情変更, カメラ, 条件分岐など
    private Map<String, Object> features;

    // --- Getter / Setter ---
    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Map<String, Object> getFeatures() { return features; }
    public void setFeatures(Map<String, Object> features) { this.features = features; }
}
