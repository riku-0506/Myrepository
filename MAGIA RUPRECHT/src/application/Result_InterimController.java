package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Result_InterimController {

    @FXML
    private VBox resultItemList;

    int Stageid = 1;
    boolean cleared = false;

    public void initialize() {

        // ★ StageController のインスタンスを取得
        StageController controller = StageController.getInstance();

        if (controller == null) {
            System.out.println("Result_InterimController: StageController is null");
            return;
        }

        // ★ ステージIDを StageController から取得
        Stageid = controller.getStageId();

        // ★ ドロップ結果を StageController から取得して表示
        for (StageController.DropResult dr : controller.getStageDropResults()) {
            addDrop(dr.name, dr.qty);
        }
    }

    public void addDrop(String name, int qty) {
        HBox row = new HBox();
        row.setPrefWidth(490);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("chihaya");
        nameLabel.setFont(new Font(40));

        Label timesLabel = new Label("×");
        timesLabel.getStyleClass().add("chihaya");
        timesLabel.setFont(new Font(40));

        Label qtyLabel = new Label(String.valueOf(qty));
        qtyLabel.getStyleClass().add("chihaya");
        qtyLabel.setFont(new Font(40));

        row.getChildren().addAll(nameLabel, timesLabel, qtyLabel);
        resultItemList.getChildren().add(row);
    }

    @FXML
    void GotoMenu(ActionEvent event) {
        SceneManager.changeScene("Menu.fxml");
    }
}
