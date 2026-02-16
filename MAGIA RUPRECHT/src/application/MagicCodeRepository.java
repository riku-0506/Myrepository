package application;

import java.util.HashMap;
import java.util.Map;

/**
 * 各魔法のコードを保持するリポジトリクラス。
 * 魔法名をキーに、ソースコード文字列を取得できる。
 */
public class MagicCodeRepository {

    private static final Map<String, String> MAGIC_CODE_MAP = new HashMap<>();

    static {
        MAGIC_CODE_MAP.put("アロー", """
		public class Arrow implements Magic{
			private int costMP = 1;
			int atk = 5;
			
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
        """);
        
        MAGIC_CODE_MAP.put("ショット", """
		public class Shot implements Magic{
			private int costMP = 10;
			int atk = 20;
			
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
        """);
        
        MAGIC_CODE_MAP.put("カノン", """
		public class Cannon implements Magic{
			private int costMP = 20;
			int atk = 40;
			
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
        """);
        
        MAGIC_CODE_MAP.put("アローレイン", """
		import java.util.List;

		public class ArrowRain implements Magic {
			private int costMP = 3;
			int atk = 2;
			
			@Override
			public void cast(List<MagicTarget> targets) {
				System.out.println("敵全体に攻撃！");
				for(MagicTarget target:targets){
					target.receiveDamage(atk);
					System.out.printf
					("→ %s に %d ダメージを与えた！", 
					target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """);
        
        MAGIC_CODE_MAP.put("サイクロン", """
		import java.util.List;

		public class Cyclon {
			private int costMP = 16;
			int atk = 10;
			public void castMultiTarget(List<MagicTarget> targets) {
				System.out.println("敵全体に攻撃！");
				for(MagicTarget target:targets){
					target.receiveDamage(atk);
					System.out.printf
					("→ %s に %d ダメージを与えた！", 
					target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """);
        
        MAGIC_CODE_MAP.put("テンペスト", """
		import java.util.List;

		public class Tempest {
			private int costMP = 32;
			int atk = 20;
			public void castMultiTarget(List<MagicTarget> targets) {
				System.out.println("敵全体に攻撃！");
				for(MagicTarget target:targets){
					target.receiveDamage(atk);
					System.out.printf
					("→ %s に %d ダメージを与えた！", 
					target.getName(), atk);
				}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """);
        
        MAGIC_CODE_MAP.put("火属性付与", """
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
        """);
        
        MAGIC_CODE_MAP.put("雷属性付与", """
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
        """);
        
        MAGIC_CODE_MAP.put("氷属性付与", """
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
			
			public int getCostMP() {
				return costMP;
			}
			}
			
			public int getCostMP() {
				return costMP;
			}
		}
        """);
        
        MAGIC_CODE_MAP.put("聖属性付与", """
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
		}
        """);
        
        MAGIC_CODE_MAP.put("やけど確率UP", """
        		import java.util.Random;

        		public class EnchantBurn {
        		    private int costMP = 20;
        		    private final Random random = new Random();

        		    public void castEnchant(MagicTarget target) {
        		        System.out.println("やけど確率UP");
        		        if (random.nextInt(100) < 50) {
        		            target.addStatusEffect
        		            ("やけど");
        		            System.out.println
        		            ("やけど状態を付与しました！");
        		        }
        		    }
        		    public int getCostMP() {
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("感電確率UP", """
        		import java.util.Random;

        		public class EnchantElectricShock {
        		    private int costMP = 20;
        		    private final Random random = new Random();

        		    public void castEnchant(MagicTarget target) {
        		        System.out.println("感電確率UP");
        		        if (random.nextInt(100) < 50) {
        		            target.addStatusEffect
        		            ("感電");
        		            System.out.println
        		            ("感電状態を付与しました！");
        		        }
        		    }
        		    public int getCostMP() {
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("凍傷確率UP", """
        		import java.util.Random;

        		public class EnchantFrostbite {
        		    private int costMP = 20;
        		    private final Random random = new Random();

        		    public void castEnchant(MagicTarget target) {
        		        System.out.println("凍傷確率UP");
        		        if (random.nextInt(100) < 50) {
        		            target.addStatusEffect
        		            ("凍傷");
        		            System.out.println
        		            ("凍傷状態を付与しました！");
        		        }
        		    }
        		    public int getCostMP() {
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("睡眠確率UP", """
        		import java.util.Random;

        		public class EnchantSleep {
        		    private int costMP = 20;
        		    private final Random random = new Random();

        		    public void castEnchant(MagicTarget target) {
        		        System.out.println("睡眠確率UP");
        		        if (random.nextInt(100) < 50) {
        		            target.addStatusEffect
        		            ("睡眠");
        		            System.out.println
        		            ("睡眠状態を付与しました！");
        		        }
        		    }
        		    public int getCostMP() {
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("毒確率UP", """
        		import java.util.Random;

        		public class EnchantPoison {
        		    private int costMP = 20;
        		    private final Random random = new Random();

        		    public void castEnchant(MagicTarget target) {
        		        System.out.println("毒確率UP");
        		        if (random.nextInt(100) < 50) {
        		            target.addStatusEffect
        		            ("毒");
        		            System.out.println
        		            ("毒状態を付与しました！");
        		        }
        		    }
        		    public int getCostMP() {
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("攻撃UP", """
        		public class EnchantATK {
        		    private int costMP = 10;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("攻撃UP");
        		        self.applyBuff("攻撃力", 1.1, 5);
        		        System.out.println
        		        ("攻撃力が5ターンの間 1.1倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("攻撃UP+", """
        		public class EnchantATKPlus {
        		    private int costMP = 22;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("攻撃UP+");
        		        self.applyBuff("攻撃力", 1.2, 5);
        		        System.out.println
        		        ("攻撃力が5ターンの間 1.2倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("攻撃UP++", """
        		public class EnchantATKPlus2 {
        		    private int costMP = 33;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("攻撃UP++");
        		        self.applyBuff("攻撃力", 1.3, 5);
        		        System.out.println
        		        ("攻撃力が5ターンの間 1.3倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("防御UP", """
        		public class EnchantDEF {
        		    private int costMP = 20;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("防御UP");
        		        self.applyBuff("防御力", 1.1, 5);
        		        System.out.println
        		        ("防御力が5ターンの間 1.1倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("防御UP+", """
        		public class EnchantDEFPlus {
        		    private int costMP = 30;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("防御UP+");
        		        self.applyBuff("防御力", 1.2, 5);
        		        System.out.println
        		        ("防御力が5ターンの間 1.2倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("防御UP++", """
        		public class EnchantDEFPlus2 {
        		    private int costMP = 40;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("防御UP++");
        		        self.applyBuff("防御力", 1.3, 5);
        		        System.out.println
        		        ("防御力が5ターンの間 1.3倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("威力UP", """
        		public class EnchantPOW {
        		    private int costMP = 5;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("威力UP");
        		        self.applyBuff("攻撃力", 1.1, 1);
        		        System.out.println
        		        ("魔法の威力が1ターンの間 1.1倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("威力UP+", """
        		public class EnchantPOWPlus {
        		    private int costMP = 11;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("威力UP+");
        		        self.applyBuff("攻撃力", 1.2, 1);
        		        System.out.println
        		        ("魔法の威力が1ターンの間 1.2倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("威力UP++", """
        		public class EnchantPOWPlus2 {
        		    private int costMP = 17;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("威力UP++");
        		        self.applyBuff("攻撃力", 1.3, 1);
        		        System.out.println
        		        ("魔法の威力が1ターンの間 1.3倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");

        		        MAGIC_CODE_MAP.put("状態異常確率UP", """
        		public class AbnormalityBoost {
        		    private int costMP = 35;

        		    public void castEnchant(MagicTarget self) {
        		        System.out.println("状態異常確率UP");
        		        self.applyBuff("状態異常確率アップ", 1.3, 1);
        		        System.out.println
        		        ("状態異常の成功率が1ターンの間 1.3倍 されました！");
        		    }
        		    public int getCostMP(){
        		        return costMP;
        		    }
        		}
        		""");
        		
        		        MAGIC_CODE_MAP.put("攻撃DOWN", """
        		        		public class ATKDOWN {
        		        		    private int costMP = 10;
        		        		    
        		        		    public void castEnchant(MagicTarget target) {
        		        		        System.out.println("攻撃力DOWN");
        		        		        
        		        		        //相手の攻撃力を10％下げる
        		        		        target.applyBuff
        		        		        ("攻撃力ダウン", 0.9, 5); // 5T持続
        		        		        System.out.println
        		        		        ("相手の攻撃力が5ターンの間10％下がりました！");
        		        		    }
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("火耐性DOWN", """
        		        		public class FireResistDOWN {
        		        		    private int costMP = 20;
        		        		    
        		        		    public void castEnchant(MagicTarget target) {
        		        		        System.out.println("火耐性DOWN");
        		        		        
        		        		        //相手の火耐性を下げる
        		        		        target.applyBuff("火耐性ダウン", 1.1, 5); // 5T持続
        		        		        System.out.println
        		        		        ("相手の火耐性が5ターンの間10％下がりました！");
        		        		    }
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("雷耐性DOWN", """
        		        		public class ThunderResistDOWN {
        		        		    private int costMP = 20;
        		        		    
        		        		    public void castEnchant(MagicTarget target) {
        		        		        System.out.println("雷耐性DOWN");
        		        		        
        		        		        //相手の雷耐性を下げる
        		        		        target.applyBuff("雷耐性ダウン", 1.1, 5); // 5T持続
        		        		        System.out.println
        		        		        ("相手の雷耐性が5ターンの間10％下がりました！");
        		        		    }
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("氷耐性DOWN", """
        		        		public class IceResistDOWN {
        		        		    private int costMP = 20;
        		        		    
        		        		    public void castEnchant(MagicTarget target) {
        		        		        System.out.println("氷耐性DOWN");
        		        		        
        		        		        //相手の氷耐性を下げる
        		        		        target.applyBuff("氷耐性ダウン", 1.1, 5); // 5T持続
        		        		        System.out.println
        		        		        ("相手の氷耐性が5ターンの間10％下がりました！");
        		        		    }
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("聖耐性DOWN", """
        		        		public class HolyResistDOWN {
        		        		    private int costMP = 20;
        		        		    
        		        		    public void castEnchant(MagicTarget target) {
        		        		        System.out.println("聖耐性DOWN");
        		        		        
        		        		        //相手の聖耐性を下げる
        		        		        target.applyBuff("聖耐性ダウン", 1.1, 5); // 5T持続
        		        		        System.out.println
        		        		        ("相手の聖耐性が5ターンの間10％下がりました！");
        		        		    }
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("HP回復", """
        		        		public class SelfHeal {
        		        		    private int costMP = 5;
        		        		    private int healAmount = 20;
        		        		    
        		        		    public void cast(MagicTarget self) {
        		        		        self.healHP(healAmount);
        		        		        System.out.println("自分のHPを20回復しました！");
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("HP回復+", """
        		        		public class SelfHealPlus {
        		        		    private int costMP = 15;
        		        		    private int healAmount = 70;
        		        		    
        		        		    public void cast(MagicTarget self) {
        		        		        self.healHP(healAmount);
        		        		        System.out.println("自分のHPを70回復しました！");
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("HP回復++", """
        		        		public class SelfHealPlus2 {
        		        		    private int costMP = 30;
        		        		    private int healAmount = 150;
        		        		    
        		        		    public void cast(MagicTarget self) {
        		        		        self.healHP(healAmount);
        		        		        System.out.println("自分のHPを150回復しました！");
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("状態異常回復", """
        		        		public class CureStatusEffects {
        		        		    private int costMP = 5;
        		        		    
        		        		    public void cast(MagicTarget self) {
        		        		        List<String> effects = self.getStatusEffects();
        		        		    
        		        		        if (effects.isEmpty()) {
        		        		            System.out.printf("%s にかかっている状態異常は
        		        		        		     ありません。", self.getName());
        		        		            return;
        		        		        }
        		        		        
        		        		        System.out.printf("%s の状態異常 [%s] をすべて
        		        		        		 解除しました！", 
        		        		        		 self.getName(), String.join
        		        		        		 ("・", effects));
        		        		        effects.clear(); // 状態異常をすべて解除
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("バーサク", """
        		        		public class Berserk {
        		        		    private int costMP = 30;
        		        		    
        		        		    public void cast(MagicTarget self) {
        		        		        System.out.printf("%s はバーサクを発動！", 
        		        		        self.getName());
        		        		    
        		        		        // 攻撃力を1.5倍に
        		        		        self.applyBuff("攻撃力", 1.5, 5);
        		        		        System.out.println
        		        		        ("攻撃力が5ターンの間1.5倍されました！");
        		        		        
        		        		        // 防御力を低下（0.8倍、5ターン）
        		        		        self.applyBuff("防御力", 0.8, 5);
        		        		        System.out.println
        		        		        ("防御力が5ターンの間0.8倍に低下しました");
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("フォトレス", """
        		        		public class Fortress {
        		        		    private int costMP = 30;
        		        		    
        		        		    public void cast(MagicTarget self) {
        		        		        System.out.printf("%s はフォトレスを発動！", 
        		        		        self.getName());
        		        		    
        		        		        // 攻撃力を0.8倍に
        		        		        self.applyBuff("攻撃力", 0.8, 5);
        		        		        System.out.println
        		        		        ("攻撃力が5ターンの間0.8倍に下がりました！");
        		        		        
        		        		        // 防御力を上昇（1.5倍、5ターン）
        		        		        self.applyBuff("防御力", 1.5, 5);
        		        		        System.out.println("防御力が5ターンの間
        		        		        		 1.5倍に上昇しました");
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("ケイオスフィルド", """
        		        		public class ChaosField {
        		        		    private int costMP = 10;

        		        		    public void castEnchant
        		        		    (MagicTarget self, List<MagicTarget> enemies) {
        		        		        System.out.println
        		        		        ("ケイオスフィルドにより状態異常にかかりやすくなった！");
        		        		        
        		        		        // 自分に状態異常確率アップ（1.4倍、3ターン）
        		        		        self.applyBuff("状態異常確率アップ", 1.4, 3);
        		        		        System.out.println
        		        		        ("状態異常の成功率が3ターンの間 1.4倍 されました！");
        		        		        
        		        		        // 敵全体に状態異常確率アップ（1.4倍、3ターン）
        		        		        for (MagicTarget enemy : enemies) {
        		        		            enemy.applyBuff("状態異常確率アップ", 1.4, 3);
        		        		            System.out.printf
        		        		            ("%sの状態異常成功率アップ！", enemy.getName());
        		        		        }
        		        		    }

        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

        		        		        MAGIC_CODE_MAP.put("サバイバー", """
        		        		public class Survivor {
        		        		    private int costMP = 20;
        		        		    
        		        		    public void cast
        		        		    (MagicTarget self, List<MagicTarget> enemies) {
        		        		        System.out.printf
        		        		        ("サバイバーの効果により辺り一帯の生物が興奮状態になった");
        		        		    
        		        		        // 攻撃力を1.5倍に
        		        		        self.applyBuff("攻撃力", 1.5, 5);
        		        		        System.out.println
        		        		        ("攻撃力が5ターンの間1.5倍されました！");
        		        		        
        		        		        // 防御力を低下（0.8倍、5ターン）
        		        		        self.applyBuff("防御力", 0.8, 5);
        		        		        System.out.println
        		        		        ("防御力が5ターンの間0.8倍に低下しました");
        		        		        
        		        		        // 敵全体の攻撃力を1.5倍に、防御力を0.8倍に（5T)
        		        		        for (MagicTarget enemy : enemies) {
        		        		            enemy.applyBuff("攻撃力", 1.5, 5);
        		        		            enemy.applyBuff("防御力", 0.8, 5);
        		        		            System.out.printf
        		        		            ("%sの攻撃力があがった！防御力が下がった！", enemy.getName());
        		        		        }
        		        		    }
        		        		    
        		        		    public int getCostMP(){
        		        		        return costMP;
        		        		    }
        		        		}
        		        		""");

    }

    /**
     * 魔法名に対応するコードを取得
     */
    public static String getCodeByName(String magicName) {
        return MAGIC_CODE_MAP.getOrDefault(
                magicName,
                "この魔法のコードは登録されていません。"
        );
    }
}
