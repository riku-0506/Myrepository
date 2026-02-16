package application;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.VBox;


public class BuyItemController {

    @FXML private Spinner<Integer> QuantitySpinner;
    @FXML private VBox ConsumableListVBox;
    @FXML private VBox MagicListVBox;
    @FXML private Label PriceLabel;
    @FXML private javafx.scene.text.Text DescriptionText;
    @FXML private Label Money;

    private ItemDAO itemDAO;
    private ShopMagicDAO shopMagicDAO;
    private PlayerMagicDAO playerMagicDAO;
    private MagicDAO magicDAO;
    private CharacterDAO characterDAO;
    private InventoryDAO inventoryDAO;

    // 現在選択している項目
    private Item selectedItem;
    private Magic selectedMagic;

    @FXML
    public void initialize() {
        try {
            Connection conn = DBManager.getConnection();

            this.itemDAO = new ItemDAO(conn);
            this.characterDAO = new CharacterDAO(conn);
            this.inventoryDAO = new InventoryDAO(conn);

            // 追加 DAO
            this.shopMagicDAO = new ShopMagicDAO(conn);
            this.playerMagicDAO = new PlayerMagicDAO(conn);
            this.magicDAO = new MagicDAO(conn);

            SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
            QuantitySpinner.setValueFactory(valueFactory);
            QuantitySpinner.setEditable(true);

            fixSpinnerEmptyBug();
            loadConsumableButtons();
            loadMagicButtons();

            updateMoneyDisplay();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateMoneyDisplay() {
        try {
            int money = characterDAO.getMoney();
            Money.setText(money + "G");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fixSpinnerEmptyBug() {
        QuantitySpinner.getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String text = QuantitySpinner.getEditor().getText();
                if (text == null || text.isEmpty()) {
                    QuantitySpinner.getValueFactory().setValue(1);
                    QuantitySpinner.getEditor().setText("1");
                }
            }
        });
    }

    private void loadConsumableButtons() {
        try {
            List<Item> items = itemDAO.getAllItems();
            for (Item item : items) {
                if (!item.isConsumable()) continue;

                Button btn = new Button(item.getName());
                btn.getStyleClass().addAll("chihaya", "BuyItem");
                btn.setPrefWidth(280);
                btn.setPrefHeight(60);

                btn.setOnAction(e -> {
                	SEPlayer.play("イベント/click.mp3");
                	showItemDetail(item);
                });

                ConsumableListVBox.getChildren().add(btn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMagicButtons() {
        try {
            List<Integer> magicIds = shopMagicDAO.getAllShopMagics();

            // 念のため昇順ソート（DAO側でORDER BYしていれば不要）
            java.util.Collections.sort(magicIds);

            for (int magicId : magicIds) {
                Magic magic = MagicDAO.getById(magicId);
                if (magic != null) {
                    Button btn = new Button(magic.getName());
                    btn.getStyleClass().addAll("chihaya", "BuyMagic");
                    btn.setPrefWidth(280);
                    btn.setPrefHeight(60);

                    // ★ 購入済みチェック
                    if (playerMagicDAO.hasPlayerMagic(magicId)) {
                        // すでに購入済みならグレースケール化して無効化
                        ColorAdjust grayscale = new ColorAdjust();
                        grayscale.setSaturation(-1.0);
                        btn.setEffect(grayscale);
                        btn.setDisable(true);
                    } else {
                        // 未購入ならクリックで詳細表示
                        btn.setOnAction(e -> showMagicDetail(magic));
                    }

                    MagicListVBox.getChildren().add(btn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void showItemDetail(Item item) {
        this.selectedItem = item;
        this.selectedMagic = null; // 同時選択防止

        DescriptionText.setText(item.getDescription());
        PriceLabel.setText(item.getPurchasePrice() + "G");

        QuantitySpinner.setDisable(false);
        // デフォルト数量を1に戻す
        QuantitySpinner.getValueFactory().setValue(1);
    }

    private void showMagicDetail(Magic magic) {
        this.selectedMagic = magic;
        this.selectedItem = null; // 同時選択防止

        DescriptionText.setText(magic.getDescription());
        PriceLabel.setText(magic.getPrice() + "G");

        // 魔法は数量1固定
        QuantitySpinner.getValueFactory().setValue(1);
        QuantitySpinner.setDisable(true);
    }


    /** ▼ 購入ボタン */
    @FXML
    void Buy(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        // 何も選択されていない
        if (selectedItem == null && selectedMagic == null) {
        	showAlert("購入対象が選択されていません");
            return;
        }

        // 価格と数量の決定
        final int qty = (selectedMagic != null) ? 1 : QuantitySpinner.getValue();
        final int unitPrice = (selectedMagic != null)
                ? selectedMagic.getPrice()
                : selectedItem.getPurchasePrice();
        final int total = unitPrice * qty;

        try {
            // FXML 読み込み
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BuyItemDialog.fxml"));
            DialogPane pane = loader.load();

            BuyItemDialogController controller = loader.getController();

            // JavaFX Dialog
            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setDialogPane(pane);

            // ダイアログへ情報を渡す
            controller.setDialog(dialog);
            String displayName = (selectedMagic != null) ? selectedMagic.getName() : selectedItem.getName();
            controller.setItemInfo(displayName, qty, unitPrice);

            // ダイアログ表示
            Optional<Boolean> result = dialog.showAndWait();

            // YESを押した場合だけ購入
            if (result.isPresent() && result.get()) {

                int currentMoney = characterDAO.getMoney();

                if (currentMoney < total) {
                	showAlert("お金が足りません！");
                    return;
                }

                // 支払い
                characterDAO.updateMoney(currentMoney - total);

                if (selectedMagic != null) {
                    // 魔法購入処理
                    int magicId = selectedMagic.getMagicId();

                    // 重複購入チェック
                    if (playerMagicDAO.hasPlayerMagic(magicId)) {
                        System.out.println("既に習得済みの魔法です");
                        updateMoneyDisplay();
                        return;
                    }

                    // 習得
                    playerMagicDAO.addPlayerMagic(magicId);
                    System.out.println("魔法「" + selectedMagic.getName() + "」を購入しました");

                    // 購入済みボタンをグレースケール化
                    for (javafx.scene.Node node : MagicListVBox.getChildren()) {
                        if (node instanceof Button btn && btn.getText().equals(selectedMagic.getName())) {
                            ColorAdjust grayscale = new ColorAdjust();
                            grayscale.setSaturation(-1.0);
                            btn.setEffect(grayscale);
                            btn.setDisable(true);
                        }
                    }

                    QuantitySpinner.setDisable(true);

                } else {
                    // ★ アイテム購入処理
                    inventoryDAO.addItem(selectedItem.getId(), qty);
                    System.out.println("アイテム「" + selectedItem.getName() + "」を " + qty + " 個購入しました");

                    // アイテムは複数購入可能なのでグレースケール化は不要
                    QuantitySpinner.setDisable(false);
                }

                updateMoneyDisplay();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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
    private void handleGoToSell(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("SellItem.fxml");
    }

    @FXML
    private void handleGoToQuest(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
//        System.out.println("未実装です");
         SceneManager.changeScene("Quest.fxml");
    }

    @FXML
    private void handleGoToGuild(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("Guild.fxml");
    }
}