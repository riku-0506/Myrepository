package novelengine.controller;

import novelengine.model.Dialogue;
import novelengine.model.Scenario;
import novelengine.model.SceneData;

public class ScenarioController {

    private Scenario scenario;
    private int currentSceneIndex = 0;
    private int currentDialogueIndex = 0;

    public ScenarioController(Scenario scenario) {
        this.scenario = scenario;
    }

    /** 現在のシーンを取得 */
    public SceneData getCurrentScene() {
        if (currentSceneIndex < scenario.getScenes().size()) {
            return scenario.getScenes().get(currentSceneIndex);
        }
        return null;
    }

    /** 現在のダイアログを取得 */
    public Dialogue getCurrentDialogue() {
        SceneData scene = getCurrentScene();
        if (scene != null && currentDialogueIndex < scene.getDialogues().size()) {
            return scene.getDialogues().get(currentDialogueIndex);
        }
        return null;
    }

    /** 次のダイアログへ進む */
    public boolean nextDialogue() {
        SceneData scene = getCurrentScene();
        if (scene == null) return false;

        currentDialogueIndex++;
        if (currentDialogueIndex >= scene.getDialogues().size()) {
            // シーンの最後まで来た場合
            currentSceneIndex++;
            currentDialogueIndex = 0;
            return currentSceneIndex < scenario.getScenes().size();
        }
        return true;
    }

    /** シナリオの最後まで再生済みか */
    public boolean isFinished() {
        return currentSceneIndex >= scenario.getScenes().size();
    }

    /** デバッグ用: 現在のセリフを取得 */
    public String getCurrentText() {
        Dialogue dialogue = getCurrentDialogue();
        if (dialogue != null) {
            return dialogue.getSpeaker() + ": " + dialogue.getText();
        }
        return null;
    }
}
