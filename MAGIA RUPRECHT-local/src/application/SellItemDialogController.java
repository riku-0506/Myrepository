package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SellItemDialogController {

    @FXML private Label itemLabel;  // 「アイテムをXX個売却しますか？」表示
    @FXML private Label totalLabel; // 「合計: XXXG」表示

    /**
     * アイテム情報をセット
     * @param itemName アイテム名
     * @param qty 個数
     * @param pricePerItem 1個あたりの売却価格
     */
    public void setItemDetail(String itemName, int qty, int pricePerItem) {
        itemLabel.setText(itemName + "を " + qty + "個 売却しますか？");

        int total = qty * pricePerItem;
        totalLabel.setText("合計: " + total + "G");
    }
}
