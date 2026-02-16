package application;

public class StageFactory {

    public static StageController create(int stageId) {

        return switch (stageId) {
            case 5 -> new StageEXController(); // EXステージ
            default -> new StageController();    // 通常ステージ
        };
    }
}
