package application;

/**
 * クエスト条件の抽象基底クラス
 */
public abstract class QuestCondition {
    /** 条件達成判定 */
    public abstract boolean isSatisfied();

    /** 条件文言表示 */
    public abstract String getConditionText();
}
