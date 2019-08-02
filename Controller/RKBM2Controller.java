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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
public class RKBM2Controller implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final FilterController filterController;
    private final StemController stemController;
    private final RKBMController rkbmController;
    private final Stage thisStage;
    @FXML
    private TextArea srcTextArea;
    @FXML
    private TextArea testTextArea;
    @FXML
    private Text kgramValText;
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

    public RKBM2Controller(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController, FilterController filterController, StemController stemController, RKBMController rkbmController) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        this.filterController = filterController;
        this.stemController = stemController;
        this.rkbmController = rkbmController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/rkbm2.fxml"));
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

    private void parseKgram() {
        String srcString = rkbmController.getTextArea(true);
        String testString = rkbmController.getTextArea(false);
        StringBuilder srcSB = new StringBuilder();
        int k = rkbmController.getKgramVal();
        int srcStrSize = srcString.length() - k;
        for (int i = 0; i <= srcStrSize; i++) {
            srcSB.append(srcString.substring(i, k)).append(" ");
            k++;
        }
        srcTextArea.setText(srcSB.toString());
        k = rkbmController.getKgramVal();
        StringBuilder testSB = new StringBuilder();
        int testStrSize = testString.length() - k;
        for (int i = 0; i <= testStrSize; i++) {
            testSB.append(testString.substring(i, k)).append(" ");
            k++;
        }
        testTextArea.setText(testSB.toString());
    }

    public String getTextArea(boolean isSource) {
        return (isSource ? srcTextArea.getText() : testTextArea.getText());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        srcLabel.setText("Source" + System.lineSeparator() + "Document");
        testLabel.setText("Test" + System.lineSeparator() + "Document");
        kgramValText.setText(Integer.toString(rkbmController.getKgramVal()));
        Instant start = Instant.now();
        parseKgram();
        Instant end = Instant.now();
        timerText.setText("Generated in " + Duration.between(start, end).toMillis() + "ms.");
        btnNext.setOnMouseClicked((MouseEvent event) -> {
            RKBM3Controller rb3Controller = new RKBM3Controller(selectDocController, caseFoldController, tokenController, filterController, stemController, rkbmController, this);
        });
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            RKBMController rbController = new RKBMController(selectDocController, caseFoldController, tokenController, filterController, stemController);
        });
    }

}
