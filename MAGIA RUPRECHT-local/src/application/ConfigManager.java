package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static final Properties props = new Properties();
    private static final File configFile;

    static {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, "AppData/Local/.MAGIA_RUPRECHT");
        configDir.mkdirs();
        configFile = new File(configDir, "config.properties");
        load();
    }

    public static void load() {
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                System.out.println("[ConfigManager] 設定ファイルを読み込みました");
            } catch (IOException e) {
                System.out.println("[ConfigManager] 読み込み失敗: " + e.getMessage());
            }
        } else {
            System.out.println("設定ファイルがないため新規作成します");
            save();
        }
    }

    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "Game Settings");
            System.out.println("[ConfigManager] 設定ファイルを保存しました");
        } catch (IOException e) {
            System.out.println("[ConfigManager] 保存失敗: " + e.getMessage());
        }
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
