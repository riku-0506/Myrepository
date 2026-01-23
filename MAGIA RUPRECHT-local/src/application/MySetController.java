package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import application.MyCustomizeDAO.MyCustomize;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MySetController implements Initializable {

    // 魔法選択スロット
    @FXML private ChoiceBox<String> ChoiceMagic1, ChoiceMagic2, ChoiceMagic3;
    @FXML private ChoiceBox<String> ChoiceMagic4, ChoiceMagic5, ChoiceMagic6;

    // マイセット名
    @FXML private TextField SetNameField1, SetNameField2, SetNameField3, SetNameField4, SetNameField5;

    // 操作ボタン
    @FXML private Button RegisterButton, EquipButton, GoBackButton;
    @FXML private Label EquippedSetLabel;

    private int currentSetId = 1;

    // スロットロック状態（index 0..5 がスロット1..6）
    // 初期は 1〜3 編集可能、4〜6 ロック
    private boolean[] slotLocked = { false, false, false, true, true, true };

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 魔法一覧取得して ChoiceBox にセット
        List<String> magicNames = MyCustomizeDAO.getMagicNames();
        List<ChoiceBox<String>> boxes = List.of(
                ChoiceMagic1, ChoiceMagic2, ChoiceMagic3,
                ChoiceMagic4, ChoiceMagic5, ChoiceMagic6
        );
        for (ChoiceBox<String> box : boxes) {
            box.getItems().clear();
            box.getItems().add("(外す)");
            box.getItems().addAll(magicNames);
            box.setValue("(外す)"); // デフォルト表示
        }

        // マイセットデータ読み込み（一覧を画面にセット）
        loadSet(1, SetNameField1);
        loadSet(2, SetNameField2);
        loadSet(3, SetNameField3);
        loadSet(4, SetNameField4);
        loadSet(5, SetNameField5);

        // 装備中セット読み込み・表示（これが「現在装備中の魔法を表示」する箇所）
        refreshEquippedDisplay();

        // ---------- 初期ロック設定 ----------
        setNameFieldsDisabled(true);     // 名前入力は禁止
        RegisterButton.setDisable(true);
        EquipButton.setDisable(true);

        // ChoiceBox は applyChoiceBoxLocks() のみで管理する
        applyChoiceBoxLocks();
    }

    // Magic 名 ⇔ ID 変換
    private int convertMagicNameToId(String name) {
        if (name == null || name.equals("(外す)")) return 0;
        String sql = "SELECT customize_id FROM customizes WHERE name = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("customize_id");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private String convertMagicIdToName(int id) {
        if (id == 0) return "(外す)";
        String sql = "SELECT name FROM customizes WHERE customize_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) { e.printStackTrace(); }
        return "(不明)";
    }


    // ================================
    // 編集ボタン
    // ================================

    @FXML void handleMysetButton1(ActionEvent e){ 
    	SEPlayer.play("イベント/click.mp3");
    	activateEditing(1, SetNameField1);
    }
    @FXML void handleMysetButton2(ActionEvent e){ 
    	SEPlayer.play("イベント/click.mp3");
    	activateEditing(2, SetNameField2); 
    }
    @FXML void handleMysetButton3(ActionEvent e){ 
    	SEPlayer.play("イベント/click.mp3");
    	activateEditing(3, SetNameField3); 
    }
    @FXML void handleMysetButton4(ActionEvent e){ 
    	SEPlayer.play("イベント/click.mp3");
    	activateEditing(4, SetNameField4); 
    }
    @FXML void handleMysetButton5(ActionEvent e){ 
    	SEPlayer.play("イベント/click.mp3");
    	activateEditing(5, SetNameField5); 
    }

    private void resetSlotLocks() {
        slotLocked = new boolean[]{ false, false, false, true, true, true };
        for(int i = 1; i <= 3; i++) {
	        boolean cleared = updateUnLock(i);
	        slotLocked[i+2] = !cleared;
        }
        
    }
    
    public boolean updateUnLock(int i) {
    	boolean cleared = false;
    	try (Connection conn = DBManager.getConnection()) {
    		try (PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT cleared FROM stages WHERE stage_id = ?")) {
                checkStmt.setInt(1, i);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        cleared = Boolean.parseBoolean(rs.getString("cleared"));
                    }
                }
	        }} catch (SQLException e) {
	            e.printStackTrace();
	            System.out.println("ステージ更新に失敗しました: " + e.getMessage());
	        }
    	return cleared;
    }

    private void activateEditing(int id, TextField field) {

        resetSlotLocks();

        currentSetId = id;
        loadSet(id, field);

        // マイセット名の編集を許可（ChoiceBox は applyChoiceBoxLocksで制御）
        setNameFieldsDisabled(true);
        field.setMouseTransparent(false);
        field.setFocusTraversable(true);

        RegisterButton.setDisable(false);
        EquipButton.setDisable(false);

        applyChoiceBoxLocks();
        // ここでは 4〜6 はロックしたまま（仕様）
    }




    // ================================
    // DB 読み込み
    // ================================

    /**
     * 指定したマイセット(id)の内容を ChoiceBox と nameField にセットする。
     * （loadSet は画面の全 ChoiceBox を上書きするため、呼び出す際の nameField 引数は
     *  表示するためのターゲットに使われますが、ChoiceBox 側に全データを入れます）
     */
    private void loadSet(int id, TextField nameField) {
        MyCustomize data = MyCustomizeDAO.get(id);
        if (data != null) {
            nameField.setText(data.setName);
            ChoiceMagic1.setValue(convertMagicIdToName(data.my_magic1));
            ChoiceMagic2.setValue(convertMagicIdToName(data.my_magic2));
            ChoiceMagic3.setValue(convertMagicIdToName(data.my_magic3));
            ChoiceMagic4.setValue(convertMagicIdToName(data.my_magic4));
            ChoiceMagic5.setValue(convertMagicIdToName(data.my_magic5));
            ChoiceMagic6.setValue(convertMagicIdToName(data.my_magic6));
        } else {
            // 存在しないセットなら初期化
            if (nameField != null) nameField.setText("");
            ChoiceMagic1.setValue("(外す)");
            ChoiceMagic2.setValue("(外す)");
            ChoiceMagic3.setValue("(外す)");
            ChoiceMagic4.setValue("(外す)");
            ChoiceMagic5.setValue("(外す)");
            ChoiceMagic6.setValue("(外す)");
        }
    }


    private void setNameFieldsDisabled(boolean disabled) {
        List<TextField> fields = List.of(
                SetNameField1, SetNameField2, SetNameField3,
                SetNameField4, SetNameField5
        );
        for (TextField f : fields) {
            f.setMouseTransparent(disabled);
            f.setFocusTraversable(!disabled);
        }
    }


    // ================================
    // 登録処理
    // ================================
    @FXML
    void handleRegister(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        TextField nameField = switch (currentSetId) {
            case 1 -> SetNameField1;
            case 2 -> SetNameField2;
            case 3 -> SetNameField3;
            case 4 -> SetNameField4;
            case 5 -> SetNameField5;
            default -> null;
        };

        if (nameField == null) return;

        int m1 = convertMagicNameToId(ChoiceMagic1.getValue());
        int m2 = convertMagicNameToId(ChoiceMagic2.getValue());
        int m3 = convertMagicNameToId(ChoiceMagic3.getValue());
        int m4 = convertMagicNameToId(ChoiceMagic4.getValue());
        int m5 = convertMagicNameToId(ChoiceMagic5.getValue());
        int m6 = convertMagicNameToId(ChoiceMagic6.getValue());

        List<Integer> ids = List.of(m1,m2,m3,m4,m5,m6);
        Set<Integer> filtered = new HashSet<>();
        for (Integer idv : ids)
            if (idv != null && idv != 0) filtered.add(idv);

        if (filtered.size() < ids.stream().filter(idv -> idv != null && idv != 0).count()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("登録エラー");
            alert.setContentText("同じ魔法を複数スロットに登録することはできません。");
            alert.showAndWait();
            return;
        }

        MyCustomize customize =
                new MyCustomize(nameField.getText(), m1,m2,m3,m4,m5,m6);
        MyCustomizeDAO.save(currentSetId, customize);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("登録完了");
        alert.setContentText("マイセット「" + customize.getSetName() + "」を登録しました！");
        alert.showAndWait();

        // 編集終了 → UI をロック
        setNameFieldsDisabled(true);

        // 登録後は「現在装備しているセット表示」を再読み込みして反映
        refreshEquippedDisplay();

        applyChoiceBoxLocks();  // スロットロック再反映
    }


    // ================================
    // 装備処理
    // ================================
    @FXML
    void handleEquip(ActionEvent event) {
    	
    	SEPlayer.play("イベント/click.mp3");
        MyCustomize equippedSet = MyCustomizeDAO.get(currentSetId);
        if (equippedSet == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("装備失敗");
            alert.setContentText("指定されたマイセットが存在しません。");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE `character` SET `mycustomize_number`=?")) {
            stmt.setInt(1, currentSetId);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        // 装備ラベル更新
        EquippedSetLabel.setText(equippedSet.getSetName());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("装備完了");
        alert.setContentText("マイセット「" + equippedSet.getSetName() + "」を装備しました！");
        alert.showAndWait();

        setNameFieldsDisabled(true);

        // 装備後は画面の ChoiceBox 表示を装備中のセットに合わせて更新
        refreshEquippedDisplay();

        applyChoiceBoxLocks();
    }


    // ================================
    // 装備中セット取得
    // ================================
    public static MyCustomize getEquippedSet() {
        String sql = "SELECT mycustomize_number FROM `character`";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return MyCustomizeDAO.get(rs.getInt("mycustomize_number"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * 装備中セットを再読み込みして ChoiceBox とラベルに反映する。
     * slot4〜6 は常に編集不可（表示のみ）にする（仕様）
     */
    private void refreshEquippedDisplay() {
        MyCustomize equipped = getEquippedSet();
        if (equipped != null) {

            EquippedSetLabel.setText(equipped.setName);

            // ★ 正しい変数名に修正
            ChoiceMagic1.setValue(convertMagicIdToName(equipped.my_magic1));
            ChoiceMagic2.setValue(convertMagicIdToName(equipped.my_magic2));
            ChoiceMagic3.setValue(convertMagicIdToName(equipped.my_magic3));
            ChoiceMagic4.setValue(convertMagicIdToName(equipped.my_magic4));
            ChoiceMagic5.setValue(convertMagicIdToName(equipped.my_magic5));
            ChoiceMagic6.setValue(convertMagicIdToName(equipped.my_magic6));

        } else {
            EquippedSetLabel.setText("未設定");
        }

        // 4〜6 編集不可（仕様）
        ChoiceMagic4.setDisable(true);
        ChoiceMagic5.setDisable(true);
        ChoiceMagic6.setDisable(true);
    }




    // ================================
    // 🔒 ロック処理
    // ================================

    /** スロットロックを UI に反映 */
    private void applyChoiceBoxLocks() {

        ChoiceBox<String>[] boxes = new ChoiceBox[]{
            ChoiceMagic1, ChoiceMagic2, ChoiceMagic3,
            ChoiceMagic4, ChoiceMagic5, ChoiceMagic6
        };

        boolean editing = !RegisterButton.isDisable();  
        // RegisterButton が有効 = 編集中
        // RegisterButton が無効 = 編集前

        for (int i = 0; i < boxes.length; i++) {

            if (!editing) {
                // 編集前 → 1〜6 全てロック（選択は表示されるが変更不可）
                boxes[i].setMouseTransparent(true);
                boxes[i].setFocusTraversable(false);
                continue;
            }

            // 編集中かつ slotLocked[i] = false のみ操作可能
            boolean locked = slotLocked[i];
            boxes[i].setDisable(locked);
            boxes[i].setMouseTransparent(locked);
            boxes[i].setFocusTraversable(!locked);
        }
    }

    /** 手動解放API（必要なら使用） */
    public void unlockSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slotLocked.length) return;
        slotLocked[slotIndex] = false;
        applyChoiceBoxLocks();
    }

    @FXML
    void handleGoBack(ActionEvent event) {
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
                    ・ここでは作成したオリジナルの魔法を編成することができます。
                    ・テキストエリア→マイセット名を設定できます。
                    ・編集ボタン→マイセット名や編成をする際に押す必要があります。
                    ・ボックス１～６→作成したオリジナルの魔法をセットできます。
                    ・登録する→編成した内容やマイセット名を登録します。
                    ・装備する→編成したマイセットを装備します。
                      現在装備しているマイセット名は右上に表示されます。
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
