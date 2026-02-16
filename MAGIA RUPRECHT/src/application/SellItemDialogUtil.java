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

public class SellItemDialogUtil {

    /**
     * 売却確認ダイアログを表示
     * @param ownerWindow 親ウィンドウ
     * @param itemName アイテム名
     * @param qty 売却個数
     * @param pricePerItem 1個あたりの売却価格
     * @param onResult 結果を受け取るコールバック
     */
    public static void showConfirmDialog(
            Window ownerWindow,
            String itemName,
            int qty,
            int pricePerItem,
            Consumer<Boolean> onResult) {

        try {
            FXMLLoader loader = new FXMLLoader(SellItemDialogUtil.class.getResource("SellItemDialog.fxml"));
            DialogPane dialogPane = loader.load();

            // コントローラ取得
            SellItemDialogController controller = loader.getController();

            // アイテム情報をセット（合計金額も）
            controller.setItemDetail(itemName, qty, pricePerItem);

            // ButtonType を追加して閉じるボタンは非表示
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
                dialog.close();
            });

            noButton.setOnAction(e -> {
                onResult.accept(false);
                dialog.close();
            });

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
