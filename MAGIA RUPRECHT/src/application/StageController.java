package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import application.UnifiedBuff.Type;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;


public class StageController {

	private static StageController instance;//ステージ変更
	
	 private StageData stageData;
	private CharacterDAO characterDAO;
	
	private String bgmPath;
	private String boss_bgmPath;
	private int StageId;

	@FXML private AnchorPane rootPane;
	@FXML private Label player_HP, Player_MP, player_Level, WeveNumber;
	@FXML private HBox state_correction;
	@FXML private Text state_correction_label;
    @FXML private HBox state_effects;
    @FXML private Text state_effects_label;
    @FXML private VBox state_correction_container;
    @FXML private VBox state_effects_container;
	
	private List<CustomMagic> customMagics;
	@FXML private GridPane MagicButtonGrid;
	@FXML private Button magicBtn1, magicBtn2, magicBtn3, magicBtn4, magicBtn5, magicBtn6;
	private List<Button> magicButtons;

	@FXML private ScrollPane ItemListPane;
	@FXML private GridPane ItemListGrid;
	private Inventory inventory;
	private List<Item> usableItems = new ArrayList<>();
	private BattleItemMenu itemMenu;

	@FXML private ImageView enemy1, enemy2, enemy3, enemy4, enemy5;
	@FXML private ProgressBar enemy1_HP, enemy2_HP, enemy3_HP, enemy4_HP, enemy5_HP;
	private List<ProgressBar> enemyHpBars;

	@FXML private Label enemy_Name1, enemy_Name2, enemy_Name3, enemy_Name4, enemy_Name5;
	private List<Label> enemyNameLabels;

	@FXML private Label enemy_status1, enemy_status2, enemy_status3, enemy_status4, enemy_status5;
	private List<Label> enemyStatusLabels;

	private List<HBox> enemyNameStatusBoxes;

	@FXML private HBox enemyHBox;
	@FXML private VBox enemyBox1, enemyBox2, enemyBox3, enemyBox4, enemyBox5;
	@FXML private HBox enemy_NameStatus1, enemy_NameStatus2, enemy_NameStatus3, enemy_NameStatus4, enemy_NameStatus5;

	@FXML private ScrollPane battleLogScroll;
	@FXML private VBox battleLogBox;
	
	@FXML private Button withdrawal;

	private Character player;
	private List<Character> enemies;
	private final List<Enemy> defeatedEnemiesAllWaves = new ArrayList<>();
	private final List<Enemy> defeatedEnemiesCurrentWave = new ArrayList<>();
	private List<Enemy> currentWaveRawEnemies = new ArrayList<>();

	private final List<DropResult> stageDropResults = new ArrayList<>();

	private EnemyDisplayController enemyDisplay;

	private int currentWave = 1;
	protected int maxWave = 5;

	private CustomMagic selectedMagic = null;
	private Item selectedItem = null;
	private Character selectedEnemy = null;

	boolean playerActing = false;
	private int remainingAnimations = 0;

	private enum TurnPhase { PLAYER_TURN, ENEMY_TURN, END }
    private TurnPhase currentPhase = TurnPhase.PLAYER_TURN;
    private boolean preparingNextWave = false;
    
    private List<ImageView> enemyImageViews;
    
    public void init(StageData data) {
    	System.out.println("init() 開始");

        this.stageData = data;
        this.bgmPath = data.bgmPath;
        this.boss_bgmPath = data.boss_bgmPath;
        this.setStageId(data.stageId);
        StageId = getStageId();
        
        if (enemies == null) {
            enemies = new ArrayList<>();
        }
        enemies.clear();


        // ここから差分を使う処理
        BGMPlayer.play(bgmPath);

        currentWave = 1;
        WeveNumber.setText(String.valueOf(currentWave));

        currentWaveRawEnemies = WaveEnemySelector.selectEnemies(StageId);
        enemyDisplay.showEnemies(currentWaveRawEnemies);

        enemies.clear();
        for (int i = 0; i < currentWaveRawEnemies.size(); i++) {

            Enemy e = currentWaveRawEnemies.get(i);
            Character enemyChar = e.toCharacter(i);

            // ============================
            // ★ ここから AI 統合処理 ★
            // ============================

            try {
                // 1. JSON を読み込む
                EnemyAIData AIdata = new EnemyAIDataLoader().load(e.getId());

                // 2. SkillRegistry にスキルメタ情報を登録
                for (var s : AIdata.skills) {
                    SkillRegistry.register(s);
                }

                // 3. AI を生成して敵にセット
                DataDrivenEnemyAI ai = new DataDrivenEnemyAI(AIdata);
                enemyChar.setAI(ai);

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("敵AIの読み込みに失敗しました: " + e.getId());
            }

            // ============================
            // ★ ここまで AI 統合処理 ★
            // ============================

            enemies.add(enemyChar);
            appendLog(enemyChar.getName() + "が現れた！");

            enemyChar.setVisualNode(enemyImageViews.get(i));
            enemyChar.setDisplaySlotId(i);

            enemyNameLabels.get(i).setText(enemyChar.getName());
            enemyNameLabels.get(i).setVisible(true);
            enemyNameStatusBoxes.get(i).setVisible(true);
            enemyStatusLabels.get(i).setText("");
            enemyStatusLabels.get(i).setVisible(true);
        }


        for (int i = 0; i < enemyImageViews.size(); i++) {
            ImageView view = enemyImageViews.get(i);
            if (i < currentWaveRawEnemies.size()) {
                view.setVisible(true);
                view.setUserData(i);
                view.setOnMouseClicked(this::onEnemyClicked);
            } else {
                view.setVisible(false);
                view.setOnMouseClicked(null);
            }
        }

        try {
            Character loaded = characterDAO.getCharacterStatus();
            player = (loaded != null) ? loaded : new Character("ループ", 100, 100, 100, 10, 1, true);
            updatePlayerStatsUI();
            player.setLogConsumer(this::appendLog);
        } catch (SQLException e) {
            e.printStackTrace();
            appendLog("キャラクター初期化に失敗しました");
            player = new Character("ループ", 100, 100, 100, 10, 1, true);
            updatePlayerStatsUI();
            player.setLogConsumer(this::appendLog);
        }

        loadCustomMagics();
        bindMagicButtons();

        try {
            Connection conn = DBManager.getConnection();
            InventoryDAO inventoryDAO = new InventoryDAO(conn);
            ConsumableDAO consumableDAO = new ConsumableDAO(conn);

            List<Item> allItems = consumableDAO.getAllConsumables();
            allItems.sort(Comparator.comparingInt(Item::getId));

            inventory = new Inventory(inventoryDAO, allItems);
            usableItems = inventory.getUsableItems();

            itemMenu = new BattleItemMenu(ItemListGrid, inventory);
            itemMenu.setOnItemSelected(this::selectItem);

        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("アイテム初期化失敗: " + e.getMessage());

        }
        
        enableAllActions();
    }



