package notes.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notes.models.Note;
import notes.util.DB;

import java.sql.ResultSet;
import java.util.List;

public class MainController {
    @FXML
    private ProgressIndicator PI;
    @FXML
    private TableView<Note> table;
    @FXML
    private TableColumn<Note, String> dtcol;
    @FXML
    private TableColumn<Note, String> textcol;

    @FXML
    protected void handleDeleteButton(ActionEvent event) {
        if (table.getSelectionModel().getSelectedItem() == null) return;
        Note toDelete = table.getSelectionModel().getSelectedItem();
        deleteSelection(toDelete);
    }

    @FXML
    protected void handleCreateButton(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("create_layout.fxml"));
        Stage createWindow = new Stage();
        createWindow.setTitle("Create Note");
        createWindow.setScene(new Scene(root, 400, 100));
        createWindow.initModality(Modality.APPLICATION_MODAL);
        createWindow.setResizable(false);
        createWindow.initStyle(StageStyle.UTILITY);
        createWindow.setOnHidden(e -> printFullTable());
        createWindow.show();
    }

    @FXML
    private void initialize() {
        Thread thread = new Thread(init);
        thread.setDaemon(true);
        thread.start();
    }

    private Task init = new Task<Void>() {
        @Override
        protected Void call() {
            PI.setVisible(true);
            DB.createTable();
            dtcol.setCellValueFactory(cellData -> cellData.getValue().DateTimeProperty());
            textcol.setCellValueFactory(cellData -> cellData.getValue().TextProperty());
            printFullTable();
            return null;
        }

        @Override
        protected void succeeded() {
            PI.setVisible(false);
        }
    };

    private void printFullTable() {
        final String query = "SELECT * FROM " + DB.name;
        Task<List<Note>> task = new Task<List<Note>>() {
            @Override
            protected ObservableList<Note> call() throws Exception {
                PI.setVisible(true);
                ObservableList<Note> tabledata = FXCollections.observableArrayList();
                ResultSet resultSet = DB.queryDB(query);
                while (resultSet.next()) {
                    Note note = new Note();
                    note.setDateTime(resultSet.getString("date_and_time"));
                    note.setText(resultSet.getString("note_text"));
                    tabledata.add(note);
                }
                return tabledata;
            }
        };
        task.setOnSucceeded(e -> {
            table.setItems((ObservableList<Note>) task.getValue());
            PI.setVisible(false);
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void deleteSelection(Note toDelete) {
        final String query = "DELETE FROM " + DB.name + " WHERE date_and_time = '" + toDelete.getDateTime() + "' AND note_text = '" + toDelete.getText() + "'";
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                PI.setVisible(true);
                DB.updateDB(query);
                return null;
            }

            @Override
            protected void succeeded() {
                printFullTable();
                PI.setVisible(false);
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
