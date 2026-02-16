package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ResultController {
    
    @FXML
    private VBox resultItemList;

    int Stageid = 1;
    boolean cleared = false;
    public static int clearStageId = 0;
    
    
    public void initialize() {
    	clearStageId = 0;
    	StageController controller = StageController.getInstance();

        if (controller == null) {
            System.out.println("ResultController: StageController is null");
            return;
        }

        // ステージID
        Stageid = controller.getStageId();

        // ドロップ結果を取得
        for (StageController.DropResult dr : controller.getStageDropResults()) {
            addDrop(dr.name, dr.qty);
        }

        
        
        try (Connection conn = DBManager.getConnection()) {
            // まずステージのクリア状況を確認
            try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT cleared FROM stages WHERE stage_id = ?")) {
                checkStmt.setInt(1, Stageid);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        cleared = Boolean.parseBoolean(rs.getString("cleared"));
                    }
                }
            }

            if (!cleared) {
                // 初クリアの場合のみ処理
            	clearStageId = Stageid;
            	
            	//魔法追加処理
            	List<Integer> magicIds = new ArrayList<Integer>();
            	
            	switch(Stageid) {
                case 1 -> magicIds.addAll(Arrays.asList(2,17,19,22,25,28));
                case 2 -> magicIds.addAll(Arrays.asList(3,5,13,26,27,29,30,32,34));
                case 3 -> magicIds.addAll(Arrays.asList(6,14,20,23,33,35,36));
                case 4 -> magicIds.addAll(Arrays.asList(18,21,24,37,38));
        	}
            	
            	// shop_magic に magic_id を追加（例: stage_id に紐づく魔法を登録）
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO shop_magic (magic_id) VALUES (?)")) {
                	for (int magicId : magicIds) { 
                		insertStmt.setInt(1, magicId);
                		insertStmt.addBatch();
                    }
                	insertStmt.executeBatch();
                    System.out.println("shop_magic に魔法を追加しました");
                }

            	
                try (PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE stages SET cleared = ? WHERE stage_id = ?")) {
                    updateStmt.setString(1, "true");
                    updateStmt.setInt(2, Stageid);
                    updateStmt.executeUpdate();
                    System.out.println("クリア済みにしました");
                }
            } else {
                System.out.println("既にクリア済みのため処理をスキップしました");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ステージ更新に失敗しました: " + e.getMessage());
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
    	if(!cleared) {
    		Stage currentStage = SceneManager.getCurrentStage();
            Scene currentScene = SceneManager.getCurrentScene();
            System.out.println("[DEBUG] Passing Stage and Scene to Story_Sample: " + currentStage + ", " + currentScene);
            BGMPlayer.stop();
            new Story().show(currentStage, currentScene, Stageid,() -> {
                System.out.println("[DEBUG] Story_Sample finished, switching to Menu.fxml");
                SceneManager.changeScene("Menu.fxml");
            });
    	}else{
    		SceneManager.changeScene("Menu.fxml");
    	}
    }
    
    static int getClearStageId() {
    	return clearStageId;
    }

}