    public void initialize() {

        // 内部状態リセット
        playerActing = false;
        remainingAnimations = 0;
        currentPhase = TurnPhase.PLAYER_TURN;
        preparingNextWave = false;
        state_correction.setFocusTraversable(false);
        state_effects.setFocusTraversable(false);

        selectedMagic = null;
        selectedItem  = null;
        selectedEnemy = null;

        enemies = new ArrayList<>();
        currentWaveRawEnemies = new ArrayList<>();
        defeatedEnemiesAllWaves.clear();
        defeatedEnemiesCurrentWave.clear();
        stageDropResults.clear();

        BattleAnimationManager.cancelAllAnimations();
        BattleAnimationManager.reset();
        
     // ★ CharacterDAO 初期化（ここに戻す）
        try {
            Connection conn = DBManager.getConnection();
            characterDAO = new CharacterDAO(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            appendLog("キャラクターDAOの初期化に失敗しました");
        }


        // UI ノード束ね
        enemyImageViews = List.of(enemy1, enemy2, enemy3, enemy4, enemy5);
        enemyHpBars     = List.of(enemy1_HP, enemy2_HP, enemy3_HP, enemy4_HP, enemy5_HP);
        enemyNameLabels = List.of(enemy_Name1, enemy_Name2, enemy_Name3, enemy_Name4, enemy_Name5);
        enemyNameStatusBoxes = List.of(
            enemy_NameStatus1, enemy_NameStatus2, enemy_NameStatus3, enemy_NameStatus4, enemy_NameStatus5
        );
        enemyStatusLabels = List.of(
            enemy_status1, enemy_status2, enemy_status3, enemy_status4, enemy_status5
        );

        enemyDisplay = new EnemyDisplayController();
        enemyDisplay.injectNodes(
            enemyHBox,
            List.of(enemyBox1, enemyBox2, enemyBox3, enemyBox4, enemyBox5),
            enemyImageViews,
            enemyHpBars
        );

        magicButtons = List.of(magicBtn1, magicBtn2, magicBtn3, magicBtn4, magicBtn5, magicBtn6);

        MagicButtonGrid.setVisible(true);
        MagicButtonGrid.setManaged(true);

        ItemListPane.setVisible(false);
        ItemListPane.setManaged(false);

        battleLogScroll.vvalueProperty().bind(battleLogBox.heightProperty());
    }


	public void showMyCustomize(MyCustomizeDAO.MyCustomize myCustomize, List<CustomizeDAO.Customize> allCustomizes) {
        if (myCustomize == null) {
            return;
        }

        int[] ids = {
            myCustomize.my_magic1, myCustomize.my_magic2, myCustomize.my_magic3,
            myCustomize.my_magic4, myCustomize.my_magic5, myCustomize.my_magic6
        };

        for (int i = 0; i < ids.length; i++) {
            final int index = i; // ラムダ式用にfinal化
            CustomizeDAO.Customize c = allCustomizes.stream()
                .filter(cz -> cz.customizeId == ids[index])
                .findFirst()
                .orElse(null);
        }
    }
    
    private void loadCustomMagics() {
    	int customizeNumber = 1;
    	try {
			customizeNumber = characterDAO.getMyCustomizeNumber();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        MyCustomizeDAO.MyCustomize my = MyCustomizeDAO.get(customizeNumber);
        
        List<CustomizeDAO.Customize> all = CustomizeDAO.getAll();
        customMagics = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            int cid = switch(i) {
                case 0 -> my.my_magic1;
                case 1 -> my.my_magic2;
                case 2 -> my.my_magic3;
                case 3 -> my.my_magic4;
                case 4 -> my.my_magic5;
                default -> my.my_magic6;
            };
            CustomizeDAO.Customize cz = all.stream()
                .filter(x -> x.customizeId == cid)
                .findFirst()
                .orElse(null);
            if (cz != null) {
                CustomMagic cm = new CustomMagic(cz.name);

                cz.componentIds.forEach(pid -> {
                    PrimitiveMagic part = PrimitiveMagicDAO.getById(pid);
                    cm.addComponent(part);
                });

                customMagics.add(cm);
            } else {
                customMagics.add(null);
            }
        }
    }

    private void bindMagicButtons() {
        for (int i = 0; i < magicButtons.size(); i++) {
            final int idx = i;
            magicButtons.get(i).setOnAction(evt -> {
                // 連打防止：最初に即時ガード
                if (playerActing) return;
                playerActing = true;

                // まずUI全体をロック（単体選択モードでも他ボタンは押せない）
                disableAllActions();

                // 実行
                onMagicSelected(idx);
            });

            updateMagicButton(i);
        }
    }


    private void updateMagicButton(int i) {
        CustomMagic cm = customMagics.get(i);
        Button btn = magicButtons.get(i);

        if (cm == null) {
            btn.setText("未設定");
            btn.setDisable(true);
            return;
        }

        // 表示
        btn.setText(cm.getName() + " (" + cm.getTotalMP() + "MP)");

        // 有効条件：
        // - プレイヤーターンである
        // - 行動中ではない
        // - MPが足りている
        boolean enable = (currentPhase == TurnPhase.PLAYER_TURN)
            && !playerActing
            && (player.getMP() >= cm.getTotalMP());

        btn.setDisable(!enable);
    }

    
    
    //敵の対象選択
 // 敵の対象選択／即時発動
    private void onMagicSelected(int slotIndex) {
        CustomMagic cm = customMagics.get(slotIndex);

        // 念のため
        playerActing = false;

        if (cm == null) {
            playerActing = false;
//            enableAllActions();
            return;
        }

        selectedMagic = cm;

        // ============================
        // ① AoE（全体攻撃）
        // ============================
        if (cm.isAoE()) {
            SEPlayer.play("戦闘SE/魔法.mp3");

            MagicExecutor.cast(cm, player, enemies, enemies, () -> {
                updatePlayerStatsUI();
                player.updateBuffsEachTurn();
                enemies.forEach(Character::updateBuffsEachTurn);

                for (int i = 0; i < magicButtons.size(); i++) {
                    updateMagicButton(i);
                }

                selectedMagic = null;
                selectedEnemy = null;

                if (remainingAnimations == 0) {
                    checkBattleEndAsync(battleEnded -> {
                        if (!battleEnded) {
                            playerActing = false;
                            nextTurnAsync();   // ★ enableAllActions() は呼ばない
                        }
                    });
                }
            });
            return;
        }

        // ============================
        // ② 単体ターゲット選択
        // ============================
        if (cm.requiresTargetSelection()) {
            appendLog("対象を選択してください");

            playerActing = false;
            selectedEnemy = null;

            return;
        }

        // ============================
        // ③ 自己対象など即時系
        // ============================
        remainingAnimations++;

        MagicExecutor.cast(cm, player, List.of(player), enemies, () -> {
            updatePlayerStatsUI();
            player.updateBuffsEachTurn();
            enemies.forEach(Character::updateBuffsEachTurn);

            for (int i = 0; i < magicButtons.size(); i++) {
                updateMagicButton(i);
            }

            selectedMagic = null;
            selectedEnemy = null;

            remainingAnimations--;

            if (remainingAnimations == 0) {
                checkBattleEndAsync(battleEnded -> {
                    if (!battleEnded) {
                        playerActing = false;
                        nextTurnAsync();   // ★ enableAllActions() は呼ばない
                    }
                });
            }
        });
    }
    
    /**
     * 敵のUIを単純に更新するだけのメソッド。
     * 演出は呼ばず、HPバーやラベルの状態を最新化する。
     */
    public void updateEnemyUI(Character enemy) {
        int idx = enemies.indexOf(enemy);

        // ★ 敵がリストに存在しない（倒された後など）
        if (idx < 0 || idx >= enemies.size()) {
            return;
        }

        ProgressBar bar = enemyDisplay.getHPBarList().get(idx);
        ImageView image = enemyImageViews.get(idx);
        Label status = enemyStatusLabels.get(idx);
        Label nameLabel = enemyNameLabels.get(idx);
        HBox enemyNameStatus = enemyNameStatusBoxes.get(idx);

        if (enemy.getHP() > 0) {
            status.setText(enemy.getStatusEffectSummary());
            bar.setProgress((double) enemy.getHP() / enemy.getMaxHP());
            bar.setVisible(true);
            image.setVisible(true);
            image.setOnMouseClicked(this::onEnemyClicked);
            nameLabel.setText(enemy.getName());
            enemyNameStatus.setVisible(true);

        } else {
            bar.setProgress(0);
            bar.setVisible(false);
            image.setVisible(false);
            image.setOnMouseClicked(null);
            nameLabel.setText(enemy.getName());
            enemyNameStatus.setVisible(false);

            Enemy raw = currentWaveRawEnemies.get(idx);
            if (!defeatedEnemiesCurrentWave.contains(raw)) {
                defeatedEnemiesCurrentWave.add(raw);
                onEnemyDefeated(raw);
            }
        }
    }
    

    public void updateEnemyUIWithDamage(Character enemy) {
        System.out.println(">>> updateEnemyUIWithDamage entered for " + enemy.getName());
        int idx = enemy.getDisplaySlotId(); // ← 直接参照
        if (idx < 0 || idx >= enemies.size()) return;

        ProgressBar bar = enemyDisplay.getHPBarList().get(idx);
        ImageView image = enemyImageViews.get(idx);
        Label status = enemyStatusLabels.get(idx);
        Label nameLabel = enemyNameLabels.get(idx);
        HBox enemyNameStatus = enemyNameStatusBoxes.get(idx);

        int afterHP = enemy.getHP();
        Enemy raw = currentWaveRawEnemies.get(idx);

        remainingAnimations++;
        System.out.println(">>> ++ for " + enemy.getName() + ", remaining=" + remainingAnimations);
        
        System.out.println("playDamageAnimation呼び出し for " + enemy.getName());
        BattleAnimationManager.playDamageAnimation(
            enemy, bar, nameLabel, status, enemyNameStatus,
            () -> {
                remainingAnimations--;
                System.out.println(">>> Runnable invoked for " + enemy.getName() + ", remaining=" + remainingAnimations);

                if (afterHP > 0) {
                	status.setText(enemy.getStatusEffectSummary());
                    bar.setProgress((double) afterHP / enemy.getMaxHP());
                    bar.setVisible(true);
                    image.setVisible(true);
                    image.setOnMouseClicked(this::onEnemyClicked);
                    nameLabel.setText(enemy.getName());
                    enemyNameStatus.setVisible(true);
                } else {
                    if (!defeatedEnemiesCurrentWave.contains(raw)) {
                        defeatedEnemiesCurrentWave.add(raw);
                        onEnemyDefeated(raw);
                        grantExpForEnemy(raw);
                    }
                    bar.setProgress(0);
                    bar.setVisible(false);
                    image.setVisible(false);
                    image.setOnMouseClicked(null);
                    enemyNameStatus.setVisible(false);
                }
                
                
                if (remainingAnimations == 0) {
                    System.out.println(">>> updateEnemyUIWithDamage 呼び出し");
                    checkBattleEndAsync(battleEnded -> {
                        if (!battleEnded) {
                            playerActing = false;
//                            enableAllActions();
                            nextTurnAsync();
                        }
                    });
                }
            }
        );
    }
    
    boolean effectif() {return true;}
    
    private void handleSleepUIEnd(Character enemy) {
        if(enemy.canAct() && enemy.isSleepUI()) {
        	enemy.endSleepDamageBonusThisTurn();
            enemy.setSleepUI(false);
            updateEnemyUI(enemy);
        }
    }


    
    //敵の状態異常表示
    public void updateEnemyStatusUI(Character enemy, int slotId) {
        Label statusLabel = switch (slotId) {
            case 0 -> enemy_status1;
            case 1 -> enemy_status2;
            case 2 -> enemy_status3;
            case 3 -> enemy_status4;
            case 4 -> enemy_status5;
            default -> null;
        };

        if (statusLabel != null) {
            // ★ 睡眠も含めて Character 側でまとめて生成
            statusLabel.setText(enemy.getStatusEffectSummary());
        }
    }

    
    //状態異常ラベルを空にする
    public void clearEnemyStatusUI(int slotId) {
        switch (slotId) {
            case 0 -> enemy_status1.setText("");
            case 1 -> enemy_status2.setText("");
            case 2 -> enemy_status3.setText("");
            case 3 -> enemy_status4.setText("");
            case 4 -> enemy_status5.setText("");
        }
    }


    //バトルログにメッセージを追加
    public void appendLog(String text) {
        Label logEntry = new Label(text);
        logEntry.setWrapText(true);
        battleLogBox.getChildren().add(logEntry);
    }
    
    //プレイヤーの操作を無効化
    public void disableAllActions() {

        // 魔法ボタンを無効化
        for (Button btn : magicButtons) {
            btn.setDisable(true);
        }

        // アイテムメニューを無効化（★追加）
        if (itemMenu != null) {
            itemMenu.setDisabled(true);
        }
        
        //撤退ボタンを無効化
        withdrawal.setDisable(true);
    }
    
    
    //ターン制御
    
    public void incrementAnimations() {
        remainingAnimations++;
        System.out.println(">>> ++ " + ", remaining=" + remainingAnimations);

    }


    void nextTurnAsync() {
        System.out.println(">>> nextTurnAsync 呼び出し");

        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> {
            // 演出が残っている間は待機
            if (remainingAnimations > 0) {
                nextTurnAsync(); // 演出が終わるまで再度待機
                return;
            }

            checkBattleEndAsync(battleEnded -> {
                if (battleEnded) return;

                switch (currentPhase) {
                case PLAYER_TURN -> {
                    disableAllActions();
                    currentPhase = TurnPhase.ENEMY_TURN;

                    // ★ このプレイヤーターンでの睡眠1.5倍はここで終了
                    for (Character enemy : enemies) {
                        handleSleepUIEnd(enemy);
                    }
                    updatePlayerStatsUI();
                    remainingAnimations = 0;
                    executeEnemyTurn(); // 敵ターン開始

                    }
                    case ENEMY_TURN -> {
                        currentPhase = TurnPhase.PLAYER_TURN;
                        playerActing = false;
                        
                        updatePlayerStatsUI();
                        if(player.isSleeping()) {
                        	appendLog("ループは眠っていて動けない!");
                        	remainingAnimations = 0;
                        	executeEnemyTurn(); // 敵ターン開始
                        }else {
                        	enableAllActions();          // ★ 魔法ボタン復活
	                        restoreEnemyClickHandlers(); // ★ 敵クリック復活はここだけ
                        }
                    }
                    case END -> {
                        // 終了時は何もしない
                    }
                }
            });
        });
        pause.play();
    }


    
    private void executeEnemyTurn() {
        executeEnemyTurnRecursive(0);
    }

