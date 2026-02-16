package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuController implements Initializable {
	
	int clearStageId = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // メニュー画面用BGMを再生
    	BGMPlayer.play("拠点/maou_bgm_fantasy10.mp3");
    	applyLock();
    	clearStageId = ResultController.getClearStageId();
    	if(clearStageId > 0) {
    		System.out.println("初クリアのステージID" + clearStageId);
    		try {
    	        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuDialog.fxml"));
    	        DialogPane dialogPane = loader.load();

    	        Dialog<ButtonType> dialog = new Dialog<>();
    	        dialog.setDialogPane(dialogPane);
    	        dialog.setTitle("ステージ初クリア");

    	        // OK ボタンだけにしたい場合（必要なら）
//    	        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

    	        dialog.showAndWait();

    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        clearStageId = 0;
    	    }
    	}
    	
    }

    @FXML
    private Button GoToRepaymentButton;
    
    @FXML
    private Button Illustrated;
    
    @FXML
    private ImageView LockImage1;

    @FXML
    private ImageView LockImage2;
    
    @FXML
    private void handleGoToCustomize(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("Customize.fxml");
    }
    

    @FXML
    private void handleGoToRepayment(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Repayment.fxml");
    }
    
    @FXML
    void handleGoToCheckEquipment(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("StageSelection.fxml");
    }


    @FXML
    void handleGoToGuild(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("Guild.fxml");
    }
    
    @FXML
    void handleGoToPictureBook(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("PictureBook.fxml");
    }
    
    @FXML
    void handleGoToCharacterPage(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("CharacterPage.fxml");
    }
    
    @FXML
    void Option(MouseEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        OptionDialogUtil.showConfirmDialog(currentStage, result -> {
            if (result) {
                System.out.print("OK");
            } else {
                System.out.println("Cancel");
            }
        });
    }
    
    @FXML
    void Inventory(MouseEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("Inventory.fxml");
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
                    ・ここから各ページに移動できます
                    ・主人公の顔のアイコン→ステータス
                    ・カバンのアイコン→インベントリ
                    ・画面右上の歯車アイコン→設定が開けます
                    ・出撃→ステージを選択して出撃するができます。
                    ・開発→手持ちの魔法を組み合わせてオリジナルの魔法を作り、
                      それらを装備することが出来ます。
                    ・ギルド→アイテムや素材の売買が出来ます。
                    ・図鑑→モンスターの生態や攻略情報、魔法の情報が載っています。
                    ・返済→幼馴染に借金を返済します。返済額によってはご褒美も…？
                    ・メインメニュー、返済、ギルドのキャラクターはそれぞれクリックすることで話すことが出来ます。時には攻略に役立つことを話すかも？
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void applyLock() {
    	if(applyUnLock()) {
	    	Illustrated.setDisable(false);
	    	Illustrated.setOpacity(0.75);
	    	LockImage1.setVisible(false);
    	}else {
	    	Illustrated.setDisable(true);
	    	Illustrated.setOpacity(0.4);	    	
    	}
    }
    
    public boolean applyUnLock() {
    	boolean cleared = true;
        try (Connection conn = DBManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                 "SELECT cleared FROM stages WHERE stage_id = 1");
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next()) {
                cleared = Boolean.parseBoolean(rs.getString("cleared"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ステージのクリア状況取得に失敗しました" + e.getMessage());
        }
        return cleared;
    }
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private ImageView characterImage;

    // 通常時
    private final Image normalFace =
    			new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_＝＝.png"));

    // 喋っている時
    private final Image smileFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_笑顔.png"));
    
    //眼反らし
    private final Image sideeyeFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_眼反らし.png"));
    
    //oo眼
    private final Image surpriseFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_〇〇.png"));
    
    //= =眼
    private final Image disbeliefFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_＝＝.png"));
    
    //= =眼
    private final Image closeeyeFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_目をつむる.png"));
    
    //泣き顔
    private final Image cryFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_号泣.png"));
    
    //闇
    private final Image darkFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_黒落ち.png"));
    
    //００眼泣き
    private final Image surpriseEXFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_〇〇泣き.png"));
    
    //怒り顔
    private final Image angryFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_キレ.png"));
    
  //怒り顔
    private final Image cryEXFace =
            new Image(getClass().getResourceAsStream("images/キャラ_images/ループレヒト_シクシク.png"));
    
		// ステージ1用
    	private final TalkLine[] talksStage1 = {
    	    new TalkLine(
    	        "今日も稼がなきゃ…。これもエリカのせいだーっ！鬼―ッ！悪魔ーッ！ひゃっ！エリカ！？"
    	        + "何も言ってないですよ！何も言っていないので借金は増やさないでください～！！",
    	        cryFace
    	    ),
    	    new TalkLine(
    	        "うぅ、研究したいです。",
    	        closeeyeFace
    	    ),
    	    new TalkLine(
    	        "エリカぁ～、お腹がすきました～。もうペコペコです～",
    	        smileFace
    	    ),
    	    new TalkLine(
    	        "今日はいい天気です。こんな日は昼寝に限り…ませんよね！クエストに行ってきます！",
    	        surpriseFace
    	    ),
    	    new TalkLine(
    	        "んぁ、もう朝ですか…？研究に没頭しすぎました…。",
    	        disbeliefFace
    	    ),
    	    new TalkLine(
    	        "違います！この部屋は汚いんじゃなくて物を取りやすいような最適な配置にしているんです！",
    	        sideeyeFace
    	    ),
    	    new TalkLine(
    	        "ひゃ！ゴキブリ！仕方ありません…。殺虫魔法を急いで完成させます！",
    	        angryFace
    	    )
    	};


	
	    // ステージ2用
    	private final TalkLine[] talksStage2 = {
    	    new TalkLine(
    	        "ここは潮で少しべとべとしますね…本が傷まないか心配です…",
    	        disbeliefFace
    	    ),
    	    new TalkLine(
    	        "新しい街…見知らぬモンスター…ギルドから渡された図鑑を読み込んでおかなくっちゃですね…面倒です…",
    	        closeeyeFace
    	    ),
    	    new TalkLine(
    	        "ここは全体的に雷が効くんですねー",
    	        normalFace
    	    ),
    	    new TalkLine(
    	        "ここはお魚が美味しいですね。今度はエリカも一緒に…来ますかね？",
    	        smileFace
    	    ),
    	    new TalkLine(
    	        "漁船を襲った謎のモンスター、いったい何なんだろ",
    	        normalFace
    	    )
    	};
	
    	private final TalkLine[] talksStage3 = {
    	    new TalkLine(
    	        "やっと目的地に到着しました…きれいな街ですね～。",
    	        closeeyeFace
    	    ),
    	    new TalkLine(
    	        "流石にここまで一人だと寂しいですね…そうだ！食事に受付嬢さんを誘ってみましょう！",
    	        smileFace
    	    ),
    	    new TalkLine(
    	        "ここは強いモンスターが多いですね。気を付けていきましょう！",
    	        normalFace
    	    ),
    	    new TalkLine(
    	        "うぅ…ウサギちゃん可愛いのに…狩らないといけないなんて…でもお肉は美味しいんですよねぇ",
    	        sideeyeFace
    	    ),
    	    new TalkLine(
    	        "ふ～ん…エリカちゃんも可愛いところがありますねぇ。これは帰ったら"
    	        + "たっぷりいじらなければなりませんねぇ。",
    	        smileFace
    	    ),
    	    new TalkLine(
    	        "連絡？誰だろ…？えっエリカ！？無駄遣いしてないでしょうねって…。サァー…",
    	        disbeliefFace
    	    )
    	};


	    // ステージ4クリア前
    	private final TalkLine[] talksStage4BeforeClear = {
    	    new TalkLine(
    	        "なんかここのモンスター、今までと比べ物にならないくらい強くないですか？",
    	        closeeyeFace
    	    ),
    	    new TalkLine(
    	        "辺境伯爵サマに粗相がないようにしなきゃ！アワアワ",
    	        surpriseFace
    	    ),
    	    new TalkLine(
    	        "お家かえって研究したーい！ここの敵強すぎです！でもお金たくさん稼げるんですよね…",
    	        disbeliefFace
    	    ),
    	    new TalkLine(
    	        "うぅ、ゴハンが美味しくないです。リーベックのお魚、"
    	        + "ラインラントのレストラン、エリカのお屋敷のごはんが恋しいです。",
    	        cryEXFace
    	    ),
    	    new TalkLine(
    	        "お給金たくさんで新しい魔法が買えました！辺境伯サマサマですね！ワッ！辺境伯様！？"
    	        + "いやっ、労働はほどほどがいいなぁって…ハハハッ…",
    	        sideeyeFace
    	    )
    	};

	    
	    // ステージ4クリア後
    	private final TalkLine[] talksStage4AfterClear = {
    		    new TalkLine(
    		        "はぁ、旅にモンスター討伐に戦場に駆り出されて…疲れました",
    		        closeeyeFace
    		    ),
    		    new TalkLine(
    		        "でもアイテムや魔法をたくさんゲットできましたし結果オーライですね！",
    		        smileFace
    		    ),
    		    new TalkLine(
    		        "そういえばギルドの受付嬢さん、エリカとお知り合いみたいですよ？",
    		        normalFace
    		    ),
    		    new TalkLine(
    		        "え”いや彼女から何も聞いてませんよ？ホントです！ホントですからっ！わぁ～！おってこないでーー！",
    		        surpriseEXFace
    		    ),
    		    new TalkLine(
    		        "どうにか借金チャラってことには～…なりませんよね、そうですよね…ハイ…討伐行ってきます",
    		        disbeliefFace
    		    ),
    		    new TalkLine(
    		        "わっ！エリカ！？いや何も隠してないですよ！あっ！領収書！見ないでください！ダメですってば！",
    		        cryFace
    		    ),
    		    new TalkLine(
    		        "これは無駄遣いじゃなくて研究に必要なんです！あ、ダメそうですね。逃げます。",
    		        sideeyeFace
    		    )
    		};


        private final Random random = new Random();

        private PauseTransition hideTimer;
        
        private int lastTalkIndex = -1;

        @FXML
        private void onTalkButton(ActionEvent event) {

            TalkLine[] targetTalks;

            int currentStage = StageDAO.getCurrentUnlockStage();
            boolean stage4Cleared = StageDAO.isStage4Cleared();

            if (currentStage <= 1) {
                targetTalks = talksStage1;
            } else if (currentStage == 2) {
                targetTalks = talksStage2;
            } else if (currentStage == 3) {
                targetTalks = talksStage3;
            } else {
                targetTalks = stage4Cleared
                        ? talksStage4AfterClear
                        : talksStage4BeforeClear;
            }

            int index;

            if (targetTalks.length == 1) {
                index = 0;
            } else {
                do {
                    index = random.nextInt(targetTalks.length);
                } while (index == lastTalkIndex);
            }

            lastTalkIndex = index;
            TalkLine talk = targetTalks[index];

            messageLabel.setText(talk.getText());
            messageLabel.setVisible(true);
            characterImage.setImage(talk.getFace());

            if (hideTimer != null) {
                hideTimer.stop();
            }

            hideTimer = new PauseTransition(Duration.seconds(5));
            hideTimer.setOnFinished(e -> {
                messageLabel.setVisible(false);
                characterImage.setImage(normalFace);
            });
            hideTimer.play();
        }
}