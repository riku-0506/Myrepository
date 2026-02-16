package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class OptionDialogController {

    @FXML
    private ChoiceBox<String> ChoiceBoxBGM;
    @FXML
    private ChoiceBox<String> ChoiceBoxSE;
    @FXML
    private Button CancelButton;

    @FXML
    public void initialize() {

        // ★音量選択肢
        ObservableList<String> volumeOptions =
                FXCollections.observableArrayList("0%", "20%", "40%", "60%", "80%", "100%");

        ChoiceBoxBGM.setItems(volumeOptions);
        ChoiceBoxSE.setItems(volumeOptions);

        // 設定ファイルから読み込み
        String savedBgm = ConfigManager.get("bgmVolume", "60%");
        String savedSe  = ConfigManager.get("seVolume",  "60%");

        // BGM 音量を UI & BGMPlayer に反映
        ChoiceBoxBGM.setValue(savedBgm);
        BGMPlayer.setVolume(parsePercent(savedBgm));

        // SE 音量を UI & SEPlayer に反映
        ChoiceBoxSE.setValue(savedSe);
        SEPlayer.setVolume(parsePercent(savedSe));

        // BGM変更時
        ChoiceBoxBGM.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                BGMPlayer.setVolume(parsePercent(newV));
                ConfigManager.set("bgmVolume", newV);
                System.out.println("BGM音量: " + newV);
            }
        });

        // SE変更時
        ChoiceBoxSE.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                SEPlayer.setVolume(parsePercent(newV));
                ConfigManager.set("seVolume", newV);
                System.out.println("SE音量: " + newV);
                SEPlayer.play("イベント/click.mp3");
            }
        });

        CancelButton.setOnAction(e -> CancelButton.getScene().getWindow().hide());
    }

    private double parsePercent(String value) {
        return Integer.parseInt(value.replace("%", "")) / 100.0;
    }
}
