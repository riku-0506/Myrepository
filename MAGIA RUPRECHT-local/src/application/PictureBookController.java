package application;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;

public class PictureBookController {

	@FXML
    private void handleGoToMenu(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Menu.fxml");
    }
	
	@FXML
    private void handleGoToPictureBook_Monster(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("PictureBook_Monster.fxml");
    }
	
	@FXML
    private void handleGoToPictureBook_Magic(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("PictureBook_Magic.fxml");
    }
	
	@FXML
    private void handleGoToPictureBook_Abnormality(ActionEvent event) {
		SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("PictureBook_Abnormality.fxml");
    }
	
	@FXML
    void Help(MouseEvent event) {
		SEPlayer.play("イベント/click.mp3");
        showHelpDialog();
    }

    private void showHelpDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HelpDialog.fxml"));
            DialogPane pane = loader.load();

            HelpDialogController controller = loader.getController();

            // Menu画面専用のヘルプ文章
            controller.setHelpText("""
                    【メニュー画面の説明】
                    ・ここではモンスターや魔法の情報を見ることが出来ます。
                    ・モンスターの図鑑はHPや攻撃力、弱点属性などが載っています。
                    ・魔法の図鑑は魔法の効果が載っています。
                    ・状態異常の図鑑はそれぞれの効果内容が載っています。
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
