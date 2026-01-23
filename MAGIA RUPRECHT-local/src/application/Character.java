package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;

public class Character {
    // 基本ステータス
    private final String name;
    private int maxHP;
    private int maxMP;
    private int hp;
    private int mp;
    private int baseAttack;
    private double baseDefense;
    private boolean isPlayer;
    private double damageTakenMultiplier = 1.0;
    private Enemy.AttackType attackType;

    // DB連携用フィールド
    private int level;
    private int exp;
    private int nextExp;
    private int money;
    private int customizeNumber;

    // 耐性・状態・バフ
    private final Map<String, Integer> abnormalResistances = new HashMap<>();
    private final Map<String, Boolean> status = new HashMap<>();
    private final Map<String, Double> elementResistances = new HashMap<>();
    private final List<UnifiedBuff> activeBuffs = new ArrayList<>();
    private boolean sleepFlag;      // 1.5倍判定用
    private boolean sleepSkipTurn;  // 行動不能用
    private boolean sleepUI = false;



    // 魔法属性継承
    private String nextMagicElement = null;

    // 状態異常の持続管理
    private final Map<StatusEffect, Integer> statusTurns = new HashMap<>();

    // ログ出力用
    private Consumer<String> logConsumer;

    // UI表示用スロットID
    private int displaySlotId = -1;
    
