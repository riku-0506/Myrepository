package application;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EnemyDisplayController {

    private HBox enemyHBox;
    private List<VBox> boxList;
    private List<ImageView> imageList;
    private List<ProgressBar> hpList;

    public void injectNodes(
        HBox hbox,
        List<VBox> boxes,
        List<ImageView> images,
        List<ProgressBar> hps
    ) {
        this.enemyHBox  = hbox;
        this.boxList    = boxes;
        this.imageList  = images;
        this.hpList     = hps;
    }

    public void showEnemies(List<Enemy> enemies) {
        enemyHBox.setAlignment(Pos.CENTER);

        // 既存のボックスを非表示に
        for (VBox box : boxList) {
            box.setVisible(false);
        }
        enemyHBox.getChildren().clear();

        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);

            VBox box = boxList.get(i);
            ImageView imageView = imageList.get(i);
            ProgressBar hpBar = hpList.get(i);

            try {
                // クラスパスからリソースを読み込む
                Image image = new Image(
                    getClass().getResource("/application/" + e.imagePath).toExternalForm()
                );
                imageView.setImage(image);
                System.out.println("画像読み込み成功: " + "/application/" + e.imagePath);
            } catch (Exception ex) {
                System.err.println("画像読み込み失敗: " + "/application/" + e.imagePath);
                ex.printStackTrace();
            }

            box.setVisible(true);
            enemyHBox.getChildren().add(box);
        }
    }

    // HP更新用
    public void updateHP(int index, int currentHP, int maxHP) {
        if (index < 0 || index >= hpList.size()) return;
        ProgressBar bar = hpList.get(index);
        double progress = maxHP > 0 ? (double) currentHP / maxHP : 0.0;
        bar.setProgress(progress);
    }

    public List<ProgressBar> getHPBarList() {
        return hpList;
    }
    
}