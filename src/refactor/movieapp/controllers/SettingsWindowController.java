/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package refactor.movieapp.controllers;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.converter.DefaultStringConverter;
import refactor.movieapp.util.SceneManager;
import refactor.movieapp.util.Settings;

/**
 *
 * @author kristof
 */
public class SettingsWindowController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ListView<String> extensions, directories;
    @FXML
    private TextField extField, dirField;

    private final Set<String> extToDel;
    private final Set<String> extToAdd;
    private final Set<String> dirToDel;
    private final Set<String> dirToAdd;
    private final ObservableList<String> extensionsContent;
    private final ObservableList<String> directoriesContent;
    private final SimpleObjectProperty<ContextMenuListCell> selectedCell;
    private DirectoryChooser dirchooser;

    public SettingsWindowController() {
        extToDel = new HashSet<>();
        extToAdd = new HashSet<>();
        dirToDel = new HashSet<>();
        dirToAdd = new HashSet<>();
        extensionsContent = FXCollections.observableArrayList();
        directoriesContent = FXCollections.observableArrayList();
        selectedCell = new SimpleObjectProperty<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        extensions.setItems(new SortedList<>(extensionsContent, String.CASE_INSENSITIVE_ORDER));
        directories.setItems(new SortedList<>(directoriesContent, String.CASE_INSENSITIVE_ORDER));
        extField.setOnKeyPressed(e -> {
            if (KeyCode.ENTER.equals(e.getCode())) {
                addExt();
            }
        });
        dirField.setOnKeyPressed(e -> {
            if (KeyCode.ENTER.equals(e.getCode())) {
                addDir();
            }
        });
        final EventHandler<MouseEvent> onMouseClickFilter = e -> {
            if (!(e.getTarget() instanceof ListCell)) {
                ContextMenuListCell cell = selectedCell.get();
                if (cell != null && cell.isEditing()) {
                    cell.cancelEdit();
                }
            }
        };
        root.sceneProperty().addListener((s, o, n) -> {
            if (o != null) {
                o.removeEventFilter(MouseEvent.MOUSE_CLICKED, onMouseClickFilter);
            }
            if (n != null) {
                extensionsContent.setAll(Settings.getExtensions());
                directoriesContent.setAll(Settings.getDirectories());
                n.addEventFilter(MouseEvent.MOUSE_CLICKED, onMouseClickFilter);
            }
        });

        selectedCell.addListener((s, o, n) -> {
            if (o != null && o.isEditing()) {
                o.cancelEdit();
            }
        });

        final MenuItem edit = new MenuItem("edit"), remove = new MenuItem("remove");
        final ContextMenu contextMenu = new ContextMenu(edit, remove);
        edit.setOnAction(e -> selectedCell.get().edit());
        remove.setOnAction(e -> selectedCell.get().remove());

        extensions.setCellFactory(param -> new ContextMenuListCell(contextMenu) {
            @Override
            public void commit(String newValue) {
                super.commitEdit(newValue);
                String oldValue = getOldValue();
                if (!(oldValue == null || oldValue.equalsIgnoreCase(newValue))) {
                    removeExt(oldValue);
                    addExt(newValue);
                }
            }

            @Override
            public void remove() {
                removeExt(getText());
            }
        });
        directories.setCellFactory(param -> new ContextMenuListCell(contextMenu) {
            @Override
            public void commit(String newValue) {
                super.commitEdit(newValue);
                String oldValue = getOldValue();
                if (!(oldValue == null || oldValue.equalsIgnoreCase(newValue))) {
                    removeDir(oldValue);
                    addDir(newValue);
                }
            }

            @Override
            public void remove() {
                removeDir(getText());
            }
        });
    }

    @FXML
    private void save() {
        Settings.removeExtensions(extToDel);
        Settings.addExtensions(extToAdd);
        Settings.removeDirectories(dirToDel);
        Settings.addDirectories(dirToAdd);
        clear();
        exit();
    }

    private void clear() {
        extToDel.clear();
        extToAdd.clear();
        dirToDel.clear();
        dirToAdd.clear();
    }

    @FXML
    private void exit() {
        clear();
        SceneManager.previousRoot();
    }

    @FXML
    private void openFileChooser() {
        if (dirchooser == null) {
            dirchooser = new DirectoryChooser();
        }
        File dir = dirchooser.showDialog(root.getScene().getWindow());
        if (dir != null) {
            dirField.setText(dir.toString());
        }
    }

    @FXML
    private void addDir() {
        addDir(dirField.getText());

    }

    @FXML
    private void addExt() {
        addExt(extField.getText());

    }

    @FXML
    private void resetDirs() {
        Settings.resetDirectories();
        directoriesContent.setAll(Settings.getDirectories());
    }

    @FXML
    private void resetExts() {
        Settings.resetExtensions();
        extensionsContent.setAll(Settings.getExtensions());
    }

    private void addDir(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            dir = file.getAbsolutePath();
            if (dirToDel.remove(dir)) {
                directoriesContent.add(dir);
            } else {
                dirToAdd.add(dir);
                if (!directoriesContent.contains(dir)) {
                    directoriesContent.add(dir);
                }
            }
        }
        dirField.clear();
    }

    private void addExt(String ext) {
        if (!ext.isEmpty()) {
            if (extToDel.remove(ext)) {
                extensionsContent.add(ext);
            } else {
                extToAdd.add(ext);
                if (!extensionsContent.contains(ext)) {
                    extensionsContent.add(ext);
                }
            }
        }
        extField.clear();
    }

    private void removeDir(String dir) {
        if (!dirToAdd.remove(dir)) {
            dirToDel.add(dir);
        }
        directoriesContent.remove(dir);

    }

    private void removeExt(String ext) {
        if (!extToAdd.remove(ext)) {
            extToDel.add(ext);
        }
        extensionsContent.remove(ext);
    }

    private abstract class ContextMenuListCell extends TextFieldListCell<String> {

        private String oldValue;

        private ContextMenuListCell(ContextMenu menu) {
            super(new DefaultStringConverter());
            setContextMenu(menu);
            setOnMouseClicked(e -> selectedCell.set(this));
        }

        public String getOldValue() {
            return oldValue;
        }

        @Override
        public void startEdit() {

        }

        public void edit() {
            oldValue = getText();
            super.startEdit();

        }

        public abstract void commit(String newValue);

        public abstract void remove();
    }
}
