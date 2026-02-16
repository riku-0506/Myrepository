package application;

import java.util.ArrayList;
import java.util.List;

//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;


public class StageEXController extends StageController{
	
	StageData data = StageData.stageEX();
	
    @Override
    public void init(StageData data) {
        // 通常ステージと同じステージセットアップ
        super.init(data);

        // ★ ここに EX ステージ専用追加
        this.maxWave = 41;
    }
    
    //アークデーモン(第一形態・第二形態)出現用
    @Override
    List<Enemy> StageEXWaveEnemyCreate(int waveNumber) {
    	List<Enemy> currentWaveRawEnemies = new ArrayList<>();
    	
    	if (waveNumber < 40) {
            currentWaveRawEnemies = WaveEnemySelector.selectEnemies(getStageId());//ステージ変更
        } 
    	
    	else if (waveNumber == 40) {
        	BGMPlayer.play(data.boss_bgmPath);
            currentWaveRawEnemies = List.of(WaveEnemySelector.getBossByStage(getStageId()));
        }
    	
    	else if (waveNumber == maxWave) {
        	BGMPlayer.play(data.boss_bgmPath);
            currentWaveRawEnemies = List.of(EnemyDAO.getEnemyById(22));
        }
    	
    	else {
            currentWaveRawEnemies = List.of();
        }
    	
    	return currentWaveRawEnemies;
    }
    
    @Override
    boolean effectif() {
    	if(getcurrentWave() < (getmaxwave() - 1)) {
    		return true;
    	}else {
    		return false;
    	}
    }
}