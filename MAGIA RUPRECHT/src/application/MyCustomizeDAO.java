package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyCustomizeDAO {

    // 🧱 マイセットデータを保持する内部クラス
    public static class MyCustomize {
        public String setName; // セット名（ユーザーが入力）
        public int my_magic1, my_magic2, my_magic3, my_magic4, my_magic5, my_magic6; // 魔法ID（0〜7）

        // 🏗 コンストラクタ：セット名と6つの魔法IDを受け取って初期化
        public MyCustomize(String setName, int m1, int m2, int m3, int m4, int m5, int m6) {
            this.setName = setName;
            this.my_magic1 = m1;
            this.my_magic2 = m2;
            this.my_magic3 = m3;
            this.my_magic4 = m4;
            this.my_magic5 = m5;
            this.my_magic6 = m6;
        }

        // 🔍 ゲッター：setNameを取得（コントローラー側で使用）
        public String getSetName() {
            return setName;
        }
    }

    // 📥 指定IDのマイセットをDBから取得
    public static MyCustomize get(int id) {
        String sql = "SELECT set_name, my_magic1, my_magic2, my_magic3, my_magic4, my_magic5, my_magic6 FROM mycustomize WHERE My_customize = ?";

        try (
            Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id); // IDをバインド

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // DBから取得した値をMyCustomizeオブジェクトに変換して返す
                    return new MyCustomize(
                        rs.getString("set_name"),
                        rs.getInt("my_magic1"),
                        rs.getInt("my_magic2"),
                        rs.getInt("my_magic3"),
                        rs.getInt("my_magic4"),
                        rs.getInt("my_magic5"),
                        rs.getInt("my_magic6")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // エラー表示（開発用）
        }

        return null; // データが存在しない場合
    }

    // 💾 指定IDのマイセットをDBに保存（存在すればUPDATE、なければINSERT）
    public static void save(int id, MyCustomize customize) {
        // データの存在確認用SQL
        String checkSql = "SELECT COUNT(*) FROM mycustomize WHERE My_customize = ?";

        // 更新用SQL（既存行がある場合）
        String updateSql = "UPDATE mycustomize SET set_name=?, my_magic1=?, my_magic2=?, my_magic3=?, my_magic4=?, my_magic5=?, my_magic6=? WHERE My_customize = ?";

        // 挿入用SQL（新規行の場合）
        String insertSql = "INSERT INTO mycustomize (My_customize, set_name, my_magic1, my_magic2, my_magic3, my_magic4, my_magic5, my_magic6) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection()) {
            boolean exists = false;

            // 🔍 既存データの有無をチェック
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next() && rs.getInt(1) > 0;
                }
            }

            // 🛠 適切なSQLを選択して実行
            String sql = exists ? updateSql : insertSql;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (exists) {
                    // 更新処理
                    stmt.setString(1, customize.getSetName());
                    stmt.setInt(2, customize.my_magic1);
                    stmt.setInt(3, customize.my_magic2);
                    stmt.setInt(4, customize.my_magic3);
                    stmt.setInt(5, customize.my_magic4);
                    stmt.setInt(6, customize.my_magic5);
                    stmt.setInt(7, customize.my_magic6);
                    stmt.setInt(8, id);
                } else {
                    // 挿入処理
                    stmt.setInt(1, id);
                    stmt.setString(2, customize.getSetName());
                    stmt.setInt(3, customize.my_magic1);
                    stmt.setInt(4, customize.my_magic2);
                    stmt.setInt(5, customize.my_magic3);
                    stmt.setInt(6, customize.my_magic4);
                    stmt.setInt(7, customize.my_magic5);
                    stmt.setInt(8, customize.my_magic6);
                }

                stmt.executeUpdate(); // SQL実行
            }

        } catch (SQLException e) {
            e.printStackTrace(); // エラー表示（開発用）
        }
    }

    // 📋 customizesテーブルから魔法名一覧を取得（ChoiceBox表示用）
    public static List<String> getMagicNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM customizes";

        try (
            Connection conn = DBManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                names.add(rs.getString("name")); // 魔法名をリストに追加
            }
        } catch (SQLException e) {
            e.printStackTrace(); // エラー表示（開発用）
        }

        return names;
    }
}