/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author kristof
 */
public class FileCopyTask extends AnchorPane implements Initializable {

    private final CopyTask task;
    @FXML
    private Label progresslabel, infofrom, infoto;
    @FXML
    private ProgressBar progressbar;
    @FXML
    private ImageView go, pause, cancel;
    private final String infofromtext, infototext;
    private final Thread th;

    public FileCopyTask(File srcfile, File destdir, String filename) {
        task = new CopyTask(srcfile, new File(destdir, filename));
        th = new Thread(task);
        infofromtext = String.format("copying %s", filename);
        infototext = String.format("to %s", destdir);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fileCopyTask.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("failed to make FileCopyTask");
        }
    }

    public Thread startTask() {
        th.start();
        return th;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infofrom.setText(infofromtext);
        infoto.setText(infototext);
        go.setVisible(false);
        go.setOnMouseClicked(e -> {
            go.setVisible(false);
            pause.setVisible(true);
            task.go();
        });
        pause.setOnMouseClicked(e -> {
            pause.setVisible(false);
            go.setVisible(true);
            task.pause();
            progresslabel.setText("paused");
        });
        cancel.setOnMouseClicked(e -> {
            go.setVisible(false);
            pause.setVisible(false);
            cancel.setVisible(false);
            task.cancel();
        });
        task.progressProperty().addListener((s, o, n) -> Platform.runLater(() -> {
            progressbar.setProgress((double) n == 1.0 ? ProgressBar.INDETERMINATE_PROGRESS : (double) n);
            progresslabel.setText(String.format("%.2f%% complete", (double) n * 100));
        }));
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            progressbar.setProgress(1.0);
            progresslabel.setText("Done");
        }));
        task.setOnCancelled(e -> {
            Platform.runLater(() -> {
                System.out.println("test");
                progressbar.setProgress(1.0);
                progresslabel.setText("Cancelled");
            });
        });
    }

    public class CopyTask extends Task<Void> {

        private final File srcfile;
        private final File destfile;
        private final long totalsize;
        private final byte[] buffer;
        private boolean paused;

        public CopyTask(File srcfile, File destfile) {
            this.srcfile = srcfile;
            this.destfile = destfile;
            totalsize = srcfile.length();
            paused = false;
            buffer = new byte[1024];
        }

        public File getSrcfile() {
            return srcfile;
        }

        public File getDestfile() {
            return destfile;
        }

        public void pause() {
            paused = true;
        }

        public void go() {
            paused = false;
        }

        @Override
        protected Void call() {
            try (FileInputStream is = new FileInputStream(srcfile);
                    FileOutputStream os = new FileOutputStream(destfile)) {
                double count = 0.01;
                long copiedsize = 0;
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                    copiedsize += length;
                    double val = (double) copiedsize / totalsize;
                    if (val > count) {
                        count += 0.01;
                        os.getFD().sync();
                    }
                    if (isCancelled()) {
                        break;
                    }
                    while (paused) {
                        Thread.sleep(100);
                    }
                    updateProgress(copiedsize, totalsize);
                }

            } catch (IOException | InterruptedException ex) {
                System.err.println("Failed to copy files");
            } finally {
                if (isCancelled()) {
                    destfile.delete();
                }
            }
            System.out.println("very last thing in run");
            return null;
        }

    }

}
