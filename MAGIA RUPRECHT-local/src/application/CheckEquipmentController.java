package application;

import java.util.Random;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CheckEquipmentController {
	public String stageFxml;
	
	@FXML
    public void initialize() {

        stageFxml = StageSelectionController.getSelectedStage();
        System.out.println("前画面で選ばれたFXML: " + stageFxml);

        // 必要なら、ラベルに表示したり、次の画面遷移に使ったり
    }
	
	@FXML
    void ChengeMyset(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
		SceneManager.changeScene("MySet.fxml");
    }
	

	
	public void Deployment(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
		PauseTransition pause = new PauseTransition(Duration.seconds(0.2));
        pause.setOnFinished(e -> SceneManager.changeScene(stageFxml));
        SceneManager.clearHistory();
        pause.play();
	}
		
	@FXML
	public void Undo(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
		SceneManager.changeScene("StageSelection.fxml");
	}
	
	@FXML
    private Label messageLabel;
	
	// ステージ1用
    private final String[] messagesStage1 = {
	    "うわぁ、ホントにいかないとですか…やだなぁ",
	    "何があるか分からないししっかり準備を整えないと、といってもお金ないんですよね…",
	    "うぅ、エリカのオニめぇ…",
	    "装備確認よし、アイテム確認よし。さて、いきますか"
    };

    // ステージ2用
    private final String[] messagesStage2 = {
        "あれ、ここのモンスターの弱点は雷でいいんだっけ？",
        "ポーションや魔力補填薬程度なら買えるようになりましたがまだまだお財布は寂しいですね…",
        "新しい魔法、早く試したいですね…フフフ…"
    };

    // ステージ3
    private final String[] messagesStage3 = {
    		"お遣いだけって聞いていたのに…エリカは噓つきです。嘘つきエリカです。",
    		"ポーションだけじゃ回復が間に合わなくなってきました…。お高くてもハイポーションとか買うべきでしょうか。",
    		"MPの回復薬なら買うのためらわないんですけどねぇ。試したい魔法はたくさんありますし。",
    		"ウサギちゃん、今日は会えるでしょうか？お肉食べたいです。"
    };

    // ステージ4クリア前
    private final String[] messagesStage4BeforeClear = {
    		"戦場かぁ…流石に緊張しちゃいますね…。",
            "私は研究がしたいだけなのに何で戦場にいるのでしょうか…？",
            "今さら装備ミスなんてやりませんとも！えぇ！でも念のために確認しますね？",
            "戦場に出るのに軍用回復薬とかあれ、貰えないのひどくないです？",
    };
    
    // ステージ4クリア後
    private final String[] messagesStage4AfterClear  = {
    		"はぁ、今日もモンスター討伐ですか…。研究したいなあ…。",
            "今日はとっておきの魔法を作ってきましたからね！早く試したいです！",
            "そういえばエリカはモンスター討伐とかできるくらい強いのでしょうか？",
            "装備確認はいつでもしなきゃですね。慢心ダメ。ゼッタイ。"
    };

    private final Random random = new Random();

    private PauseTransition hideTimer;

    @FXML
    private void onTalkButton(ActionEvent event) {

        int currentStage = StageDAO.getCurrentUnlockStage();
        boolean stage4Cleared = StageDAO.isStage4Cleared();

        String[] targetMessages;

        if (currentStage <= 1) {
            targetMessages = messagesStage1;
        } else if (currentStage == 2) {
            targetMessages = messagesStage2;
        } else if (currentStage == 3) {
            targetMessages = messagesStage3;
        } else {
            // ★ ステージ4：クリア前／後で分岐
            if (stage4Cleared) {
                targetMessages = messagesStage4AfterClear;
            } else {
                targetMessages = messagesStage4BeforeClear;
            }
        }

        String message = targetMessages[random.nextInt(targetMessages.length)];

        messageLabel.setText(message);
        messageLabel.setVisible(true);

        if (hideTimer != null) {
            hideTimer.stop();
        }

        hideTimer = new PauseTransition(Duration.seconds(5));
        hideTimer.setOnFinished(e -> messageLabel.setVisible(false));
        hideTimer.play();
    }
}
