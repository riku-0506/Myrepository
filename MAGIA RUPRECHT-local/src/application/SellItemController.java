package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SellItemController {

    @FXML private VBox MaterialListVBox;
    @FXML private VBox ConsumableListVBox;

    @FXML private Label ItemNameLabel;
    @FXML private Label ItemDescriptionLabel;
    @FXML private Label ItemPriceLabel;
    
    @FXML private Label Money;

    @FXML private Spinner<Integer> QuantitySpinner;

    private ItemDAO itemDAO;
    private InventoryDAO inventoryDAO;
    private CharacterDAO characterDAO;

    private Item selectedItem;
    Connection conn;
    @FXML
    public void initialize() {

        try {
            conn = DBManager.getConnection();
            this.itemDAO = new ItemDAO(conn);
            this.inventoryDAO = new InventoryDAO(conn);
            this.characterDAO = new CharacterDAO(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
     // initialize() の中
        updateMoneyLabel();


        SpinnerValueFactory<Integer> vf =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        QuantitySpinner.setValueFactory(vf);
        QuantitySpinner.setEditable(true);

        fixSpinnerEmptyBug();
        
        MaterialListVBox.setSpacing(8); // ボタン間に8pxの隙間
        ConsumableListVBox.setSpacing(10);

        loadSellableItems();
        QuantitySpinner.setStyle("-fx-font-size: 24px;");
    }
    
    private void updateMoneyLabel() {
        try {
            int money = characterDAO.getMoney();
            Money.setText(money + "G");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** ▼ 空欄バグ修正 */
    private void fixSpinnerEmptyBug() {
        QuantitySpinner.getEditor().focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                String t = QuantitySpinner.getEditor().getText();
                if (t == null || t.trim().isEmpty()) {
                    QuantitySpinner.getValueFactory().setValue(1);
                    QuantitySpinner.getEditor().setText("1");
                }
            }
        });
    }

    /** ▼ 素材・消耗品のボタン生成 */
    private void loadSellableItems() {

        MaterialListVBox.getChildren().clear();
        ConsumableListVBox.getChildren().clear();

        try {
            var counts = inventoryDAO.loadItemCounts();

            // ★ 素材を取得
            List<Item> materials = itemDAO.getMaterials();

            for (Item item : materials) {
                int owned = counts.getOrDefault(item.getId(), 0);
                if (owned <= 0) continue; // 所持数 0 は表示しない

                Button btn = makeItemButton(item, owned);
                MaterialListVBox.getChildren().add(btn);
            }

            // ★ 消耗品を取得
            List<Item> consumables = itemDAO.getConsumables();

            for (Item item : consumables) {
                int owned = counts.getOrDefault(item.getId(), 0);
                if (owned <= 0) continue;

                Button btn = makeItemButton(item, owned);
                ConsumableListVBox.getChildren().add(btn);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ▼ ボタン生成 */
    private Button makeItemButton(Item item, int owned) {

        Button btn = new Button(item.getName() + "（所持：" + owned + "）");
        btn.getStyleClass().addAll("chihaya", "BuyItem");
        btn.setPrefWidth(320);
        btn.setPrefHeight(60);
        btn.setMinHeight(60);
        btn.setMaxHeight(60);

        btn.setOnAction(e -> {
        	SEPlayer.play("イベント/click.mp3");
        	showItemDetail(item, owned);
        });

        return btn;
    }

    /** ▼ アイテム詳細表示 */
    private void showItemDetail(Item item, int owned) {
        selectedItem = item;
        ItemNameLabel.setText(item.getName());
        ItemDescriptionLabel.setText(item.getDescription());
        ItemPriceLabel.setText(item.getSellingPrice() + "G");
    }

    /** ▼ 売却処理 */
    @FXML
    void Sell(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        if (selectedItem == null) {
            System.out.println("アイテムが選択されていません");
            return;
        }

        int qty = QuantitySpinner.getValue();

        try {
            int owned = inventoryDAO.getItemCount(selectedItem.getId());
            if (qty > owned) {
            	showAlert("所持数が足りません");
                return;
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            SellItemDialogUtil.showConfirmDialog(
            	    stage,
            	    selectedItem.getName(),
            	    qty,
            	    selectedItem.getSellingPrice(),
            	    result -> {
            	        if (!result) return;

            	        try {
            	            inventoryDAO.updateItemCount(selectedItem.getId(), owned - qty);

            	            int total = selectedItem.getSellingPrice() * qty;
            	            int money = characterDAO.getMoney();
            	            characterDAO.updateMoney(money + total);

            	            loadSellableItems();
            	            ItemNameLabel.setText("");
            	            ItemDescriptionLabel.setText("");
            	            ItemPriceLabel.setText("");
            	            updateMoneyLabel();
            	            QuantitySpinner.getValueFactory().setValue(1);
            	            QuantitySpinner.getEditor().setText("1");

            	        } catch (Exception ex) {
            	            ex.printStackTrace();
            	        }
            	    }
            	);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleGoToBuy(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("BuyItem.fxml");
    }

    @FXML
    private void handleGoToQuest(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
//    	System.out.println("未実装です");
        SceneManager.changeScene("Quest.fxml");
    }

    @FXML
    private void handleGoToGuild(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Guild.fxml");
    }
}
