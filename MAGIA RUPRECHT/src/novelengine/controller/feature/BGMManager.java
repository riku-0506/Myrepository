package novelengine.controller.feature;

import java.net.URISyntaxException;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import novelengine.model.BGMData;

public class BGMManager {

    private MediaPlayer currentPlayer;
    private String currentBgmName;

    /** BGM再生（SceneData用） */
    public void playOrStopBGM(BGMData bgmData) {
        if (bgmData == null || bgmData.getFileName() == null) {
            System.out.println("[確認用ログ] データが null のためBGM停止");
            stopBGM();
            return;
        }

        String newBgmName = bgmData.getFileName();

        if (newBgmName.equals(currentBgmName)) {
            System.out.println("[確認用ログ] 同じBGMのため再生スキップ: " + newBgmName);
            return;
        }

        stopBGM();
        playBGM(bgmData);
    }

    /** BGM再生処理（URI エンコード対応・フェードイン対応） */
    private void playBGM(BGMData bgmData) {
        try {
            String resourcePath = "/novelengine/data/audio/bgm/" + bgmData.getFileName();
            var url = getClass().getResource(resourcePath);

            if (url == null) {
                System.err.println("[確認用ログ] BGMファイルが見つかりません: " + resourcePath);
                return;
            }

            String uri;
            try {
                uri = url.toURI().toASCIIString(); // ★ URI エンコード対応
            } catch (URISyntaxException e) {
                System.err.println("[確認用ログ] URI変換に失敗: " + resourcePath);
                e.printStackTrace();
                return;
            }

            System.out.println("[確認用ログ] BGM読み込み成功: " + uri);

            Media media = new Media(uri);
            currentPlayer = new MediaPlayer(media);

            currentPlayer.setCycleCount(bgmData.isLoop() ? MediaPlayer.INDEFINITE : 1);

            double targetVolume = bgmData.getVolume();
            double fadeIn = bgmData.getFadeInSeconds();

            if (fadeIn > 0) {
                currentPlayer.setVolume(0);
                currentPlayer.play();
                System.out.println("[確認用ログ] フェードイン開始: " + bgmData.getFileName());

                // UIスレッドでフェードイン処理
                new Thread(() -> {
                    try {
                        int steps = (int) (fadeIn * 20);
                        double step = targetVolume / steps;
                        for (int i = 0; i < steps; i++) {
                            Thread.sleep(50);
                            Platform.runLater(() -> {
                                if (currentPlayer != null) {
                                    double newVol = currentPlayer.getVolume() + step;
                                    currentPlayer.setVolume(Math.min(newVol, targetVolume));
                                }
                            });
                        }
                        System.out.println("[確認用ログ] フェードイン完了: " + bgmData.getFileName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } else {
                currentPlayer.setVolume(targetVolume);
                currentPlayer.play();
                System.out.println("[確認用ログ] BGM再生開始: " + bgmData.getFileName());
            }

            currentBgmName = bgmData.getFileName();
        } catch (Exception e) {
            System.err.println("[確認用ログ] BGM再生中に例外発生: " + bgmData.getFileName());
            e.printStackTrace();
        }
    }

    /** BGM停止 */
    public void stopBGM() {
        if (currentPlayer != null) {
            System.out.println("[確認用ログ] BGM停止: " + currentBgmName);
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
            currentBgmName = null;
        } else {
            System.out.println("[確認用ログ] 停止対象のBGMなし");
        }
    }

    /** features["bgm"]対応 */
    public void handleBGM(Map<String, Object> bgmFeature) {
        if (bgmFeature == null) return;

        String action = (String) bgmFeature.getOrDefault("action", "play");

        switch (action) {
            case "stop":
                stopBGM();
                break;
            case "play":
            default:
                BGMData data = new BGMData();
                data.setFileName((String) bgmFeature.get("file"));
                if (bgmFeature.containsKey("fadeIn"))
                    data.setFadeInSeconds(((Number) bgmFeature.get("fadeIn")).doubleValue());
                if (bgmFeature.containsKey("volume"))
                    data.setVolume(((Number) bgmFeature.get("volume")).doubleValue());
                if (bgmFeature.containsKey("loop"))
                    data.setLoop((Boolean) bgmFeature.get("loop"));

                playOrStopBGM(data);
                break;
        }
    }

    /** シナリオ終了時のクリア処理 */
    public void clearBGM() {
        stopBGM();
    }
}
