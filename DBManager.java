package application;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    public static Connection getConnection() throws SQLException {
        try {
            // ▼ 1. jar の場所（＝ app フォルダ）
            File jarFile = new File(DBManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            File jarDir = jarFile.getParentFile(); // app/

            // ▼ 2. app 内の game.db（テンプレート）
            File appDb = new File(jarDir, "game.db");
            if (!appDb.exists()) {
                throw new SQLException("app 内に game.db が見つかりません: " + appDb.getAbsolutePath());
            }

            // ▼ 3. init.sql の場所（初期化用）
            Path sqlPath = Paths.get(jarDir.getAbsolutePath(), "init.sql");
            if (!Files.exists(sqlPath)) {
                throw new SQLException("init.sql が見つかりません: " + sqlPath);
            }

            // ▼ 4. ユーザーデータ保存先
            String userHome = System.getProperty("user.home");
            File saveDir = new File(userHome, "AppData/Local/.JavaRPG");
            saveDir.mkdirs();

            File userDb = new File(saveDir, "game.db");

            // ▼ 5. 初回起動 → app からコピー
            if (!userDb.exists()) {
                Files.copy(appDb.toPath(), userDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[DBManager] 初回起動のため game.db をコピーしました");

                // ★ コピー後に初期化SQLを実行
                runInitSql(userDb, sqlPath);
            }

            // ▼ 6. DB が壊れている場合 → 再コピー＋初期化
            try (Connection testConn = DriverManager.getConnection("jdbc:sqlite:" + userDb.getAbsolutePath())) {
                // OK
            } catch (SQLException corrupt) {
                System.out.println("[DBManager] DB が壊れているため再構築します");
                Files.copy(appDb.toPath(), userDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
                runInitSql(userDb, sqlPath);
            }

            // ▼ 7. 書き込み可能チェック
            if (!userDb.canWrite()) {
                throw new SQLException("game.db に書き込みできません: " + userDb.getAbsolutePath());
            }

            // ▼ 8. 接続を返す
            return DriverManager.getConnection("jdbc:sqlite:" + userDb.getAbsolutePath());

        } catch (Exception e) {
            throw new SQLException("DB 初期化に失敗しました", e);
        }
    }

    // =========================================
    // コピー後に init.sql を実行するメソッド
    // =========================================
    private static void runInitSql(File userDb, Path sqlPath) throws Exception {

        String sql = Files.readString(sqlPath);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + userDb.getAbsolutePath());
             Statement stmt = conn.createStatement()) {

            for (String s : sql.split(";")) {
                s = s.trim();
                if (!s.isEmpty() && !s.startsWith("--")) {
                    try {
                        stmt.executeUpdate(s);
                    } catch (SQLException e) {
                        System.out.println("[DBManager] SQL 実行失敗: " + s);
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("[DBManager] init.sql による初期化が完了しました");
    }
}