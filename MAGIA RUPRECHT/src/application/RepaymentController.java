package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RepaymentController implements Initializable {

    @FXML private Button RepaymentButton;  
    @FXML private Button BackMenu;          
    @FXML private Text Money;              
    @FXML private Text Repayment;          
    @FXML private Text BonusItem;         
    @FXML private Label messageLabel;

    private CharacterDAO characterDAO;
    private RepaymentDAO repaymentDAO;
    private RewardDAO rewardDAO;
    private InventoryDAO inventoryDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Connection conn = DBManager.getConnection();
            characterDAO = new CharacterDAO(conn);
            repaymentDAO = new RepaymentDAO(conn);
            rewardDAO = new RewardDAO(conn);
            inventoryDAO = new InventoryDAO(conn);

            updateDisplay();
        } catch (SQLException e) {
            e.printStackTrace();
            Money.setText("所持金:---");
            Repayment.setText("借金額:---");
            BonusItem.setText("次の報酬:---");
            RepaymentButton.setDisable(true);
        }
        
    }

    private void updateDisplay() {
        try {
            int money = characterDAO.getMoney();
            int remainingDebt = repaymentDAO.getRemainingDebt();
            Money.setText("所持金: " + money + "G");
            Repayment.setText("借金額: " + remainingDebt + "G");

            int paidAmount = RewardDAO.INITIAL_DEBT - remainingDebt;

            // 未取得報酬のリストを取得
            List<Integer> nextRewards = rewardDAO.getNextRewardsByPaidAmount(remainingDebt);

            if (!nextRewards.isEmpty()) {
                // 未取得報酬がある場合は最初の報酬を表示
                int rewardId = nextRewards.get(0);
                String rewardName = rewardDAO.getRewardName(rewardId);
                int threshold = rewardDAO.getRewardThreshold(rewardId);
                int remainingToNext = threshold - paidAmount;
                if (remainingToNext < 0) remainingToNext = 0;
                BonusItem.setText("次の報酬：" + rewardName + "（あと " + remainingToNext + " G返済で入手）");

            } else {
                // 未取得報酬がない場合も、次の報酬の threshold を取得して表示
                int nextThreshold = rewardDAO.getNextRewardThreshold(remainingDebt);
                if (nextThreshold > 0) {
                    int remainingToNext = nextThreshold - paidAmount;

                    // 次の報酬のIDを取得するSQL
                    String rewardName = "不明";
                    try {
                        List<Integer> allRewards = rewardDAO.getAllRewards(); // 未取得・取得済みすべて
                        for (int id : allRewards) {
                            int threshold = rewardDAO.getRewardThreshold(id);
                            if (threshold == nextThreshold) {
                                rewardName = rewardDAO.getRewardName(id);
                                break;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    BonusItem.setText("次の報酬：" + rewardName + "（あと " + remainingToNext + " G返済で入手）");
                } else {
                    // 借金完済チェック
                    if (remainingDebt <= 0) {
                        BonusItem.setText("Congratulations！ すべての返済が完了しました！");
                    } else {
                        BonusItem.setText("次の報酬：なし");
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            Money.setText("所持金:---");
            Repayment.setText("借金額:---");
            BonusItem.setText("次の報酬:---");
        }
    }

    @FXML
    private void handleRepayment(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RepaymentDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.initOwner(ownerStage);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(loader.load()));

            RepaymentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDAOs(characterDAO, repaymentDAO, inventoryDAO, rewardDAO);

            dialogStage.showAndWait();
            updateDisplay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleBack(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Menu.fxml");
    }
    
    @FXML
    void Help(MouseEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        showHelpDialog();
    }
    
    private void showHelpDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HelpDialog.fxml"));
            DialogPane pane = loader.load();

            HelpDialogController controller = loader.getController();

            // Menu画面専用のヘルプ文章
            controller.setHelpText("""
                    【メニュー画面の説明】
                    ・所持金→現在の所持金です。
                    ・借金額→現在の残り借金額です。
                    ・次の報酬→返済した額によってアイテムを貰えます（最大100万まで）。
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private ImageView characterImage;

 // 通常
    private final Image normalFace =
            new Image(getClass().getResourceAsStream("images/エリカ.png"));

    private final Image angryFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_笑顔（キレ）.png"));

    private final Image smileFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_笑顔.png"));
    
    private final Image closeeyeFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_目をつむる（通常）.png"));

    private final Image closeeyeEXFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_目をつむる（困り眉）.png"));
    
    private final Image disbeliefFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_ジト目.png"));
    
    private final Image disbeliefEXFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_ジト目（白）.png"));
    
    private final Image longbressFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_溜息.png"));
    
    private final Image smilepressiorFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/エリカ_笑顔（圧）.png"));
    
    private final TalkLine[] messagesStage1 = {
    	    new TalkLine("今日もきびきび働きなさい。", normalFace),
    	    new TalkLine("あら。返済に来たの。そう、偉いわね。", smileFace),
    	    new TalkLine("返済が遅れたらどうなるか…分かるわね？", angryFace),
    	    new TalkLine("お疲れ様。休憩も大事よ。体調には気をつけなさい。心配？そうね、返済が滞ったら大変だもの。", normalFace),
    	    new TalkLine("また無駄遣いしてないでしょうね？こら！逃げるな！", angryFace),
    	    new TalkLine("目の下、ひどいクマよ？ちゃんと寝てる？", normalFace),
    	    new TalkLine("うわ…。汚い部屋。部屋を散らかす大会があったらきっとあなたはチャンピオンね。", disbeliefEXFace),
    	    new TalkLine("はいはい、少し待ってなさい。今ごはんを用意させるから。", normalFace),
    	    new TalkLine("今鬼とか悪魔とか聞こえたけど気のせいかしら？借金、増やすわよ。", angryFace),
    	    new TalkLine("おはよう、いい天気ね…って、何だらだらしてるの。貴方にはゆっくりしている暇があるのかしら？", smilepressiorFace),
    	    new TalkLine("うちも南部の戦争の影響で財政が良くないんだから。しっかり返しなさいよ？", longbressFace)
    	};

	
    private final TalkLine[] messagesStage2 = {
    	    new TalkLine("あの子ちゃんと外で生活出来てるのかしら。野垂れ死んでないといいけど。", closeeyeEXFace),
    	    new TalkLine("今頃はリーベックかしら。あそこの魚は絶品なのよね", closeeyeFace),
    	    new TalkLine("あら、サイクロプスを倒したのね。やっぱりあの子、才能はあるようね。", normalFace),
    	    new TalkLine("あら、手紙？", normalFace),
    	    new TalkLine("さて、行ったかしら？。あそこまで追い込めば多少は眠ってた才能も目覚めるでしょ。", disbeliefEXFace)
    	};

	
    private final TalkLine[] messagesStage3 = {
    	    new TalkLine("あら、手紙？", normalFace),
    	    new TalkLine("サイクロプスに続いてクラーケンも倒したのね。順調そうで何よりね。", normalFace),
    	    new TalkLine("無事お遣いは済ませたみたいね。良かったわ。", smileFace),
    	    new TalkLine(
    	        "ヘクチッ！…誰かが噂でもしてるのかしら？だとしたらあの子ね。全く、今度はどんな悪口をいっているやら。"
    	      + "帰ってきたら借金、増やしてやろうかしら。",
    	      disbeliefEXFace
    	    )
    	};

	
	    // ステージ4
    private final TalkLine[] messagesStage4BeforeClear = {
    	    new TalkLine("おかえりなさい、帰ってきて早々悪いけど貴方には仕事を用意しているわ。ついてきてもらうわよ。", normalFace),
    	    new TalkLine("ここはあなたでも危険な場所だからね、無理は禁物よ。貴方には稼いでもらわないと困るもの。", normalFace),
    	    new TalkLine("無事帰ってきたらまたご飯を作ってあげるわ。だから無事に帰ってきなさい。", normalFace),
    	    new TalkLine(
    	        "頑張ったあなたに報酬を用意したわ。買い物も必要でしょうけど返済のことも考えなさいよ？",
    	        disbeliefEXFace
    	    )
    	};

	    
	 // ステージ4
    private final TalkLine[] messagesStage4AfterClear = {
    	    new TalkLine("おかえりなさい、無事帰ってきてくれて何よりよ。よく頑張ったわね。", normalFace),
    	    new TalkLine("さて、私からの仕事の依頼は一通り済んだわけだけど借金の返済、まだ頑張ってもらうわよ。", smileFace),
    	    new TalkLine("そう、あの子は今はギルドで受付嬢をしているのね。…行ったかしら？無事でよかったわ。マリア…", closeeyeFace),
    	    new TalkLine("彼女が姉のように慕っていた騎士の方も助かっていればよかったのですけれどね…。", closeeyeEXFace),
    	    new TalkLine("ところで彼女からなにか聞いたかしら。ふーん。嘘ね。彼女から何を聞いたのか白状…こら、逃げるな！", angryFace),
    	    new TalkLine("はやくモンスター討伐に向かいなさい。", normalFace),
    	    new TalkLine(
    	        "頑張ったあなたに報酬を用意したわ。買い物も必要でしょうけど返済のことも考えなさいよ？",
    	        disbeliefEXFace
    	    )
    	};


        private final Random random = new Random();

        private PauseTransition hideTimer;
        
        private int lastMessageIndex = -1;

        @FXML
        private void onTalkButton(ActionEvent event) {

            int currentStage = StageDAO.getCurrentUnlockStage();
            boolean stage4Cleared = StageDAO.isStage4Cleared();

            TalkLine[] targetMessages;

            if (currentStage <= 1) {
                targetMessages = messagesStage1;
            } else if (currentStage == 2) {
                targetMessages = messagesStage2;
            } else if (currentStage == 3) {
                targetMessages = messagesStage3;
            } else {
                // ステージ4：クリア前／後
                targetMessages = stage4Cleared
                        ? messagesStage4AfterClear
                        : messagesStage4BeforeClear;
            }

            int index;

            if (targetMessages.length == 1) {
                index = 0;
            } else {
                do {
                    index = random.nextInt(targetMessages.length);
                } while (index == lastMessageIndex);
            }

            lastMessageIndex = index;

            // ★ TalkLine を取得
            TalkLine line = targetMessages[index];

            // ★ セリフ表示
            messageLabel.setText(line.getText());
            messageLabel.setVisible(true);

            // ★ 表情切り替え
            characterImage.setImage(line.getFace());

            // タイマー処理
            if (hideTimer != null) {
                hideTimer.stop();
            }

            hideTimer = new PauseTransition(Duration.seconds(5));
            hideTimer.setOnFinished(e -> messageLabel.setVisible(false));
            hideTimer.play();
        }


}
