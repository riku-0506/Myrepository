package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    public static Connection getConnection() throws SQLException {
    	System.out.println("DB接続先: " + new java.io.File("game.db").getAbsolutePath());
        return DriverManager.getConnection("jdbc:sqlite:game.db");
    }
    
    
    //配布用メソッド
//    public static Connection getConnection() throws SQLException {
//        try {
//            // 実行中の JAR の場所を取得
//            File jarFile = new File(DBManager.class.getProtectionDomain()
//                    .getCodeSource().getLocation().toURI());
//            File jarDir = jarFile.getParentFile();
//
//            // JAR と同じ階層の game.db を参照
//            File dbFile = new File(jarDir, "game.db");
//
//            System.out.println("DB接続先: " + dbFile.getAbsolutePath());
//
//            return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
//
//        } catch (URISyntaxException e) {
//            throw new SQLException("JAR の場所を取得できませんでした", e);
//        }
//    }

}