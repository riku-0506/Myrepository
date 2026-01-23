package application;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class BuyItemDialogUtil {

	public static void showConfirmDialog(Window ownerWindow, Consumer<Boolean> onResult) {
	    try {
	        FXMLLoader loader = new FXMLLoader(BuyItemDialogUtil.class.getResource("BuyItemDialog.fxml"));
	        DialogPane dialogPane = loader.load();
	        
	        // CSS適用
	        dialogPane.getStyleClass().add("item_dp");
	        dialogPane.getStylesheets().add(BuyItemDialogUtil.class.getResource("/application.css").toExternalForm());

	        // 追加：最低限の ButtonType を登録
	        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
	        Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
	        closeButton.setVisible(false);


	        Dialog<ButtonType> dialog = new Dialog<>();
	        dialog.setDialogPane(dialogPane);
	        dialog.initOwner(ownerWindow);
	        dialog.initModality(Modality.APPLICATION_MODAL);
	        dialog.initStyle(StageStyle.UNDECORATED);

	        Button yesButton = (Button) dialogPane.lookup("#yes");
	        Button noButton = (Button) dialogPane.lookup("#no");

	        yesButton.setOnAction(e -> {
	            onResult.accept(true);
	            dialog.setResult(ButtonType.CLOSE); // 明示的に結果を設定
	            dialog.close();
	        });

	        noButton.setOnAction(e -> {
	            onResult.accept(false);
	            dialog.setResult(ButtonType.CLOSE); // 明示的に結果を設定
	            dialog.close();
	        });

	        dialog.showAndWait();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