    private CharacterDAO characterDAO;{
    try {
        Connection conn = DBManager.getConnection();
        characterDAO = new CharacterDAO(conn); // ★インスタンス生成
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    // コンストラクタ：バトル用
    public Character(String name, int maxHP, int currentHP, int mp, int attack, double defense, boolean isPlayer) {
        this.name = name;
        this.maxHP = maxHP;
        this.hp = currentHP;
        this.maxMP = mp;
        this.mp = mp;
        this.baseAttack = attack;
        this.baseDefense = defense;
        this.isPlayer = isPlayer;

        abnormalResistances.put("やけど", 30);
        abnormalResistances.put("感電", 20);
        abnormalResistances.put("凍傷", 10);
        abnormalResistances.put("睡眠", 0);
        abnormalResistances.put("毒", 15);
    }

    // コンストラクタ：DBロード用
    public Character(int level, int exp, int nextExp,
                     int hp, int mp, int atk, double def,
                     int money, int customizeNumber) {
        this.name = "ループ"; // DBに名前がないので固定値か
        this.level = level;
        this.exp = exp;
        this.nextExp = nextExp;
        this.maxHP = hp;
        this.hp = hp;
        this.maxMP = mp;
        this.mp = mp;
        this.baseAttack = atk;
        this.baseDefense = def;
        this.money = money;
        this.customizeNumber = customizeNumber;
        this.isPlayer = true;
    }
    
    
    public Enemy.AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(Enemy.AttackType attackType) {
        this.attackType = attackType;
    }

    
 // 経験値加算とレベルアップ判定
    public void addExp(int amount) {
        this.exp += amount;
        while (this.exp >= this.nextExp && this.level < 100) {
            this.exp -= this.nextExp;
            levelUp();
        }
    }

    // レベルアップ処理
    public void levelUp() {
        if (level >= 100) return; // 上限

        this.level++;

        // HP成長
        if (level <= 20) {
            maxHP += 20;
        } else if (level <= 59) {
            maxHP = (int)Math.floor(maxHP * 1.015 + 5);
        } else {
            maxHP = (int)Math.floor(maxHP * 1.013);
        }

        // MP成長
        if (level <= 15) {
            maxMP += 10;
        } else if (level <= 59) {
            maxMP = (int)Math.floor(maxMP * 1.008 + 5);
        } else {
            maxMP = (int)Math.floor(maxMP * 1.005);
        }

        // ATK成長（切り上げ）
        baseAttack = (int)Math.ceil(baseAttack * 1.015);

        // DEFは仕様に含まれていないので据え置き

        // 次の必要経験値（1.1倍）
        nextExp = (int)Math.floor(nextExp * 1.1);
        
        try {
			characterDAO.updateStats(maxHP, maxMP, baseAttack);
		} catch (SQLException e) {
			e.printStackTrace();
		}

        // ログ出力
        log(name + "はレベル" + level + "に上がった！ HP:" + maxHP + " MP:" + maxMP + " ATK:" + baseAttack);
        SEPlayer.play("イベント/revelup3.mp3");
    }


    // 攻撃力（バフ込み）を取得
    public int getModifiedAttack() {
        double result = baseAttack;
        // バフ補正
        for (UnifiedBuff buff : activeBuffs) {
            if (buff.getType() == UnifiedBuff.Type.ATTACK) {
                result *= buff.getModifier();
            }
        }
        // 状態異常補正（感電）
        if (statusTurns.containsKey(StatusEffect.PARALYSIS)) {
            result *= 0.7; // 攻撃力30%DOWN
        }
        
        System.out.println("最終ダメージ" + result);
        return (int) result;
    }


    // 防御力（バフ込み）を取得
    public double getModifiedDefense() {
        double result = baseDefense;
        // バフ補正
        for (UnifiedBuff buff : activeBuffs) {
            if (buff.getType() == UnifiedBuff.Type.DEFENSE) {
                result -= buff.getModifier(); // 防御力は「カット率」なのでマイナス
            }
        }
        // 状態異常補正（凍傷）
        if (statusTurns.containsKey(StatusEffect.FREEZE)) {
            result += 0.1; // 防御DOWN（固定値）
        }
        System.out.println("最終防御率:" + result);
        return result; // 防御力がマイナスにならないように
    }

    //魔法威力UP対応
    public double getPowerModifier() {
        double modifier = 1.0;
        Iterator<UnifiedBuff> iterator = activeBuffs.iterator();
        while (iterator.hasNext()) {
            UnifiedBuff buff = iterator.next();
            if (buff.getType() == UnifiedBuff.Type.POWER && buff.isSingleUse()) {
                modifier *= buff.getModifier();
                iterator.remove(); // 1回限りなので使用後に削除
            }
        }
        return modifier;
    }




    // MPを消費
    public void consumeMP(int amount) {
        mp = Math.max(0, mp - amount);
    }

    // HPを回復
    public void heal(int amount) {
        hp = Math.min(maxHP, hp + amount);
    }

    // ダメージを受ける
    public void applyDamage(int amount) {
        hp = Math.max(0, hp - amount);
        playDamageFlash();
    }

    
 // バフを付与（重複制御付き・完全一致判定あり）
    public void applyBuff(UnifiedBuff newBuff) {

        // ★ 一回限りのバフは常に追加（重複OK）
        if (newBuff.isSingleUse()) {
            activeBuffs.add(newBuff);
            log(name + "に" + (newBuff.isBuff() ? "バフ" : "デバフ")
                + "「" + newBuff.getLabel() + "」を付与！");
            return;
        }

        boolean updated = false;

        for (UnifiedBuff existing : activeBuffs) {

            boolean sameType      = existing.getType() == newBuff.getType();
            boolean sameElement   = Objects.equals(existing.getElement(), newBuff.getElement());
            boolean sameModifier  = existing.getModifier() == newBuff.getModifier();
            boolean sameBuffType  = existing.isBuff() == newBuff.isBuff();
            boolean sameSingleUse = existing.isSingleUse() == newBuff.isSingleUse();

            // ★ 完全一致 → duration だけ更新
            if (sameType && sameElement && sameModifier && sameBuffType && sameSingleUse) {
                existing.setDuration(newBuff.getDuration());
                log(name + "の" + (newBuff.isBuff() ? "バフ" : "デバフ")
                    + "「" + existing.getLabel() + "」の効果時間を更新！");
                updated = true;
                break;
            }
        }

        // ★ 完全一致でなければ新規追加（modifier が違えば重複OK）
        if (!updated) {
            activeBuffs.add(newBuff);
            log(name + "に" + (newBuff.isBuff() ? "バフ" : "デバフ")
                + "「" + newBuff.getLabel() + "」を付与！");
        }
    }


    // 毎ターンバフを更新（残りターン減少・期限切れ削除）
    public void updateBuffsEachTurn() {
        for (UnifiedBuff buff : activeBuffs) buff.tick();
        activeBuffs.removeIf(UnifiedBuff::isExpired);
        if (isPlayer) {
            StageController controller = StageController.getInstance();
            if (controller != null) controller.updatePlayerStatsUI();
        }
        System.out.println("Buffs: " + activeBuffs);
        System.out.println("Labels: " + getActiveBuffLabels());

    }
    
    
    public void consumeSingleUseBuffs() {
        activeBuffs.removeIf(UnifiedBuff::isSingleUse);
    }


    public boolean tryApplyStatus(StatusEffect effect, Character attacker, Element element) {

        double chance = effect.getBaseChance();

        // 属性ボーナス
        if (element == effect.getElement()) {
            chance += 0.10;
        }

        // 特定状態異常確率UP
        chance += attacker.getSpecificStatusUp(effect);

        // 全状態異常確率UP（条件付き）
        double allUp = attacker.getStatusChanceUp();
        if (chance > 0.0) {
            chance += allUp;
        }

        // 敵の耐性DOWN（条件付き）
        double resistDown = this.getStatusResistDown();
        if (chance > 0.0) {
            chance += resistDown;
        }

        chance = Math.min(1.0, chance);

        boolean success = Math.random() < chance;

        if (success) {

            // ★ ここを先に判定する（最重要）
            if (effect == StatusEffect.SLEEP) {

                sleepFlag = true;      // 1.5倍判定ON
                sleepSkipTurn = true;  // 次の敵ターン行動不能
                sleepUI = true;        // UI表示ON

                // ★ 睡眠は statusTurns に入れない
                return true;
            }

            // ★ 睡眠以外の状態異常だけ duration を入れる
            int duration = attacker.isPlayer()
                ? effect.getEnemyDuration()
                : effect.getPlayerDuration();

            statusTurns.put(effect, duration);

            return true;
        }

        return false;
    }
    
    public List<String> getActiveStatusEffectLabels() {
        return statusTurns.keySet()
                .stream()
                .map(StatusEffect::getLabel)
                .toList();
    }

    public List<String> getActiveBuffLabels() {
        return activeBuffs.stream()
                .filter(buff -> !buff.isExpired())   // 期限切れは除外
                .map(UnifiedBuff::getLabel)          // 表示用ラベルを取得
                .toList();
    }



    // 属性耐性倍率を取得（バフ込み）
    public double getElementResistanceRate(String element) {
        if (element == null) return elementResistances.getOrDefault("物理", 1.0);
        double resist = elementResistances.getOrDefault(element, 1.0);
        for (UnifiedBuff buff : activeBuffs) {
            if (buff.getType() == UnifiedBuff.Type.ELEMENT_RESIST_DOWN &&
                element.equals(buff.getElement())) {
                resist += buff.getModifier();
            }
        }
        return Math.max(0.0, resist);
    }


    // 状態異常（列挙型）を保持しているか判定
    public boolean hasStatus(StatusEffect effect) {
        return statusTurns.containsKey(effect);
    }


    // ターン開始時に状態異常の効果を適用
 // ターン開始時に呼ぶ（毒・やけどダメージ、ログなど）
    public void applyStatusEffectsAtTurnStart() {

        // コピーして走査（中で解除が起きても安全にするため）
        for (StatusEffect effect : new ArrayList<>(statusTurns.keySet())) {

            switch (effect) {
                case BURN -> {
                    if (isPlayer) {
                        int cut = Math.max(1, getMaxHP() / 10); // 10%
                        hp = Math.max(0, hp - cut);
                        log(name + "はやけどで最大HPの10%が削られた！");
                    } else {
                        int dmg = Math.max(1, getMaxHP() / 20); // 5%
                        applyDamage(dmg);
                        log(name + "はやけどで" + dmg + "ダメージ！");
                    }
                }

                case POISON -> {
                    int dmg = Math.max(1, getMaxHP() / 12); // 約8%
                    applyDamage(dmg);
                    log(name + "は毒で" + dmg + "ダメージ！");
                }

                case PARALYSIS -> {
                    // 攻撃力DOWNは getModifiedAttack で反映済み
                    log(name + "は感電して攻撃力が低下している！");
                }

                case FREEZE -> {
                    // 防御DOWN・被ダメ倍率は他メソッドで反映済み
                    log(name + "は凍傷で防御が低下している！");
                }

                case SLEEP -> {
                    log(name + "は眠っている！");
                }
            }
            
        }
    }
    
    public double getSpecificStatusUp(StatusEffect effect) {
        double bonus = 0.0;

        for (UnifiedBuff buff : activeBuffs) {
            if (buff.getType() == UnifiedBuff.Type.SPECIFIC_STATUS_CHANCE_UP) {
                // buffElement が「毒」「やけど」「睡眠」などのラベル
                if (buff.getElement() != null && effect.getLabel().contains(buff.getElement())) {
                    bonus += buff.getModifier(); // 例：+0.5
                }
            }
        }

        return bonus;
    }
    
    public double getStatusChanceUp() {
        double bonus = 0.0;

        for (UnifiedBuff buff : activeBuffs) {
            if (buff.getType() == UnifiedBuff.Type.STATUS_CHANCE_UP) {
                bonus += buff.getModifier(); // 例：+0.3
            }
        }

        return bonus;
    }

    public double getStatusResistDown() {
        double down = 0.0;

        for (UnifiedBuff buff : activeBuffs) {
            if (buff.getType() == UnifiedBuff.Type.STATUS_CHANCE_DOWN) {
                down += buff.getModifier(); // 例：+0.1
            }
        }

        return down;
    }

    
    //バフ表示用
    public String getBuffSummary() {
        if (activeBuffs.isEmpty()) return "";
        return activeBuffs.stream()
            .map(UnifiedBuff::getLabel)
            .reduce((a, b) -> a + " / " + b)
            .orElse("");
    }
    
    //バフ解除用
    public void removeBuffByType(UnifiedBuff.Type type) {
        activeBuffs.removeIf(buff -> buff.getType() == type);
    }

    // 行動可能か判定（睡眠）
    public boolean canAct() {
        return !sleepSkipTurn;
    }
    
    // 行動不能を解除
    public void clearSkipTurn() {
        sleepSkipTurn = false;
    }
    
    public void endSleepDamageBonusThisTurn() {
        if (sleepFlag) {
            sleepFlag = false;
        }
    }
    
    public boolean isSleepUI() {
        return sleepUI;
    }
    
    public void setSleepUI(boolean value) {
        sleepUI = value;
    }
    
    public boolean isSleeping() {
        return sleepFlag;
    }

    public void clearSleep() {
        sleepFlag = false;
        statusTurns.remove(StatusEffect.SLEEP);
    }


    // 凍傷・睡眠のダメージ倍率変化
    public double getDamageTakenMultiplier() {
        double multiplier = damageTakenMultiplier; // ← 既存のフィールド値（バフなどで変更される可能性あり）

        // 状態異常による倍率補正
        if (statusTurns.containsKey(StatusEffect.FREEZE)) multiplier *= 1.3;
        if (sleepFlag) multiplier *= 1.5;
        System.out.println("ダメージ倍率:" + multiplier);
        System.out.println("睡眠:" + sleepFlag);

        return multiplier;
    }


    public void setDamageTakenMultiplier(double multiplier) {
        this.damageTakenMultiplier = multiplier;
    }


    //状態異常を文字列で取得
    public String getStatusEffectSummary() {

        String text = statusTurns.keySet().stream()
            .map(StatusEffect::getLabel)
            .reduce((a, b) -> a + " / " + b)
            .orElse("");

        // ★ sleepUI が true なら必ず睡眠を表示
        if (sleepUI) {
            if (!text.contains("睡眠")) {
                if (text.isEmpty()) text = "睡眠";
                else text += " / 睡眠";
            }
        }

        return text;
    }

    
    // HPを最大値まで回復
    public void setHPToMax() {
        this.hp = this.maxHP;
    }

    // MPを最大値まで回復
    public void setMPToMax() {
        this.mp = this.maxMP;
    }
    
    
    public void setMP(int mp) {
        this.mp = Math.max(0, Math.min(mp, this.maxMP));
    }
    
    public void resetMP() {
        this.setMP(this.getMaxMP());
    }
    
    public void clearStatusEffects() {
        statusTurns.clear(); // 状態異常のみ全解除
        sleepFlag = false;
        sleepSkipTurn = false;
        sleepUI = false;

    }
    
    
    //アイテム実装に関して
    public void recoverMP(int amount) {
        mp = Math.min(maxMP, mp + amount);
    }
    
    
    //状態異常のターン経過処理
    public void updateStatusTurnsEachTurn() {

        List<StatusEffect> toRemove = new ArrayList<>();

        for (var entry : statusTurns.entrySet()) {
            StatusEffect effect = entry.getKey();
            int turns = entry.getValue();

            // 永続（-1）は減らさない
            if (turns == -1) continue;

            turns -= 1;

            if (turns <= 0) {
                toRemove.add(effect);
            } else {
                statusTurns.put(effect, turns);
            }
        }

        for (StatusEffect effect : toRemove) {
            statusTurns.remove(effect);
            
            log(name + "の" + effect.getLabel() + "が解除された！");

            // ✅ 解除時 UI 更新（敵のみ）
            if (!isPlayer) {
                StageController controller = StageController.getInstance();
                if (controller != null) {
                    controller.updateEnemyStatusUI(this, getDisplaySlotId());
                }
            }
            
            if (isPlayer) {
                StageController controller = StageController.getInstance();
                if (controller != null) controller.updatePlayerStatsUI();
            }

        }
        
    }

    
    //状態異常の強制付与
    public void applyStatusEffect(StatusEffect effect) {
        if (!statusTurns.containsKey(effect)) {
            int duration = isPlayer ? effect.getPlayerDuration() : effect.getEnemyDuration();
            statusTurns.put(effect, duration);
            log(name + "に状態異常「" + effect.getLabel() + "」が付与された！");
        }
    }
    
    
    //演出用
    private Node visualNode;

    public void setVisualNode(Node node) {
        this.visualNode = node;
    }

    public Node getVisualNode() {
        return visualNode;
    }
    
    //ダメージ演出
    public void playDamageFlash() {
        if (visualNode == null) {
            System.out.println("⚠️ visualNode is null for " + name);
            return;
        }

        
//        SEPlayer.play("player_attack.mp3");
        visualNode.setEffect(null);
        ColorAdjust flash = new ColorAdjust();
        flash.setBrightness(1.0);
        visualNode.setEffect(flash);

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> {
            visualNode.setEffect(null);
        });
        pause.play();
    }
    
    
    //撃破時演出
    public void playDefeatAnimation(Runnable onFinished) {
        if (visualNode == null) return;

        // 縮小アニメーション
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), visualNode);
        scale.setToX(0);
        scale.setToY(0);

