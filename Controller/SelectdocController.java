/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author exneval
 */
public class SelectdocController implements Initializable {

    private final DashboardController dashboardController;
    private final Stage thisStage;
    private File selectSourceFile;
    private File selectTestFile;
    @FXML
    private BorderPane detectPane;
    @FXML
    private AnchorPane detectCenterPane;
    @FXML
    private Text actionStatus1;
    @FXML
    private Text actionStatus2;
    @FXML
    private ImageView imgSourceDoc;
    @FXML
    private ImageView imgTestDoc;
    @FXML
    private Button btnNext;
    @FXML
    private FontAwesomeIconView imgStep1;
    @FXML
    private FontAwesomeIconView imgStep2;
    @FXML
    private FontAwesomeIconView imgStep3;
    @FXML
    private FontAwesomeIconView imgStep4;
    @FXML
    private FontAwesomeIconView imgStep5;
    @FXML
    private FontAwesomeIconView imgStep6;
    @FXML
    private Button btnChoose1;
    @FXML
    private Button btnChoose2;
    @FXML
    private Text pathStatus1;
    @FXML
    private Text pathStatus2;

    public SelectdocController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/selectdoc.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            dashboardController.setCenterDashboard(loader.getRoot());
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkUpload() {
        if (selectSourceFile != null && selectTestFile != null) {
            btnNext.setVisible(true);
        } else {
            btnNext.setVisible(false);
        }
    }

    public void imageStep1() {
        imgStep1.setGlyphName("CHECK");
        imgStep1.setFill(Color.GREEN);
        imgStep2.setGlyphName("TIMES");
        imgStep2.setFill(Color.RED);
    }

    public void imageStep2() {
        imgStep2.setGlyphName("CHECK");
        imgStep2.setFill(Color.GREEN);
        imgStep3.setGlyphName("TIMES");
        imgStep3.setFill(Color.RED);
    }

    public void imageStep3() {
        imgStep3.setGlyphName("CHECK");
        imgStep3.setFill(Color.GREEN);
        imgStep4.setGlyphName("TIMES");
        imgStep4.setFill(Color.RED);
    }

    public void imageStep4() {
        imgStep4.setGlyphName("CHECK");
        imgStep4.setFill(Color.GREEN);
        imgStep5.setGlyphName("TIMES");
        imgStep5.setFill(Color.RED);
    }

    public void imageStep5() {
        imgStep5.setGlyphName("CHECK");
        imgStep5.setFill(Color.GREEN);
        imgStep6.setGlyphName("TIMES");
        imgStep6.setFill(Color.RED);
    }

    public void imageStep6() {
        imgStep6.setGlyphName("CHECK");
        imgStep6.setFill(Color.GREEN);
    }

    public String getSourceFileName() {
        return selectSourceFile.getName();
    }

    public String getTestFileName() {
        return selectTestFile.getName();
    }

    public String getSourceFilePath() {
        return selectSourceFile.getAbsolutePath();
    }

    public String getTestFilePath() {
        return selectTestFile.getAbsolutePath();
    }

    public String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    public void setCenterDashboard(Parent root) {
        detectPane.setCenter(root);
    }

    public Stage getDasboardStage() {
        return dashboardController.getStage();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            BufferedImage img1 = ImageIO.read(getClass().getResourceAsStream("/plagdetectapp/resources/img/icons8-ok.png"));
            BufferedImage img2 = ImageIO.read(getClass().getResourceAsStream("/plagdetectapp/resources/img/icons8-cancel.png"));
            Image imageOK = SwingFXUtils.toFXImage(img1, null);
            Image imageCancel = SwingFXUtils.toFXImage(img2, null);
            btnChoose1.setOnMouseClicked((MouseEvent event) -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                        new FileChooser.ExtensionFilter("Docx Files", "*.docx"),
                        new FileChooser.ExtensionFilter("Doc Files", "*.doc"),
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );
                Stage stage = (Stage) detectCenterPane.getScene().getWindow();
                selectSourceFile = fileChooser.showOpenDialog(stage);
                if (selectSourceFile != null) {
                    String path = (getSourceFilePath().length() > 60 ? (getSourceFilePath().substring(0, 60) + "...") : getSourceFilePath());
                    actionStatus1.setText("Source Document" + System.lineSeparator() + "selection succeed.");
                    actionStatus1.setFill(Color.GREEN);
                    pathStatus1.setText(path);
                    imgSourceDoc.setImage(imageOK);
                } else {
                    actionStatus1.setText("Source Document" + System.lineSeparator() + "selection cancelled.");
                    actionStatus1.setFill(Color.RED);
                    pathStatus1.setText("Cancelled. Waiting for the file...");
                    imgSourceDoc.setImage(imageCancel);
                }
                checkUpload();
            });
            btnChoose2.setOnMouseClicked((MouseEvent event) -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                        new FileChooser.ExtensionFilter("Docx Files", "*.docx"),
                        new FileChooser.ExtensionFilter("Doc Files", "*.doc"),
                        new FileChooser.ExtensionFilter("Text Files", "*.txt")
                );
                Stage stage = (Stage) detectCenterPane.getScene().getWindow();
                selectTestFile = fileChooser.showOpenDialog(stage);
                if (selectTestFile != null) {
                    String path = (getTestFilePath().length() > 60 ? (getTestFilePath().substring(0, 60) + "...") : getTestFilePath());
                    actionStatus2.setText("Test Document" + System.lineSeparator() + "selection succeed.");
                    actionStatus2.setFill(Color.GREEN);
                    pathStatus2.setText(path);
                    imgTestDoc.setImage(imageOK);
                } else {
                    actionStatus2.setText("Test Document" + System.lineSeparator() + "selection cancelled.");
                    actionStatus2.setFill(Color.RED);
                    pathStatus2.setText("Cancelled. Waiting for the file...");
                    imgTestDoc.setImage(imageCancel);
                }
                checkUpload();
            });
            btnNext.setOnMouseClicked((MouseEvent event) -> {
                CasefoldController caseFoldController = new CasefoldController(this);
            });
        } catch (IOException ex) {
            Logger.getLogger(SelectdocController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
