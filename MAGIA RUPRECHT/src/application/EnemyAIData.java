package application;

import java.util.List;

public class EnemyAIData {

    public int enemyId;
    public String name;

    public static class SkillEntry {
        public int id;
        public String name;
        public int weight;
    }
    public List<SkillEntry> skills;

    public static class ActionEntry {
        public String condition;
        public int skillId;
    }
    public List<ActionEntry> actions;
}