package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class QuestController implements Initializable {

    @FXML
    private TextArea QuestDescription;   // クエスト説明文表示欄

    @FXML
    private VBox questListBox;           // クエスト一覧（ボタンを並べる VBox）
    
    @FXML private Label Money;
    
    // クエスト名 → QuestData のマップ
    private final Map<String, QuestData> questMap = new LinkedHashMap<>();

    // 現在選択中のクエスト
    private QuestData selectedQuest = null;

    // Singleton（他クラスから達成チェックを呼べるようにする）
    public static QuestController instance;

    public QuestController() {
    }
    
    @FXML
    private void handleDeliverQuest(ActionEvent event) {

        if (selectedQuest == null) {
            QuestDescription.setText("クエストが選択されていません。");
            return;
        }

        // DeliverCondition かチェック
        if (!(selectedQuest.getCondition() instanceof DeliverCondition)) {
            QuestDescription.setText("このクエストは納品型ではありません。");
            return;
        }

        // 本体処理へ
        handleDeliverQuest(selectedQuest);
    }
    
    //所持金表示
    private void updateMoneyDisplay() {
        int money = PlayerStatus.getMoney();
        Money.setText(money + "G");
    }

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = this;
        QuestDescription.setText("クエストを選択してください");

        // ① クエスト一覧を作成
        questMap.clear();
        questMap.putAll(QuestList.getAllQuests());

        // ② セーブデータを読み込み（accepted / completed を反映）
        QuestDBManager.load(questMap);

        // ③ UI を構築
        refreshQuestButtons();
        
        //所持金表示
        updateMoneyDisplay();

        // デバッグ出力
        System.out.println("[QuestController] initialize complete. Quests loaded: " + questMap.size());
    }

    // =====================================================
    // クエスト一覧ボタンを再作成して表示更新
    // =====================================================
    private void refreshQuestButtons() {
        questListBox.getChildren().clear();
        int clearedStage = StageDAO.getCurrentUnlockStage();

        for (QuestData q : questMap.values()) {

         // ★ ステージ未解放ならスキップ
            if (q.getUnlockStage() > clearedStage) {
                continue;
            }
            
            // ボタンの表示名
            String label = q.getName();
            
            if (q.isCompleted())        label += "（達成）";
            else if (q.isAccepted())    label += "（受注中）";

            Button btn = new Button(label);

         // ===============================
         // サイズを完全固定
         // ===============================
         btn.setPrefWidth(360);
         btn.setPrefHeight(60);

         btn.setMinWidth(360);
         btn.setMaxWidth(360);
         btn.setMinHeight(60);
         btn.setMaxHeight(60);

         // 文字サイズ少し大きめに（任意）
         btn.setStyle("-fx-font-size: 18px;");

         // CSSのscaleをリセット（ホバーで大きくなるのを防ぐ）
         btn.setStyle(
             "-fx-font-size: 18px;" +
             "-fx-scale-x: 1;" +
             "-fx-scale-y: 1;"
         );

         // クエストボタン用のCSSクラスを使うなら追加
         btn.getStyleClass().add("chihaya");
         btn.getStyleClass().add("Quest");


            // ボタン押下時の処理
            btn.setOnAction(e -> {
                selectedQuest = q;

                String status =
                        q.isCompleted() ? "【達成済み】" :
                        q.isAccepted()  ? "【受注中】" :
                                          "【未受注】";

                QuestDescription.setText(
                        "【" + q.getName() + "】\n" +
                        status + "\n\n" +
                        q.getDescription() + "\n\n" +
                        "― 条件 ―\n" +
                        q.getCondition().getConditionText() + "\n\n" +
                        "― 報酬 ―\n" +
                        "ゴールド：" + q.getRewardGold() + "G"
                );

                // デバッグ出力
                System.out.println("[QuestController] Selected quest: " + q.getName() + ", status: " + status);
            });

            questListBox.getChildren().add(btn);
        }
    }
    
 // =====================================================
 // 「受注」ボタン
 // =====================================================
    @FXML
    private void handleAcceptQuest(ActionEvent event) {

        if (selectedQuest == null) {
            QuestDescription.setText("クエストが選択されていません。");
            return;
        }

        // ★ 達成済みは受注不可
        if (selectedQuest.isCompleted()) {
            QuestDescription.setText("このクエストはすでに達成済みです。");
            return;
        }

        // すでに受注済み
        if (selectedQuest.isAccepted()) {
            QuestDescription.setText("このクエストはすでに受注しています。");
            return;
        }

        // ★ 受注上限チェック（最大4つ）
        long activeQuests = questMap.values().stream()
                .filter(QuestData::isAccepted)
                .count();

        if (activeQuests >= 4) {
            QuestDescription.setText("同時に受注できるクエストは最大4つです。");
            return;
        }

        // 受注処理
        selectedQuest.setAccepted(true);

        QuestDescription.setText(
                "クエストを受注しました！\n\n" +
                selectedQuest.getName()
        );

        // DBへ反映
        QuestDBManager.save(questMap);

        // UI更新
        refreshQuestButtons();
    }

    
    // =====================================================
    // 「破棄」ボタン
    // =====================================================
    @FXML
    private void handleCancelQuest(ActionEvent event) {

        if (selectedQuest == null) {
            QuestDescription.setText("クエストが選択されていません。");
            return;
        }

        // 受注中でない場合はキャンセル不可
        if (!selectedQuest.isAccepted()) {
            QuestDescription.setText("このクエストは受注していません。");
            return;
        }

        // 破棄処理
        selectedQuest.setAccepted(false);

        // DeliverCondition の納品状態もリセット
        if (selectedQuest.getCondition() instanceof DeliverCondition) {
            DeliverCondition dc = (DeliverCondition) selectedQuest.getCondition();
            dc.setDelivered(false);
            dc.setDeliveredCount(0);
        }

        QuestDescription.setText("クエストを破棄しました：\n" + selectedQuest.getName());

        // DBに反映
        QuestDBManager.save(questMap);

        // UI更新
        refreshQuestButtons();
    }


    // =====================================================
    // 「納品」ボタン
    // =====================================================
    /**
     * 納品ボタン処理（2回目以降は納品不可）
     */
    private void handleDeliverQuest(QuestData quest) {

    	// ★ クエストがすでに達成済みのときは納品できない
    	if (quest.isCompleted()) {
    	    QuestDescription.setText("このクエストはすでに達成済みです。");
    	    return;
    	}
    	
    	// --- クエスト未受注 ---
        if (!quest.isAccepted()) {
            QuestDescription.setText("このクエストは受注していません。");
            return;
        }

        // --- すでに納品済みなら再納品不可 ---
        DeliverCondition dc = (DeliverCondition) quest.getCondition();
        if (dc.isDelivered()) {
            QuestDescription.setText("このクエストはすでに納品済みです。");
            return;
        }

        int itemId = dc.getItemId();
        int require = dc.getRequireCount();

        try {
            // --- InventoryDAO 生成 ---
            InventoryDAO inventoryDAO = new InventoryDAO(DBManager.getConnection());

            // --- 所持数チェック ---
            int have = inventoryDAO.getItemCount(itemId);
            if (have < require) {
                QuestDescription.setText("必要なアイテムが足りません。\n(" + have + "/" + require + ")");
                return;
            }

            // --- 納品（アイテム減少） ---
            inventoryDAO.updateItemCount(itemId, have - require);

            // --- 納品状態を反映 ---
            dc.setDelivered(true);
            dc.setDeliveredCount(require);

            QuestDescription.setText("アイテムを納品しました！");

            // --- クエスト達成判定 ---
            checkCompletion();  // QuestController のメソッドを呼ぶ

            // --- DBへ保存 ---
            QuestDBManager.save(questMap);

            // --- UI更新 ---
            refreshQuestButtons();

        } catch (Exception e) {
            e.printStackTrace();
            QuestDescription.setText("納品処理中にエラーが発生しました。");
        }
    }

    // =====================================================
    // 全クエストの達成チェック（内部用）
    // =====================================================
 // クエスト達成チェック & 報酬付与
    private void checkCompletion() {

        for (QuestData quest : questMap.values()) {

            if (!quest.isAccepted()) continue;
            if (quest.isCompleted()) continue;

            if (quest.getCondition().isSatisfied()) {

                quest.setCompleted(true);

                // ── 達成時に自動で受注解除 ──
                quest.setAccepted(false);

                // ── 報酬付与 ──
                int reward = quest.getRewardGold();
                PlayerStatus.addMoney(reward); // Moneyカラムに加算
                updateMoneyDisplay();
                System.out.println("[QuestController] Quest completed: " + quest.getName()
                        + ", reward: " + reward + "G");

                // メッセージ表示
                QuestDescription.setText(
                        "クエスト達成！\n\n" +
                        quest.getName() + "\n" +
                        "報酬：" + reward + "G を獲得！\n" +
                        "所持金：" + PlayerStatus.getMoney() + "G"
                );
            }

        }

        refreshQuestButtons();
        QuestDBManager.save(questMap);
    }

    public void updateQuestProgress() {
        checkCompletion();
        refreshQuestButtons();

        if (selectedQuest != null) {
            QuestData q = selectedQuest;
            String status =
                q.isCompleted() ? "【達成済み】" :
                q.isAccepted()  ? "【受注中】" :
                                  "【未受注】";

            QuestDescription.setText(
                    "【" + q.getName() + "】\n" +
                    status + "\n\n" +
                    q.getDescription() + "\n\n" +
                    "― 条件 ―\n" +
                    q.getCondition().getConditionText() + "\n\n" +
                    "― 報酬 ―\n" +
                    "ゴールド：" + q.getRewardGold() + "G"
            );
        }
    }
    
    public static QuestController getInstance() {
        return instance;
    }

    // =====================================================
    // シーン遷移ボタン
    // =====================================================
    @FXML
    private void handleGoToSell(ActionEvent event) { SceneManager.changeScene("SellItem.fxml"); }

    @FXML
    private void handleGoToGuild(ActionEvent event) { SceneManager.changeScene("Guild.fxml"); }

    @FXML
    private void handleGoToBuy(ActionEvent event) { SceneManager.changeScene("BuyItem.fxml"); }

    // =====================================================
    // ゴールド管理（外部に移せる）
    // =====================================================
	 // ───────────────
	 // QuestController 内部クラス
	 // ───────────────
	 public static class PlayerStatus {
	
	     // ── デバッグ用ログ ──
	     private static void debug(String msg) { System.out.println("[PlayerStatus] " + msg); }
	
	     // ── 所持金を DB から取得 ──
	     public static int getMoney() {
	         int money = 0;
	         try (Connection conn = DBManager.getConnection();
	              PreparedStatement stmt = conn.prepareStatement("SELECT Money FROM character LIMIT 1");
	              ResultSet rs = stmt.executeQuery()) {
	
	             if (rs.next()) {
	                 money = rs.getInt("Money");
	             }
	
	         } catch (SQLException e) {
	             e.printStackTrace();
	         }
	         debug("getMoney = " + money);
	         return money;
	     }
	
	     // ── 所持金に加算して DB 更新 ──
	     public static void addMoney(int amount) {
	         int current = getMoney();
	         int newMoney = current + amount;
	
	         try (Connection conn = DBManager.getConnection();
	              PreparedStatement stmt = conn.prepareStatement("UPDATE character SET Money=?")) {
	
	             stmt.setInt(1, newMoney);
	             int rows = stmt.executeUpdate();
	             debug("addMoney: +" + amount + " -> total=" + newMoney + ", rows=" + rows);
	
	         } catch (SQLException e) {
	             e.printStackTrace();
	         }
	     }
	 }
	 
	 public Map<String, QuestData> getQuestMap() {
		    return questMap;
		}


}