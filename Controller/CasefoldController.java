/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.HWPFDocument;

/**
 *
 * @author exneval
 */
public class CasefoldController implements Initializable {

    private final SelectdocController selectDocController;
    private final Stage thisStage;
    private Alert alert;
    @FXML
    private TextArea srcTextArea;
    @FXML
    private TextArea testTextArea;
    @FXML
    private Text srcLabel;
    @FXML
    private Text testLabel;
    @FXML
    private Text timerText;
    @FXML
    private Button btnNext;
    @FXML
    private Label nextLabel;

    public CasefoldController(SelectdocController selectDocController) {
        this.selectDocController = selectDocController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/casefold.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            selectDocController.setCenterDashboard(loader.getRoot());
            selectDocController.imageStep1();
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readFile(String path, String ext, boolean isSource) {
        switch (ext) {
            case "txt":
                StringBuilder stringBuffer = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuffer.append(line).append(System.lineSeparator());
                    }
                    if (isSource) {
                        srcTextArea.appendText(stringBuffer.toString().toLowerCase());
                    } else {
                        testTextArea.appendText(stringBuffer.toString().toLowerCase());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CasefoldController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case "doc":
                File docFile = new File(path);
                try (FileInputStream fileInputStream = new FileInputStream(docFile)) {
                    HWPFDocument document = new HWPFDocument(fileInputStream);
                    WordExtractor extractor = new WordExtractor(document);
                    if (isSource) {
                        srcTextArea.appendText(extractor.getText().toLowerCase());
                    } else {
                        testTextArea.appendText(extractor.getText().toLowerCase());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CasefoldController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case "docx":
                File docxFile = new File(path);
                try (FileInputStream fileInputStream = new FileInputStream(docxFile)) {
                    XWPFDocument document = new XWPFDocument(fileInputStream);
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                    if (isSource) {
                        srcTextArea.appendText(extractor.getText().toLowerCase());
                    } else {
                        testTextArea.appendText(extractor.getText().toLowerCase());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CasefoldController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case "pdf":
                File pdfFile = new File(path);
                try (PDDocument document = PDDocument.load(pdfFile)) {
                    document.getClass();
                    if (!document.isEncrypted()) {
                        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                        stripper.setSortByPosition(true);
                        PDFTextStripper tStripper = new PDFTextStripper();
                        if (isSource) {
                            srcTextArea.appendText(tStripper.getText(document).toLowerCase());
                        } else {
                            testTextArea.appendText(tStripper.getText(document).toLowerCase());
                        }
                    } else {
                        if (isSource) {
                            srcTextArea.setText("Error: Can't show the content. This PDF file is encrypted.");
                        } else {
                            testTextArea.setText("Error: Can't show the content. This PDF file is encrypted.");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CasefoldController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }
    }

    public String getTextArea(boolean isSource) {
        return (isSource ? srcTextArea.getText() : testTextArea.getText());
    }

    public String getFileName(boolean isSource) {
        return (isSource ? selectDocController.getSourceFilePath() : selectDocController.getTestFilePath());
    }

    public String getFileExt(boolean isSource) {
        return (isSource ? selectDocController.getExtension(getFileName(true)) : selectDocController.getExtension(getFileName(false)));
    }

    private void showAlert() {
        alert = new Alert(AlertType.INFORMATION);
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.setHeaderText("Running Operation");
        alert.setContentText("Operation in progress." + System.lineSeparator() + "Please wait for a second...");
        DialogPane pane = alert.getDialogPane();
        pane.lookupButton(ButtonType.OK).setVisible(false);
        pane.getStylesheets().add(getClass().getResource("/plagdetectapp/resources/css/plagdetectapp.css").toExternalForm());
        pane.getStyleClass().add("myDialog");
        pane.getScene().setFill(Color.TRANSPARENT);
        ProgressBar progressBar = new ProgressBar();
        alert.setGraphic(progressBar);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        alert.show();
    }

    private void workingProgress() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Instant start = Instant.now();
                readFile(getFileName(true), getFileExt(true), true);
                readFile(getFileName(false), getFileExt(false), false);
                Instant end = Instant.now();
                timerText.setText("Generated in " + Duration.between(start, end).toMillis() + "ms.");
                return null;
            }
        };
        task.setOnRunning((e) -> showAlert());
        task.setOnSucceeded((e) -> {
            alert.close();
            btnNext.setVisible(true);
            nextLabel.setVisible(true);
        });
        new Thread(task).start();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        srcLabel.setText("Source" + System.lineSeparator() + "Document");
        testLabel.setText("Test" + System.lineSeparator() + "Document");
        workingProgress();
        btnNext.setOnMouseClicked((MouseEvent event) -> {
            TokenController tokenController = new TokenController(selectDocController, this);
        });
    }
}