    private void executeEnemyTurnRecursive(int index) {

        // 全敵の行動が終わった
        if (index >= enemies.size()) {

            // ★ バフのターン減少
            player.updateBuffsEachTurn();
            enemies.forEach(Character::updateBuffsEachTurn);

            // ★ 状態異常のターン減少
            player.updateStatusTurnsEachTurn();
            enemies.forEach(Character::updateStatusTurnsEachTurn);

            checkBattleEndAsync(battleEnded -> {
                if (!battleEnded) {
                    nextTurnAsync();
                }
            });
            return;
        }

        Character e = enemies.get(index);

        // 死亡している敵はスキップ
        if (e.getHP() <= 0) {
            executeEnemyTurnRecursive(index + 1);
            return;
        }

        // ★ ターン開始時の状態異常処理
        e.applyStatusEffectsAtTurnStart();

        // UI 更新（睡眠などの表示）
        updateEnemyUI(e);

        // ★ 行動不能（睡眠など）
        if (!e.canAct()) {
            appendLog(e.getName() + " は眠っていて動けなかった！");
            e.clearSkipTurn(); // 次ターンは行動可能
            executeEnemyTurnRecursive(index + 1);
            return;
        }

        // ★ AI によるスキル選択
        int skillId = e.getAI().decideSkillId(e, player);

        appendLog(e.getName() + " は " + SkillRegistry.getName(skillId) + " を使った！");
        EnemySkillExecutor.execute(skillId, e, player);
        appendLog("ループは" + EnemySkillExecutor.getdmg() + "ダメージを受けた！");

        // ★ アニメーション（攻撃モーションを流用）
        BattleAnimationManager.playEnemyAttack(
                e.getVisualNode(),
                rootPane,
                e.getAttackType(),
                () -> {

                    // UI 更新
                    updatePlayerStatsUI();
                    updateEnemyUI(e);
                    
                    // ★ プレイヤー死亡判定
                    if (player.getHP() <= 0) {
                        BGMPlayer.playOnce("イベント/maou_game_jingle10.mp3");
                        appendLog("ゲームオーバー…");
                        disableAllActions();
                        currentPhase = TurnPhase.END;

                     // ★ レイアウト確定後に実行する
                        Platform.runLater(() -> {
                            BattleAnimationManager.showGameOver((AnchorPane) battleLogBox.getScene().getRoot());
                        });


                        Scene scene = battleLogBox.getScene();
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(event -> {
                            appendLog("画面をクリックしてメニューに戻ります");
                            resetBattle();
                            scene.setOnMouseClicked(e2 -> SceneManager.changeScene("Menu.fxml"));
                        });
                        pause.play();

                        return;
                    }

                    // 次の敵へ
                    executeEnemyTurnRecursive(index + 1);
                }
        );
    }

    
    /// ウェーブ更新処理
    void prepareNextWave(int waveNumber) {
    	
    	//プレイヤーのバフ・状態異常のターン経過
        player.updateBuffsEachTurn();
        player.updateStatusTurnsEachTurn();
        updatePlayerStatsUI(); // UI反映

    	
        // 旧アニメ停止
        for (ImageView view : enemyImageViews) {
            BattleAnimationManager.cancelAnimationsFor(view);
        }

        // ログリセット
        battleLogBox.getChildren().clear();

        // プレイヤーのMP・状態異常リセット
        player.setMP(player.getMP() + (player.getMaxMP() / 4));
        Player_MP.setText("" + player.getMP());
        player.clearStatusEffects();

        // Wave表示更新
        if(waveNumber == 41) {
        	WeveNumber.setText("" + (waveNumber - 1));
        }else {
        	WeveNumber.setText("" + waveNumber);
        }

        // ノード初期化
        for (ImageView view : enemyImageViews) {
            view.setScaleX(1.0);
            view.setScaleY(1.0);
            view.setOpacity(1.0);
            view.setTranslateX(0);
            view.setTranslateY(0);
            view.setEffect(null);
            view.setOnMouseClicked(null);
            view.setVisible(false);
        }
        for (ProgressBar bar : enemyHpBars) {
            bar.setVisible(false);
            bar.setProgress(1.0);
        }
        for (HBox box : enemyNameStatusBoxes) {
            box.setVisible(false);
        }
        for (Label lbl : enemyNameLabels) {
            lbl.setText("");
            lbl.setVisible(false);
        }
        for (Label lbl : enemyStatusLabels) {
            lbl.setText("");
            lbl.setVisible(false);
        }

        // 敵生成（ステージ番号は常に1）
        if (waveNumber < maxWave) {
            currentWaveRawEnemies = WaveEnemySelector.selectEnemies(getStageId());//ステージ変更
        } else if (waveNumber == maxWave) {
        	BGMPlayer.play(boss_bgmPath);
            currentWaveRawEnemies = List.of(WaveEnemySelector.getBossByStage(getStageId()));
        } else {
            currentWaveRawEnemies = List.of();
        }
        
        currentWaveRawEnemies = StageEXWaveEnemyCreate(waveNumber);

        enemyDisplay.showEnemies(currentWaveRawEnemies);

        // 敵リスト構築
        enemies.clear();
        for (int i = 0; i < currentWaveRawEnemies.size(); i++) {

            Enemy e = currentWaveRawEnemies.get(i);
            Character enemyChar = e.toCharacter(i);

            // ============================
            // ★ AI 統合処理（追加部分）★
            // ============================
            try {
                EnemyAIData data = new EnemyAIDataLoader().load(e.getId());

                // SkillRegistry にスキルメタ情報を登録
                for (var s : data.skills) {
                    SkillRegistry.register(s);
                }

                // AI を生成してセット
                DataDrivenEnemyAI ai = new DataDrivenEnemyAI(data);
                enemyChar.setAI(ai);

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("敵AIの読み込みに失敗しました: " + e.getId());
            }
            // ============================
            // ★ ここまで AI 統合処理 ★
            // ============================

            ImageView view = enemyImageViews.get(i);
            view.setUserData(i);
            view.setOnMouseClicked(this::onEnemyClicked);
            view.setOpacity(1.0);
            view.setScaleX(1.0);
            view.setScaleY(1.0);
            view.setVisible(true);

            enemyChar.setVisualNode(view);
            enemies.add(enemyChar);

            enemyNameLabels.get(i).setText(enemyChar.getName());
            enemyNameLabels.get(i).setVisible(true);

            enemyHpBars.get(i).setVisible(true);
            enemyHpBars.get(i).setProgress((double) enemyChar.getHP() / enemyChar.getMaxHP());

            enemyNameStatusBoxes.get(i).setVisible(true);
            enemyStatusLabels.get(i).setText(" " + enemyChar.getStatusEffectSummary());
            enemyStatusLabels.get(i).setVisible(true);
        }

        // 余りスロット非表示
        for (int i = currentWaveRawEnemies.size(); i < enemyImageViews.size(); i++) {
            enemyImageViews.get(i).setVisible(false);
            enemyImageViews.get(i).setOnMouseClicked(null);
            enemyHpBars.get(i).setVisible(false);
            enemyNameLabels.get(i).setVisible(false);
            enemyNameStatusBoxes.get(i).setVisible(false);
            enemyStatusLabels.get(i).setVisible(false);
        }

        // UI更新
        updateAllEnemyUI();

        // 次ウェーブ開始時は必ずプレイヤーターン
        currentPhase = TurnPhase.PLAYER_TURN;
        playerActing = false; // ← 行動解除
        enableAllActions();
    }
    
