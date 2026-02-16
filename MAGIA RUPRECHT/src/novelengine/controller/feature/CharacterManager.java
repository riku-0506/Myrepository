package novelengine.controller.feature;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import novelengine.model.CharacterData;

public class CharacterManager {

    private Pane characterLayer = new Pane();

    // image (ファイル名) -> 代表的な ImageView（ただし複数スロットで同一 ImageView を使えない場合はクローンして使う）
    private Map<String, ImageView> characterViews = new HashMap<>();

    // 名前 -> image（speakerName から該当 image を見つけるため）
    private Map<String, String> nameToImage = new HashMap<>();

    // 左・中央・右のスロット（現在スロットに置かれている ImageView）
    private ImageView leftSlot = null;
    private ImageView centerSlot = null;
    private ImageView rightSlot = null;

    private String activeSpeaker = null;
    private final double DEFAULT_DIM = 0.6;

    public Pane getNode() { return characterLayer; }

    // ==========================================================
    //   統合画像ローダー
    // ==========================================================
    private Image loadCharacterImage(String fileName) {

        // クラスパス
        String resourcePath = "/novelengine/data/images/characters/" + fileName;
        var url = getClass().getResource(resourcePath);
        if (url != null) {
            return new Image(url.toExternalForm());
        }

        // ローカルフォルダ
        File file = new File("data/images/characters/" + fileName);
        if (file.exists()) {
            return new Image(file.toURI().toString());
        }

        System.err.println("キャラクター画像が見つかりません: " + fileName);
        return null;
    }

    // ==========================================================
    //   ヘルパ: ImageView をスロット用に用意する
    //   - 同じ imageKey の代表 ImageView が既に他スロットで使われている場合はクローンを作成
    //   - そうでなければ代表 ImageView を返す（代表がなければ新規作成して Map に登録）
    // ==========================================================
    private ImageView obtainImageViewForImage(String imageKey, Image img) {
        ImageView canonical = characterViews.get(imageKey);
        if (canonical == null) {
            canonical = new ImageView(img);
            canonical.setPreserveRatio(true);
            canonical.setFitHeight(600);
            // 初期は暗めにしておく（フォーカス時に明るくする）
            ColorAdjust adjust = new ColorAdjust();
            adjust.setBrightness(-DEFAULT_DIM);
            canonical.setEffect(adjust);
            canonical.setScaleX(1.5);
            canonical.setScaleY(1.5);

            // 代表として登録（charName はスロット割当時にセット）
            characterViews.put(imageKey, canonical);
            return canonical;
        }

        // 代表が既に別スロットにあるか調べる
        boolean isInLeft = canonical == leftSlot;
        boolean isInCenter = canonical == centerSlot;
        boolean isInRight = canonical == rightSlot;

        // もし既にどこかのスロットに割り当てられていて、かつそのスロットとは別のスロットへ入れようとする場合
        // 同一 ImageView を複数の親ノードで使えないためクローンを作る
        if (isInLeft || isInCenter || isInRight) {
            // クローン（同じ Image オブジェクトを再利用）
            ImageView clone = new ImageView(canonical.getImage());
            clone.setPreserveRatio(true);
            clone.setFitHeight(canonical.getFitHeight());
            // 同じ見た目の設定をコピー
            if (canonical.getEffect() instanceof ColorAdjust) {
                ColorAdjust adjust = new ColorAdjust();
                adjust.setBrightness(((ColorAdjust) canonical.getEffect()).getBrightness());
                clone.setEffect(adjust);
            }
            clone.setScaleX(canonical.getScaleX());
            clone.setScaleY(canonical.getScaleY());
            // UserData/properties はスロット割当時に上書きする
            return clone;
        }

        // 代表がまだどのスロットにも割り当てられていなければそのまま返す
        return canonical;
    }

