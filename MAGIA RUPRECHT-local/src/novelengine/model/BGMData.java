package novelengine.model;

public class BGMData {
    private String fileName;
    private double volume;
    private boolean loop = true;
    private double fadeInSeconds = 1.5;
    private double fadeOutSeconds = 1.0;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }

    public boolean isLoop() { return loop; }
    public void setLoop(boolean loop) { this.loop = loop; }

    public double getFadeInSeconds() { return fadeInSeconds; }
    public void setFadeInSeconds(double fadeInSeconds) { this.fadeInSeconds = fadeInSeconds; }

    public double getFadeOutSeconds() { return fadeOutSeconds; }
    public void setFadeOutSeconds(double fadeOutSeconds) { this.fadeOutSeconds = fadeOutSeconds; }
}
