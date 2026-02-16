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

public class OptionDialogUtil {

	public static void showConfirmDialog(Window ownerWindow, Consumer<Boolean> onResult) {
	    try {
	        FXMLLoader loader = new FXMLLoader(OptionDialogUtil.class.getResource("OptionDialog.fxml"));
	        DialogPane dialogPane = loader.load();

	        // 追加：最低限の ButtonType を登録
	        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
	        Button closeButton = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
	        closeButton.setVisible(false);

	        Dialog<ButtonType> dialog = new Dialog<>();
	        dialog.setDialogPane(dialogPane);
	        dialog.initOwner(ownerWindow);
	        dialog.initModality(Modality.APPLICATION_MODAL);
	        dialog.initStyle(StageStyle.DECORATED);
	        
	        Button CancelButton = (Button) dialogPane.lookup("#CancelButton");

	        CancelButton.setOnAction(e -> {
	            onResult.accept(true);
	            dialog.setResult(ButtonType.CLOSE); 
	            dialog.close();
	        });
	        

	        dialog.showAndWait();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
