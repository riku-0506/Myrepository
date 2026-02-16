package application;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    // ★ 履歴には「画面遷移の FXML」だけを保存
    private static final Deque<String> sceneHistory = new ArrayDeque<>();

    private static ImageCursor defaultCursor;
    private static ImageCursor hoverCursor;

    // ★ 画面遷移専用の FXML
    private static String currentFxml;


    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setCursors(ImageCursor normal, ImageCursor hover) {
        defaultCursor = normal;
        hoverCursor = hover;
    }


    // ★ 画面遷移
    public static void changeScene(String fxmlPath) {
        // ★ バトル画面なら自動で changeBattleScene に振り分け
        if (isBattleStage(fxmlPath)) {
            changeBattleScene(fxmlPath);
            return;
        }

        // ★ 通常画面
        try {
            if (currentFxml != null) sceneHistory.push(currentFxml);
            currentFxml = fxmlPath;

            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/application/" + fxmlPath)
            );

            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            scene.getStylesheets().add(
                    SceneManager.class.getResource("application.css").toExternalForm()
                );

            if (defaultCursor != null) scene.setCursor(defaultCursor);
            applyHoverCursorToAllButtons(root, scene);

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void changeBattleScene(String fxmlPath) {
        try {
            if (currentFxml != null) sceneHistory.push(currentFxml);
            currentFxml = fxmlPath;

            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/application/" + fxmlPath)
            );
            
            StageData data = getStageDataFromPath(fxmlPath);

            // ★ StageController を自分で生成して注入
            StageController controller = StageFactory.create(data.stageId);
            loader.setController(controller);

            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            scene.getStylesheets().add(
                    SceneManager.class.getResource("application.css").toExternalForm()
                );

            StageController.setInstance(controller);
            controller.init(data);

            if (defaultCursor != null) scene.setCursor(defaultCursor);
            applyHoverCursorToAllButtons(root, scene);

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static boolean isBattleStage(String fxmlPath) {
        return switch (fxmlPath) {
            case "Stage1.fxml", "Stage2.fxml", "Stage3.fxml",
                 "Stage4.fxml", "StageEX.fxml" -> true;
            default -> false;
        };
    }

    
    private static StageData getStageDataFromPath(String fxmlPath) {
    	return switch (fxmlPath) {
	        case "Stage1.fxml" -> StageData.stage1();
	        case "Stage2.fxml" -> StageData.stage2();
	        case "Stage3.fxml" -> StageData.stage3();
	        case "Stage4.fxml" -> StageData.stage4();
	        case "StageEX.fxml" -> StageData.stageEX();
	        default -> throw new IllegalArgumentException("Unknown battle stage: " + fxmlPath);
    	};

    }



    private static void applyHoverCursorToAllButtons(Parent root, Scene scene) {
        root.lookupAll(".button").forEach(node -> {
            if (node instanceof Button btn) {

                btn.setOnMouseEntered(e -> {
                    if (hoverCursor != null)
                        scene.setCursor(hoverCursor);
                });

                btn.setOnMouseExited(e -> {
                    if (defaultCursor != null)
                        scene.setCursor(defaultCursor);
                });
            }
        });
    }
    
    public static Stage getCurrentStage() {
        return primaryStage;
    }
    
    public static Scene getCurrentScene() {
        return primaryStage != null ? primaryStage.getScene() : null;
    }


    // ★ 戻る
    public static void goBack() {
        if (!sceneHistory.isEmpty()) {
            String previousFxml = sceneHistory.pop();
            changeScene(previousFxml);
        }
    }


    public static void clearHistory() {
        sceneHistory.clear();
    }
}