package application;

public class StageData {
    public final int stageId;
    public final String bgmPath;
    public final String boss_bgmPath;

    public StageData(int stageId, String bgmPath, String boss_bgmPath) {
        this.stageId = stageId;
        this.bgmPath = bgmPath;
        this.boss_bgmPath = boss_bgmPath;
    }

	public static StageData stage1() {
		return new StageData(
	            1,
	            "戦闘/maou_game_battle08.mp3",
	            "ボス/maou_game_boss07.mp3"
	        );

	}
	
	public static StageData stage2() {
		return new StageData(
	            2,
	            "戦闘/maou_game_battle08.mp3",
	            "ボス/maou_game_boss07.mp3"
	        );

	}
	
	public static StageData stage3() {
		return new StageData(
	            3,
	            "戦闘/maou_game_battle08.mp3",
	            "ボス/maou_game_boss07.mp3"
	        );

	}
	
	public static StageData stage4() {
		return new StageData(
	            4,
	            "戦闘/maou_game_battle08.mp3",
	            "ボス/maou_game_boss07.mp3"
	        );

	}
}