        // フェードアウトアニメーション
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), visualNode);
        fade.setToValue(0);

        // 並列に再生
        ParallelTransition pt = new ParallelTransition(scale, fade);
        pt.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });

        pt.play();

    }
    
    
    //攻撃時演出
    public void playJumpAnimation(Runnable onFinished) {
        if (visualNode == null) return;

        TranslateTransition jump = new TranslateTransition(Duration.seconds(0.15), visualNode);
        jump.setByY(-20); // 上に跳ねる
        jump.setAutoReverse(true);
        jump.setCycleCount(2); // 上下で1回ジャンプ

        jump.setOnFinished(e -> {
            if (onFinished != null) onFinished.run();
        });

        jump.play();
    }
    
    


    // 次に使う魔法の属性を設定
    public void setNextMagicElement(String element) {
        this.nextMagicElement = element;
    }

    // 次に使う魔法の属性を取得
    public String getNextMagicElement() {
        return nextMagicElement;
    }

    // 属性耐性を手動で設定
    public void setElementResistance(String element, double rate) {
        elementResistances.put(element, rate);
    }

    // ログ出力先を設定（UI連携用）
    public void setLogConsumer(Consumer<String> consumer) {
        this.logConsumer = consumer;
    }

    // ログを出力（コンソール＋UI）
    public void log(String message) {
        if (logConsumer != null) {
            logConsumer.accept("[" + name + "] " + message);
        }
    }

    // 表示スロットIDを設定（UI用）
    public void setDisplaySlotId(int id) {
        this.displaySlotId = id;
    }

    // 表示スロットIDを取得
    public int getDisplaySlotId() {
        return displaySlotId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Character c)) return false;
        return this.displaySlotId == c.displaySlotId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(displaySlotId);
    }

    // 基本情報の取得（getter群）
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    public int getNextExp() { return nextExp; }
    public void setNextExp(int nextExp) { this.nextExp = nextExp; }

    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }

    public int getCustomizeNumber() { return customizeNumber; }
    public Map<StatusEffect, Integer> getStatusTurns() {return statusTurns;}
    public String getName() { return name; }
    public int getMaxHP() { return maxHP; }
    public int getMaxMP() { return maxMP; }
    public int getHP() { return hp; }
    public int getMP() { return mp; }
    public int getATK() {return baseAttack;}
    public double getDEF() {return baseDefense;}
    public boolean isPlayer() { return isPlayer; }
}