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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class PictureBook_MonsterController implements Initializable {

    @FXML
    private AnchorPane MonsterButtonArea;

    @FXML
    private TextArea MonsterDescription;
    
    @FXML
    private TextArea MonsterCode;
    
    @FXML
    private ImageView Monster_illust;
    
    @FXML
    private Text MonsterName;


    private final Map<String, Monster> monsters = new LinkedHashMap<>();

    @FXML
    private void handleGoBack(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.goBack();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // モンスター情報を登録
        monsters.put("スライム", new Monster("スライム",
        """
        言わずと知れた最弱モンスター。しかし侮るなかれ。
        作品によっては最強主人公だったり単為生殖でポコポコ増えて数の暴力をしたり
        強力な酸性を有していたりすることもあることを…
        ーーまぁ、この世界では最弱なのだがーー
        """,
        """
		public class Slime {
		    public int hp = 20;
		    public int atk = 5;
		            
		    public void normalAttack() {
		        System.out.println("Slimeの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE, Element.THUNDER, 
		        Element.ICE,Element.HOLY);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "スライムコア";
		    }
		}
        """,
        "images/monster/Slime.png"
        ));

        monsters.put("ゴブリン", new Monster("ゴブリン", """
        ５歳児ぐらいの身長と知能を持っている比較的弱めのモンスター。
        ２、３匹程で動き、こん棒やショートボウを用い、時にはわなも使う。
        ゴブリンメイジやホフゴブリンなど派生先も多数。
        ーーグギャギャギャッ、グギャ（ニンゲン、コロス！）ーー
        """,
		"""
		public class Goblin {
		    public int hp = 30;
		    public int atk = 13;
		            
		    public void normalAttack() {
		        System.out.println("Goblinの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE, Element.THUNDER, 
		        	      Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of();
		    }
		
		    public String dropItem() {
		        return "ゴブリンの耳";
		    }
		}
        """,
        "images/monster/Goblin.png"
        ));

        monsters.put("ワイルドボア", new Monster("ワイルドボア", """
        森にすむイノシシで、でかい牙が特徴。
        焼いても野性味が抜けずおいしくないが、ジャーキーにして非常食に用いられる。
        突進力が高く、その頭突きと牙は脅威である。
        しかし、その特徴的な牙は工芸品の素材になったりもする。
        ーーワイルドジャーキー10Gーー
        """,
        """
		public class WildBoa {
		    public int hp = 36;
		    public int atk = 15;
		            
		    public void normalAttack() {
		        System.out.println("WildBoaの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE, Element.THUNDER, 
		        	      Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of();
		    }
		
		    public String dropItem() {
		        return "ワイルドボアの牙";
		    }
		}
        """,
        "images/monster/WildBoa.png"
        ));

        monsters.put("ジャイアントアンツ", new Monster("ジャイアントアンツ", """
        森や草原に暮らす巨大なアリ。鋭いきゅう覚や巨大な顎、毒を有しているが、
        恐るべきは危険を感じると仲間を呼ぶ習性である。
        見かけたら全力で音を立てないように逃げるべきだろう。
        ーー酸だ！これは酸だーー！！ーー
        """,
        """
		public class Giant-Ants {
		    public int hp = 40;
		    public int atk = 8;
		            
		    public void normalAttack() {
		        System.out.println("Giant-Antsの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE, Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "ジャイアントアンツの毒針";
		    }
		}
        """,
        "images/monster/ジャイアントアンツ.png"
        ));

        monsters.put("サイクロプス", new Monster("サイクロプス", """
        普段は極寒の地に棲む一つ目の巨人。普段は極寒の地に棲んでいるがなぜこの地に来たのだろうか…
        力が強く知能は低い。
        ーー見セテヤル、「痛恨ノ一撃」ヲーー
        """,
        """
		public class Cyclops {
		    public int hp = 200;
		    public int atk = 50;
		            
		    public void normalAttack() {
		        System.out.println("Cyclopsの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.THUNDER, Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "一つ目巨人の巨眼";
		    }
		}
        """,
        "images/monster/Cyclops.png"
        ));

        monsters.put("マーメイド", new Monster("マーメイド", """
  人の上半身を持ち魚の下半身を持つモンスター。
		上半身はどの個体も絶世の美女でその歌声は透き通るような美しさを持つ。
		しかし、それに惹かれた者たちは誰一人帰らぬ人となり果てた。
		ーーフフフ、私たちの唄に溺れなさい！ーー
        """,
        """
		public class Mermaid {
		    public int hp = 60;
		    public int atk = 14;
		            
		    public void normalAttack() {
		        System.out.println("Mermaidの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.THUNDER, Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.FIRE);
		    }
		
		    public String dropItem() {
		        return "人魚の涙";
		    }
		}
        """,
        "images/monster/Mermaid.png"
        ));
        
        monsters.put("マーマン", new Monster("マーマン", """
  二足歩行の全身がうろこに覆われ、優れた水かきをもつ半魚人。
		鋭い槍を巧みに扱い高い攻撃力を持つ。地上でも強いが海に落ちたなら命はないだろう。
		ーーゴギガ、ガガギゴォッッ！ーー
        """,
        """
		public class Merman {
		    public int hp = 70;
		    public int atk = 18;
		            
		    public void normalAttack() {
		        System.out.println("Mermanの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.THUNDER, Element.FIRE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "発達した水かき";
		    }
		}
        """,
        "images/monster/Merman.png"
        ));
        
        monsters.put("ゴーストシップクルー", new Monster("ゴーストシップクルー", """
        幽霊船に縛り付けられ成仏を許されえぬ船員。
        常にうめき声をあげているがそれは常世に縛られている苦しみか
        生者からすべてを奪う悦びの声だろうか
        ーーア、アァ、アウアゥアァァァｌ（奪エ！奪エエェェ！！）ーー
        """,
        """
		public class Ghost Ship Crew {
		    public int hp = 45;
		    public int atk = 12;
		            
		    public void normalAttack() {
		        System.out.println("Ghost Ship Crewの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.HOLY, Element.THUNDER);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.ICE);
		    }
		
		    public String dropItem() {
		        return "古びたバンダナ";
		    }
		}
        """,
        "images/monster/GhostShipCrew.png"
        ));
        
        monsters.put("ゴーストシップキャプテン", new Monster("ゴーストシップキャプテン", """
		幽霊船にしがみつく妄執の成れ果て。黄金時代の敗北者。
		夢半ばに沈んだものの諦めることを善しとせず奪うことを善しとした。
		彼の黄金時代は終わらない。たとえ当時の頂点が消え去っただけの凡夫であろうとも。
		ーー俺ノ、宝ダァァァァァｌーー
        """,
        """
		public class Ghost Ship Captain {
		    public int hp = 80;
		    public int atk = 25;
		            
		    public void normalAttack() {
		        System.out.println("Ghost Ship Captainの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.HOLY, Element.THUNDER);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.ICE, Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "古びた金貨";
		    }
		}
        """,
        "images/monster/GhostShipCaptain.png"
        ));

        monsters.put("クラーケン", new Monster("クラーケン", """
		海底に棲まう悪魔、伝説上の生き物。
		その巨体と剛腕は数多くの船と命を沈めてきた。
		ーー気ヲ付ケヨ　既ニ此処ハ彼ノ者ノ領域ナルゾーー
        """,
        """
		public class Kraken {
		    public int hp = 400;
		    public int atk = 90;
		            
		    public void normalAttack() {
		        System.out.println("Krakenの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.THUNDER, Element.FIRE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.ICE, Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "海悪魔ノ心臓";
		    }
		}
        """,
        "images/monster/Kraken.png"
        ));
        
        monsters.put("オーガ", new Monster("オーガ", """
		草原、森、洞窟などどこにでも生息し、見かけたものには片っ端から襲い掛かる。
		振るわれる二本の剛腕は、たとえ武器が粗雑なこん棒であろうとも命をたやすく消し去るだろう。
		ーーWoooo！Guaaa!!!ーー
        """,
        """
		public class Ogre {
		    public int hp = 150;
		    public int atk = 40;
		            
		    public void normalAttack() {
		        System.out.println("Ogreの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE,  Element.THUNDER, 
		        	      Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of();
		    }
		
		    public String dropItem() {
		        return "オーガの牙";
		    }
		}
        """,
        "images/monster/Ogre.png"
        ));
        
        monsters.put("ワーウルフ", new Monster("ワーウルフ", """
		人型の狼で通常の狼より素早く力も強い。また、知能が高く動きで翻弄し、獲物の喉元を爪で掻き切る。
		その強さもさることながら群れで行動することが多く、その統率力は格上すら狩ることもある。
		君がもし、きれいな月を見たいなら野外に家の外へ出るのはやめるべきだろう。
		ーー獣の夜　狩人よ　我らの狩りを知るがいいーー
        """,
        """
		public class WarWolf {
		    public int hp = 120;
		    public int atk = 36;
		            
		    public void normalAttack() {
		        System.out.println("ワーウルフの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.HOLY, Element.THUNDER);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.ICE, Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "ワーウルフの爪";
		    }
		}
        """,
        "images/monster/WarWolf.png"
        ));
        
        monsters.put("ホーンラビット", new Monster("ホーンラビット", """
		その肉は柔らかく、そして脂が乗っている。故に人気食材であり人からも獣からも狙われることが多い。
		だが気をつけろ、奴らは凶暴だ。
		額に生えた一本の角は命を穿ち、雷を呼ぶ。
		ーー私たちを狩るものよ、弱者の意地を思い知れーー
        """,
        """
		public class Horn-Rabbit {
		    public int hp = 100;
		    public int atk = 35;
		            
		    public void normalAttack() {
		        System.out.println("Horn-Rabbitの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.THUNDER);
		    }
		
		    public String dropItem() {
		        return "ホーンラビットの牙";
		    }
		}
        """,
        "images/monster/HornRabbit.png"
        ));
        
        monsters.put("サンダーバード", new Monster("サンダーバード", """
		雷を纏い空を駆ける雷鳥。
		翼から放たれし雷は大地をえぐり、草木を燃やす。
		ーー特徴的な鳴き声が聞こえたら奴が来た合図だ。ーー
        """,
        """
		public class Thunder-Bird {
		    public int hp = 135;
		    public int atk = 38;
		            
		    public void normalAttack() {
		        System.out.println("Thunder-Birdの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.ICE, Element.FIRE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.THUNDER, Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "サンダーバードの雷羽";
		    }
		}
        """,
        "images/monster/ThunderBird.png"
        ));
        
        monsters.put("レッサードラゴン", new Monster("レッサードラゴン", """
		空を駆ける術を持たぬ、劣等の竜。龍に至ることのない天の頂を知らぬ哀れな者。
		しかし、なめてかかれば地上に適応した発達した後ろ足と戦車を思わせる
		重厚な肉体に一瞬で肉塊にされてしまうだろう。劣っていても竜は竜である。
		ーー不遜であろう　我は竜の末えい　この世の頂点に属するものである　劣等種よ　地に伏せよーー
        """,
        """
		public class Lessor-Dragon {
		    public int hp = 1000;
		    public int atk = 150;
		            
		    public void normalAttack() {
		        System.out.println("Lessor-Dragonの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.THUNDER, Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.FIRE);
		    }
		
		    public String dropItem() {
		        return "竜の心臓";
		    }
		}
        """,
        "images/monster/LessorDragon.png"
        ));
        
        monsters.put("ゴーストソルジャー", new Monster("ゴーストソルジャー", """
		南方最前線の戦場にて死んだ帝国兵の怨霊。
		最早、敵も味方も分からずただ目の前の命を消し去るためだけに動く。
		せめて、これ以上彼らに仲間を殺させないために倒してやるのが手向けであろう。
		ーー彼は言っていた。戦場に花はない。だからせめて命のあだ花を咲かすのだーー
        """,
        """
		public class Ghost-Soldier {
		    public int hp = 250;
		    public int atk = 50;
		            
		    public void normalAttack() {
		        System.out.println("Ghost-Soldierの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.HOLY);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.FIRE, Element.THUNDER, 
		        	      Element.ICE);
		    }
		
		    public String dropItem() {
		        return "帝国兵のドッグタグ";
		    }
		}
        """,
        "images/monster/GhostSoldier.png"
        ));
        
        monsters.put("ゾンビ", new Monster("ゾンビ", """
		死体が生者の肉を求め理性なく徘徊するモンスター。
		冷たく腐ったその体は血と肉を求める。まるで自分にはない温かさと新鮮さを欲するように...。
		ーー食欲に果てはない。故に、死んでもむさぼるのだーー
        """,
        """
		public class Zombies {
		    public int hp = 200;
		    public int atk = 42;
		            
		    public void normalAttack() {
		        System.out.println("ゾンビの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE, Element.HOLY);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.THUNDER);
		    }
		
		    public String dropItem() {
		        return "蕩けた目玉";
		    }
		}
        """,
        "images/monster/Zombies.png"
        ));
        
        monsters.put("バーゲスト", new Monster("バーゲスト", """
		鎖と炎を操る不幸を告げる黒犬。
		攻撃力、守備力、俊敏性がどれも高く戦場で見かけることあったならば修羅場を覚悟するべきである。
		ーー黒い犬を見かけました。翌日あなたは泣いていましたーー
        """,
        """
		public class Barghest {
		    public int hp = 280;
		    public int atk = 65;
		            
		    public void normalAttack() {
		        System.out.println("Barghestの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.HOLY, Element.ICE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.NORMAL, Element.FIRE);
		    }
		
		    public String dropItem() {
		        return "黒妖犬の鎖";
		    }
		}
        """,
        "images/monster/Barghest.png"
        ));
        
        monsters.put("オニ", new Monster("オニ", """
		人を喰らい酒を飲み退治に来た者はぶん殴る暴れ者。
		たとえ異国の地なれど、暴力と快楽に誘われて海を渡って人間を喰らいにきた悪鬼である。
		ーー我は悪鬼羅刹、日の本一の鬼にして総大将「酒呑童子」配下が一人である。
		人間よ首を垂れて喰らわれるがいいーー
        """,
        """
		public class Oni {
		    public int hp = 350;
		    public int atk = 70;
		            
		    public void normalAttack() {
		        System.out.println("Oniの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.HOLY, Element.THUNDER);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.FIRE);
		    }
		
		    public String dropItem() {
		        return "鬼の首級";
		    }
		}
        """,
        "images/monster/Oni.png"
        ));
        
        monsters.put("デュラハン", new Monster("デュラハン", """
		目撃すれば近いうちに死ぬとも生前は忠義の厚い騎士だったともいわれる。
		しかし、その噂は嘘ではないだろう。守るべき主を守れず、誇りも誉れも失い、
		魔王に忠誠を誓い死してなお生き延びた彼の者を見たが最後、貴方は死ぬのだから。
		ーー命乞いは無意味である　慈悲も　聞き届ける首も無いのだからーー
        """,
        """
		public class Durahan {
		    public int hp = 1500;
		    public int atk = 175;
		            
		    public void normalAttack() {
		        System.out.println("Durahanの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.ICE, Element.FIRE);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.THUNDER, Element.NORMAL);
		    }
		
		    public String dropItem() {
		        return "騎士の首飾り";
		    }
		}
        """,
        "images/monster/Durahan.png"
        ));
        
        monsters.put("アークデーモン", new Monster("アークデーモン", """
		禍々しくも美しき美貌を持ち、人々を魅了する。だが、ひとたび化けの皮がはがれた時、
		君はこの世のものとは思えぬ光景を見るだろう。
		おぉ、神よ。彼の者に安寧を。
		ーーほぅ、我について詳しいではないか。では死を与えてやる。魂を寄越すがいいーー
        """,
        """
		public class Archdemon {
		    public int hp = 2500;
		    public int atk = 250;
		            
		    public void normalAttack() {
		        System.out.println("アークデーモンの攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(Element.FIRE, Element.THUNDER, Element.ICE, Element.HOLY);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(Element.NORMAL);
		    }
		    
		    public String dropItem() {
		        return "????????";
		    }
		}
        """,
        "images/monster/アークデーモン.png"
        ));
        
        monsters.put("????????", new Monster("????????", """
		縺?ｏ縺上??ｭ皮視縺ｮ逶ｴ謗･縺ｮ驟堺ｸ九〒縺ゅｋ縲
		縺?ｏ縺上?∽ｸ?菴薙〒蝗ｽ繧呈ｻ?⊂縺励°縺ｭ縺ｪ縺??
		縺?ｏ縺上?√∪縺輔＠縺乗が鬲斐〒縺ゅｋ縲
		ーー縺翫○ｻ縺??∬憶縺丞?縺九▲縺ｦ縺?ｋ縺ｧ縺ｯ縺ｪ縺?°縲ゅ〒縺ｯ豁ｻ繧剃ｸ弱∴縺ｦ繧?ｋ縲るｭゅｒ蟇?ｶーー
        """,
        """
		public class ?????????{
		    public int hp = ????;
		    public int atk = ???;
		            
		    public void normalAttack() {
		        System.out.println("????????の攻撃！");
		    }
		
		    public List<Element> getWeaknesses() {
		        return List.of(?????????????);
		    }
		
		    public List<Element> getResistances() {
		        return List.of(?????????);
		    }
		
		    public String dropItem() {
		        return "????????";
		    }
		}
        """,
        "images/monster/真アークデーモン.png"
        ));

        int i = 0;
        for (Monster monster : monsters.values()) {

            // 1行レイアウト
            HBox row = new HBox(10);
            row.setLayoutX(10);
            row.setLayoutY(20 + i * 110);

            // アイコン
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(monster.getIconPath())));
            icon.setFitWidth(50);
            icon.setFitHeight(50);

            // ボタン
            Button btn = new Button("");   // 名前は表示しない
            btn.setPrefSize(50, 50);
            btn.getStyleClass().add("chihaya");

            // ★ ボタンのクリック処理
            btn.setOnAction(event -> {
            	SEPlayer.play("イベント/click.mp3");
                MonsterDescription.setText(monster.getDescription());
                MonsterCode.setText(monster.getCode());

                // ★ モンスター名を Text に表示
                MonsterName.setText(monster.getName());

                // イラスト更新
                Image img = new Image(getClass().getResourceAsStream(monster.getIconPath()));
                Monster_illust.setImage(img);
            });

            // アイコンをボタンに設定
            btn.setGraphic(icon);
            btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            // 行へ追加
            row.getChildren().add(btn);
            MonsterButtonArea.getChildren().add(row);

            i++;
        }



        // 初期表示
        showMonsterDescription("スライム");
    }

    private void showMonsterDescription(String name) {
        Monster monster = monsters.get(name);
        if (monster != null) {
            MonsterDescription.setText(monster.getDescription());
            MonsterCode.setText(monster.getCode());

            // スクロール位置リセット
            Platform.runLater(() -> {
                MonsterDescription.setScrollTop(0);
                MonsterCode.setScrollTop(0);
            });
        }
    }
}