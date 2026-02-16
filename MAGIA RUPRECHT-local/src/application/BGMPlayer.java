package application;

import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class BGMPlayer {

    private static MediaPlayer player;
    private static Timeline fadeInTimeline;
    private static double currentVolume = 0.6;

    // ======================================
    // BGM 再生
    // ======================================
    public static void play(String filename) {
        System.out.println("[BGMPlayer.play] called: " + filename + " | currentVolume=" + currentVolume);

        try {
            // ★フェードイン中断（UI音量変更後は fadeInTimeline==null）
            if (fadeInTimeline != null) {
                fadeInTimeline.stop();
                fadeInTimeline = null;
            }

            URL url = BGMPlayer.class.getResource("/sounds/BGM2/" + filename);
            System.out.println("[BGMPlayer.play] resolved URL = " + url);

            if (url == null) {
                System.err.println("[BGMPlayer.play] BGMファイルが見つかりません: " + filename);
                return;
            }

            Media media = new Media(url.toExternalForm());

            // 同じBGMならボリュームだけ適用して終了
            if (player != null && player.getMedia() != null &&
                player.getMedia().getSource().equals(media.getSource())) {

                System.out.println("[BGMPlayer.play] same media, setVolume -> " + currentVolume);
                player.setVolume(currentVolume);
                dumpPlayerState("after setVolume (same media)");
                return;
            }

            // 古いプレイヤー停止
            if (player != null) {
                player.stop();
                player.dispose();
                player = null;
            }

            player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE);

            // メディア準備完了
            player.setOnReady(() -> {
                player.setVolume(currentVolume);
                System.out.println("[BGMPlayer] onReady setVolume=" + currentVolume);
            });

            player.play();
            System.out.println("[BGMPlayer.play] started play()");

            // ==============================
            // ◆フェードインは UI で音量変更されていない時のみ
            // ==============================
            if (fadeInTimeline != null) {
                System.out.println("[BGMPlayer.play] fade-in skipped because fadeInTimeline != null");
            } else {
                System.out.println("[BGMPlayer.play] fade-in disabled (UI volume change applied)");
            }

        } catch (Exception e) {
            System.err.println("BGM再生失敗: " + filename);
            e.printStackTrace();
        }
    }
    
	 // ======================================
	 // BGM 一度だけ再生（ループなし）
	 // ======================================
    public static void playOnce(String filename) {
        System.out.println("[BGMPlayer.playOnce] called: " + filename);

        try {
            if (fadeInTimeline != null) {
                fadeInTimeline.stop();
                fadeInTimeline = null;
            }

            URL url = BGMPlayer.class.getResource("/resources/sounds/BGM2/" + filename);
            if (url == null) {
                System.err.println("[BGMPlayer.playOnce] BGMファイルが見つかりません: " + filename);
                return;
            }

            Media media = new Media(url.toExternalForm());

            // ★ 古いプレイヤーを完全破棄
            if (player != null) {
                player.stop();
                player.dispose();
                player = null;
            }

            MediaPlayer p = new MediaPlayer(media); // ← ローカル変数にするのが安全
            p.setCycleCount(1);
            p.setVolume(currentVolume);

            // ★ 再生終了後に必ず破棄
            p.setOnEndOfMedia(() -> {
                p.stop();
                p.dispose();
                System.out.println("[BGMPlayer.playOnce] disposed");
            });

            p.play();
            System.out.println("[BGMPlayer.playOnce] started play()");

        } catch (Exception e) {
            System.err.println("BGM一度再生失敗: " + filename);
            e.printStackTrace();
        }
    }



    // ======================================
    // フェードイン（必要な場合のみ）
    // ======================================
    private static void startFadeIn(double from, double to, int durationMillis) {

        // ★UI で音量変更された後は呼ばれないようにした
        fadeInTimeline = new Timeline();
        int steps = 30;
        double stepTime = durationMillis / (double) steps;

        for (int i = 0; i <= steps; i++) {
            final double progress = i / (double) steps;

            KeyFrame frame = new KeyFrame(
                Duration.millis(i * stepTime),
                e -> {
                    if (player != null) {
                        double target = from + (to - from) * progress;
                        player.setVolume(target);
                    }
                }
            );
            fadeInTimeline.getKeyFrames().add(frame);
        }

        fadeInTimeline.play();
    }


    // ======================================
    // UI による音量変更
    // ======================================
    public static void setVolume(double volume) {
        System.out.println("[BGMPlayer.setVolume] volume=" + volume);

        currentVolume = volume;

        // ★フェードイン完全停止 → 次の play() で適用されないようにする
        if (fadeInTimeline != null) {
            fadeInTimeline.stop();
            fadeInTimeline = null;
        }

        if (player != null) {
            player.setVolume(volume);
            System.out.println("[BGMPlayer.setVolume] applied to player: " + player.getVolume());
        }
    }


    public static double getCurrentVolume() {
        return currentVolume;
    }


    private static void dumpPlayerState(String where) {
        if (player == null) return;

        System.out.println("[dumpPlayerState] " + where
                + " status=" + player.getStatus()
                + " volume=" + player.getVolume()
                + " mediaSource=" + player.getMedia().getSource());
    }

    public static void stop() {
        System.out.println("[BGMPlayer.stop] called");

        if (fadeInTimeline != null) {
            fadeInTimeline.stop();
            fadeInTimeline = null;
        }

        if (player != null) {
            player.stop();
            player.dispose();   // ★重要：完全破棄
            player = null;      // ★状態リセット
        }
    }

    public static void pause() {
        if (player != null) player.pause();
    }

    public static void resume() {
        if (player != null) player.play();
    }

    static {
        String saved = ConfigManager.get("bgmVolume", "60%");
        currentVolume = Integer.parseInt(saved.replace("%", "")) / 100.0;

        System.out.println("[BGMPlayer] 初期音量読込: " + currentVolume);
    }

}
