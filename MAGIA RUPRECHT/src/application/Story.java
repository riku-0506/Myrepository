package application;

import javafx.scene.Scene;
import javafx.stage.Stage;
import novelengine.ScenarioPlayer;

public class Story {

    public void show(Stage stage, Scene currentScene, int ScenarioNumber, Runnable onFinished) {
        System.out.println("[DEBUG] Story_Sample.show called, stage=" + stage + ", currentScene=" + currentScene);

        try {
        	double BGMVol = BGMPlayer.getCurrentVolume(); //音量の値を取る
        	double SEVol = SEPlayer.getCurrentVolume(); //SEの値を取る（現在未使用）
        	
            // ScenarioPlayer に既存 Scene を渡す
            ScenarioPlayer player = new ScenarioPlayer(stage, currentScene, "chapter" + ScenarioNumber, BGMVol);
            System.out.println("[DEBUG] ScenarioPlayer created with existing Scene");

            // 終了時の通知だけ受け取る
            player.setOnFinished(() -> {
                System.out.println("[DEBUG] ScenarioPlayer finished");
                if (onFinished != null) {
                    onFinished.run();
                    System.out.println("[DEBUG] onFinished Runnable executed");
                }
            });

            // シナリオ再生開始
            System.out.println("[DEBUG] Starting ScenarioPlayer.play()");
            player.play();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("[DEBUG] Exception in Story_Sample.show: " + ex.getMessage());
        }
    }
}
