package novelengine.model;

import java.util.List;

public class Scenario {
    private String title;
    private List<SceneData> scenes;

    // --- Getter/Setter ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<SceneData> getScenes() { return scenes; }
    public void setScenes(List<SceneData> scenes) { this.scenes = scenes; }
}
