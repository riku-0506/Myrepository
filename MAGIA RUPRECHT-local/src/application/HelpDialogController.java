package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HelpDialogController {

    @FXML
    private TextFlow help;

    /** 外部から文章を渡すためのメソッド */
    public void setHelpText(String text) {

        String key = "・ちなみに";
        int index = text.lastIndexOf(key);

        Text normal;
        Text highlight;

        if (index == -1) {
            normal = new Text(text);
            normal.setFont(Font.font(24));
            help.getChildren().setAll(normal);
            return;
        }

        normal = new Text(text.substring(0, index));
        normal.setFont(Font.font(24));

        highlight = new Text(text.substring(index));
        highlight.setFont(Font.font(24));
        highlight.setStyle("""
            -fx-fill: #d32f2f;
            -fx-font-weight: bold;
            """);

        help.getChildren().setAll(normal, highlight);
    }

    @FXML
    void Undo(ActionEvent event) {
        SEPlayer.play("イベント/click.mp3");
        help.getScene().getWindow().hide();
    }
    
    
}
