package application;

import javafx.scene.image.Image;

public class TalkLine {

    private final String text;
    private final Image face;

    public TalkLine(String text, Image face) {
        this.text = text;
        this.face = face;
    }

    public String getText() {
        return text;
    }

    public Image getFace() {
        return face;
    }
}
