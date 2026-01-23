package application;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 返済ダイアログのコントローラ
 * - ユーザーが返済額を入力
 * - 所持金・借金を更新
 * - 返済額に応じた報酬アイテムをインベントリに追加
 */
public class RepaymentDialogController {

    @FXML private TextField RepaymentAmountField;
    @FXML private Button RepayButton;
    @FXML private Button CancelButton;

    private CharacterDAO characterDAO;
    private RepaymentDAO repaymentDAO;
    private InventoryDAO inventoryDAO;
    private RewardDAO rewardDAO;

    private Stage dialogStage;

    private static final int INITIAL_DEBT = RewardDAO.INITIAL_DEBT;

    /** DAO をセット */
    public void setDAOs(CharacterDAO characterDAO, RepaymentDAO repaymentDAO,
                        InventoryDAO inventoryDAO, RewardDAO rewardDAO) {
        this.characterDAO = characterDAO;
        this.repaymentDAO = repaymentDAO;
        this.inventoryDAO = inventoryDAO;
        this.rewardDAO = rewardDAO;
    }

    /** ダイアログステージをセット */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /** 返済ボタン押下時 */
    @FXML
    public void handleRepay() {
    	SEPlayer.play("イベント/click.mp3");
        try {
            int amount = Integer.parseInt(RepaymentAmountField.getText());
            int currentMoney = characterDAO.getMoney();
            int beforeDebt = repaymentDAO.getRemainingDebt();
            int afterDebt = beforeDebt - amount;

            // 入力チェック
            if (amount <= 0) { showAlert("返済額は1円以上にしてください。"); return; }
            if (amount > currentMoney) { showAlert("所持金が足りません！"); return; }
            if (amount > beforeDebt) { showAlert("借金額を超えています！"); return; }

            // DB更新
            characterDAO.updateMoney(currentMoney - amount);
            repaymentDAO.updateRemainingDebt(afterDebt);

            // 累計返済額に達した未取得報酬を取得
            List<Integer> crossedRewards = getRewardsCrossed(afterDebt);

            // メッセージ作成
            StringBuilder message = new StringBuilder();
            message.append("返済完了！\n残り借金：").append(afterDebt).append("円\n");

            if (!crossedRewards.isEmpty()) {
                message.append("報酬を入手しました：\n");
                for (int rewardId : crossedRewards) {
                    inventoryDAO.addItem(rewardId, 1);
                    rewardDAO.markRewardGiven(rewardId);
                    message.append("・").append(rewardDAO.getRewardName(rewardId)).append("\n");
                }
            }

            showAlert(message.toString());
            dialogStage.close();

        } catch (NumberFormatException e) {
            showAlert("数字を入力してください！");
        } catch (Exception e) {
            showAlert("エラーが発生しました。");
            e.printStackTrace();
        }
    }

    /**
     * 累計返済額に達している未取得報酬だけを返す
     * rewardId は DB に登録されているものを正しい順序で指定
     * 例: ポーション(1), 魔力補填薬(2), ハイポーション(3)
     */
    private List<Integer> getRewardsCrossed(int afterDebt) throws Exception {
        int paidAmount = INITIAL_DEBT - afterDebt;

        // DB rewardId に基づく固定順序
        int[] rewardOrder = {1, 6, 2, 7, 34, 37, 9, 10, 11, 12, 13, 35, 38, 3, 8, 36, 39, 4, 5, 14, 15, 29, 30, 31, 32, 33, 18, 19, 21, 40};
        List<Integer> result = new ArrayList<>();

        for (int rewardId : rewardOrder) {
            int threshold = rewardDAO.getRewardThreshold(rewardId);
            boolean alreadyGiven = rewardDAO.isRewardAlreadyGiven(rewardId);

            System.out.println("rewardId=" + rewardId + " threshold=" + threshold +
                               " paidAmount=" + paidAmount + " alreadyGiven=" + alreadyGiven);

            if (!alreadyGiven && paidAmount >= threshold) {
                result.add(rewardId);
            }
        }
        return result;
    }


    /** キャンセルボタン押下時 */
    @FXML
    private void handleCancel() {
    	SEPlayer.play("イベント/click.mp3");
        dialogStage.close();
    }

    /** 情報ダイアログを表示 */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("返済結果");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

	public Stage getDialogStage() {
		return dialogStage;
	}
}
