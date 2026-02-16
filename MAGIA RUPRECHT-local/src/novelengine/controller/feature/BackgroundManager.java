package novelengine.controller.feature;

import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import novelengine.model.BackgroundData;

/**
 * 背景画像の切り替え・フェード管理を行うクラス。
 */
public class BackgroundManager {

    private final ImageView backgroundView = new ImageView();
    private final Pane backgroundLayer = new Pane();   // ★レイヤーを固定化
    private String currentBackgroundFile;

    public BackgroundManager() {

        // ImageView の設定
        backgroundView.setFitWidth(1280);
        backgroundView.setFitHeight(720);
        backgroundView.setPreserveRatio(false);

        // ★Pane に ImageView を1回だけセットする
        backgroundLayer.getChildren().add(backgroundView);
        backgroundLayer.setPrefSize(1280, 720);
    }

    /** 背景レイヤーを返す（SceneRendererのrootに追加される） */
    public Pane getNode() {
        return backgroundLayer;  // ★毎回同じインスタンスを返す
    }

    /** 背景を設定（BackgroundData対応） */
    public void setBackground(BackgroundData data) {
        if (data == null || data.getFileName() == null || data.getFileName().isEmpty()) return;

        String filename = data.getFileName();
        if (filename.equals(currentBackgroundFile)) {
            // 同じ背景なら何もしない
            return;
        }

        String resourcePath = "/novelengine/data/images/bg/" + filename;
        var url = getClass().getResource(resourcePath);

        if (url == null) {
            System.err.println("背景画像がクラスパス上に見つかりません: " + resourcePath);
            return;
        }

        Image newImage = new Image(url.toExternalForm());
        Image oldImage = backgroundView.getImage();
        backgroundView.setImage(newImage);

        if (oldImage != null && data.getFadeInSeconds() > 0) {
            // フェードイン演出
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(data.getFadeInSeconds()), backgroundView);
            backgroundView.setOpacity(0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } else {
            backgroundView.setOpacity(1.0);
        }

        currentBackgroundFile = filename;

        // ★正常に表示されたことを示すログ
        System.out.println("背景画像を設定しました: " + filename);
    }

    /** 現在の背景ファイル名を取得 */
    public String getCurrentBackgroundFile() {
        return currentBackgroundFile;
    }

    /** 終了処理 */
    public void clearBackground() {
        // 現在の背景画像をクリア
        backgroundView.setImage(null);

        // 不要なら透過度を初期化（黒残り対策）
        backgroundView.setOpacity(1.0);

        // 現在のファイル情報をリセット
        currentBackgroundFile = null;
    }

}
