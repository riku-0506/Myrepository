package novelengine.controller.feature;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import novelengine.model.Dialogue;

public class TextManager {

    private final AnchorPane textLayer = new AnchorPane();   // AnchorPane に変更
    private final ImageView textBoxView;         // テキストボックス背景
    private final Label nameLabel = new Label(); // 話者名
    private final Label textLabel = new Label(); // 本文

    private Timeline textTimeline;
    private double textSpeed = 30; // 1文字あたりのミリ秒

    public TextManager() {
        // --- テキストボックス画像 ---
        String resourcePath = "/novelengine/data/images/ui/ele2.png";
        var url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("UI画像が見つかりません: " + resourcePath);
            textBoxView = new ImageView();
        } else {
            textBoxView = new ImageView(new Image(url.toExternalForm()));
            System.out.println("テキストボックス画像を設定しました: " + resourcePath);
        }

        // AnchorPane の場合、左右下固定にする例
        AnchorPane.setLeftAnchor(textBoxView, 0.0);
        AnchorPane.setRightAnchor(textBoxView, 0.0);
        AnchorPane.setBottomAnchor(textBoxView, 0.0);

        // --- フォント読み込み ---
        Font nameFont;
        Font textFont;
        try {
            // 後でゲームプロジェクト側のフォントファイルパスに置き換え
            String fontPath = "fonts/chigfont/ちはやゴシック.ttf";
            nameFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 40);
            textFont = Font.loadFont(getClass().getResourceAsStream(fontPath), 35);

            if (nameFont == null || textFont == null) {
                System.err.println("フォントファイルが見つかりません。デフォルトフォントを使用します: " + fontPath);
                nameFont = Font.font(40);
                textFont = Font.font(35);
            } else {
                System.out.println("フォントを読み込みました: " + fontPath);
            }
        } catch (Exception e) {
            System.err.println("フォント読み込み中に例外発生。デフォルトフォントを使用します: " + e.getMessage());
            nameFont = Font.font(40);
            textFont = Font.font(35);
        }

        // --- 名前ラベル ---
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(nameFont);
        nameLabel.setStyle("-fx-font-weight: bold;");
        AnchorPane.setLeftAnchor(nameLabel, 160.0);
        AnchorPane.setBottomAnchor(nameLabel, 188.0);

        // --- 本文ラベル ---
        textLabel.setTextFill(Color.WHITE);
        textLabel.setFont(textFont);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(1000);

        // 高さ固定・上揃えに設定
        textLabel.setPrefHeight(120);         // ラベル高さを固定
        textLabel.setAlignment(Pos.TOP_LEFT); // 上から描画

        AnchorPane.setLeftAnchor(textLabel, 150.0);
        AnchorPane.setBottomAnchor(textLabel, 50.0);

        // --- コンテナ構築 ---
        textLayer.getChildren().addAll(textBoxView, nameLabel, textLabel);
        textLayer.setMouseTransparent(true);
    }

    /** Scene に追加するためのノードを返す */
    public AnchorPane getNode() {
        return textLayer;
    }

    /** セリフを文字送りアニメーションで表示 */
    public void playDialogueWithAnimation(Dialogue dialogue, Runnable onFinished) {
        if (dialogue == null) return;

        nameLabel.setText(dialogue.getSpeaker() != null ? dialogue.getSpeaker() : "");
        textLabel.setText("");

        if (textTimeline != null) textTimeline.stop();

        final String fullText = (dialogue.getText() == null) ? "" : dialogue.getText();
        final int[] index = {0};

        textTimeline = new Timeline(
            new KeyFrame(Duration.millis(textSpeed), e -> {
                if (index[0] < fullText.length()) {
                    textLabel.setText(fullText.substring(0, index[0] + 1));
                    index[0]++;
                } else {
                    textTimeline.stop();
                    if (onFinished != null) onFinished.run();
                }
            })
        );

        textTimeline.setCycleCount(Timeline.INDEFINITE);
        textTimeline.play();
    }

    /** skipText = true の場合 */
    public void playSkippedDialogue(Dialogue dialogue, Runnable onFinished) {
        if (dialogue == null) return;

        nameLabel.setText(dialogue.getSpeaker() != null ? dialogue.getSpeaker() : "");
        textLabel.setText(dialogue.getText() != null ? dialogue.getText() : "");

        if (textTimeline != null) {
            textTimeline.stop();
            textTimeline = null;
        }

        if (onFinished != null) onFinished.run();
    }

    public void setTextSpeed(double millisPerChar) {
        this.textSpeed = millisPerChar;
    }

    /** 終了処理 */
    public void clearText() {
        if (textTimeline != null) {
            textTimeline.stop();
            textTimeline = null;
        }
        nameLabel.setText("");
        textLabel.setText("");
    }
}
