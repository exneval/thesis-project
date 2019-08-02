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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author exneval
 */
public class RKBM3Controller implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final FilterController filterController;
    private final StemController stemController;
    private final RKBMController rkbmController;
    private final RKBM2Controller rkbm2Controller;
    private final Stage thisStage;
    private final int NO_OF_CHARS = 65536;
    private final int BASE_VALUE = 10;
    private final int MODULO_PRIME = 100151;
    private int srcHashCount;
    private int testHashCount;
    private int matchHashCount;
    private Map<String, Integer> srcMap;
    private Map<String, Integer> testMap;
    private String srcHtml;
    private String testHtml;
    private Alert alert;
    @FXML
    private Button btnNext;
    @FXML
    private Label nextLabel;
    @FXML
    private Button btnPrev;
    @FXML
    private Text srcLabel;
    @FXML
    private Text testLabel;
    @FXML
    private Text timerText;
    @FXML
    private WebView srcWebview;
    @FXML
    private WebView testWebview;

    public RKBM3Controller(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController, FilterController filterController, StemController stemController, RKBMController rkbmController, RKBM2Controller rkbm2Controller) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        this.filterController = filterController;
        this.stemController = stemController;
        this.rkbmController = rkbmController;
        this.rkbm2Controller = rkbm2Controller;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/rkbm3.fxml"));
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
        Set<String> srcStrSet = new LinkedHashSet<>(Arrays.asList(rkbm2Controller.getTextArea(true).split("\\W+")));
        srcMap = new LinkedHashMap<>();
        srcStrSet.forEach((str) -> {
            srcMap.put(str, 0);
        });
        Set<String> testStrSet = new LinkedHashSet<>(Arrays.asList(rkbm2Controller.getTextArea(false).split("\\W+")));
        testMap = new LinkedHashMap<>();
        testStrSet.forEach((str) -> {
            testMap.put(str, 0);
        });
    }

    private void parseHashing() {
        Iterator<String> srcItr = srcMap.keySet().iterator();
        while (srcItr.hasNext()) {
            String key = srcItr.next();
            int hashVal;
            double val = 0.0;
            int k = 0;
            for (int j = 0; j < key.length(); j++) {
                val += key.charAt(j) * Math.pow(BASE_VALUE, k);
                k++;
            }
            val %= MODULO_PRIME;
            hashVal = (int)val;
            srcMap.put(key, hashVal);
            srcHashCount++;
        }
        Iterator<String> testItr = testMap.keySet().iterator();
        while (testItr.hasNext()) {
            String key = testItr.next();
            int hashVal;
            double val = 0.0;
            int k = 0;
            for (int j = 0; j < key.length(); j++) {
                val += key.charAt(j) * Math.pow(BASE_VALUE, k);
                k++;
            }
            val %= MODULO_PRIME;
            hashVal = (int)val;
            testMap.put(key, hashVal);
            testHashCount++;
        }
    }

    private int[] computeLastOcc(String P) {
        int[] lastOcc = new int[NO_OF_CHARS];
        for (int i = 0; i < lastOcc.length; i++) {
            lastOcc[i] = -1;
        }
        for (int i = 0; i < P.length() - 1; i++) {
            lastOcc[P.charAt(i)] = i;
        }
        return lastOcc;
    }

    public List<Integer> compareString(String T, String P) {
        List<Integer> pos = new ArrayList<>();
        int[] lastOcc;
        int i, j, m, n;
        n = T.length();
        m = P.length();
        lastOcc = computeLastOcc(P);
        i = 0;
        while (i <= (n - m)) {
            j = m - 1;
            while (j >= 0 && P.charAt(j) == T.charAt(i + j)) {
                j--;
            }
            if (j < 0) {
                pos.add(i);
            }
            i += (m - 1) - lastOcc[T.charAt(i + (m - 1))];
        }
        return pos;
    }

    private void setTextFlow(boolean isSource) {
        if (isSource) {
            Label[] text = new Label[srcMap.size()];
            StringBuilder sb = new StringBuilder();
            Set<Entry<String, Integer>> srcEntrySet = srcMap.entrySet();
            List<Entry<String, Integer>> srcEntry = new ArrayList<>(srcEntrySet);
            Set<Entry<String, Integer>> testEntrySet = testMap.entrySet();
            List<Entry<String, Integer>> testEntry = new ArrayList<>(testEntrySet);
            for (int i = 0; i < srcEntry.size(); i++) {
                text[i] = new Label();
                text[i].setText(String.valueOf(srcEntry.get(i).getValue()));
                boolean match = false;
                for (int j = 0; j < testEntry.size(); j++) {
                    if (srcEntry.get(i).getValue().equals(testEntry.get(j).getValue())
                            && compareString(srcEntry.get(i).getKey(), testEntry.get(j).getKey()) != null) {
                        match = true;
                    }
                }
                if (match) {
                    sb.append("<span class=\"highlightgreen\">").append(text[i].getText()).append("</span>");
                } else {
                    sb.append(text[i].getText());
                }
                sb.append(" ");
            }
            srcHtml = String.format("<html><body>%s</body></html>", sb.toString());
        } else {
            Label[] text = new Label[testMap.size()];
            StringBuilder sb = new StringBuilder();
            Set<Entry<String, Integer>> testEntrySet = testMap.entrySet();
            List<Entry<String, Integer>> testEntry = new ArrayList<>(testEntrySet);
            Set<Entry<String, Integer>> srcEntrySet = srcMap.entrySet();
            List<Entry<String, Integer>> srcEntry = new ArrayList<>(srcEntrySet);
            for (int i = 0; i < testEntry.size(); i++) {
                text[i] = new Label();
                text[i].setText(String.valueOf(testEntry.get(i).getValue()));
                boolean match = false;
                for (int j = 0; j < srcEntry.size(); j++) {
                    if (testEntry.get(i).getValue().equals(srcEntry.get(j).getValue())
                            && compareString(testEntry.get(i).getKey(), srcEntry.get(j).getKey()) != null) {
                        match = true;
                    }
                }
                if (match) {
                    sb.append("<span class=\"highlightblue\">").append(text[i].getText()).append("</span>");
                    matchHashCount++;
                } else {
                    sb.append(text[i].getText());
                }
                sb.append(" ");
            }
            testHtml = String.format("<html><body>%s<body></html>", sb.toString());
        }
    }

    public int getHashCount(boolean isSource) {
        return (isSource ? srcHashCount : testHashCount);
    }

    public int getMatchHashCount() {
        return matchHashCount;
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
                parseString();
                parseHashing();
                setTextFlow(true);
                setTextFlow(false);
                Platform.runLater(() -> {
                    WebEngine srcWebEngine = srcWebview.getEngine();
                    srcWebEngine.loadContent(srcHtml);
                    srcWebEngine.setUserStyleSheetLocation(getClass().getResource("/plagdetectapp/resources/css/plagdetectapp.css").toString());
                    WebEngine testWebEngine = testWebview.getEngine();
                    testWebEngine.loadContent(testHtml);
                    testWebEngine.setUserStyleSheetLocation(getClass().getResource("/plagdetectapp/resources/css/plagdetectapp.css").toString());
                });
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
            RKBM4Controller rb4Controller = new RKBM4Controller(selectDocController, caseFoldController, tokenController, filterController, stemController, rkbmController, rkbm2Controller, this);
        });
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            RKBM2Controller rb2Controller = new RKBM2Controller(selectDocController, caseFoldController, tokenController, filterController, stemController, rkbmController);
        });
    }

}
