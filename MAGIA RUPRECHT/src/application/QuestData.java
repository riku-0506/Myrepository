package application;

public class QuestData {

    private String id;               // クエストID（DBと一致）
    private String name;
    private String description;
    private int rewardGold;

    private boolean accepted = false;
    private boolean completed = false;
    
    private QuestCondition condition;
    private int unlockStage;

    public QuestData(String id, String name, String description, int rewardGold, QuestCondition condition, int unlockStage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rewardGold = rewardGold;
        this.condition = condition;
        this.unlockStage = unlockStage;
        
    }
    
    public void checkCompletion() {
        if (condition.isSatisfied()) {
            this.completed = true;
        }
    }

    public String getId() { return id; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean b) { accepted = b; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean b) { completed = b; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getRewardGold() { return rewardGold; }

    public QuestCondition getCondition() { return condition; }
    
    public int getUnlockStage() {
        return unlockStage;
    }
}