    // ==========================================================
    //   キャラクター配置（image をキーにして再利用。ただし必要に応じてクローン）
    // ==========================================================
    public void loadCharactersWithSlots(List<CharacterData> characters) {
        if (characters == null) return;

        // 毎回 name->image マップを更新する（古い対応は上書き）
        nameToImage.clear();

        // スロットに割当てる ImageView は前回のまま残すが、
        // 新たに割当てられるスロットがある場合は前のスロットの ImageView を除去する処理を行う。
        // まず、現状のスロット割当は変更前のままにしておき、ループ内で差分処理をする。

        for (CharacterData character : characters) {
            if (character.getName() == null || character.getImage() == null) continue;

            String imgKey = character.getImage();
            nameToImage.put(character.getName(), imgKey);

            Image img = loadCharacterImage(imgKey);
            if (img == null) continue;

            // 代表的な ImageView を取得（必要ならクローン）
            ImageView candidate = obtainImageViewForImage(imgKey, img);

            // スロット割当前に、candidate にキャラ名を紐づけ（プロパティ）
            candidate.getProperties().put("charName", character.getName());
            candidate.getProperties().put("imageKey", imgKey);

            // スロットに配置する前に、位置・向き・初期効果等を調整する
            candidate.setFitHeight(600); // 保険
            candidate.setPreserveRatio(true);

            // 基本Y
            double baseY = 720 - candidate.getFitHeight();

            ImageView previousForTarget = null;
            switch (character.getPosition()) {
                case "left":
                    previousForTarget = leftSlot;
                    // もし previousForTarget と candidate が別なら previous をレイヤーから取り除く
                    if (previousForTarget != null && previousForTarget != candidate) {
                        characterLayer.getChildren().remove(previousForTarget);
                    }
                    leftSlot = candidate;
                    candidate.setLayoutX(100);
                    candidate.setLayoutY(baseY);
                    break;

                case "center":
                    previousForTarget = centerSlot;
                    if (previousForTarget != null && previousForTarget != candidate) {
                        characterLayer.getChildren().remove(previousForTarget);
                    }
                    centerSlot = candidate;
                    candidate.setLayoutX(1280 - 900);
                    candidate.setLayoutY(baseY);
                    break;

                case "right":
                    previousForTarget = rightSlot;
                    if (previousForTarget != null && previousForTarget != candidate) {
                        characterLayer.getChildren().remove(previousForTarget);
                    }
                    rightSlot = candidate;
                    candidate.setLayoutX(1280 - 500);
                    candidate.setLayoutY(baseY);
                    break;

                default:
                    previousForTarget = leftSlot;
                    if (previousForTarget != null && previousForTarget != candidate) {
                        characterLayer.getChildren().remove(previousForTarget);
                    }
                    leftSlot = candidate;
                    candidate.setLayoutX(100);
                    candidate.setLayoutY(baseY);
                    break;
            }

            // 向き（左右反転）
            if ("left".equalsIgnoreCase(character.getFacing())) {
                candidate.setScaleX(Math.abs(candidate.getScaleX()));
            } else if ("right".equalsIgnoreCase(character.getFacing())) {
                candidate.setScaleX(-Math.abs(candidate.getScaleX()));
            }

            // フェードイン処理 — 常に確実に動くように opacity を 0 にしてから再生
            if (character.isFadeIn()) {
                candidate.setOpacity(0.0);
                FadeTransition fade = new FadeTransition(
                    Duration.seconds(character.getFadeInSeconds()), candidate
                );
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();
            } else {
                candidate.setOpacity(1.0);
            }

            // レイヤーに追加（既にあれば add は子の移動になるので安全）
            if (!characterLayer.getChildren().contains(candidate)) {
                characterLayer.getChildren().add(candidate);
            } else {
                // 既に含まれている場合、ensure it's on top of background but below text etc.
                // ここでは追加処理は行わない（既に表示状態）
            }
        }

        // --- 注意 ---
        // ループ終了後、スロットに割り当てられていない（以前はいたが今回はいない） ImageView が
        // characterLayer に残っている場合がある。完全に消したければ clearAllCharacters() を使うか、
        // ここで nameToImage を元に差分で削除処理を行う実装を追加できます。
    }

    // ==========================================================
    //   発話者フォーカス（name -> image で対応付けを行う）
    // ==========================================================
    public void updateCharacterFocus(String speakerName) {

        // 全員暗くする（speakerName null の場合）
        if (speakerName == null || speakerName.isEmpty()) {
            for (ImageView iv : new ImageView[]{ leftSlot, centerSlot, rightSlot }) {
                if (iv == null) continue;
                ColorAdjust adjust = new ColorAdjust();
                adjust.setBrightness(-DEFAULT_DIM);
                iv.setEffect(adjust);
            }
            activeSpeaker = null;
            return;
        }

        this.activeSpeaker = speakerName;

        // speakerName -> imageKey を得る
        String speakerImage = nameToImage.get(speakerName);

        // 各スロットをチェックして、speaker と同一なら明るく、それ以外は暗くする
        ImageView[] slots = { leftSlot, centerSlot, rightSlot };
        for (ImageView iv : slots) {
            if (iv == null) continue;

            Object imageKeyObj = iv.getProperties().get("imageKey");
            String ivImageKey = (imageKeyObj instanceof String) ? (String) imageKeyObj : null;

            if (speakerImage != null && speakerImage.equals(ivImageKey)) {
                // 明るく（エフェクト解除）
                iv.setEffect(null);
            } else {
                ColorAdjust adjust = new ColorAdjust();
                adjust.setBrightness(-DEFAULT_DIM);
                iv.setEffect(adjust);
            }
        }
    }

    // ==========================================================
    //   スロット表示制御
    // ==========================================================
    public void showAllCharacters() {
        if (leftSlot != null) leftSlot.setVisible(true);
        if (centerSlot != null) centerSlot.setVisible(true);
        if (rightSlot != null) rightSlot.setVisible(true);
    }

    public void showOnlyActiveCharacter() {
        // speakerName -> imageKey で一致判定
        String speakerImage = (activeSpeaker == null) ? null : nameToImage.get(activeSpeaker);
        ImageView[] slots = { leftSlot, centerSlot, rightSlot };
        for (ImageView iv : slots) {
            if (iv == null) continue;
            Object imageKeyObj = iv.getProperties().get("imageKey");
            String ivImageKey = (imageKeyObj instanceof String) ? (String) imageKeyObj : null;
            iv.setVisible(speakerImage != null && speakerImage.equals(ivImageKey));
        }
    }

    // ==========================================================
    //   スロットクリア
    // ==========================================================
    public void clearCharactersBySlot(String clearType) {

        switch (clearType.toLowerCase()) {

            case "left":
                if (leftSlot != null) {
                    characterLayer.getChildren().remove(leftSlot);
                }
                leftSlot = null;
                break;

            case "center":
                if (centerSlot != null) {
                    characterLayer.getChildren().remove(centerSlot);
                }
                centerSlot = null;
                break;

            case "right":
                if (rightSlot != null) {
                    characterLayer.getChildren().remove(rightSlot);
                }
                rightSlot = null;
                break;

            case "all":
                clearAllCharacters();
                break;
        }
    }

    // ==========================================================
    //   全クリア
    // ==========================================================
    public void clearAllCharacters() {
        characterLayer.getChildren().clear();
        leftSlot = null;
        centerSlot = null;
        rightSlot = null;
        characterViews.clear();
        nameToImage.clear();
        activeSpeaker = null;
    }

    public Map<String, ImageView> getCharacterViews() {
        return characterViews;
    }
}
