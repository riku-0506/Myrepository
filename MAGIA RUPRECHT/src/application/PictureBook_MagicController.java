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

public class PictureBook_MagicController implements Initializable {

    @FXML
    private AnchorPane MagicButtonArea;

    @FXML
    private TextArea MagicDescription;

    private final Map<String, Magic> magics = new LinkedHashMap<>();
    
    @FXML
    private TextArea MagicCode;

    @FXML
    private void handleGoBack(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.goBack();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 魔法情報を登録
        magics.put("アロー", new Magic("アロー", """
		基礎的な魔法。これ自体の威力は低いがエンチャントして放つことで何者にも成れる魔法の矢
        """,
        """
		public class Arrow implements Magic{
			private int costMP = 1;
			int atk = 7;
			
			@Override
			public void cast(List<MagicTarget> targets) {
				if (!targets.isEmpty()) {
				MagicTarget target = targets.get(0);//単体攻撃
				System.out.println("敵１体に攻撃！");
				target.receiveDamage(atk);
				System.out.printf("→ %s に %d ダメージを与えた！",
					         target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));

        magics.put("ショット", new Magic("ショット", """
		魔力を収束させ高速で放つ魔法。アローに比べて燃費は悪いが威力も上がる魔法である
        """,
        """
		public class Shot implements Magic{
			private int costMP = 10;
			int atk = 30;
			
			@Override
			public void cast(List<MagicTarget> targets) {
				if (!targets.isEmpty()) {
				MagicTarget target = targets.get(0);//単体攻撃
				System.out.println("敵１体に攻撃！");
				target.receiveDamage(atk);
				System.out.printf("→ %s に %d ダメージを与えた！",
              		         target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));

        magics.put("カノン", new Magic("カノン", """
		Normal属性の単体技最上位魔法である。
		ショットに更に魔力を収束させ高速かつ巨大な弾を放つ魔法。
		その質量はすさまじい破壊力を誇る
        """,
        """
		public class Cannon implements Magic{
			private int costMP = 20;
			int atk = 60;
			
			@Override
			public void cast(List<MagicTarget> targets) {
				if (!targets.isEmpty()) {
				MagicTarget target = targets.get(0);//単体攻撃
				System.out.println("敵１体に攻撃！");
				target.receiveDamage(atk);
				System.out.printf("→ %s に %d ダメージを与えた！", 
					         target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));

        magics.put("アローレイン", new Magic("アローレイン", """
		アローを複数生成して同時に放ち、雨のように降らせる魔法。
		単体での威力は低いもののそこそこのせん滅力を誇る
        """,
        """
		import java.util.List;

		public class ArrowRain {
			private int costMP = 3;
			int atk = 2;
			public void castMultiTarget(List<MagicTarget> targets) {
				System.out.println("敵全体に攻撃！");
				for(MagicTarget target:targets){
					target.receiveDamage(atk);
					System.out.printf("→ %s に %d ダメージを
					与えた！", target.getName(), atk);
				}
			}
			
			public int getCostMP(){
				return costMP;
			}
		}
        """
        ));

        magics.put("サイクロン", new Magic("サイクロン", """
		魔力により局所的な竜巻を起こす。
		雑魚を吹っ飛ばすのにはうってつけである。
        """,
        """
		import java.util.List;

		public class Cyclon {
			private int costMP = 16;
			int atk = 10;
			public void castMultiTarget(List<MagicTarget> targets) {
				System.out.println("敵全体に攻撃！");
				for(MagicTarget target:targets){
					target.receiveDamage(atk);
					System.out.printf("→ %s に %d ダメージを
					与えた！", target.getName(), atk);
				}
			}
			
			public int getCostMP(){
				return costMP;
			}
		}
        """
        ));

        magics.put("テンペスト", new Magic("テンペスト", """
		Normal属性の全体技最上位魔法でありその破壊力は嵐のようである。
		敵をせん滅するときこの魔法の右に出るものはないだろう。
        """,
        """
		import java.util.List;

		public class Tempest {
			private int costMP = 32;
			int atk = 20;
			public void castMultiTarget(List<MagicTarget> targets) {
				System.out.println("敵全体に攻撃！");
				for(MagicTarget target:targets){
					target.receiveDamage(atk);
					System.out.printf("→ %s に %d ダメージを
					与えた！", target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));

        magics.put("火属性付与", new Magic("火属性付与", """
		魔法に火属性を付与する魔法。Normal属性は消えてしまうが他の属性とは共存可。
		低確率ではあるがやけど状態を付与することも出来るようになる。
		火が弱点の敵に対し,有効である。
        """,
        """
		import java.util.List;
		import java.util.Random;

		public class EnchantFire {
			private int costMP = 5;
			
			private final Random random = new Random();

			public void castEnchant(List<MagicTarget> targets) {
				for (MagicTarget target : targets) {
					// Normal属性があれば削除
					target.removeAttribute("NORMAL");
					
					// FIRE属性を付与
					target.addAttribute("FIRE");
					System.out.println("火属性を付与");
				
					// 10%の確率でやけど状態を付与
					if (random.nextInt(100) < 10) {
						target.addStatusEffect
						("やけど");
						System.out.println
						("やけど状態を付与しました！");
					}
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));

        magics.put("雷属性付与", new Magic("雷属性付与", """
		魔法に雷属性を付与する魔法。Normal属性は消えてしまうが他の属性とは共存可。
		低確率ではあるが感電状態を付与することも出来るようになる。
		雷が弱点の敵に対し,有効である。
        """,
        """
		import java.util.List;
		import java.util.Random;

		public class EnchantThunder {
			private int costMP = 5;
			
			private final Random random = new Random();

			public void castEnchant(List<MagicTarget> targets) {
				for (MagicTarget target : targets) {
					// Normal属性があれば削除
					target.removeAttribute("NORMAL");
					
					// THUNDER属性を付与
					target.addAttribute("THUNDER");
					System.out.println("雷属性を付与");
				
					// 10%の確率で感電状態を付与
					if (random.nextInt(100) < 10) {
						target.addStatusEffect
						("感電");
						System.out.println
						("感電状態を付与しました！");
					}
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));
        
        magics.put("氷属性付与", new Magic("氷属性付与", """
        		魔法に氷属性を付与する魔法。Normal属性は消えてしまうが他の属性とは共存可。
        		低確率ではあるが凍傷状態を付与することも出来るようになる。
        		雷が弱点の敵に対し,有効である。
                """,
                """
		import java.util.List;
		import java.util.Random;

		public class EnchantIce {
			private int costMP = 5;
			
			private final Random random = new Random();

			public void castEnchant(List<MagicTarget> targets) {
				for (MagicTarget target : targets) {
					// Normal属性があれば削除
					target.removeAttribute("NORMAL");
					
					// ICE属性を付与
					target.addAttribute("ICE");
					System.out.println("氷属性を付与");
				
					// 10%の確率で凍傷状態を付与
					if (random.nextInt(100) < 10) {
						target.addStatusEffect
						("凍傷");
						System.out.println
						("凍傷状態を付与しました！");
					}
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """
        ));
        
        magics.put("聖属性付与", new Magic("聖属性付与", """
                		魔法に聖属性を付与する魔法。Normal属性は消えてしまうが他の属性とは共存可。
                		状態異常を付与することは出来ないが特定の敵に対する弱点ダメージが非常に大きい。
                        """,
                        """
                		import java.util.List;
                		import java.util.Random;

                		public class EnchantHoly {
                			private int costMP = 5;
                			
                			private final Random random = new Random();

                			public void castEnchant(List<MagicTarget> targets) {
                				for (MagicTarget target : targets) {
                					// Normal属性があれば削除
                					target.removeAttribute("NORMAL");
                					
                					// HOLY属性を付与
                					target.addAttribute("HOLY");
                					System.out.println("聖属性を付与");
                				}
                			}
                			
                			public int getCostMP(){
                				return costMP;
                			}
                		}
                        """
                ));

                magics.put("やけど確率UP", new Magic("やけど確率UP", """
        		魔法にやけど確率UPを付与する魔法。
        		魔法の属性に関係なく50％の確率で付与が可能になる。
                """,
                """
        		import java.util.Random;

        		public class EnchantBurn {
        			private int costMP = 20;
        			private final Random random = new Random();

        			public void castEnchant(MagicTarget target) {
        				System.out.println("やけど確率UP");
        				
        				// 50%の確率でやけど状態を付与
        				if (random.nextInt(100) < 50) {
        					target.addStatusEffect("やけど");
        					System.out.println
        					("やけど状態を付与しました！");
        				}
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("感電確率UP", new Magic("感電確率UP", """
        		魔法に感電確率UPを付与する魔法。
        		魔法の属性に関係なく50％の確率で付与が可能になる。
                """,
                """
        		import java.util.Random;

        		public class EnchantElectricShock {
        			private int costMP = 20;
        			private final Random random = new Random();

        			public void castEnchant(MagicTarget target) {
        				System.out.println("感電確率UP");
        				
        				// 50%の確率で感電状態を付与
        				if (random.nextInt(100) < 50) {
        					target.addStatusEffect("感電");
        					System.out.println
        					("感電状態を付与しました！");
        				}
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("凍傷確率UP", new Magic("凍傷確率UP", """
        		魔法に凍傷確率UPを付与する魔法。
        		魔法の属性に関係なく50％の確率で付与が可能になる。
                """,
                """
        		import java.util.Random;

        		public class EnchantFrostbite {
        			private int costMP = 20;
        			private final Random random = new Random();

        			public void castEnchant(MagicTarget target) {
        				System.out.println("凍傷確率UP");
        				
        				// 50%の確率で凍傷状態を付与
        				if (random.nextInt(100) < 50) {
        					target.addStatusEffect("凍傷");
        					System.out.println
        					("凍傷状態を付与しました！");
        				}
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("睡眠確率UP", new Magic("睡眠確率UP", """
        		魔法に睡眠確率UPを付与する魔法。
        		魔法の属性に関係なく50％の確率で付与が可能になる。
                """,
                """
        		import java.util.Random;

        		public class EnchantSleep {
        			private int costMP = 20;
        			private final Random random = new Random();

        			public void castEnchant(MagicTarget target) {
        				System.out.println("睡眠確率UP");
        				
        				// 50%の確率で睡眠状態を付与
        				if (random.nextInt(100) < 50) {
        					target.addStatusEffect("睡眠");
        					System.out.println
        					("睡眠状態を付与しました！");
        				}
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("毒確率UP", new Magic("毒確率UP", """
        		魔法に毒確率UPを付与する魔法。
        		魔法の属性に関係なく50％の確率で付与が可能になる。
                """,
                """
        		import java.util.Random;

        		public class EnchantPoison {
        			private int costMP = 20;
        			private final Random random = new Random();

        			public void castEnchant(MagicTarget target) {
        				System.out.println("毒確率UP");
        				
        				// 50%の確率で毒状態を付与
        				if (random.nextInt(100) < 50) {
        					target.addStatusEffect("毒");
        					System.out.println
        					("毒状態を付与しました！");
        				}
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("攻撃UP", new Magic("攻撃UP", """
        		魔道具に魔力を込めてオーバークロックさせ、
        		一時的に魔力効率や出力を上げることが出来る。
                """,
                """
        		public class EnchantATK {
        			private int costMP = 10;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("攻撃UP");
        				
        				// 攻撃力を一時的に強化(5T)
        				self.applyBuff("攻撃力", 1.1, 5);
        				System.out.println
        				("攻撃力が5ターンの間 1.1倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("攻撃UP+", new Magic("攻撃UP+", """
        		魔道具に更に魔力を込めてオーバークロックさせ、
        		一時的に魔力効率や出力を更に上げることが出来る。
                """,
                """
        		public class EnchantATKPlus {
        			private int costMP = 22;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("攻撃UP+");
        				
        				// 攻撃力を一時的に強化(5T)
        				self.applyBuff("攻撃力", 1.2, 5);
        				System.out.println
        				("攻撃力が5ターンの間 1.2倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("攻撃UP++", new Magic("攻撃UP++", """
        		魔道具に最大限魔力を込めてオーバークロックさせ、
        		一時的に限界まで魔力効率や出力を上げることが出来る。
        		攻撃力UPを３つ同時に展開したとき魔道具は爆発ギリギリになるため
        		使うのは注意し、帰還出来たならば買い替えるのが良いだろう。
                """,
                """
        		public class EnchantATKPlus2 {
        			private int costMP = 33;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("攻撃UP++");
        				
        				// 攻撃力を一時的に強化(5T)
        				self.applyBuff("攻撃力", 1.3, 5);
        				System.out.println
        				("攻撃力が5ターンの間 1.3倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("防御UP", new Magic("防御UP", """
        		自らの衣服に魔力をまとい、一時的に防御力を上げることが出来る。
                """,
                """
        		public class EnchantDEF {
        			private int costMP = 20;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("防御UP");
        				
        				// 防御力を一時的に強化(5T)
        				self.applyBuff("防御力", 1.1, 5);
        				System.out.println
        				("防御力が5ターンの間 1.1倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("防御UP+", new Magic("防御UP+", """
        		自らの衣服に更に魔力をまとい、一時的に防御力を更に上げることが出来る。
        		その強度は刃も通さないほどである
                """,
                """
        		public class EnchantDEFPlus {
        			private int costMP = 30;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("防御UP+");
        				
        				// 防御力を一時的に強化(5T)
        				self.applyBuff("防御力", 1.14, 5);
        				System.out.println
        				("防御力が5ターンの間 1.14倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("防御UP++", new Magic("防御UP++", """
        		自らの衣服に最大限魔力をまとい、一時的に防御力を限界まで上げることが出来る。
        		普通の衣服でも重さは変わらず鎧の如き防御力を得る。その硬さは帝国兵からもお墨付きである。
                """,
                """
        		public class EnchantDEFPlus2 {
        			private int costMP = 40;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("防御UP");
        				
        				// 防御力を一時的に強化(5T)
        				self.applyBuff("防御力", 1.18, 5);
        				System.out.println
        				("防御力が5ターンの間 1.18倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("威力UP", new Magic("威力UP", """
        		魔法に更に魔力を込めることで威力を上げることが出来る。
                """,
                """
        		public class EnchantPOW {
        			private int costMP = 5;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("威力UP");
        				
        				// 魔法の威力を1T強化
        				self.applyBuff("攻撃力", 1.1, 1);
        				System.out.println
        				("魔法の威力が1ターンの間 1.1倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("威力UP+", new Magic("威力UP+", """
        		魔法にさらに魔力を込めることで威力を上げることが出来る。
                """,
                """
        		public class EnchantPOWPlus {
        			private int costMP = 11;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("威力UP+");
        				
        				// 魔法の威力を1T強化
        				self.applyBuff("攻撃力", 1.2, 11);
        				System.out.println
        				("魔法の威力が1ターンの間 1.2倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("威力UP++", new Magic("威力UP++", """
        		魔法に大量の魔力を込めて最大まで強化することが出来る。
        		魔法に魔力を込める量に制限はないが込めれば込めるほど制御も難しくなるので注意は必要。
                """,
                """
        		public class EnchantPOWPlus2 {
        			private int costMP = 17;
        			
        			public void castEnchant(MagicTarget self) {
        				System.out.println("威力UP++");
        				
        				// 魔法の威力を1T強化
        				self.applyBuff("攻撃力", 1.3, 1);
        				System.out.println
        				("魔法の威力が1ターンの間 1.3倍 されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("状態異常確率UP", new Magic("状態異常確率UP", """
        		魔法に侵食しやすい魔力を込めて相手を状態異常にかけやすくする。
        		特殊な魔力なため消費MPも多い。
                """,
                """
        		public class AbnormalityBoost {
        			private int costMP = 35;
        			public void castEnchant(MagicTarget self) {
        				System.out.println("状態異常確率UP");
        				
        				//状態異常にかける確率を1T上げる
        				self.applyBuff("状態異常確率アップ", 1.3, 1);
        				
        				System.out.println
        				("状態異常の成功率が1ターンの間
        				1.3倍されました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("攻撃DOWN", new Magic("攻撃DOWN", """
        		相手の動きを鈍らせ攻撃力を下げる魔法。
                """,
                """
        		public class ATKDOWN {
        			private int costMP = 10;
        			
        			public void castEnchant(MagicTarget target) {
        				System.out.println("攻撃力DOWN");
        				
        				//相手の攻撃力を5Tの間10％下げる
        				target.applyBuff("攻撃力ダウン", 0.9, 5);
        				System.out.println
        				("相手の攻撃力が5ターンの間10％下がりました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("火耐性DOWN", new Magic("火耐性DOWN", """
        		相手の耐性を下げる魔法。
                """,
                """
        		public class FireResistDOWN {
        			private int costMP = 20;
        			
        			public void castEnchant(MagicTarget target) {
        				System.out.println("火耐性DOWN");
        				
        				//相手の火耐性を5T下げる
        				target.applyBuff("火耐性ダウン", 1.1, 5);
        				System.out.println
        				("相手の火耐性が5ターンの間10％下がりました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("雷耐性DOWN", new Magic("雷耐性DOWN", """
        		相手の耐性を下げる魔法。
                """,
                """
        		public class ThunderResistDOWN {
        			private int costMP = 20;
        			
        			public void castEnchant(MagicTarget target) {
        				System.out.println("雷耐性DOWN");
        				
        				//相手の雷耐性を5T下げる
        				target.applyBuff("雷耐性ダウン", 1.1, 5);
        				System.out.println
        				("相手の雷耐性が5ターンの間10％下がりました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("氷耐性DOWN", new Magic("氷耐性DOWN", """
        		相手の耐性を下げる魔法。
                """,
                """
        		public class IceResistDOWN {
        			private int costMP = 20;
        			
        			public void castEnchant(MagicTarget target) {
        				System.out.println("氷耐性DOWN");
        				
        				//相手の氷耐性を5T下げる
        				target.applyBuff("氷耐性ダウン", 1.1, 5);
        				System.out.println
        				("相手の氷耐性が5ターンの間10％下がりました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("聖耐性DOWN", new Magic("聖耐性DOWN", """
        		相手の耐性を下げる魔法。
                """,
                """
        		public class HolyResistDOWN {
        			private int costMP = 20;
        			
        			public void castEnchant(MagicTarget target) {
        				System.out.println("聖耐性DOWN");
        				
        				//相手の聖耐性を5T下げる
        				target.applyBuff("聖耐性ダウン", 1.1, 5);
        				System.out.println
        				("相手の聖耐性が5ターンの間10％下がりました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("HP回復", new Magic("HP回復", """
        		魔力により即座に傷を回復させる。重症や病気などは治せない。
                """,
                """
        		public class SelfHeal {
        			private int costMP = 5;
        			private int healAmount = 20;
        			
        			public void cast(MagicTarget self) {
        				self.healHP(healAmount);
        				System.out.println
        				("自分のHPを20回復しました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("HP回復+", new Magic("HP回復+", """
        		回復の上位魔法。回復よりも重症のけがや軽い病気を治すことが出来る。
                """,
                """
        		public class SelfHealPlus {
        			private int costMP = 15;
        			private int healAmount = 70;
        			
        			public void cast(MagicTarget self) {
        				self.healHP(healAmount);
        				System.out.println
        				("自分のHPを70回復しました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));

                magics.put("HP回復++", new Magic("HP回復++", """
        		回復の最上位魔法。大体の怪我や病気も瞬時に治すことが出来る。
                """,
                """
        		public class SelfHealPlus2 {
        			private int costMP = 30;
        			private int healAmount = 150;
        			
        			public void cast(MagicTarget self) {
        				self.healHP(healAmount);
        				System.out.println
        				("自分のHPを150回復しました！");
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));
                
                magics.put("状態異常回復", new Magic("状態異常回復", """
        		HPを回復することは出来ないが状態異常を消すことのできる魔法。
        		一度に複数の状態異常を消すこともできる。
                """,
                """
        		public class CureStatusEffects {
        			private int costMP = 5;
        			
        			public void cast(MagicTarget self) {
        				List<String> effects = self.getStatusEffects();
        			
	        			if (effects.isEmpty()) {
	        				System.out.printf
	        				("%s にかかっている状態異常はありません。"
	        				, self.getName());
	        				return;
	        			}
	        			
	        			System.out.printf
	        			("%s の状態異常 [%s] をすべて解除しました！", 
	        			self.getName(), String.join("・", effects));
	        			effects.clear(); // 状態異常をすべて解除

	        		}
	        			
	        		public int getCostMP(){
	        			return costMP;
	        		}
        		}
                """
                ));
                
                magics.put("バーサク", new Magic("バーサク", """
        		魔道具にかかっているリミッターを完全に解除することで一時的に攻撃力を上げる魔法。
        		ただし、全てマニュアル操作になる点や制御が困難になるため
        		防御まで気を回すことが難しくなる点に注意。
                """,
                """
        		public class Berserk {
        			private int costMP = 30;
        			
        			public void cast(MagicTarget self) {
        				System.out.printf("%s はバーサクを発動！", 
        				self.getName());
        			
	        			// 攻撃力を1.5倍に
	        			self.applyBuff("攻撃力", 1.5, 5);
	        			System.out.println("攻撃力が5ターンの間
	        			1.5倍されました！");
	        			
	        			// 防御力を低下（0.8倍、5ターン）
	        			self.applyBuff("防御力", 0.8, 5);
	        			System.out.println("防御力が5ターンの間
	        			0.8倍に低下しました");
	        		}
	        			
	        		public int getCostMP(){
	        			return costMP;
	        		}
        		}
                """
                ));
                

                
                magics.put("フォトレス", new Magic("フォトレス", """
        		魔道具に供給量を一時的に減らしその分を防具にまわす魔法。
        		その分魔法の出力は低下するが防御力が飛躍的に上昇する。
                """,
                """
        		public class Fortress {
        			private int costMP = 30;
        			
        			public void cast(MagicTarget self) {
        				System.out.printf("%s はフォトレスを発動！", 
        				self.getName());
        			
	        			// 攻撃力を0.8倍に
	        			self.applyBuff("攻撃力", 0.8, 5);
	        			System.out.println("攻撃力が5ターンの間
	        			0.8倍に下がりました！");
	        			
	        			// 防御力を低下（1.5倍、5ターン）
	        			self.applyBuff("防御力", 1.5, 5);
	        			System.out.println("防御力が5ターンの間
	        			1.5倍に上昇しました");
	        		}
	        			
	        		public int getCostMP(){
	        			return costMP;
	        		}
        		}
                """
                ));

                magics.put("ケイオスフィルド", new Magic("ケイオスフィルド", """
        		侵食する魔力を辺りに充満させて状態異常にかけやすくする。
        		制御する必要がないため消費MPや持続ターンが長いが
        		自分も状態異常にかかかりやすくなる。
                """,
                """
        		public class ChaosField {
        			private int costMP = 10;
        			public void castEnchant(MagicTarget self, 
        				               List<MagicTarget> enemies) 
        			{
        				System.out.println
        				("ケイオスフィルドにより状態異常にかかりやすくなった！");
        				
        				// 自分に状態異常確率アップ（1.4倍、3ターン）
        				self.applyBuff("状態異常確率アップ", 1.4, 3);
        				System.out.println("状態異常の成功率が3ターンの間
        				1.4倍されました！");
        				
        				// 敵全体に状態異常確率アップ（1.4倍、3ターン）
        				for (MagicTarget enemy : enemies) {
        					enemy.applyBuff
        					("状態異常確率アップ", 1.4, 3);
        					System.out.printf
        					("%sの状態異常成功率アップ！", 
        					enemy.getName());
        				}
        			}
        			
        			public int getCostMP(){
        				return costMP;
        			}
        		}
                """
                ));
                
                magics.put("サバイバー", new Magic("サバイバー", """
        		フィールド一帯の生物を興奮状態にし、闘争心を昂らせる魔法。
        		凶暴化し、攻撃力が上がるが防御力が下がる。
        		敵味方関係なく作用するため注意が必要。
                """,
                """
        		public class Survivor {
        			private int costMP = 20;
        			
        			public void cast(MagicTarget self, 
        					List<MagicTarget> enemies) {
        				System.out.printf
        				("サバイバーの効果により辺り一帯の生物が
        				興奮状態になった");
        			
	        			// 攻撃力を1.5倍に
	        			self.applyBuff("攻撃力", 1.5, 5);
	        			System.out.println
	        			("攻撃力が5ターンの間1.5倍されました！");
	        			
	        			// 防御力を低下（0.8倍、5ターン）
	        			self.applyBuff("防御力", 0.8, 5);
	        			System.out.println
	        			("防御力が5ターンの間0.8倍に低下しました");
	        			
	        			// 敵全体の攻撃力を1.5倍に、防御量を0.8倍に（5T)
        				for (MagicTarget enemy : enemies) {
        					enemy.applyBuff("攻撃力", 1.5, 5);
        					enemy.applyBuff("防御力", 0.8, 5);
        					System.out.printf("%sの攻撃力があがった！
        					防御力が下がった！", enemy.getName());
        				}
	        		}
	        			
	        		public int getCostMP(){
	        			return costMP;
	        		}
        		}
                """
                ));
                
                
                

                magics.put("オマケ", new Magic("オマケ", """
        		使用してるインターフェース
                """,
                """
        		import java.util.List;
        		
        		// 魔法の基本インターフェース（多様な形式に対応）
        		public interface Magic {
        			String getName();
        			String getDescription();
        			String getCode();
        			int getCostMP();

        			default void cast(List<MagicTarget> targets) {}
        			default void cast(MagicTarget self) {}
        			default void castEnchant(MagicTarget self) {}
        			default void castEnchant(MagicTarget self, 
        			List<MagicTarget> enemies) {}
        		}
                """
                ));

                magics.put("オマケ2", new Magic("オマケ2", """
        		使用してるクラス
                """,
                """
import java.util.*;

public class MagicTarget {

    // 名前（例：主人公、スライムなど）
    private String name;

    // HP管理
    private int currentHP;
    private int maxHP;

    // 最大HP補正（やけど時に0.9など）
    private double maxHPModifier = 1.0;

    // 攻撃力と防御力（防御はカット率）
    private double baseAttackPower = 10.0;
    private double baseDefenseCutRate = 1.0;

    // 属性耐性（基準値）
    private double baseFireResist = 1.0;
    private double baseThunderResist = 1.0;
    private double baseIceResist = 1.0;
    private double baseHolyResist = 1.0;

    // 属性・状態異常・バフ
    private Set<String> attributes = new HashSet<>();
    private List<String> statusEffects = new ArrayList<>();
    private Map<String, Buff> activeBuffs = new HashMap<>();

    public MagicTarget(String name, int hp) {
        this.name = name;
        this.currentHP = hp;
        this.maxHP = hp;
        attributes.add("NORMAL");
    }

    // 基本情報取得
    public String getName() { return name; }
    public int getMaxHP() { return maxHP; }
    public int getCurrentHP() { return currentHP; }
    public void setCurrentHP(int hp) {
        this.currentHP = Math.max(0, Math.min
        (hp, getEffectiveMaxHP()));
    }

    // 最大HP補正
    public void setMaxHPModifier(double modifier) {
        this.maxHPModifier = modifier;
    }

    public int getEffectiveMaxHP() {
        return (int) Math.round(maxHP * maxHPModifier);
    }

    // 属性操作
    public void addAttribute(String attr) { 
        attributes.add(attr); 
    }
    public void removeAttribute(String attr) { 
        attributes.remove(attr); 
    }
    public Set<String> getAttributes() { 
        return attributes; 
    }

    // 状態異常操作
    public void addStatusEffect(String effect) {
        statusEffects.add(effect);
        System.out.printf("%s に状態異常 [%s] を付与しました！",
                          name, effect);
    }

    public List<String> getStatusEffects() { 
        return statusEffects; 
    }

	public void cureAllAbnormalities() {
	    if (statusEffects.isEmpty()) {
	        System.out.printf
	        ("%s に状態異常はかかっていません。", name);
	        return;
	    }
	
	    // やけど：最大HP補正を元に戻す
	    if (statusEffects.contains("やけど")) {
	        setMaxHPModifier(1.0);
	        System.out.printf
	        ("%s の最大HPが元に戻りました（%d）", name, maxHP);
	    }
	
	    // 感電：攻撃力バフ解除
	    if (statusEffects.contains("感電")) {
	        activeBuffs.remove("攻撃力");
	        System.out.printf
	        ("%s の感電による攻撃力低下を
	        解除しました！", name);
	    }
	
	    // 凍傷：防御力・被ダメージ倍率バフ解除
	    if (statusEffects.contains("凍傷")) {
	        activeBuffs.remove("防御力");
	        activeBuffs.remove("被ダメージ倍率");
	        System.out.printf
	        ("%s の凍傷による防御力低下と
	        ダメージ補正を解除しました！", name);
	    }
	
	    // 眠り：行動不能・被ダメージ倍率バフ解除
	    if (statusEffects.contains("眠り")) {
	        activeBuffs.remove("行動不能");
	        activeBuffs.remove("被ダメージ倍率");
	        System.out.printf
	        ("%s の眠りによる行動不能と
	        ダメージ補正を解除しました！", name);
	    }
	
	    System.out.printf("%s の状態異常[%s]を
                	    すべて解除しました！", 
                	    name, String.join("・", 
                	    statusEffects));
	    statusEffects.clear();
	}


    // 攻撃力・防御力の実効値
    public double getEffectiveAttackPower() {
        Buff buff = activeBuffs.get("攻撃力");
        return 
        baseAttackPower * (buff != null ? buff.multiplier : 1.0);
    }

    public double getEffectiveDefenseCutRate() {
        Buff buff = activeBuffs.get("防御力");
        return 
        baseDefenseCutRate * 
        (buff != null ? buff.multiplier : 1.0);
    }

    // 被ダメージ倍率（凍傷・睡眠など）
    public double getDamageTakenMultiplier() {
        Buff buff = activeBuffs.get("被ダメージ倍率");
        return 
        buff != null ? buff.multiplier : 1.0;
    }

    // 行動不能チェック（睡眠など）
    public boolean isActionDisabled() {
        return activeBuffs.containsKey("行動不能");
    }

    // ダメージ処理（防御・状態異常補正込み）
    public void receiveDamage(int rawDamage) {
        double cutRate = getEffectiveDefenseCutRate();
        double damageMultiplier = getDamageTakenMultiplier();
        int finalDamage = (int) Math.round
        (rawDamage * cutRate * damageMultiplier);
        currentHP -= finalDamage;
        currentHP = Math.max(0, currentHP);
        System.out.printf("%s は %d ダメージを受けた！
        （防御補正: ×%.2f, 被ダメ補正: ×%.2f） 残りHP: %d",
            name, finalDamage, cutRate, 
            damageMultiplier, currentHP);
    }

    // HP回復（最大HP補正を考慮）
    public void healHP(int amount) {
        int effectiveMax = getEffectiveMaxHP();
        int before = currentHP;
        currentHP = Math.min(effectiveMax, currentHP + amount);
        System.out.printf("%s のHPを %d 回復！（%d → %d）", 
        name, amount, before, currentHP);
    }

    // バフ付与
    public void applyBuff
    (String stat, double multiplier, int duration) 
    {
        activeBuffs.put(stat, new Buff(multiplier, duration));
        System.out.printf("%s に [%s ×%.1f] を
                	 %dターン付与しました！", 
        name, stat, multiplier, duration);
    }

    // ターン進行（バフ解除）
    public void advanceTurn() {
        Iterator<Map.Entry<String, Buff>> iterator = 
        activeBuffs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Buff> entry = iterator.next();
            Buff buff = entry.getValue();
            buff.remainingTurns--;
            if (buff.remainingTurns <= 0) {
                iterator.remove();
                System.out.printf
                ("%s の [%s] バフが解除されました。", 
                name, entry.getKey());
            }
        }
    }

    // 属性耐性取得（火・雷・氷・聖）
    public double getEffectiveFireResist() {
        Buff debuff = activeBuffs.get("火耐性ダウン");
        return baseFireResist * 
        (debuff != null ? debuff.multiplier : 1.0);
    }

    public double getEffectiveThunderResist() {
        Buff debuff = activeBuffs.get("雷耐性ダウン");
        return baseThunderResist * 
        (debuff != null ? debuff.multiplier : 1.0);
    }

    public double getEffectiveIceResist() {
        Buff debuff = activeBuffs.get("氷耐性ダウン");
        return baseIceResist * 
        (debuff != null ? debuff.multiplier : 1.0);
    }

    public double getEffectiveHolyResist() {
        Buff debuff = activeBuffs.get("聖耐性ダウン");
        return baseHolyResist * 
        (debuff != null ? debuff.multiplier : 1.0);
    }

    // 毎ターン呼び出す状態異常効果（やけど・毒）
    public void applyTurnBasedAbnormalEffects(boolean isPlayer) {
        if (statusEffects.contains("やけど") && !isPlayer) {
            int damage = (int) Math.round(getMaxHP() * 0.05);
            receiveDamage(damage);
            System.out.printf
            ("%s はやけどの継続ダメージで %d ダメージ！", 
            name, damage);
        }

        if (statusEffects.contains("毒")) {
            int damage = (int) Math.round(getMaxHP() * 0.08);
            receiveDamage(damage);
            System.out.printf
            ("%s は毒のダメージで %d ダメージを受けた！", 
            name, damage);
        }
    }

    // バフ構造
    public static class Buff {
        public double multiplier;
        public int remainingTurns;

        public Buff(double multiplier, int duration) {
            this.multiplier = multiplier;
            this.remainingTurns = duration;
        }
    }
}
                """
                ));
        
        // ボタン生成とイベント設定
        int i = 0;
        for (Magic magic : magics.values()) {
            Button btn = new Button(magic.getName());
            btn.setPrefSize(550, 60);
            btn.setLayoutX(20);
            btn.setLayoutY(20 + i * 90);
            btn.getStyleClass().add("chihaya");

            btn.setOnAction(e -> {
                // SE を鳴らす
            	SEPlayer.play("イベント/click.mp3");

                // 元の処理
                showMagicDescription(magic.getName());
            });
            MagicButtonArea.getChildren().add(btn);
            i++;
        }

        // 初期表示（アロー）
        showMagicDescription("アロー");
    }

    private void showMagicDescription(String name) {
        Magic magic = magics.get(name);
        if (magic != null) {
            MagicDescription.setText(magic.getDescription());
            MagicCode.setText(magic.getCode());
         
            // スクロール位置を次の描画フレームでリセット
            Platform.runLater(() -> {
            	MagicDescription.setScrollTop(0);
                MagicCode.setScrollTop(0);
            });
        }
    }
}