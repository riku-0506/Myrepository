package application;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class StartDialogUtil {

    public static void showConfirmDialog(Window ownerWindow, Consumer<Boolean> onResult) {
        System.out.println("[DEBUG] showConfirmDialog called, ownerWindow=" + ownerWindow);

        try {
            FXMLLoader loader = new FXMLLoader(StartDialogUtil.class.getResource("StartDialog.fxml"));
            DialogPane dialogPane = loader.load();
            System.out.println("[DEBUG] FXML loaded successfully: StartDialog.fxml");

            // 最低限の ButtonType を登録
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
                System.out.println("[DEBUG] Yes button clicked");
                onResult.accept(true);
                dialog.setResult(ButtonType.CLOSE);
                dialog.close();

                Stage currentStage = (Stage) ownerWindow;
                Scene currentScene = currentStage.getScene();
                System.out.println("[DEBUG] Passing Stage and Scene to Story_Sample: " + currentStage + ", " + currentScene);
                BGMPlayer.stop();
                
                new Story_Sample().show(currentStage, currentScene, 0,() -> {
                    System.out.println("[DEBUG] Story_Sample finished, switching to Menu.fxml");
                    SceneManager.changeScene("Menu.fxml");
                });
            });

            noButton.setOnAction(e -> {
                System.out.println("[DEBUG] No button clicked");
                onResult.accept(false);
                dialog.setResult(ButtonType.CLOSE);
                dialog.close();
            });

            System.out.println("[DEBUG] Showing StartDialog");
            dialog.showAndWait();
            System.out.println("[DEBUG] StartDialog closed");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[DEBUG] Exception occurred in showConfirmDialog: " + e.getMessage());
        }
    }
}
