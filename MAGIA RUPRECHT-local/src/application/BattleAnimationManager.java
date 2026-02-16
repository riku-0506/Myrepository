package application;

import java.util.Map;
import java.util.WeakHashMap;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BattleAnimationManager {
	
	
	// ノードごとの進行中アニメーションを管理
    private static final Map<Node, Animation> ACTIVE = new WeakHashMap<>();
	
	// 共通ダメージ演出（倒した場合は撃破演出まで流す）
    public static void playDamageAnimation(
            Character enemy,
            ProgressBar hpBar,
            Label nameLabel,
            Label statusLabel,
            HBox enemyNameStatus,
            Runnable onFinished) {

        System.out.println(">>> playDamageAnimation invoked for " + enemy.getName());

        try {
            Node enemyNode = enemy.getVisualNode();
            if (enemyNode == null) {
                System.out.println("⚠ visualNode is null for " + enemy.getName() + " → calling onFinished immediately");
                if (onFinished != null) {
                    System.out.println(">>> calling onFinished (visualNode null)");
                    onFinished.run();
                }
                return;
            }

            // 既存アニメがあれば止める（重複防止）
            cancelAnimationsFor(enemyNode);

            FadeTransition damageFlash = new FadeTransition(Duration.seconds(0.2), enemyNode);
            damageFlash.setFromValue(1.0);
            damageFlash.setToValue(0.3);
            damageFlash.setCycleCount(2);
            damageFlash.setAutoReverse(true);

            double newProgress = Math.max(0.0, (double) enemy.getHP() / enemy.getMaxHP());
            Timeline hpReduce = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(hpBar.progressProperty(), hpBar.getProgress())),
                    new KeyFrame(Duration.seconds(0.5), new KeyValue(hpBar.progressProperty(), newProgress))
            );

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.4), enemyNode);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(0.4), enemyNode);
            scaleOut.setFromX(1.0);
            scaleOut.setToX(0.6);
            scaleOut.setFromY(1.0);
            scaleOut.setToY(0.6);

            ParallelTransition disappear = new ParallelTransition(fadeOut, scaleOut);

            SequentialTransition sequence = (enemy.getHP() <= 0)
                    ? new SequentialTransition(damageFlash, hpReduce, disappear)
                    : new SequentialTransition(damageFlash, hpReduce);

            // 登録
            ACTIVE.put(enemyNode, sequence);

            sequence.setOnFinished(e -> {
                System.out.println(">>> playDamageAnimation finished for " + enemy.getName());

                Animation current = ACTIVE.get(enemyNode);
                if (current != sequence) {
                    System.out.println("⚠ ACTIVE mismatch → calling onFinished");
                    if (onFinished != null) {
                        System.out.println(">>> calling onFinished (ACTIVE mismatch)");
                        onFinished.run();
                    }
                    return;
                }

                ACTIVE.remove(enemyNode);

                if (enemy.getVisualNode() != enemyNode) {
                    System.out.println("⚠ visualNode changed → calling onFinished");
                    if (onFinished != null) {
                        System.out.println(">>> calling onFinished (visualNode changed)");
                        onFinished.run();
                    }
                    return;
                }

                if (enemy.getHP() <= 0) {
                    System.out.println(">>> enemy HP <= 0 → hiding UI for " + enemy.getName());
                    enemyNode.setOnMouseClicked(null);
                    if (hpBar != null) hpBar.setVisible(false);
                    if (nameLabel != null) { nameLabel.setText(""); nameLabel.setVisible(false); }
                    if (statusLabel != null) { statusLabel.setText(""); statusLabel.setVisible(false); }
                    if (enemyNameStatus != null) enemyNameStatus.setVisible(false);
                }

                // ★ 必ず最後に呼ぶ（ここだけで十分）
                if (onFinished != null) {
                    System.out.println(">>> calling onFinished (normal end) for " + enemy.getName());
                    onFinished.run();
                }
            });

            // ★ JavaFX Application Thread 上で必ず再生
            System.out.println(">>> playDamageAnimation → sequence.play() scheduled");
            Platform.runLater(sequence::play);

        } catch (Exception ex) {
            System.out.println("⚠ playDamageAnimation error: " + ex.getMessage());
            if (onFinished != null) {
                System.out.println(">>> calling onFinished (exception)");
                onFinished.run();
            }
        }
    }
    
    
    // 特定ノードの進行中アニメーションを停止して登録解除
    public static void cancelAnimationsFor(Node node) {
        Animation a = ACTIVE.remove(node);
        if (a != null) {
            a.stop();
        }
    }

    // 全て停止（必要なら）
    public static void cancelAllAnimations() {
        for (Animation a : ACTIVE.values()) {
            a.stop();
        }
        ACTIVE.clear();
    }
    
    private static final Map<Enemy.AttackType, String> EFFECT_MAP =
            Map.ofEntries(
                Map.entry(Enemy.AttackType.SCRATCH, "images/effect/SCRATCH.gif"),
                Map.entry(Enemy.AttackType.BITE, "images/effect/BITE.gif"),
                Map.entry(Enemy.AttackType.SLASH, "images/effect/SLASH.gif"),
                Map.entry(Enemy.AttackType.BOSS1, "images/effect/boss1.gif"),
                Map.entry(Enemy.AttackType.BOSS2, "images/effect/boss2.gif"),
                Map.entry(Enemy.AttackType.BOSS3, "images/effect/boss3.gif"),
                Map.entry(Enemy.AttackType.BOSS4, "images/effect/boss4.gif"),
                Map.entry(Enemy.AttackType.ONI, "images/effect/Oni_attack (2).gif"),
                Map.entry(Enemy.AttackType.THUNDERBIRD, "images/effect/Thunderbird_attack.gif"),
                Map.entry(Enemy.AttackType.GHOSTSHIPCAPTAIN, "images/effect/GhostShipCaptain_attack.gif"),
                Map.entry(Enemy.AttackType.EXBOSS1, "images/effect/Exboss1.gif"),
                Map.entry(Enemy.AttackType.EXBOSS2, "images/effect/Exboss2.gif")
            );

    
    private static final Map<Enemy.AttackType, Double> EFFECT_DURATION_MAP =
            Map.ofEntries(
                Map.entry(Enemy.AttackType.SCRATCH, 0.45),
                Map.entry(Enemy.AttackType.BITE, 0.45),
                Map.entry(Enemy.AttackType.SLASH, 0.5),
                Map.entry(Enemy.AttackType.BOSS1, 0.6),
                Map.entry(Enemy.AttackType.BOSS2, 0.85),
                Map.entry(Enemy.AttackType.BOSS3, 2.0),
                Map.entry(Enemy.AttackType.BOSS4, 1.2),
                Map.entry(Enemy.AttackType.ONI, 1.2),
                Map.entry(Enemy.AttackType.THUNDERBIRD, 0.9),
                Map.entry(Enemy.AttackType.GHOSTSHIPCAPTAIN, 0.8),
                Map.entry(Enemy.AttackType.EXBOSS1, 1.0),
                Map.entry(Enemy.AttackType.EXBOSS2, 1.0)
            );


    /**
     * 敵攻撃アニメーション（ジャンプ → 中央エフェクト）
     * battleRoot は AnchorPane や Pane など「バトル画面の最上位コンテナ」
     */
    public static void playEnemyAttack(
            Node enemyNode,
            Pane battleRoot,
            Enemy.AttackType type,
            Runnable onFinished
    ) {

        // ▼ 敵ジャンプ
        TranslateTransition up = new TranslateTransition(Duration.seconds(0.2), enemyNode);
        up.setByY(-30);

        TranslateTransition down = new TranslateTransition(Duration.seconds(0.2), enemyNode);
        down.setByY(30);

        SequentialTransition jump = new SequentialTransition(up, down);

        jump.setOnFinished(e -> {

            SEPlayer.play("戦闘SE/敵攻撃.mp3");

            // ▼ 中央エフェクト（攻撃本体） → 即開始
            playCenterEffect(battleRoot, type, () -> {
            	
            });

            // ▼ ダメージリアクションだけ遅延して開始
            PauseTransition damageDelay = new PauseTransition(Duration.seconds(0.25)); // ← 調整ポイント
            damageDelay.setOnFinished(ev -> {
            	
                // ひび割れ演出
                playScreenCrackEffect(battleRoot, () -> {

                    // 最後に onFinished
                    if (onFinished != null) onFinished.run();
                });
            });

            damageDelay.play();
        });


        jump.play();
    }


    /**
     * 中央エフェクト（必要な時だけ追加 → 終わったら削除）
     */
    private static void playCenterEffect(
            Pane battleRoot,
            Enemy.AttackType type,
            Runnable onFinished
    ) {
        String path = EFFECT_MAP.get(type);
        if (path == null) {
            if (onFinished != null) onFinished.run();
            return;
        }

        Image image = new Image(BattleAnimationManager.class.getResource(path).toExternalForm());
        ImageView effectView = new ImageView(image);

        effectView.setFitWidth(700);
        effectView.setFitHeight(700);
        effectView.setPreserveRatio(true);

        Platform.runLater(() -> {
            double centerX = (battleRoot.getWidth() - effectView.getFitWidth()) / 2;
            double centerY = (battleRoot.getHeight() - effectView.getFitHeight()) / 2;
            effectView.setLayoutX(centerX);
            effectView.setLayoutY(centerY);
        });

        battleRoot.getChildren().add(effectView);
        effectView.toFront();

        // ★ 攻撃タイプごとのエフェクト時間を使う
        double duration = EFFECT_DURATION_MAP.getOrDefault(type, 0.6);

        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
        pause.setOnFinished(e -> {
            battleRoot.getChildren().remove(effectView);
            if (onFinished != null) onFinished.run();
        });

        pause.play();
    }

    
    public static void playScreenCrackEffect(
            Pane battleRoot,
            Runnable onFinished
    ) {
        // 方向別の画像を読み込む
        Image imgLT = new Image(BattleAnimationManager.class.getResource(
                "/application/images/effect/crack_lt.png").toExternalForm());
        Image imgLB = new Image(BattleAnimationManager.class.getResource(
                "/application/images/effect/crack_lb.png").toExternalForm());
        Image imgRT = new Image(BattleAnimationManager.class.getResource(
                "/application/images/effect/crack_rt.png").toExternalForm());
        Image imgRB = new Image(BattleAnimationManager.class.getResource(
                "/application/images/effect/crack_rb.png").toExternalForm());
        Image img = new Image(BattleAnimationManager.class.getResource(
                "/application/images/effect/crack.png").toExternalForm());

        // ImageView を作成
        ImageView leftTop = new ImageView(imgLT);
        ImageView leftBottom = new ImageView(imgLB);
        ImageView rightTop = new ImageView(imgRT);
        ImageView rightBottom = new ImageView(imgRB);
        ImageView center1 = new ImageView(img);
        ImageView center2 = new ImageView(img);
        ImageView center3 = new ImageView(img);
        ImageView center4 = new ImageView(img);
        ImageView center5 = new ImageView(img);
        

        ImageView[] cracks = {leftTop, leftBottom, rightTop, rightBottom, center1, center2, center3, center4, center5};

        cracks[0].setPreserveRatio(true);
        cracks[0].setFitWidth(320);
        cracks[0].setFitHeight(300);
        cracks[0].setOpacity(0);
        
        cracks[1].setPreserveRatio(true);
        cracks[1].setFitWidth(250);
        cracks[1].setFitHeight(250);
        cracks[1].setOpacity(0);
        
        cracks[2].setPreserveRatio(true);
        cracks[2].setFitWidth(250);
        cracks[2].setFitHeight(250);
        cracks[2].setOpacity(0);
        
        cracks[3].setPreserveRatio(true);
        cracks[3].setFitWidth(200);
        cracks[3].setFitHeight(200);
        cracks[3].setOpacity(0);
        //-------------------------------------
        cracks[4].setPreserveRatio(true);
        cracks[4].setFitWidth(290);
        cracks[4].setFitHeight(242);
        cracks[4].setOpacity(0);
        
        cracks[5].setPreserveRatio(true);
        cracks[5].setFitWidth(180);
        cracks[5].setFitHeight(174);
        cracks[5].setOpacity(0);
        
        cracks[6].setPreserveRatio(true);
        cracks[6].setFitWidth(131);
        cracks[6].setFitHeight(118);
        cracks[6].setOpacity(0);
        
        cracks[7].setPreserveRatio(true);
        cracks[7].setFitWidth(180);
        cracks[7].setFitHeight(174);
        cracks[7].setOpacity(0);
        
        cracks[8].setPreserveRatio(true);
        cracks[8].setFitWidth(204);
        cracks[8].setFitHeight(249);
        cracks[8].setOpacity(0);

        // battleRoot のサイズ確定後に配置
        Platform.runLater(() -> {

            leftTop.setLayoutX(318);
            leftTop.setLayoutY(138);

            leftBottom.setLayoutX(272);
            leftBottom.setLayoutY(306);

            rightTop.setLayoutX(739);
            rightTop.setLayoutY(149);

            rightBottom.setLayoutX(845);
            rightBottom.setLayoutY(315);
            
            center1.setLayoutX(-17);
            center1.setLayoutY(490);
            
            center2.setLayoutX(106);
            center2.setLayoutY(55);
            
            center3.setLayoutX(504);
            center3.setLayoutY(45);
            
            center4.setLayoutX(69);
            center4.setLayoutY(352);
            
            center5.setLayoutX(959);
            center5.setLayoutY(344);
        });

        battleRoot.getChildren().addAll(leftTop, leftBottom, rightTop, rightBottom, center1, center2, center3, center4, center5);
        for (ImageView iv : cracks) iv.toFront();

        // フェードイン・アウト
        FadeTransition[] fadeIns = new FadeTransition[9];
        FadeTransition[] fadeOuts = new FadeTransition[9];

        for (int i = 0; i < 9; i++) {
            fadeIns[i] = new FadeTransition(Duration.seconds(0.1), cracks[i]);
            fadeIns[i].setToValue(1.0);

            fadeOuts[i] = new FadeTransition(Duration.seconds(0.6), cracks[i]);
            fadeOuts[i].setToValue(0.0);
        }

        ParallelTransition fadeInAll = new ParallelTransition(fadeIns);
        ParallelTransition fadeOutAll = new ParallelTransition(fadeOuts);

        SequentialTransition seq = new SequentialTransition(fadeInAll, fadeOutAll);

        seq.setOnFinished(e -> {
            battleRoot.getChildren().removeAll(leftTop, leftBottom, rightTop, rightBottom, center1, center2, center3, center4, center5);
            if (onFinished != null) onFinished.run();
        });

        seq.play();
    }

    
    // ゲームオーバー演出
    public static void showGameOver(AnchorPane root) {
        Rectangle blackout = new Rectangle(root.getWidth(), root.getHeight(), Color.BLACK);
        blackout.setOpacity(0);
        root.getChildren().add(blackout);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), blackout);
        fade.setToValue(0.6);
        fade.play();

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");
        gameOverLabel.setOpacity(0);

        root.getChildren().add(gameOverLabel);

        // ★ 中央に配置（バインドではなく手動）
        Platform.runLater(() -> {
            gameOverLabel.setLayoutX((root.getWidth() - gameOverLabel.getWidth()) / 2);
            gameOverLabel.setLayoutY((root.getHeight() - gameOverLabel.getHeight()) / 2);
        });

        FadeTransition labelFade = new FadeTransition(Duration.seconds(1.5), gameOverLabel);
        labelFade.setToValue(1.0);
        labelFade.play();
    }
    

    // ステージクリア演出
    public static void showStageClear(AnchorPane root, Runnable onFinished) {
        Rectangle overlay = new Rectangle(root.getWidth(), root.getHeight(), Color.BLACK);
        overlay.setOpacity(0);
        root.getChildren().add(overlay);

        Label clearLabel = new Label("Stage Clear!");
        clearLabel.setTextFill(Color.YELLOW);
        clearLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");
        clearLabel.setOpacity(0);
        root.getChildren().add(clearLabel);

        // ★ 中央に配置（バインドではなく手動）
        Platform.runLater(() -> {
            clearLabel.setLayoutX((root.getWidth() - clearLabel.getWidth()) / 2);
            clearLabel.setLayoutY((root.getHeight() - clearLabel.getHeight()) / 2);
        });

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), overlay);
        fadeIn.setToValue(0.6);

        FadeTransition labelFade = new FadeTransition(Duration.seconds(1), clearLabel);
        labelFade.setToValue(1);

        ScaleTransition labelScale = new ScaleTransition(Duration.seconds(1), clearLabel);
        labelScale.setFromX(0.5);
        labelScale.setToX(1);
        labelScale.setFromY(0.5);
        labelScale.setToY(1);

        ParallelTransition appear = new ParallelTransition(labelFade, labelScale);

        SequentialTransition sequence = new SequentialTransition(fadeIn, appear);
        sequence.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });
        sequence.play();
    }
    
 // 全アニメーション状態を完全リセット
    public static void reset() {
        // 進行中アニメーションをすべて停止
        for (Animation a : ACTIVE.values()) {
            try {
                a.stop();
            } catch (Exception ex) {
                System.out.println("⚠ reset error: " + ex.getMessage());
            }
        }
        // 登録をクリア
        ACTIVE.clear();
        System.out.println("BattleAnimationManager: reset 完了（全アニメーション停止・キャッシュクリア）");
    }

}