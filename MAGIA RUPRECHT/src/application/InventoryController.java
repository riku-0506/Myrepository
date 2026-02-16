package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class InventoryController implements Initializable {

    @FXML private VBox ConsumableList;
    @FXML private VBox MaterialList;

    private Inventory inventory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Connection conn = DBManager.getConnection();
            InventoryDAO inventoryDAO = new InventoryDAO(conn);
            ConsumableDAO consumableDAO = new ConsumableDAO(conn); // ✅ 追加
            MaterialDAO materialDAO = new MaterialDAO(conn);

            List<Item> allItems = new ArrayList<>();
            allItems.addAll(consumableDAO.getAllConsumables());
            allItems.addAll(materialDAO.getAllMaterials());

            inventory = new Inventory(inventoryDAO, allItems);

            renderInventory();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("インベントリの読み込みに失敗しました");
        }
    }

    private void renderInventory() {
        ConsumableList.getChildren().clear();
        MaterialList.getChildren().clear();

        List<Item> sortedConsumables = new ArrayList<>(inventory.getUsableItems());
        List<Item> sortedMaterials = new ArrayList<>(inventory.getMaterials());

        // item_id 昇順でソート
        sortedConsumables.sort(Comparator.comparingInt(Item::getId));
        sortedMaterials.sort(Comparator.comparingInt(Item::getId));

        for (Item item : sortedConsumables) {
            if (inventory.getCount(item) > 0) {
                ConsumableList.getChildren().add(createItemRow(item));
            }
        }

        for (Item item : sortedMaterials) {
            if (inventory.getCount(item) > 0) {
                MaterialList.getChildren().add(createItemRow(item));
            }
        }
    }
    
    
    private HBox createItemRow(Item item) {
        // 名前ラベル（改行対応＋ツールチップ）
        Label nameLabel = new Label(item.getName());
        nameLabel.setWrapText(true);
        nameLabel.setPrefWidth(250);
        nameLabel.setMaxWidth(290);
        nameLabel.setPrefHeight(90);
        nameLabel.setMinHeight(Region.USE_PREF_SIZE);
        nameLabel.setStyle("-fx-font-size: 35px;");
        nameLabel.getStyleClass().add("chihaya");
        nameLabel.setAlignment(Pos.CENTER);

        Tooltip tooltip = new Tooltip(item.getName());
        tooltip.setStyle("-fx-font-size: 20px;");
        Tooltip.install(nameLabel, tooltip);

        // 名前ボックス（高さ制限を解除）
        HBox nameBox = new HBox(nameLabel);
        nameBox.setPrefWidth(580);
        nameBox.setPrefHeight(Region.USE_COMPUTED_SIZE);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setPadding(new Insets(0, 0, 0, 15));

        // "×" ラベル
        Label crossLabel = new Label("×");
        crossLabel.setStyle("-fx-font-size: 40px;");
        crossLabel.getStyleClass().add("chihaya");
        crossLabel.setAlignment(Pos.CENTER);

        // 所持数ラベル
        Label countLabel = new Label(String.valueOf(inventory.getCount(item)));
        countLabel.setPrefWidth(65);
        countLabel.setPrefHeight(58);
        countLabel.setStyle("-fx-font-size: 40px;");
        countLabel.getStyleClass().add("chihaya");
        countLabel.setAlignment(Pos.CENTER);

        // 所持数ボックス
        HBox countBox = new HBox(crossLabel, countLabel);
        countBox.setPrefWidth(395);
        countBox.setPrefHeight(96);
        countBox.setAlignment(Pos.CENTER_LEFT);

        // 全体行（高さは自動調整）
        HBox row = new HBox(nameBox, countBox);
        row.setPrefWidth(415);
        row.setPrefHeight(Region.USE_COMPUTED_SIZE);
        row.setSpacing(10);

        return row;
    }

    
    @FXML
    void Undo(MouseEvent event) {
    		SceneManager.goBack();
    }
}