package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class BuyItemDialogController {

    @FXML
    private Label itemLabel;

    @FXML
    private Button yes;

    @FXML
    private Button no;

    private Dialog dialog;

    /** BuyItemController からダイアログを受け取る */
    public void setDialog(Dialog dialog) {
        this.dialog = dialog;

        // ボタン動作登録
        yes.setOnAction(e -> {
            dialog.setResult(true);   // YES を返す
            dialog.close();
        });

        no.setOnAction(e -> {
            dialog.setResult(false);  // NO を返す
            dialog.close();
        });
    }

    /** アイテム名・購入数・価格を反映する */
    public void setItemInfo(String itemName, int quantity, int pricePerItem) {
        int total = pricePerItem * quantity;
        itemLabel.setText(itemName + "を" + quantity + "個購入しますか？（合計: " + total + "G）");
    }
}
