package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // ★ 設定ファイルを必ず最初にロード
            ConfigManager.load();
            System.out.println("[Main] Config loaded at startup.");
			
        	// ★ フォントを jar 内から読み込む（jpackage でも確実に動く）
            Font.loadFont(getClass().getResourceAsStream("/application/fonts/ChihayaGothic.ttf"), 71);

            // SceneManager に Stage を登録
            SceneManager.setStage(primaryStage);
        	
            // ★ FXML を推奨形式で読み込む
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Start.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            // 画面を表示
            primaryStage.setScene(scene);
            primaryStage.setTitle("MAGIA RUPRECHT");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(Main.class, args);
    }

}