/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author exneval
 */
public class RKBMController implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final FilterController filterController;
    private final StemController stemController;
    private final Stage thisStage;
    private int kgramVal;
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
    private Button btnPrev;

    public RKBMController(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController, FilterController filterController, StemController stemController) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        this.filterController = filterController;
        this.stemController = stemController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/rkbm.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            selectDocController.setCenterDashboard(loader.getRoot());
            selectDocController.imageStep5();
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void parseString() {
        Set<String> srcStemSet = stemController.getStemSet(true);
        Set<String> testStemSet = stemController.getStemSet(false);
        StringBuilder srcSB = new StringBuilder();
        srcStemSet.forEach((stem) -> {
            srcSB.append(stem);
        });
        srcTextArea.setText(srcSB.toString());
        StringBuilder testSB = new StringBuilder();
        testStemSet.forEach((stem) -> {
            testSB.append(stem);
        });
        testTextArea.setText(testSB.toString());
    }

    public String getTextArea(boolean isSource) {
        return (isSource ? srcTextArea.getText() : testTextArea.getText());
    }

    private void setKgramDialog() {
        TextInputDialog kgramDialog = new TextInputDialog();
        kgramDialog.initStyle(StageStyle.UTILITY);
        kgramDialog.setTitle("Plagiarism Detection Application");
        kgramDialog.setHeaderText("Set k-gram value for proceeding to the next phase");
        kgramDialog.setContentText("K-gram value :");
        Optional<String> result = kgramDialog.showAndWait();
        if (result.isPresent()) {
            try {
                kgramVal = Integer.parseInt(result.get());
            } catch (NumberFormatException e) {
                kgramVal = 0;
            }
            if (kgramVal > 0) {
                RKBM2Controller rkbm2Controller = new RKBM2Controller(selectDocController, caseFoldController, tokenController, filterController, stemController, this);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Plagiarism Detection Application");
                alert.setHeaderText("Error Invalid input");
                alert.setContentText("K-gram value can't be zero. Please set the correct number!");
                alert.showAndWait();
            }
        }
    }

    public int getKgramVal() {
        return kgramVal;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        srcLabel.setText("Source" + System.lineSeparator() + "Document");
        testLabel.setText("Test" + System.lineSeparator() + "Document");
        Instant start = Instant.now();
        parseString();
        Instant end = Instant.now();
        timerText.setText("Generated in " + Duration.between(start, end).toMillis() + "ms.");
        btnNext.setOnMouseClicked((MouseEvent event) -> {
            setKgramDialog();
        });
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            StemController stmController = new StemController(selectDocController, caseFoldController, tokenController, filterController);
        });
    }

}
