package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StartController implements Initializable {

    @FXML
    private Button NewStart;
	
	@FXML
    private Text continueText;
	
	@FXML
    private Button StartContinueButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        BGMPlayer.play("拠点/maou_bgm_fantasy10.mp3");

        Platform.runLater(() -> {

            boolean hasSave = SaveDataUtil.exists();

            // ----------------------------
            // つづきから制御
            // ----------------------------
            StartContinueButton.setOpacity(0);
            StartContinueButton.setDisable(!hasSave);

            continueText.setOpacity(hasSave ? 1.0 : 0.3);

            // 完全に消したいなら ↓
            // continueText.setVisible(hasSave);

            // ----------------------------
            // カーソル設定
            // ----------------------------
            Scene scene = NewStart.getScene();
            if (scene != null) {
                ImageCursor normal = loadCursor("images/cursor.png");
                ImageCursor hover = loadCursor("images/cursor_hover.png");

                SceneManager.setCursors(normal, hover);
                if (normal != null) scene.setCursor(normal);
            }
        });
    }

    // カーソル画像読み込み
    private ImageCursor loadCursor(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println("カーソルが見つかりません: " + path);
            return null;
        }
        Image img = new Image(url.toExternalForm(), 64, 64, true, true);
        return new ImageCursor(img);
    }

    @FXML
void NewStart(ActionEvent event) {
    SEPlayer.play("イベント/click.mp3");

    Stage currentStage =
        (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene currentScene =
        ((Node) event.getSource()).getScene();

    // ============================
    // セーブ有無チェック
    // ============================
    boolean hasSave = SaveDataUtil.exists();

    // ----------------------------
    // セーブが無い場合
    // → 確認なしで即ストーリー開始
    // ----------------------------
    if (!hasSave) {
        System.out.println("セーブなし：確認せずストーリー開始");
        initDatabaseAndStartStory(currentStage, currentScene);
        return;
    }

    // ----------------------------
    // セーブがある場合
    // → 確認ダイアログを出す
    // ----------------------------
    StartDialogUtil.showConfirmDialog(currentStage, result -> {
        if (!result) {
            System.out.println("キャンセルされました");
            return;
        }

        System.out.println("セーブあり：初期化してストーリー開始");
        initDatabaseAndStartStory(currentStage, currentScene);
    });
}
	
	// ==================================
// DB初期化 → ストーリー開始
// ==================================
private void initDatabaseAndStartStory(Stage stage, Scene scene) {

    // ----------------------------
    // DB 初期化
    // ----------------------------
    try {
        // ▼ 1. Program Files 内の init.sql の場所を取得
        File jarFile = new File(DBManager.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        File jarDir = jarFile.getParentFile(); // app/
        Path sqlPath = Paths.get(jarDir.getAbsolutePath(), "init.sql");

        if (!Files.exists(sqlPath)) {
            throw new IOException("init.sql が見つかりません: " + sqlPath);
        }

        // ▼ 2. init.sql を読み込む
        String sql = Files.readString(sqlPath);

        // ▼ 3. ユーザーディレクトリの game.db に接続
        try (Connection conn = DBManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // ▼ 4. SQL を1文ずつ実行
            for (String s : sql.split(";")) {
                if (!s.trim().isEmpty()) {
                    stmt.executeUpdate(s.trim());
                }
            }
        }

        System.out.println("初期化処理が完了しました");

    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("初期化処理に失敗しました: " + e.getMessage());
    }

    // ----------------------------
    // ストーリー開始
    // ----------------------------
    new Story_Sample().show(
        stage,
        scene,
        0,
        () -> {
            System.out.println("[DEBUG] Story finished → Menu.fxml");
            SceneManager.changeScene("Menu.fxml");
            SaveDataUtil.markStoryFinished();
        }
    );
}

    @FXML
    public void StartContinue(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Menu.fxml");
    }

    @FXML
    void Option(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        OptionDialogUtil.showConfirmDialog(currentStage, result -> {
            if (result) {
                System.out.print("OK");
            } else {
                System.out.println("Cancel");
            }
        });
    }
}