    List<Enemy> StageEXWaveEnemyCreate(int i) { return currentWaveRawEnemies;}
    
    /**
     * バトル終了判定と次ウェーブ／ゲームクリア処理を一元管理する。
     * - 撃破演出のコールバックでは呼ばず、ここだけで進行を制御する。
     */
    void checkBattleEndAsync(Consumer<Boolean> callback) {
        boolean allEnemiesDefeated = enemies.stream().allMatch(e -> e.getHP() <= 0);

        if (remainingAnimations > 0) {
            callback.accept(false);
            return;
        }

        if (allEnemiesDefeated && !preparingNextWave) {
            preparingNextWave = true; // ← 二重呼び出し防止

            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                if (currentWave < maxWave) {
                    defeatedEnemiesAllWaves.addAll(defeatedEnemiesCurrentWave);
                    defeatedEnemiesCurrentWave.clear();

                    currentWave++;
                    prepareNextWave(currentWave);
                } else {
                    // 最終ウェーブクリア処理
                	defeatedEnemiesAllWaves.addAll(defeatedEnemiesCurrentWave);
                    defeatedEnemiesCurrentWave.clear();
                    BGMPlayer.playOnce("イベント/maou_game_jingle02.mp3");

                    appendLog("全ウェーブクリア！ゲームクリア！");
                    BattleAnimationManager.showStageClear(
                        (AnchorPane) battleLogBox.getScene().getRoot(),
                        () -> {
                            disableAllActions();
                            currentPhase = TurnPhase.END;
                            enemies.clear();
                            grantStageDrops();
                            appendLog("画面をクリックしてリザルトへ進みます");
                            resetBattle();
                            battleLogBox.getScene().setOnMouseClicked(
                                e2 -> SceneManager.changeScene("Result.fxml")
                            );
                        }
                    );
                }
                preparingNextWave = false; // ← 次ウェーブ準備完了後に解除
                callback.accept(true);
            });
            pause.play();
        } else {
            callback.accept(false);
        }
    }
    
    private void restoreEnemyClickHandlers() {
        for (int i = 0; i < enemyImageViews.size(); i++) {
            ImageView view = enemyImageViews.get(i);
            boolean alive = (i < enemies.size()) && enemies.get(i).getHP() > 0;

            if (alive) {
                view.setOnMouseClicked(this::onEnemyClicked);
            } else {
                view.setOnMouseClicked(null);
            }
        }
    }
    
    @FXML
    private void withDrawal() {
  	  BGMPlayer.playOnce("イベント/maou_game_jingle10.mp3");
        appendLog("ゲームオーバー…");
        disableAllActions();
        currentPhase = TurnPhase.END;

        // 演出管理クラスでゲームオーバー演出を呼び出し
        Platform.runLater(() -> {
            BattleAnimationManager.showGameOver((AnchorPane) battleLogBox.getScene().getRoot());
        });


        // クリックでメニューに戻る処理
        Scene scene = battleLogBox.getScene();
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
      	  disableAllActions();
            currentPhase = TurnPhase.END;
            enemies.clear();
            grantStageDrops();
            appendLog("画面をクリックしてリザルトへ進みます");
            resetBattle();
            battleLogBox.getScene().setOnMouseClicked(
                e2 -> SceneManager.changeScene("Result_Interim.fxml")
            );
        });
        pause.play();

        return;
    }


 // DropResult の簡易クラス（名前と数量だけ）
 	public static class DropResult {
 	    public final String name;
 	    public final int qty;

 	    public DropResult(String name, int qty) {
 	        this.name = name;
 	        this.qty = qty;
 	    }
 	}
 	
 	public List<DropResult> getStageDropResults() {
		    return stageDropResults;
		}
    
    private void grantStageDrops() {
        defeatedEnemiesAllWaves.addAll(defeatedEnemiesCurrentWave);
        defeatedEnemiesCurrentWave.clear();

        Map<Integer, Integer> dropCounts = new HashMap<>();
        for (Enemy e : defeatedEnemiesAllWaves) {
            double rate = Math.max(0.0, Math.min(1.0, e.dropRate));
            if (e.itemId > 0 && Math.random() < rate) {
                dropCounts.merge(e.itemId, 1, Integer::sum);
            }
        }

        try {
            ItemDAO itemDAO = new ItemDAO(DBManager.getConnection());
            for (Map.Entry<Integer, Integer> entry : dropCounts.entrySet()) {
                int itemId = entry.getKey();
                int qty = entry.getValue();
                Item item = itemDAO.findById(itemId);
                if (item != null) {
                    try {
                        inventory.addItem(item, qty);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        appendLog("アイテム追加に失敗しました: " + item.getName());
                    }
                    stageDropResults.add(new DropResult(item.getName(), qty));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            appendLog("アイテム取得に失敗しました");
        }

        defeatedEnemiesAllWaves.clear();
    }

    
    
    private void updateAllEnemyUI() {
        for (Character enemy : enemies) {
            updateEnemyUI(enemy);
        }
    }

    public void enableAllActions() {
        // プレイヤーターンかつ行動中でない場合のみ有効化
        boolean canEnable = (currentPhase == TurnPhase.PLAYER_TURN) && !playerActing;

        // 魔法ボタン
        for (int i = 0; i < magicButtons.size(); i++) {
            if (canEnable) {
                updateMagicButton(i);
            } else {
                magicButtons.get(i).setDisable(true);
            }
        }

        // ★ アイテムメニューの有効化・無効化を追加
        if (itemMenu != null) {
            itemMenu.setDisabled(!canEnable);
        }
        
        //撤退ボタンの有効化
        withdrawal.setDisable(false);
    }


    
    
    //敵の対象選択
    @FXML
    private void onEnemyClicked(MouseEvent event) {

        // 敵クリック時に一旦クリック無効化
        for (ImageView view : enemyImageViews) {
            view.setOnMouseClicked(null);
        }

        if (playerActing || currentPhase != TurnPhase.PLAYER_TURN) {
            return;
        }

        Node clicked = (Node) event.getSource();
        Object data = clicked.getUserData();

        if (!(data instanceof Integer index) || index >= enemies.size()) {
            return;
        }

        Character target = enemies.get(index);
        if (target.getHP() <= 0) return;

        selectedEnemy = target;
        appendLog(target.getName() + " を選択しました");

        // ★ 魔法 or アイテムのどちらかを発動
        if (selectedMagic != null && selectedMagic.requiresTargetSelection()) {

            // ======== 魔法発動 ========
            SEPlayer.play("戦闘SE/魔法.mp3");
            playerActing = true;

            MagicExecutor.cast(selectedMagic, player, List.of(target), enemies, () -> {

                // ★ 先にバフのターンを減らす
                player.updateBuffsEachTurn();
                enemies.forEach(Character::updateBuffsEachTurn);

                // ★ その後で UI を更新
                updatePlayerStatsUI();

                selectedMagic = null;
                selectedEnemy = null;

                checkBattleEndAsync(battleEnded -> {
                    if (!battleEnded) {
                        playerActing = false;
                        nextTurnAsync();
                    }
                });
            });

        } else if (selectedItem != null) {

            // ======== アイテム発動 ========
            SEPlayer.play("戦闘SE/アイテム.mp3");
            playerActing = true;

            boolean success = ItemExecutor.use(selectedItem, player, List.of(target), inventory);

            if (success) {
                postItemUse();   // ★ ターン終了処理は postItemUse() に任せる
            }

            selectedItem = null;
            selectedEnemy = null;

            return;
        } else {
            // どちらも選ばれていない
            appendLog("行動が選択されていません");
        }
    }


    //レベル関連
    private void onEnemyDefeated(Enemy enemy) {
        int gainedExp = enemy.getExp();
        EnemyManager.addKill(enemy.getId());  
        player.addExp(gainedExp);
//        appendLog("ループは " + gainedExp + " の経験値を獲得！");

        try {
            // ★ initialize() 内で生成した characterDAO を使う
            characterDAO.updateLevelAndExp(player.getLevel(), player.getExp(), player.getNextExp());
        } catch (SQLException e) {
            e.printStackTrace();
            appendLog("経験値保存に失敗しました");
        }

        updatePlayerStatsUI();
    }
    
    
    private void grantExpForEnemy(Enemy raw) {
        int exp = raw.getExp();
        player.addExp(exp);
        appendLog("[ループ]" + raw.getName() + " を倒した！ 経験値 " + exp + " を獲得！");
    }


    private static final int BUFF_ICON_SIZE = 30;

    void updatePlayerStatsUI() {

        player_HP.setText(String.valueOf(player.getHP()));
        Player_MP.setText(String.valueOf(player.getMP()));
        player_Level.setText(String.valueOf(player.getLevel()));

        updateBuffIcons(state_correction_container, state_correction_label, player.getActiveBuffs());
        updateStatusEffectIcons(state_effects_container, state_effects_label, player.getActiveStatusEffectIcons());
//        updateIconRow(state_effects_container, state_effects_label, player.getActiveStatusEffectIcons());
    }

    
    private void updateIconRow(VBox container, Text label, List<String> iconPaths) {

        container.getChildren().clear();

        // ラベル行
        HBox labelRow = new HBox(label);
        container.getChildren().add(labelRow);

        // アイコンを 2 個ずつ詰める
        HBox currentRow = new HBox(3); // spacing=3
        int count = 0;

        for (String path : iconPaths) {
            ImageView icon = createIcon(path);
            currentRow.getChildren().add(icon);
            count++;

            // 2 個入れたら次の行へ
            if (count % 2 == 0) {
                container.getChildren().add(currentRow);
                currentRow = new HBox(3);
            }
        }

        // 余りが 1 個だけの行も追加
        if (!currentRow.getChildren().isEmpty()) {
            container.getChildren().add(currentRow);
        }
    }

    
    private ImageView createIcon(String iconPath) {
        ImageView icon = new ImageView(
            new Image(getClass().getResource(iconPath).toExternalForm())
        );
        icon.setFitWidth(BUFF_ICON_SIZE);
        icon.setFitHeight(BUFF_ICON_SIZE);
        return icon;
    }
    
    private void updateStatusEffectIcons(VBox container, Text label, List<String> iconPaths) {

        container.getChildren().clear();

        // ラベル行
        HBox labelRow = new HBox(label);
        container.getChildren().add(labelRow);

        // 横並びのアイコン行
        HBox iconRow = new HBox(3); // spacing=3

        for (String path : iconPaths) {
            ImageView icon = createIcon(path);
            iconRow.getChildren().add(icon);
        }

        container.getChildren().add(iconRow);
    }

    
    private void updateBuffIcons(VBox container, Text label, List<UnifiedBuff> buffs) {

        container.getChildren().clear();

        // ラベル行
        HBox labelRow = new HBox(label);
        container.getChildren().add(labelRow);

        // ① 種類ごとに「存在するランクのリスト」を集約
        Map<Type, List<Integer>> typeToRanks = new LinkedHashMap<>();

        for (UnifiedBuff buff : buffs) {
            Type type = buff.getType();
            int rank = buff.getRank();

            typeToRanks
                .computeIfAbsent(type, k -> new ArrayList<>())
                .add(rank);
        }

        // ② ランクを昇順にソートし、重複を排除
        for (List<Integer> ranks : typeToRanks.values()) {
            ranks.sort(Integer::compareTo);
            ranks = ranks.stream().distinct().toList();
        }

        // ③ 種類を 2 種類ずつ 1 行に詰める
        HBox currentRow = new HBox(3);
        int typeCountInRow = 0;

        for (Map.Entry<Type, List<Integer>> entry : typeToRanks.entrySet()) {

            Type type = entry.getKey();
            List<Integer> ranks = entry.getValue();

            // この種類のアイコンを作る
            List<ImageView> icons = createIconsForTypeAndRanks(type, ranks);

            // 2種類入れたら次の行へ
            if (typeCountInRow == 2) {
                container.getChildren().add(currentRow);
                currentRow = new HBox(3);
                typeCountInRow = 0;
            }

            currentRow.getChildren().addAll(icons);
            typeCountInRow++;
        }

        if (!currentRow.getChildren().isEmpty()) {
            container.getChildren().add(currentRow);
        }
    }

    private List<ImageView> createIconsForTypeAndRanks(Type type, List<Integer> ranks) {
        List<ImageView> list = new ArrayList<>();

        for (int rank : ranks) {
            String path = "/application/images/Buff_icon/" + type.getIconName() + "_" + rank + ".png";
            list.add(createIcon(path));
        }

        return list;
    }

    

    private void selectItem(Item item) {
    	
    	disableAllActions();

        // ★ 魔法選択状態を解除（これが重要）
        selectedMagic = null;

        selectedItem = item;

        switch (item.getTargetType()) {
            case ENEMY_SINGLE -> appendLog("対象を選択してください");

            case ENEMY_ALL -> {
                boolean success = ItemExecutor.use(item, player, enemies, inventory);
                if (success) postItemUse();
            }

            case SELF -> {
                boolean success = ItemExecutor.use(item, player, List.of(player), inventory);
                if (success) postItemUse();
            }
        }
    }

    
    public List<Character> getEnemies() {
        if (enemies == null) {
            return List.of(); // null安全
        }
        return enemies;
    }


    
    @FXML
    private void magic(MouseEvent event) {
        MagicButtonGrid.setVisible(true);
        ItemListPane.setVisible(false);
        MagicButtonGrid.toFront(); // StackPane内で最前面に
    }

    public static StageController getInstance() { return instance; }//ステージ変更
    public static void setInstance(StageController controller) {instance = controller;}

    
    //アイテム関連
    @FXML
    private void Item(MouseEvent event) {
        MagicButtonGrid.setVisible(false);
        MagicButtonGrid.setManaged(false);
        ItemListPane.setVisible(true);
        ItemListPane.setManaged(true);

        itemMenu.render(); // ← 表示処理を委譲
    }
    

    private void postItemUse() {
    	SEPlayer.play("戦闘SE/アイテム.mp3");
        updatePlayerStatsUI();

        boolean hasAnimation = false;

        if (selectedItem != null && selectedEnemy != null) {
            if (selectedItem.isDamageSingleItem() || selectedItem.isDebuffItem()) {
                updateEnemyUIWithDamage(selectedEnemy); // 内部で ++ する
                hasAnimation = true;
            } else {
                updateEnemyUI(selectedEnemy);
            }
        } else if (selectedItem != null && selectedItem.isDamageAllItem()) {
            for (Character enemy : enemies) {
                updateEnemyUIWithDamage(enemy); // 内部で ++ する
                hasAnimation = true;
            }
        } else {
            updateAllEnemyUI();
        }

        selectedItem = null;
        selectedEnemy = null;

        // ★ 演出がない場合のみ即判定
        if (!hasAnimation) {
            checkBattleEndAsync(battleEnded -> {
                if (!battleEnded) {
                    playerActing = false;
//                    enableAllActions();
                    nextTurnAsync();
                }
            });
        }

        itemMenu.render();
    }
    
    public void resetBattle() {
        // ★ 内部ロジック側の状態リセット
        playerActing = false;
        remainingAnimations = 0;
        currentPhase = TurnPhase.PLAYER_TURN;
        preparingNextWave = false;

        selectedMagic = null;
        selectedItem  = null;
        selectedEnemy = null;

        if (enemies != null) {
            enemies.clear();
        }
        currentWaveRawEnemies = new ArrayList<>();
        defeatedEnemiesAllWaves.clear();
        defeatedEnemiesCurrentWave.clear();

        BattleAnimationManager.cancelAllAnimations();
        BattleAnimationManager.reset();

        // ★ UIノードを初期化
        if (enemyImageViews != null) {
            for (ImageView iv : enemyImageViews) {
                BattleAnimationManager.cancelAnimationsFor(iv);
                iv.setVisible(false);
                iv.setOnMouseClicked(null);
                iv.setScaleX(1.0);
                iv.setScaleY(1.0);
                iv.setOpacity(1.0);
                iv.setTranslateX(0);
                iv.setTranslateY(0);
                iv.setEffect(null);
            }
        }

        if (enemyHpBars != null) {
            for (ProgressBar bar : enemyHpBars) {
                bar.setProgress(0);
                bar.setVisible(false);
            }
        }

        if (enemyNameLabels != null) {
            for (Label name : enemyNameLabels) {
                name.setText("");
                name.setVisible(false);
            }
        }

        if (enemyStatusLabels != null) {
            for (Label status : enemyStatusLabels) {
                status.setText("");
                status.setVisible(false);
            }
        }

        if (enemyNameStatusBoxes != null) {
            for (HBox box : enemyNameStatusBoxes) {
                box.setVisible(false);
            }
        }

        // プレイヤー関連リセット
        if (player != null) {
            player.clearStatusEffects();
            updatePlayerStatsUI();
        }

        // バトルログをクリア
        if (battleLogBox != null) {
            battleLogBox.getChildren().clear();
        }

        System.out.println("バトル状態をリセットしました");
    }
    
    public int getStageId() {return StageId;}

	public void setStageId(int stageId) {StageId = stageId;}
	
	public int getcurrentWave() {return currentWave;}
	
	public int getmaxwave() {return maxWave;}
	
	public void copyFrom(StageController original) {
		
	    // --- UI ノード ---
	    this.rootPane = original.rootPane;
	    this.player_HP = original.player_HP;
	    this.Player_MP = original.Player_MP;
	    this.player_Level = original.player_Level;
	    this.WeveNumber = original.WeveNumber;
	
	    this.state_correction = original.state_correction;
	    this.state_correction_label = original.state_correction_label;
	    this.state_effects = original.state_effects;
	    this.state_effects_label = original.state_effects_label;
	    this.state_correction_container = original.state_correction_container;
	    this.state_effects_container = original.state_effects_container;
	
	    this.MagicButtonGrid = original.MagicButtonGrid;
	    this.magicBtn1 = original.magicBtn1;
	    this.magicBtn2 = original.magicBtn2;
	    this.magicBtn3 = original.magicBtn3;
	    this.magicBtn4 = original.magicBtn4;
	    this.magicBtn5 = original.magicBtn5;
	    this.magicBtn6 = original.magicBtn6;
	
	    this.ItemListPane = original.ItemListPane;
	    this.ItemListGrid = original.ItemListGrid;
	
	    this.enemy1 = original.enemy1;
	    this.enemy2 = original.enemy2;
	    this.enemy3 = original.enemy3;
	    this.enemy4 = original.enemy4;
	    this.enemy5 = original.enemy5;
	
	    this.enemy1_HP = original.enemy1_HP;
	    this.enemy2_HP = original.enemy2_HP;
	    this.enemy3_HP = original.enemy3_HP;
	    this.enemy4_HP = original.enemy4_HP;
	    this.enemy5_HP = original.enemy5_HP;
	
	    this.enemy_Name1 = original.enemy_Name1;
	    this.enemy_Name2 = original.enemy_Name2;
	    this.enemy_Name3 = original.enemy_Name3;
	    this.enemy_Name4 = original.enemy_Name4;
	    this.enemy_Name5 = original.enemy_Name5;
	
	    this.enemy_status1 = original.enemy_status1;
	    this.enemy_status2 = original.enemy_status2;
	    this.enemy_status3 = original.enemy_status3;
	    this.enemy_status4 = original.enemy_status4;
	    this.enemy_status5 = original.enemy_status5;
	
	    this.enemyHBox = original.enemyHBox;
	    this.enemyBox1 = original.enemyBox1;
	    this.enemyBox2 = original.enemyBox2;
	    this.enemyBox3 = original.enemyBox3;
	    this.enemyBox4 = original.enemyBox4;
	    this.enemyBox5 = original.enemyBox5;
	
	    this.enemy_NameStatus1 = original.enemy_NameStatus1;
	    this.enemy_NameStatus2 = original.enemy_NameStatus2;
	    this.enemy_NameStatus3 = original.enemy_NameStatus3;
	    this.enemy_NameStatus4 = original.enemy_NameStatus4;
	    this.enemy_NameStatus5 = original.enemy_NameStatus5;
	    
	    this.enemyDisplay = original.enemyDisplay;
	
	    this.battleLogScroll = original.battleLogScroll;
	    this.battleLogBox = original.battleLogBox;
	
	    this.withdrawal = original.withdrawal;
	
	    // --- リスト類 ---
	    this.magicButtons = original.magicButtons;
	    this.enemyHpBars = original.enemyHpBars;
	    this.enemyNameLabels = original.enemyNameLabels;
	    this.enemyStatusLabels = original.enemyStatusLabels;
	    this.enemyNameStatusBoxes = original.enemyNameStatusBoxes;
	    this.enemyImageViews = original.enemyImageViews;
	    
	 // --- 魔法関連 ---
	    this.customMagics = original.customMagics;

	    // --- アイテム関連 ---
	    this.inventory = original.inventory;
	    this.usableItems = original.usableItems;
	    this.itemMenu = original.itemMenu;
	    
	    this.characterDAO = original.characterDAO;
	    
	 // --- プレイヤー関連 ---
	    this.player = original.player;

	    // --- ステージデータ ---
	    this.stageData = original.stageData;
	}
}

