package novelengine;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import novelengine.controller.ScenarioController;
import novelengine.controller.SceneRenderer;
import novelengine.model.Scenario;
import novelengine.util.ScenarioLoader;

public class ScenarioPlayer {

    private final ScenarioController controller;
    private final SceneRenderer renderer;
    private final Stage stage;
    private Runnable onFinished;

    public ScenarioPlayer(Stage stage, Scene existingScene, String chapterName, double bgmVolume) throws Exception {
        System.out.println("=== ScenarioPlayer: コンストラクタ開始 ===");
        System.out.println("入力パラメータ chapterName = " + chapterName);

        this.stage = stage;

        // 既存 Scene は AnchorPane 前提
        if (!(existingScene.getRoot() instanceof AnchorPane)) {
            System.err.println("[ERROR] existingScene.getRoot() が AnchorPane ではありません！");
        }
        AnchorPane rootPane = (AnchorPane) existingScene.getRoot();
        System.out.println("既存の rootPane 子ノード数: " + rootPane.getChildren().size());

        // ================================
        // ★ シナリオ専用レイヤー
        // ================================
        AnchorPane scenarioLayer = new AnchorPane();
        scenarioLayer.setPickOnBounds(false);

        AnchorPane.setTopAnchor(scenarioLayer, 0.0);
        AnchorPane.setBottomAnchor(scenarioLayer, 0.0);
        AnchorPane.setLeftAnchor(scenarioLayer, 0.0);
        AnchorPane.setRightAnchor(scenarioLayer, 0.0);

        rootPane.getChildren().add(scenarioLayer);
        System.out.println("scenarioLayer を rootPane に追加しました");
        System.out.println("rootPane の更新後 子ノード数: " + rootPane.getChildren().size());

        // ================================
        // シナリオ読み込み
        // ================================
        String scenarioPath = "/novelengine/data/scenario/" + chapterName + ".json";
        System.out.println("シナリオ読込パス: " + scenarioPath);

        ScenarioLoader loader = new ScenarioLoader();
        Scenario scenario = loader.load(scenarioPath);

        if (scenario == null) {
            System.err.println("[ERROR] シナリオのロードに失敗しました: " + scenarioPath);
        } else {
            System.out.println("シナリオ読込成功: " + chapterName);
            System.out.println("シーン数: " + scenario.getScenes().size());
        }

        controller = new ScenarioController(scenario);
        System.out.println("ScenarioController 初期化完了");

        // ================================
        // SceneRenderer 初期化
        // ================================
        renderer = new SceneRenderer(stage, scenarioLayer, bgmVolume);
        System.out.println("SceneRenderer 初期化完了");

        System.out.println("=== ScenarioPlayer: コンストラクタ終了 ===");
    }

    public void play() {
        System.out.println("=== ScenarioPlayer.play() 開始 ===");

        System.out.println("最初の Scene を描画します");
        System.out.println("→ currentScene: " + controller.getCurrentScene());
        System.out.println("→ currentDialogue: " + controller.getCurrentDialogue());

        renderer.renderScene(controller.getCurrentScene());
        renderer.playDialogueWithAnimation(controller.getCurrentDialogue(), null);

        stage.getScene().setOnMouseClicked(e -> {

            System.out.println("=== マウスクリック ===");

            if (controller.nextDialogue()) {
                System.out.println("次の Dialogue に進みます");
                System.out.println("→ currentScene: " + controller.getCurrentScene());
                System.out.println("→ currentDialogue: " + controller.getCurrentDialogue());

                renderer.renderScene(controller.getCurrentScene());
                renderer.playDialogueWithAnimation(controller.getCurrentDialogue(), null);

            } else {
                System.out.println("=== シナリオ終了 ===");

                if (onFinished != null) {
                    System.out.println("onFinished コールバックを実行します");
                    onFinished.run();
                }

                stopScenario();
            }
        });

        System.out.println("=== ScenarioPlayer.play() 終了 ===");
    }

    public void setTextSpeed(double millisPerChar) {
        System.out.println("文字送り速度変更: " + millisPerChar + " ms/char");
        renderer.setTextSpeed(millisPerChar);
    }

    public void stopScenario() {
        System.out.println("=== stopScenario(): シナリオ終了処理開始 ===");

        renderer.stopAllFeatures();

        System.out.println("=== stopScenario(): 完了 ===");
    }

    public void setOnFinished(Runnable callback) {
        System.out.println("終了コールバックが設定されました");
        this.onFinished = callback;
    }
}
