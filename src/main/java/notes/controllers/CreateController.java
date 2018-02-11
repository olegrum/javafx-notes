package notes.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import notes.util.DB;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreateController {
    private final int text_limit = 100;
    @FXML
    private TextField date_time;
    @FXML
    private TextField text;
    @FXML
    private CheckBox dateCheck;
    private String currDate_Time;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    protected void handleCancelButton(ActionEvent event) {
        Node source = (Node) event.getSource();
        Window Stage = source.getScene().getWindow();
        Stage.hide();
    }

    @FXML
    protected void handleCreateNoteButton(ActionEvent event) {
        if (!dateCheck.isSelected())
            try {
                format.parse(date_time.getText());
                currDate_Time = date_time.getText();
            } catch (DateTimeParseException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Date Error");
                alert.setHeaderText(null);
                alert.setContentText("Wrong date format. Try yyyy-MM-dd HH:mm:ss.");
                alert.showAndWait();
                return;
            }
        else currDate_Time = format.format(LocalDateTime.now());
        Task createRow = new Task<Void>() {
            final String finaldate = currDate_Time;
            final String finaltext = text.getText();

            @Override
            protected Void call() throws Exception {
                DB.updateDB("INSERT INTO " + DB.name + " (date_and_time,note_text) VALUES ('" + finaldate + "','" + finaltext + "')");
                return null;
            }
        };
        Thread thread = new Thread(createRow);
        thread.setDaemon(true);
        thread.start();

        Node source = (Node) event.getSource();
        Window Stage = source.getScene().getWindow();
        Stage.hide();
    }

    @FXML
    protected void handleDateCheck(ActionEvent event) {
        if (dateCheck.isSelected()) {
            date_time.clear();
            date_time.setDisable(true);
        } else {
            date_time.setDisable(false);
            date_time.setText(format.format(LocalDateTime.now()));
        }
    }

    @FXML
    protected void limitText() {
        if (text.getLength() >= text_limit) {
            text.setText(text.getText(0, text_limit - 1));
            text.positionCaret(text_limit);
        }
    }

    @FXML
    private void initialize() {
    }

}