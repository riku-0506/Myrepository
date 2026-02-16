package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GuildController implements Initializable {

    @FXML
    private Text Money;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            Connection conn = DBManager.getConnection();
            CharacterDAO dao = new CharacterDAO(conn);

            int money = dao.getMoney();
            Money.setText(String.valueOf(money) + "G");

        } catch (SQLException e) {
            e.printStackTrace();
            Money.setText("ERR");
        }
    }

    @FXML
    void Undo(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Menu.fxml");
    }

    @FXML
    void goToBuyItem(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("BuyItem.fxml");
    }

    @FXML
    void goToSellItem(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("SellItem.fxml");
    }

    @FXML
    void goToQuest(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("Quest.fxml");
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
                    ・所持金が右上に表示されます。
                    ・購入→アイテムや魔法の購入画面に移ります。
                    ・売却→倒したモンスターの素材やアイテムを売却することが出来ます。
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private Label messageLabel;
//
//    "こんにちは！ギルドに何か御用でしょうか！",
//    "食事のお誘いですか！？。行きましょう！冒険者の女の子は珍しいので嬉しいです！",
//    "カタリナ…待って…ハッ！？あ～…寝ちゃってましたか？ごめんなさい！ギルド長には内緒にしてください！"
//    + "怒ると怖いんですよ！あの人！え？後ろ？",
//    "今日はいい天気ですね～。絶好の冒険日和ですよ！",
//    "準備はしっかり整えてくださいね！冒険は何が起こるかわかりませんから！ポーションや魔力補填薬は"
//    + "ちゃんと持ちましたか？持ってなかったらぜひ買っていってくださいね！",
//    "図鑑は活用していますか？図鑑にはモンスターの弱点などが書いてあるので敵が強いと感じたら"
//    + "読んでみると倒せるようになるかもしれませんよ～。",
//    "えっ！貴方が噂のエリカちゃんの幼馴染さんですか！小さい頃にエリカちゃんからいつも聞いていたんです"
//    + "「あいつは私がいないとどうしようもないんだから！」って。でもいつもとびっきりの笑顔で嬉しそうでした！"
//    + "あ、エリカちゃんには内緒ですよ…？",
//    "サイクロプスって寒いところにいるのに氷が苦手って変ですよね～。そういうモンスター他にもいるのでしょうか？"
//};
    
    // ステージ1用
    private final String[] messagesStage1 = {
        "こんにちは！ギルドに何か御用でしょうか！",
        "今日はいい天気ですね～。絶好の冒険日和ですよ！",
        "準備はしっかり整えてくださいね！冒険は何が起こるかわかりませんから！ポーションや魔力補填薬は"
        + "ちゃんと持ちましたか？持ってなかったらぜひ買っていってくださいね！",
        "サイクロプスって寒いところにいるのに氷に弱いんですよ！不思議ですね！"
    };

    // ステージ2用
    private final String[] messagesStage2 = {
        "最近少し危険な依頼が増えてきましたね。",
        "海の方で物騒な話を聞きます。気を付けてくださいね。",
        "図鑑は活用していますか？図鑑にはモンスターの弱点などが書いてあるので敵が強いと感じたら"
        + "読んでみると倒せるようになるかもしれませんよ～。",
        "こんにちは！ギルドに何か御用でしょうか！",
        "今日はいい天気ですね～。絶好の冒険日和ですよ！",
        "準備はしっかり整えてくださいね！冒険は何が起こるかわかりませんから！ポーションや魔力補填薬は"
        + "ちゃんと持ちましたか？持ってなかったらぜひ買っていってくださいね！",
        "食事のお誘いですか！？。行きましょう！冒険者の女の子は珍しいので嬉しいです！"
    };

    // ステージ3
    private final String[] messagesStage3 = {
    		"こんにちは！ギルドに何か御用でしょうか！",
            "食事のお誘いですか！？。行きましょう！冒険者の女の子は珍しいので嬉しいです！",
            "カタリナ…待って…ハッ！？あ～…寝ちゃってましたか？ごめんなさい！ギルド長には内緒にしてください！"
            + "怒ると怖いんですよ！あの人！え？後ろ？",
            "今日はいい天気ですね～。絶好の冒険日和ですよ！",
            "準備はしっかり整えてくださいね！冒険は何が起こるかわかりませんから！ポーションや魔力補填薬は"
            + "ちゃんと持ちましたか？持ってなかったらぜひ買っていってくださいね！",
            "図鑑は活用していますか？図鑑にはモンスターの弱点などが書いてあるので敵が強いと感じたら"
            + "読んでみると倒せるようになるかもしれませんよ～。",
            "えっ！貴方が噂のエリカちゃんの幼馴染さんですか！小さい頃にエリカちゃんからいつも聞いていたんです"
            + "「あいつは私がいないとどうしようもないんだから！」って。でもいつもとびっきりの笑顔で嬉しそうでした！"
            + "あ、エリカちゃんには内緒ですよ…？"
    };
    
    // ステージ4クリア前
    private final String[] messagesStage4BeforeClear  = {
    		"こんにちは！ギルドに何か御用でしょうか！",
            "食事のお誘いですか！？。行きましょう！冒険者の女の子は珍しいので嬉しいです！",
            "カタリナ…待って…ハッ！？あ～…寝ちゃってましたか？ごめんなさい！ギルド長には内緒にしてください！"
            + "怒ると怖いんですよ！あの人！え？後ろ？",
            "今日はいい天気ですね～。絶好の冒険日和ですよ！",
            "準備はしっかり整えてくださいね！冒険は何が起こるかわかりませんから！ポーションや魔力補填薬は"
            + "ちゃんと持ちましたか？持ってなかったらぜひ買っていってくださいね！",
            "図鑑は活用していますか？図鑑にはモンスターの弱点などが書いてあるので敵が強いと感じたら"
            + "読んでみると倒せるようになるかもしれませんよ～。",
            "えっ！貴方が噂のエリカちゃんの幼馴染さんですか！小さい頃にエリカちゃんからいつも聞いていたんです"
            + "「あいつは私がいないとどうしようもないんだから！」って。でもいつもとびっきりの笑顔で嬉しそうでした！"
            + "あ、エリカちゃんには内緒ですよ…？"
    };

    // ステージ4クリア後
    private final String[] messagesStage4AfterClear  = {
    		"こんにちは！ギルドに何か御用でしょうか！",
            "食事のお誘いですか！？。行きましょう！冒険者の女の子は珍しいので嬉しいです！",
            "デュラハンの討伐ありがとうございました。きっと彼女も…今は…安らかに…ごめんなさい！湿っぽくなっちゃいましたね！"
            + "知り合いなんです。幼いころの…。さ！改めまして！冒険者ギルドへようこそ！",
            "今日はいい天気ですね～。絶好の冒険日和ですよ！",
            "準備はしっかり整えてくださいね！冒険は何が起こるかわかりませんから！ポーションや魔力補填薬は"
            + "ちゃんと持ちましたか？持ってなかったらぜひ買っていってくださいね！",
            "幼馴染…いいですね…大切にしないとエリカちゃん、泣いちゃいますよ？"
            + "フフッ♪…私にはもう居ませんから…いえ！なんでもないですよ♪",
            "ここだけの話ですが、近頃国の西側が騒がしいらしいですよ、新しいダンジョンでも生まれたのでしょうか？",
            "新しいダンジョン…聞いただけでワクワクしますね！でもどうやって探したらいいんでしょう？"
    };
    
    private final Random random = new Random();

    private PauseTransition hideTimer;
    
    private int lastMessageIndex = -1;

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

        int index;

        if (targetMessages.length == 1) {
            index = 0;
        } else {
            do {
                index = random.nextInt(targetMessages.length);
            } while (index == lastMessageIndex);
        }

        lastMessageIndex = index;

        String message = targetMessages[index];

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
