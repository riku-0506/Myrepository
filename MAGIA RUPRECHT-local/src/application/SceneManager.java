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
        try {
            // ★ 履歴に「今の画面」を保存
            if (currentFxml != null) {
                sceneHistory.push(currentFxml);
            }

            // ★ 今の画面を更新
            currentFxml = fxmlPath;

            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/application/" + fxmlPath)
            );
            Parent root = loader.load();

            Scene newScene = new Scene(root);

            newScene.getStylesheets().add(
                SceneManager.class.getResource("application.css").toExternalForm()
            );

            if (defaultCursor != null)
                newScene.setCursor(defaultCursor);

            applyHoverCursorToAllButtons(root, newScene);
            
            switch (fxmlPath) {

            case "Stage1.fxml": {
                StageController controller = loader.getController();
                controller.init(StageData.stage1());
                break;
            }

            case "Stage2.fxml": {
                StageController controller = loader.getController();
                controller.init(StageData.stage2());
                break;
            }

            case "Stage3.fxml": {
                StageController controller = loader.getController();
                controller.init(StageData.stage3());
                break;
            }

            case "Stage4.fxml": {
                StageController controller = loader.getController();
                controller.init(StageData.stage4());
                break;
            }
        }


            primaryStage.setScene(newScene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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