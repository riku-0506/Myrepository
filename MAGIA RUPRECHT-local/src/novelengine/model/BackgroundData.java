package novelengine.model;

public class BackgroundData {
    private String fileName;
    private double fadeInSeconds = 0;  // JSON未指定なら0
    private String transitionType = "none";

    public BackgroundData() {}

    public BackgroundData(String fileName) {
        this.fileName = fileName;
    }

    public BackgroundData(String fileName, double fadeInSeconds, String transitionType) {
        this.fileName = fileName;
        this.fadeInSeconds = fadeInSeconds;
        this.transitionType = transitionType;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public double getFadeInSeconds() { return fadeInSeconds; }
    public void setFadeInSeconds(double fadeInSeconds) { this.fadeInSeconds = fadeInSeconds; }

    public String getTransitionType() { return transitionType; }
    public void setTransitionType(String transitionType) { this.transitionType = transitionType; }

    @Override
    public String toString() {
        return "BackgroundData{" +
                "fileName='" + fileName + '\'' +
                ", fadeInSeconds=" + fadeInSeconds +
                ", transitionType='" + transitionType + '\'' +
                '}';
    }
}
