package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class CharacterPageController implements Initializable {

    @FXML private Label Level;
    @FXML private Label TotalEXP;
    @FXML private Label NextLevel;
    @FXML private Label MAXHP;
    @FXML private Label MAXMP;
    @FXML private Label ATK;
    @FXML private Label Money;
    @FXML private Label Myset;
    @FXML private Label LebelLavel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            Connection conn = DBManager.getConnection();
            CharacterDAO dao = new CharacterDAO(conn);
            
            // ★ 1件だけキャラ情報を取得
            Character c = dao.getCharacterStatus();

            if (c != null) {
            	if(c.getLevel() < 100) {
	                Level.setText(String.valueOf(c.getLevel()));
	                TotalEXP.setText(String.valueOf(c.getExp()));
	                NextLevel.setText(String.valueOf(c.getNextExp()));
	                MAXHP.setText(String.valueOf(c.getHP()));
	                MAXMP.setText(String.valueOf(c.getMP()));
	                ATK.setText(String.valueOf(c.getATK()));
	                Money.setText(c.getMoney() + " G");
	
	                // ★ Myset をセット名に置き換え
	                int mySetId = c.getCustomizeNumber();
	                String mySetName = dao.getMySetName(mySetId);
	                Myset.setText(mySetName != null ? mySetName : "未設定");
            	} else {
	            	Level.setText(String.valueOf(c.getLevel()));
	            	LebelLavel.setText("総経験値:");
	                TotalEXP.setText(String.valueOf(3850054));
	                NextLevel.setText(String.valueOf(0));
	                MAXHP.setText(String.valueOf(c.getHP()));
	                MAXMP.setText(String.valueOf(c.getMP()));
	                ATK.setText(String.valueOf(c.getATK()));
	                Money.setText(c.getMoney() + " G");
	
	                // ★ Myset をセット名に置き換え
	                int mySetId = c.getCustomizeNumber();
	                String mySetName = dao.getMySetName(mySetId);
	                Myset.setText(mySetName != null ? mySetName : "未設定");
            	}
            }
       }catch (SQLException e) {
            e.printStackTrace();
            Money.setText("ERR");
      }
    }
    
    @FXML
    void Undo(ActionEvent event) {
        SceneManager.changeScene("Menu.fxml");
    }
}
