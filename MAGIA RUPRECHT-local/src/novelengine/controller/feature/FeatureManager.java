package novelengine.controller.feature;

import java.util.List;
import java.util.Map;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import novelengine.model.BGMData;
import novelengine.model.BackgroundData;
import novelengine.model.CharacterData;
import novelengine.model.Dialogue;

/**
 * FeatureManager（新JSON仕様専用版）
 * AnchorPaneベース版
 */
public class FeatureManager {

    private final BackgroundManager backgroundManager;
    private final CharacterManager characterManager;
    private final BGMManager bgmManager;
    private final TextManager textManager;
    private final double bgmVolume;

    /** 
     * root は AnchorPane で統一
     */
    public FeatureManager(AnchorPane root, double bgmVolume) {
        this.backgroundManager = new BackgroundManager();
        this.characterManager = new CharacterManager();
        this.bgmManager = new BGMManager();
        this.bgmVolume = bgmVolume;
        this.textManager = new TextManager();

        // -------------------------------
        // ★ 背景チラつき防止用の黒レイヤーを最背面に追加
        // -------------------------------
        Pane blackLayer = new Pane();
        Rectangle rect = new Rectangle(1280, 720, Color.BLACK);
        blackLayer.getChildren().add(rect);
        root.getChildren().add(0, blackLayer); // 最背面に追加

        // -------------------------------
        // ★ レイヤー追加順の統一（背景 → キャラ → テキスト）
        // -------------------------------
        root.getChildren().addAll(
            backgroundManager.getNode(),   // 背景レイヤー
            characterManager.getNode(),    // キャラレイヤー
            textManager.getNode()          // テキストレイヤー
        );

        // -------------------------------
        // AnchorPane フィット処理
        // -------------------------------
        AnchorPane.setTopAnchor(backgroundManager.getNode(), 0.0);
        AnchorPane.setBottomAnchor(backgroundManager.getNode(), 0.0);
        AnchorPane.setLeftAnchor(backgroundManager.getNode(), 0.0);
        AnchorPane.setRightAnchor(backgroundManager.getNode(), 0.0);

        AnchorPane.setTopAnchor(characterManager.getNode(), 0.0);
        AnchorPane.setBottomAnchor(characterManager.getNode(), 0.0);
        AnchorPane.setLeftAnchor(characterManager.getNode(), 0.0);
        AnchorPane.setRightAnchor(characterManager.getNode(), 0.0);

        AnchorPane.setBottomAnchor(textManager.getNode(), 0.0);
        AnchorPane.setLeftAnchor(textManager.getNode(), 0.0);
        AnchorPane.setRightAnchor(textManager.getNode(), 0.0);
    }

    public BackgroundManager getBackgroundManager() { return backgroundManager; }
    public CharacterManager getCharacterManager() { return characterManager; }
    public BGMManager getBgmManager() { return bgmManager; }
    public TextManager getTextManager() { return textManager; }

    /**
     * Dialogue を処理
     */
    @SuppressWarnings("unchecked")
    public void handleDialogue(Map<String, Object> dialogue) {
        if (dialogue == null) return;

        // --- features の適用 ---
        Map<String, Object> features = (Map<String, Object>) dialogue.get("features");
        handleFeatures(features);

        // --- skipText 判定 ---
        Object skipObj = dialogue.get("skipText");
        String text = (String) dialogue.get("text");

        boolean skipText = (skipObj instanceof Boolean && (Boolean) skipObj)
                        || (text == null || text.isEmpty());

        // --- Dialogue オブジェクトを生成 ---
        Dialogue dialogueObj = new Dialogue();
        dialogueObj.setSpeaker((String) dialogue.get("speaker"));
        dialogueObj.setText(text);
        dialogueObj.setFeatures(features);

        // --- 表示処理 ---
        if (skipText) {
            textManager.playSkippedDialogue(dialogueObj, null);
        } else {
            textManager.playDialogueWithAnimation(dialogueObj, null);
        }
    }

    /**
     * features の内容を解釈して一括適用
     */
    @SuppressWarnings("unchecked")
    public void handleFeatures(Map<String, Object> features) {
        if (features == null) return;

        // --- 背景 ---
        if (features.containsKey("background")) {
            Object bgObj = features.get("background");
            if (bgObj instanceof Map) {
                Map<String, Object> bgMap = (Map<String, Object>) bgObj;
                BackgroundData bgData = new BackgroundData();
                bgData.setFileName((String) bgMap.get("file"));
                if (bgMap.containsKey("fadeIn"))
                    bgData.setFadeInSeconds(((Number) bgMap.get("fadeIn")).doubleValue());
                if (bgMap.containsKey("transitionType"))
                    bgData.setTransitionType((String) bgMap.get("transitionType"));

                backgroundManager.setBackground(bgData);
            }
        }

        // --- キャラクター ---
        if (features.containsKey("characters")) {
            Object charObj = features.get("characters");
            if (charObj instanceof List) {

                List<CharacterData> charList = ((List<?>) charObj).stream()
                    .map(obj -> {
                        if (obj instanceof Map) {
                            Map<String, Object> map = (Map<String, Object>) obj;

                            CharacterData data = new CharacterData();
                            data.setName((String) map.get("name"));
                            data.setImage((String) map.get("image"));
                            data.setPosition((String) map.get("position"));
                            data.setFacing((String) map.get("facing"));

                            if (map.containsKey("clear"))
                                data.setClear((String) map.get("clear"));

                            if (map.containsKey("fadeIn"))
                                data.setFadeIn((Boolean) map.get("fadeIn"));

                            return data;
                        }
                        return null;
                    })
                    .filter(d -> d != null)
                    .toList();

                boolean wasCleared = false;
                for (CharacterData cd : charList) {
                    if (cd.getClear() != null) {
                        characterManager.clearCharactersBySlot(cd.getClear());
                        wasCleared = true;
                    }
                }

                if (!wasCleared) {
                    characterManager.loadCharactersWithSlots(charList);
                }
            }
        }

        // --- BGM ---
        if (features.containsKey("bgm")) {
            Object bgmObj = features.get("bgm");
            if (bgmObj instanceof Map) {
                Map<String, Object> bgmMap = (Map<String, Object>) bgmObj;

                BGMData data = new BGMData();
                data.setFileName((String) bgmMap.get("file"));
                data.setVolume(bgmVolume); // ★ 外部音量を必ず適用

                if (bgmMap.containsKey("fadeIn"))
                    data.setFadeInSeconds(((Number) bgmMap.get("fadeIn")).doubleValue());
                if (bgmMap.containsKey("loop"))
                    data.setLoop((Boolean) bgmMap.get("loop"));

                bgmManager.playOrStopBGM(data);
            }
        }
    }

    /** 各マネージャーの終了処理 */
    public void stopAll() {
        if (backgroundManager != null) backgroundManager.clearBackground();
        if (bgmManager != null) bgmManager.clearBGM();
        if (textManager != null) textManager.clearText();
        if (characterManager != null) characterManager.clearAllCharacters();
    }

}
