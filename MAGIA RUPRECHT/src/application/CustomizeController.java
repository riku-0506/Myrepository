package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;



public class CustomizeController {

    @FXML private Button SetEquipmentButton;
    @FXML private ComboBox<CustomizeDAO.Customize> comboBox;

    @FXML private ComboBox<String> magicComboBox1;
    @FXML private ComboBox<String> magicComboBox2;
    @FXML private ComboBox<String> magicComboBox3;
    @FXML private ComboBox<String> magicComboBox4;
    @FXML private ComboBox<String> magicComboBox5;
    @FXML private ComboBox<String> magicComboBox6;

    @FXML private Label magicNameLabel;
    @FXML private Label magicMpLabel;
    @FXML private Label magicEffectLabel;
    @FXML private Label magicElementLabel;
    @FXML private Label magicStatusLabel;
    @FXML private Label totalMpLabel;
    
    @FXML private ScrollPane MagicScrollPane;
    @FXML private TextArea MagicCodeDescliption;

    private Magic[] assignedMagics = new Magic[6];
    private boolean isUpdatingComboBoxes = false; // ← 無限ループ防止フラグ

    @FXML
    public void initialize() {
        // カスタマイズ一覧の初期化
        comboBox.setItems(FXCollections.observableArrayList(CustomizeDAO.getAll()));
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CustomizeDAO.Customize item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name);
            }
        });
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CustomizeDAO.Customize item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name);
            }
        });
        comboBox.setOnAction(e -> {
            CustomizeDAO.Customize selected = comboBox.getSelectionModel().getSelectedItem();
            if (selected != null) showCustomizeDetails(selected);
            else clearCustomizeDetails();
        });

        // 魔法リストの取得
        List<String> PlayerMagicNames = new ArrayList<>(MagicDAO.getPlayerMagicNames());
        PlayerMagicNames.add(0, "未選択");

        List<String> top6MagicNames = new ArrayList<>(MagicDAO.getTop6MagicNames());
        top6MagicNames.add(0, "未選択");

        magicComboBox1.setItems(FXCollections.observableArrayList(top6MagicNames));
        magicComboBox2.setItems(FXCollections.observableArrayList(PlayerMagicNames));
        magicComboBox3.setItems(FXCollections.observableArrayList(PlayerMagicNames));
        magicComboBox4.setItems(FXCollections.observableArrayList(PlayerMagicNames));
        magicComboBox5.setItems(FXCollections.observableArrayList(PlayerMagicNames));
        magicComboBox6.setItems(FXCollections.observableArrayList(PlayerMagicNames));

        // 各スロットの選択イベント設定
        setupMagicSlot(magicComboBox1, 0);
        setupMagicSlot(magicComboBox2, 1);
        setupMagicSlot(magicComboBox3, 2);
        setupMagicSlot(magicComboBox4, 3);
        setupMagicSlot(magicComboBox5, 4);
        setupMagicSlot(magicComboBox6, 5);
        
        applyLockStatus();
    }

    /** 各魔法スロットに動作を設定 */
    private void setupMagicSlot(ComboBox<String> comboBox, int index) {
        comboBox.setOnAction(e -> {
            if (isUpdatingComboBoxes) return; // 再発火防止

            String selectedName = comboBox.getSelectionModel().getSelectedItem();

            if (selectedName == null || selectedName.equals("未選択")) {
                assignedMagics[index] = null;
                updateTotalMp();
                clearMagicDetails();
                updateComboBoxOptions();
                return;
            }

            if (isDuplicate(selectedName, index)) {
                System.out.println("⚠ この魔法はすでに他のスロットに登録されています: " + selectedName);
                comboBox.getSelectionModel().clearSelection();
                return;
            }

            Magic magic = MagicDAO.findByName(selectedName);
            assignedMagics[index] = magic;
            updateTotalMp();
            showMagicDetails(magic);
            updateComboBoxOptions();
        });

        // 🆕 クリックやフォーカスで詳細を再表示できるようにする
        comboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { // フォーカスを得たとき
                String selectedName = comboBox.getSelectionModel().getSelectedItem();
                if (selectedName != null && !selectedName.equals("未選択")) {
                    Magic magic = MagicDAO.findByName(selectedName);
                    showMagicDetails(magic);
                } else {
                    clearMagicDetails();
                }
            }
        });
    }


    /** 魔法の重複チェック */
    private boolean isDuplicate(String name, int currentIndex) {
        for (int i = 0; i < assignedMagics.length; i++) {
            if (i == currentIndex) continue;
            Magic m = assignedMagics[i];
            if (m != null && m.name.equals(name)) return true;
        }
        return false;
    }

    /** ComboBox一覧更新（重複防止用） */
    private void updateComboBoxOptions() {
        if (isUpdatingComboBoxes) return;
        isUpdatingComboBoxes = true;

        try {
            List<String> PlayerMagicNames = MagicDAO.getPlayerMagicNames();
            List<String> top6 = MagicDAO.getTop6MagicNames();

            Set<String> selectedNames = new HashSet<>();
            for (Magic magic : assignedMagics)
                if (magic != null) selectedNames.add(magic.name);

            for (int i = 0; i < 6; i++) {
                ComboBox<String> comboBox = getComboBoxByIndex(i);
                if (comboBox == null) continue;

                String currentSelected = comboBox.getSelectionModel().getSelectedItem();

                Set<String> exclude = new HashSet<>(selectedNames);
                if (currentSelected != null) exclude.remove(currentSelected);

                List<String> sourceList = (i == 0) ? top6 : PlayerMagicNames;
                List<String> filtered = new ArrayList<>();
                filtered.add("未選択");

                for (String name : sourceList)
                    if (!exclude.contains(name)) filtered.add(name);

                comboBox.setItems(FXCollections.observableArrayList(filtered));

                if (currentSelected != null && filtered.contains(currentSelected)) {
                    comboBox.getSelectionModel().select(currentSelected);
                } else {
                    comboBox.getSelectionModel().clearSelection();
                    assignedMagics[i] = null;
                }
            }

        } finally {
            isUpdatingComboBoxes = false;
        }
        
        applyLockStatus();

    }

    /** ComboBox取得 */
    private ComboBox<String> getComboBoxByIndex(int index) {
        return switch (index) {
            case 0 -> magicComboBox1;
            case 1 -> magicComboBox2;
            case 2 -> magicComboBox3;
            case 3 -> magicComboBox4;
            case 4 -> magicComboBox5;
            case 5 -> magicComboBox6;
            default -> null;
        };
    }

    /** MP合計更新 */
    private void updateTotalMp() {
        int total = 0;
        for (Magic magic : assignedMagics)
            if (magic != null) total += magic.costMP;
        totalMpLabel.setText("MP合計: " + total);
    }

    /** 魔法詳細を表示 */
    private void showMagicDetails(Magic magic) {
        if (magic == null) {
            clearMagicDetails();
            return;
        }
        magicNameLabel.setText(magic.name);
        magicMpLabel.setText("MP消費: " + magic.costMP);
        magicEffectLabel.setText("効果: " + (magic.effectType != null ? magic.effectType : "不明"));
        magicElementLabel.setText("属性: " + (magic.element != null ? magic.element : "なし"));
        magicStatusLabel.setText("状態異常: " + (magic.statusEffect != null ? magic.statusEffect : "なし"));
        
     // 🆕 MagicCodeRepository からコードを取得して表示
        String code = MagicCodeRepository.getCodeByName(magic.name);
        MagicCodeDescliption.setText(code);
        MagicCodeDescliption.setScrollTop(0);
    }

    /** 魔法詳細クリア */
    private void clearMagicDetails() {
        magicNameLabel.setText("");
        magicMpLabel.setText("");
        magicEffectLabel.setText("");
        magicElementLabel.setText("");
        magicStatusLabel.setText("");
        MagicCodeDescliption.setText("");
    }

    // ===== 以下は既存の登録・削除・画面遷移処理 =====

    @FXML private TextField customizeNameField;

    @FXML
    void SetEquipment(ActionEvent event) {
        String name = customizeNameField.getText().trim();
        SEPlayer.play("イベント/click.mp3");
        if (name.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("登録エラー");
            alert.setHeaderText(null);
            alert.setContentText("⚠ カスタマイズ名を入力してください。");
            alert.showAndWait();
            return;
        }

        String firstMagic = magicComboBox1.getSelectionModel().getSelectedItem();
        if (firstMagic == null || firstMagic.equals("未選択")) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("登録エラー");
            alert.setHeaderText(null);
            alert.setContentText("⚠ 1つ目の魔法は必須です。選択してください");
            alert.showAndWait();
            return;
        }

        CustomizeDAO.Customize selected = comboBox.getSelectionModel().getSelectedItem();
        boolean isUpdate = selected != null;

        List<String> magicNames = getAssignedMagicNames();
        Integer[] magicIds = new Integer[6];
        for (int i = 0; i < 6; i++) {
            String magicName = magicNames.get(i);
            magicIds[i] = (magicName != null && !magicName.isEmpty()) ? MagicDAO.getIdByName(magicName) : null;
        }

        CustomizeDAO.Customize customize = new CustomizeDAO.Customize(
                isUpdate ? selected.customizeId : 0,
                name,
                magicIds[0], magicIds[1], magicIds[2],
                magicIds[3], magicIds[4], magicIds[5],
                0
        );

        boolean success = isUpdate ? CustomizeDAO.update(customize) : CustomizeDAO.insert(customize);

        if (success) {

            // ★ ここにアラートを追加！
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setHeaderText(null);

            if (isUpdate) {
                alert.setTitle("更新完了");
                alert.setContentText("カスタマイズ「" + name + "」を更新しました。");
            } else {
                alert.setTitle("登録完了");
                alert.setContentText("カスタマイズ「" + name + "」を新規作成しました。");
            }

            alert.showAndWait();
            // ★ ここまで

            System.out.println((isUpdate ? "🔄 更新: " : "✅ 登録: ") + name);

            List<CustomizeDAO.Customize> all = CustomizeDAO.getAll();
            comboBox.setItems(FXCollections.observableArrayList(all));

            for (CustomizeDAO.Customize c : all) {
                if (c.customizeId == customize.customizeId) {
                    comboBox.getSelectionModel().select(c);
                    break;
                }
            }
        }

    }

    @FXML
    void DeleteCustomize(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        CustomizeDAO.Customize selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("⚠ 削除するカスタマイズを選択してください");
            return;
        }

        boolean success = CustomizeDAO.deleteById(selected.customizeId);
        if (success) {
            System.out.println("🗑️ 削除: " + selected.name);
            comboBox.setItems(FXCollections.observableArrayList(CustomizeDAO.getAll()));
            comboBox.getSelectionModel().clearSelection();
            customizeNameField.clear();
            for (int i = 0; i < 6; i++) {
                getComboBoxByIndex(i).getSelectionModel().clearSelection();
                assignedMagics[i] = null;
            }
            updateTotalMp();
            updateComboBoxOptions();
        }
    }

    @FXML void Undo(ActionEvent e) { 
    	SEPlayer.play("イベント/click.mp3");
    	SceneManager.changeScene("Menu.fxml"); 
    }

    private void showCustomizeDetails(CustomizeDAO.Customize customize) {
        customizeNameField.setText(customize.name);
        Integer[] magicIds = {
                customize.magic1, customize.magic2, customize.magic3,
                customize.magic4, customize.magic5, customize.magic6
        };

        for (int i = 0; i < 6; i++) {
            ComboBox<String> comboBox = getComboBoxByIndex(i);
            Magic magic = (magicIds[i] != null) ? MagicDAO.getById(magicIds[i]) : null;
            assignedMagics[i] = magic;

            if (magic != null)
                comboBox.getSelectionModel().select(magic.name);
            else
                comboBox.getSelectionModel().clearSelection();
        }

        updateTotalMp();
        updateComboBoxOptions();
    }

    private void clearCustomizeDetails() {
    	SEPlayer.play("イベント/click.mp3");
        customizeNameField.clear();
        for (int i = 0; i < 6; i++) {
            assignedMagics[i] = null;
            getComboBoxByIndex(i).getSelectionModel().clearSelection();
        }
        updateTotalMp();
        clearMagicDetails();
    }

    private List<String> getAssignedMagicNames() {
        List<String> names = new ArrayList<>();
        for (Magic magic : assignedMagics)
            names.add(magic != null ? magic.name : "未選択");
        return names;
    }

    @FXML
    void NewCustomize(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        comboBox.getSelectionModel().clearSelection();
        customizeNameField.clear();
        for (int i = 0; i < 6; i++) {
            ComboBox<String> combo = getComboBoxByIndex(i);
            combo.getSelectionModel().clearSelection();
            assignedMagics[i] = null;
        }
        updateTotalMp();
        updateComboBoxOptions();
        clearMagicDetails();
        System.out.println("🆕 新規カスタマイズ作成モード");
    }

    @FXML private Button GoToMySetButton;
    @FXML private void handleGoToMySet(ActionEvent event) {
    	SEPlayer.play("イベント/click.mp3");
        SceneManager.changeScene("MySet.fxml");
    }
    
    
    /** ComboBox のロック状態を更新 */
    private void applyLockStatus() {
        applyLockToComboBox(magicComboBox4, updateUnLock(1));
        applyLockToComboBox(magicComboBox5, updateUnLock(2));
        applyLockToComboBox(magicComboBox6, updateUnLock(3));
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

    private void applyLockToComboBox(ComboBox<String> combo, boolean unlocked) {
        combo.setDisable(!unlocked);

        if (!unlocked) {
            combo.getSelectionModel().clearSelection();
        }
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
                    ・ここではオリジナルの魔法を作成できます。
                    ・魔法を選択してください→既存の魔法を呼び出すことが出来ます。
                    ・新規作成→名前やセットされている魔法を空欄にします
                    ・登録・更新→既存でなければ新規登録、
                      既存の魔法なら名前やセットされた魔法を更新できます。
                    ・削除→指定した既存の登録されている魔法を削除します。
                    ・テキストエリア→登録、または更新する際の名前を入力します
                    ・ボックス１～６→魔法を登録できます。
                    ・右側の空白→魔法をセットした際にその魔法のコードが表示されま
                      す
                    ・下の欄→セットした魔法のMP消費量や効果の分類、
                      属性などが表示されます。
                    ・マイセット作成へ→マイセット作成画面へと移ります。実際に使用す
                      るのはこのマイセットなので魔法をカスタマイズした際はこの画面に
                      移ってください。
                    ・＊セットする魔法は順番に発動するので状態異常確率アップなど入れる順番によっては効き目が変わります！気を付けてください！
                    ・ちなみに基礎技(アローやアローレインなど１枠目の技)がJavaでいうクラスにあたり、属性付与などがフィールド、状態異常確率アップやヒールなど追加効果がメソッドに当たります。それらを組み込んで作ったクラスをマイセットでインスタンス化し、それを呼び出すことで使用するというイメージです。
                    """);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
