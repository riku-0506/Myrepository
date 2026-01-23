package novelengine.controller;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import novelengine.controller.feature.FeatureManager;
import novelengine.model.BackgroundData;
import novelengine.model.Dialogue;
import novelengine.model.SceneData;

public class SceneRenderer {

    private final Stage stage;
    private final AnchorPane root;
    private final FeatureManager featureManager;
    private final double bgmVolume; 

    // Main から直接 AnchorPane を渡すスタイルへ一本化
    public SceneRenderer(Stage stage, AnchorPane rootLayer, double bgmVolume) {
        this.stage = stage;
        this.root = rootLayer;
        this.bgmVolume = bgmVolume; 
        this.featureManager = new FeatureManager(root, bgmVolume);
    }

    /** シーン描画 */
    public void renderScene(SceneData sceneData) {
        if (sceneData == null) return;

        BackgroundData bg = sceneData.getBackground();
        if (bg != null) {
            featureManager.getBackgroundManager().setBackground(bg);
        }

        if (sceneData.getCharacters() != null) {
            featureManager.getCharacterManager().loadCharactersWithSlots(sceneData.getCharacters());
        }
    }

    /** セリフ再生（文字送り） */
    public void playDialogueWithAnimation(Dialogue dialogue, Runnable onFinished) {
        if (dialogue == null) return;

        featureManager.handleFeatures(dialogue.getFeatures());
        featureManager.getTextManager().playDialogueWithAnimation(dialogue, onFinished);
        featureManager.getCharacterManager().updateCharacterFocus(dialogue.getSpeaker());
    }

    public void setTextSpeed(double millisPerChar) {
        featureManager.getTextManager().setTextSpeed(millisPerChar);
    }

    public void stopAllFeatures() {
        featureManager.stopAll();
    }
}
