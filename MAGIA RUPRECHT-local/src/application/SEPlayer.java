package application;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.media.AudioClip;

public class SEPlayer {

    private static final String BASE_PATH = "/resources/sounds/BGM2/";

    private static double currentVolume = 0.6;

    // ★ AudioClip キャッシュ
    private static final Map<String, AudioClip> CLIP_CACHE = new ConcurrentHashMap<>();

    static {
        try {
            String saved = ConfigManager.get("seVolume", "60%");
            currentVolume = Integer.parseInt(saved.replace("%", "")) / 100.0;
            System.out.println("[SEPlayer] 初期音量読込: " + currentVolume);
        } catch (Exception e) {
            currentVolume = 0.6;
        }
    }

    /** 効果音を再生 */
    public static void play(String filename) {
        try {
            if (filename == null || filename.isEmpty()) return;

            AudioClip clip = CLIP_CACHE.computeIfAbsent(filename, f -> {
                URL url = SEPlayer.class.getResource(BASE_PATH + f);
                if (url == null) {
                    System.err.println("SEファイルが見つかりません: " + BASE_PATH + f);
                    return null;
                }
                return new AudioClip(url.toExternalForm());
            });

            if (clip == null) return;

            clip.setVolume(currentVolume);
            clip.play();

        } catch (Exception e) {
            System.err.println("SE の再生に失敗しました: " + filename);
            e.printStackTrace();
        }
    }

    public static void setVolume(double volume) {
        double v = Math.max(0.0, Math.min(1.0, volume));
        currentVolume = v;

        // ★ 既存クリップにも反映
        CLIP_CACHE.values().forEach(c -> c.setVolume(v));

        ConfigManager.set("seVolume", (int) (v * 100) + "%");
    }

    public static double getCurrentVolume() {
        return currentVolume;
    }
}
