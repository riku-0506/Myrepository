package novelengine.controller;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import novelengine.controller.feature.FeatureManager;
import novelengine.model.BackgroundData;
import novelengine.model.Dialogue;
import novelengine.model.SceneData;

public class SceneRenderer {

    private final Stage stage;
    private final AnchorPane root;
    private final FeatureManager featureManager;
    private final double bgmVolume; 

    // ▼ アイコン関連
    private Label nextIcon;
    private TranslateTransition nextIconBounce;
    private PauseTransition nextIconDelay;

    // Main から直接 AnchorPane を渡すスタイルへ一本化
    public SceneRenderer(Stage stage, AnchorPane rootLayer, double bgmVolume) {
        this.stage = stage;
        this.root = rootLayer;
        this.bgmVolume = bgmVolume; 
        this.featureManager = new FeatureManager(root, bgmVolume);

        initNextIcon();   // ★ 追加
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

        hideNextIcon();  // ★ 次のセリフ開始時に▼を消す

        featureManager.handleFeatures(dialogue.getFeatures());

        featureManager.getTextManager().playDialogueWithAnimation(dialogue, () -> {

            showNextIconDelayed();  // ★ 表示完了 → 0.25秒後に▼表示

            if (onFinished != null) {
                onFinished.run();
            }
        });

        featureManager.getCharacterManager().updateCharacterFocus(dialogue.getSpeaker());
    }

    public void setTextSpeed(double millisPerChar) {
        featureManager.getTextManager().setTextSpeed(millisPerChar);
    }

    public void stopAllFeatures() {
        featureManager.stopAll();
    }

    // =========================
    // ▼ アイコン制御
    // =========================

    /** ▼アイコン初期化 */
    private void initNextIcon() {
        nextIcon = new Label("▼");
        nextIcon.setStyle("""
            -fx-font-size: 26;
            -fx-text-fill: white;
            -fx-effect: dropshadow(gaussian, black, 4, 0.5, 0, 0);
        """);

        nextIcon.setVisible(false);

        // テキストボックス右下想定
        AnchorPane.setRightAnchor(nextIcon, 130.0);
        AnchorPane.setBottomAnchor(nextIcon, 20.0);

        root.getChildren().add(nextIcon);

        // ▼が上下にぴょんぴょん跳ねるアニメ
        nextIconBounce = new TranslateTransition(Duration.seconds(0.6), nextIcon);
        nextIconBounce.setFromY(0);
        nextIconBounce.setToY(-10);
        nextIconBounce.setAutoReverse(true);
        nextIconBounce.setCycleCount(Animation.INDEFINITE);
    }

    /** 表示完了から 0.25 秒後に ▼ を表示 */
    private void showNextIconDelayed() {
        if (nextIconDelay != null) {
            nextIconDelay.stop();
        }

        nextIconDelay = new PauseTransition(Duration.seconds(0.1));
        nextIconDelay.setOnFinished(e -> {
            nextIcon.setVisible(true);
            nextIconBounce.playFromStart();
        });

        nextIconDelay.playFromStart();
    }

    /** ▼ を非表示にする */
    private void hideNextIcon() {
        if (nextIconDelay != null) nextIconDelay.stop();
        if (nextIconBounce != null) nextIconBounce.stop();

        nextIcon.setVisible(false);
        nextIcon.setTranslateY(0);
    }
}
