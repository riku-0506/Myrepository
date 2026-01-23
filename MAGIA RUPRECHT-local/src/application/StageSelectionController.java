package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class StageSelectionController {

    @FXML private RadioButton stage1;
    @FXML private RadioButton stage2;
    @FXML private Text Stage2Name;
    @FXML private Rectangle Stage2bg;
    @FXML private RadioButton stage3;
    @FXML private Text Stage3Name;
    @FXML private Rectangle Stage3bg;
    @FXML private RadioButton stage4;
    @FXML private Text Stage4Name;
    @FXML private Rectangle Stage4bg;
    @FXML private ToggleGroup mapGroup;

    private static String selectedStage; // ← 保持するステージ名
    
    @FXML
    public void initialize() {
        stage1.setUserData("Stage1.fxml");
        stage2.setUserData("Stage2.fxml");
        stage3.setUserData("Stage3.fxml");
        stage4.setUserData("Stage4.fxml");
        
        applyStageUnlocks();
    }
    
    private void applyStageUnlocks() {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "SELECT stage_id, cleared FROM stages ORDER BY stage_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                boolean stage1Cleared = false;
                boolean stage2Cleared = false;
                boolean stage3Cleared = false;
                boolean stage4Cleared = false;

                while (rs.next()) {
                    int stageId = rs.getInt("stage_id");
                    boolean cleared = Boolean.parseBoolean(rs.getString("cleared"));

                    switch (stageId) {
                        case 1 -> stage1Cleared = cleared;
                        case 2 -> stage2Cleared = cleared;
                        case 3 -> stage3Cleared = cleared;
                        case 4 -> stage4Cleared = cleared;
                    }
                }

                // 解放条件: 前のステージがクリア済みなら次のステージを有効化
                stage1.setDisable(false); // 最初のステージは常に解放
                stage2.setDisable(!stage1Cleared);
                stage2.setVisible(stage1Cleared);
                stage3.setDisable(!stage2Cleared);
                stage3.setVisible(stage2Cleared);
                stage4.setDisable(!stage3Cleared);
                stage4.setVisible(stage3Cleared);
                
                Stage2Name.setVisible(stage1Cleared);
                Stage2bg.setVisible(stage1Cleared);
                Stage3Name.setVisible(stage2Cleared);
                Stage3bg.setVisible(stage2Cleared);
                Stage4Name.setVisible(stage3Cleared);
                Stage4bg.setVisible(stage3Cleared);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 例外時は最低限ステージ1だけ有効化
            stage1.setDisable(false);
            stage2.setDisable(true);
            stage3.setDisable(true);
            stage4.setDisable(true);
        }
    }

    @FXML
    private void StageDecision(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        RadioButton selected = (RadioButton) mapGroup.getSelectedToggle();
        if (selected != null) {
            selectedStage = selected.getUserData().toString();; // ラジオボタンの表示テキストを取得
//            SceneManager.setSelectedStageFxml(selectedStage);
            System.out.println("選択されたステージ: " + selectedStage);
            SceneManager.changeScene("CheckEquipment.fxml");
            // 必要ならDB保存や次画面への遷移など
        } else {
            System.out.println("ステージが選択されていません");
        }
    }
    
    @FXML
    void Undo(MouseEvent event) {
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.goBack();
    }
    
    @FXML
    void Help(MouseEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        showHelpDialog();
    }
    
    private void showHelpDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HelpDialog.fxml"));
            DialogPane pane = loader.load();

            HelpDialogController controller = loader.getController();

            // Menu画面専用のヘルプ文章
            controller.setHelpText("""
                    【メニュー画面の説明】
                    ・出撃する地域を選びます。
                    ・難易度は順に始まりの草原→海沿いの街リーベック
                      →竜の棲む街道・ラインラント→南部解放戦線・大聖堂前です
                    ・選ばれた地域は名前の左が白色になります。決定で出撃します。
                    ・出撃前に装備確認することもでき、
                      装備確認ボタンを押すとマイセット画面へ移行します。
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getSelectedStage() {
    	return selectedStage;
    }
}
 
