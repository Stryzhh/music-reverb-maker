package Main.MainUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {
    @FXML private AnchorPane window;

    @FXML private ImageView musicIcon;
    @FXML private ImageView dragIcon;
    @FXML private ImageView minimizeIcon;
    @FXML private ImageView closeIcon;
    @FXML private Text name;
    @FXML private Text confirm;
    @FXML private JFXButton choose;
    @FXML private JFXButton play;
    @FXML private JFXButton save;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXSlider slowed;
    @FXML private JFXSlider balance;
    @FXML private JFXSlider volume;

    private File audio;
    private boolean playing = false;
    private MediaPlayer player;

    private final FadeTransition fade = new FadeTransition(Duration.seconds(7));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        musicIcon.setImage(new Image(new java.io.File("images\\play.png").toURI().toString()));
        dragIcon.setImage(new Image(new java.io.File("images\\drag.png").toURI().toString()));
        minimizeIcon.setImage(new Image(new java.io.File("images\\minimize.png").toURI().toString()));
        closeIcon.setImage(new Image(new java.io.File("images\\close.png").toURI().toString()));

        name.setVisible(false);
        play.setVisible(false);
        volume.setValue(100);
        confirm.setVisible(false);

        fade.setNode(confirm);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);

        slowed.valueProperty().addListener((value, aBoolean, t1) -> {
            if (audio != null && player != null) {
                player.setRate(slowed.getValue() / 100 + 0.50);
            }
        });
        balance.valueProperty().addListener((value, aBoolean, t1) -> {
            if (audio != null && player != null) {
                player.setBalance(-1 + balance.getValue() / 50);
            }
        });
        volume.valueProperty().addListener((value, aBoolean, t1) -> {
            if (audio != null && player != null) {
                player.setVolume(volume.getValue() / 100);
            }
        });

        choose.setOnAction(e -> choose());
        play.setOnAction(e -> play());
        save.setOnAction(e -> {
            try {
                save();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        window.setOnMousePressed(pressEvent -> dragIcon.setOnMouseDragged(dragEvent -> {
            window.getScene().getWindow().setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            window.getScene().getWindow().setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));

        minimize.setOnAction(e -> {
            Stage stage = (Stage) window.getScene().getWindow();
            stage.setIconified(true);
        });

        close.setOnAction(e -> System.exit(1));
    }

    private void choose() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File file = chooser.showOpenDialog(window.getScene().getWindow());

        if (file != null) {
            name.setVisible(true);
            name.setText(file.getName().substring(0, file.getName().length() - 4));
            play.setVisible(true);
            audio = file;

            if (player != null) {
                player.dispose();
                musicIcon.setImage(new Image(new java.io.File("images\\play.png").toURI().toString()));
                playing = false;
            }
            player = new MediaPlayer(new Media(audio.toURI().toString()));

            player.setOnEndOfMedia(() -> {
                player.stop();
                player.seek(Duration.seconds(0));
                musicIcon.setImage(new Image(new java.io.File("images\\play.png").toURI().toString()));
                playing = false;
            });

            slowed.setValue(50);
            balance.setValue(50);
            volume.setValue(100);
        }
    }

    private void play() {
        if (audio != null) {
            if (player != null) {
                if (!playing) {
                    player.play();
                    musicIcon.setImage(new Image(new java.io.File("images\\reset.png").toURI().toString()));
                    playing = true;
                } else {
                    player.stop();
                    player.seek(Duration.seconds(0));
                    musicIcon.setImage(new Image(new java.io.File("images\\play.png").toURI().toString()));
                    playing = false;
                }
            }
        }
    }

    private void save() throws IOException {
        FileChooser save = new FileChooser();
        save.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));

        File dest = save.showSaveDialog(window.getScene().getWindow());
        if (dest != null) {
            /* TODO
             *  Retrieve manipulated audio from media player
             *  Convert audio to file
             *  Save file as .mp3
             */
            if (!dest.exists()) {
                Files.copy(audio.toPath(), dest.toPath());
            } else {
                if (dest.delete()) {
                    Files.copy(audio.toPath(), dest.toPath());
                }
            }

            confirm.setVisible(true);
            fade.playFromStart();
        }
    }

}
