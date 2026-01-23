package application;


import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

public class PictureBook_AbnormalityController implements Initializable {

	@FXML
    private void handleGoBack(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
        SceneManager.goBack();
    }
	
	@FXML
    private AnchorPane AbnormalityButtonArea;

    @FXML
    private TextArea AbnormalityDescription;
    
    @FXML
    private TextArea AbnormalityCode;

    private final Map<String, Abnormality> abnormalitys = new LinkedHashMap<>();
	
	@Override
    public void initialize(URL location, ResourceBundle resources) {
        // 状態異常情報を登録
        abnormalitys.put("やけど", new Abnormality("やけど", """
        永続的に最大体力値の10％が減少してしまう状態。
        スリップダメージではないが治さねば相手の攻撃を受け止めきれずやられてしまうことも・・・？
        敵がやけどになった場合は治るまで最大HPの5％のダメージを継続的に受ける。
        """,
        """
		public void applyBurnStatus(MagicTarget target, boolean isPlayer) {
		    target.addStatusEffect("やけど");
		
		    if (isPlayer) {
		        target.setMaxHPModifier(0.9);
		        int effectiveMax = target.getEffectiveMaxHP();
		        if (target.getCurrentHP() > effectiveMax) {
		            target.setCurrentHP(effectiveMax);
		        }
		        System.out.printf
		        ("%s の最大HPがやけどにより10%%減少しました（上限: %d）", 
		        target.getName(), effectiveMax);
		    } else {
		        int damage = (int) Math.round(target.getMaxHP() * 0.05);
		        target.receiveDamage(damage);
		        System.out.printf
		        ("%s はやけどの継続ダメージで %d ダメージ！", 
		        target.getName(), damage);
		    }
		}
        """	
        ));

        abnormalitys.put("感電", new Abnormality("感電", """
        魔道具が感電してしまい、一時的な不調を起こす状態。
        セーフティーがかかり、出力が下がるため攻撃力が下がってしまう。
        """,
        """
		public void applyShockStatus(MagicTarget target) {
		    target.addStatusEffect("感電");
		    target.applyBuff("攻撃力", 0.7, 3);
		    System.out.printf
		    ("%s は感電し、攻撃力が30%%低下しました！", target.getName());
		}
        """	
        ));

        abnormalitys.put("凍傷", new Abnormality("凍傷", """
        体が凍てつき、もろくなってしまう状態。
        この状態になると防御力が下がってしまい、
        致命傷を受けやすくなってしまう。
        """,
        """
		public void applyFrostbiteStatus(MagicTarget target) {
		    target.addStatusEffect("凍傷");
		    target.applyBuff("被ダメージ倍率", 1.3, 3);
		    target.applyBuff("防御力", 0.9, 3);
		    System.out.printf("%s は凍傷により防御力が低下し、
		    		     ダメージを受けやすくなった！", target.getName());
		}
        """));

        abnormalitys.put("毒", new Abnormality("毒", """
        毒が体を蝕み、体力を奪う。
        毎ターンHPが最大HPの8％減ってしまうため早々に解毒しなければ危険である。
        """,
        """
		public void applyPoisonStatus(MagicTarget target) {
		    target.addStatusEffect("毒");
		    System.out.printf("%s に毒状態を付与しました！
		    		     毎ターンダメージを受けます。", target.getName());
		}
        """
        ));

        abnormalitys.put("眠り", new Abnormality("眠り", """
        戦闘中であっても抗えぬ眠気を付与する状態。
        眠って動けず無防備な状態は強烈な一撃を喰らうまで目覚めない。
        見る夢はきっと悪夢だろう。
        """,
        """
		public void applySleepStatus(MagicTarget target) {
		    target.addStatusEffect("眠り");
		    target.applyBuff("行動不能", 1.0, 1);
		    target.applyBuff("被ダメージ倍率", 1.5, 1);
		    System.out.printf("%s は眠ってしまった！
		    		    1ターン行動不能でダメージを受けやすくなった！", 
		    		    target.getName());
		}
        """
        ));
        
        // ボタン生成とイベント設定
        int i = 0;
        for (Abnormality abnormality : abnormalitys.values()) {
            Button btn = new Button(abnormality.getName());
            btn.setPrefSize(550, 60);
            btn.setLayoutX(20);
            btn.setLayoutY(20 + i * 90);
            btn.getStyleClass().add("chihaya");

            btn.setOnAction(e -> {
                // SE を鳴らす
            	SEPlayer.play("イベント/click.mp3");

                // 元の処理
            	showAbnormalityDescription(abnormality.getName());
            });
            AbnormalityButtonArea.getChildren().add(btn);
            i++;
        }

        // 初期表示（やけど）
        showAbnormalityDescription("やけど");
    }

    private void showAbnormalityDescription(String name) {
        Abnormality abnormality = abnormalitys.get(name);
        if (abnormality != null) {
        	AbnormalityDescription.setText(abnormality.getDescription());
        	AbnormalityCode.setText(abnormality.getCode()); 
        }
        // スクロール位置を次の描画フレームでリセット
        Platform.runLater(() -> {
            AbnormalityDescription.setScrollTop(0);
            AbnormalityCode.setScrollTop(0);
        });
    }
}
