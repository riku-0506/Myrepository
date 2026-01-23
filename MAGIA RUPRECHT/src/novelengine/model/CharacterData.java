package novelengine.model;

public class CharacterData {
    private String name;
    private String image;
    private String position;
    private String facing;
    private double fadeInSeconds = 0.8; // 固定値
    private boolean fadeIn = false;     // JSONで true 宣言されていれば true

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getFacing() { return facing; }
    public void setFacing(String facing) { this.facing = facing; }

    public double getFadeInSeconds() { return fadeInSeconds; }
    public void setFadeInSeconds(double fadeInSeconds) { this.fadeInSeconds = fadeInSeconds; }

    public boolean isFadeIn() { return fadeIn; }
    public void setFadeIn(boolean fadeIn) { this.fadeIn = fadeIn; }
    
    private String clear;  // left, center, right, all

    public String getClear() { return clear; }
    public void setClear(String clear) { this.clear = clear; }

}
