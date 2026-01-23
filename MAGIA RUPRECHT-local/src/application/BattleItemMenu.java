package application;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class BattleItemMenu {
    private final GridPane grid;
    private final Inventory inventory;
    private Consumer<Item> onItemSelected;

    public BattleItemMenu(GridPane grid, Inventory inventory) {
        this.grid = grid;
        this.inventory = inventory;
    }

    public void setOnItemSelected(Consumer<Item> handler) {
        this.onItemSelected = handler;
    }

    public void render() {
        grid.getChildren().clear();

        int col = 0;
        int row = 0;

        // 必要に応じて余白を設定（重なり防止）
        grid.setHgap(5);
        grid.setVgap(5);

        for (Item item : inventory.getUsableItems()) {
            int count = inventory.getCount(item);
            Button button = new Button(item.getName() + " ×" + count);
            button.setPrefWidth(340);
            button.setPrefHeight(50);
            button.setStyle("-fx-font-size: 20px;");
            button.getStyleClass().add("chihaya");
            

            if (count <= 0) {
                button.setDisable(true);
                button.setStyle("-fx-opacity: 0.5; -fx-font-size: 20px;");
                Tooltip tooltip = new Tooltip("所持数が0のため使用できません");
                Tooltip.install(button, tooltip);
            } else {
                button.setOnAction(e -> {
                    if (onItemSelected != null) {
                        onItemSelected.accept(item);
                    }
                });
            }

            grid.add(button, col, row);

            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }

        // GridPaneの高さをアイテム数に応じて拡張（ScrollPaneがスクロール可能になる）
        int totalRows = (int) Math.ceil(inventory.getUsableItems().size() / 2.0);
        grid.setPrefHeight(totalRows * 55); // 45px + 10px間隔
    }

}