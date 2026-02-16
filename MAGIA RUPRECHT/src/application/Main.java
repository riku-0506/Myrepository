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

            // SceneManager に Stage を登録
            SceneManager.setStage(primaryStage);

//            Font.loadFont("/application/fonts/ChihayaGothic.ttf", 71);
          Font f = Font.loadFont(getClass().getResourceAsStream("/application/fonts/ChihayaGothic.ttf"), 20);
			System.out.println("Loaded font name: " + f.getName());

            // 最初の画面を読み込む
            AnchorPane root = FXMLLoader.load(getClass().getResource("/application/Start.fxml"));
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

            //画面を表示
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
