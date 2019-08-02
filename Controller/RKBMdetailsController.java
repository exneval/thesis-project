/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
public class RKBMdetailsController implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final FilterController filterController;
    private final StemController stemController;
    private final RKBMController rkbmController;
    private final RKBM2Controller rkbm2Controller;
    private final RKBM3Controller rkbm3Controller;
    private final RKBM4Controller rkbm4Controller;
    private final Stage thisStage;
    private final int NO_OF_ITERATE = 10;
    @FXML
    private WebView srcWebview;
    @FXML
    private WebView testWebview;
    @FXML
    private Text srcFilenameText;
    @FXML
    private Text srcWordsCountText;
    @FXML
    private Text srcWords2CountText;
    @FXML
    private Text srcPatternCountText;
    @FXML
    private Text testFilenameText;
    @FXML
    private Text testWordsCountText;
    @FXML
    private Text testWords2CountText;
    @FXML
    private Text testPatternCountText;
    @FXML
    private Text kgramText;
    @FXML
    private Text matchPatText;
    @FXML
    private Text similarityPercentText;
    @FXML
    private Text plagSeverityText;

    public RKBMdetailsController(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController, FilterController filterController, StemController stemController, RKBMController rkbmController, RKBM2Controller rkbm2Controller, RKBM3Controller rkbm3Controller, RKBM4Controller rkbm4Controller) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        this.filterController = filterController;
        this.stemController = stemController;
        this.rkbmController = rkbmController;
        this.rkbm2Controller = rkbm2Controller;
        this.rkbm3Controller = rkbm3Controller;
        this.rkbm4Controller = rkbm4Controller;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/rkbmdetails.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            rkbm4Controller.showPopup(loader.getRoot());
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillDetails() {
        String srcPath = (selectDocController.getSourceFileName().length() > 35 ? (selectDocController.getSourceFileName().substring(0, 35) + "...") : selectDocController.getSourceFileName());
        srcFilenameText.setText(srcPath);
        String testPath = (selectDocController.getTestFileName().length() > 35 ? (selectDocController.getTestFileName().substring(0, 35) + "...") : selectDocController.getTestFileName());
        testFilenameText.setText(testPath);
        srcWordsCountText.setText(String.valueOf(tokenController.getTokenSet(true).size()));
        testWordsCountText.setText(String.valueOf(tokenController.getTokenSet(false).size()));
        srcWords2CountText.setText(String.valueOf(stemController.getStemSet(true).size()));
        testWords2CountText.setText(String.valueOf(stemController.getStemSet(false).size()));
        srcPatternCountText.setText(String.valueOf(rkbm3Controller.getHashCount(true)));
        testPatternCountText.setText(String.valueOf(rkbm3Controller.getHashCount(false)));
        kgramText.setText(String.valueOf(rkbmController.getKgramVal()));
        matchPatText.setText(String.valueOf(rkbm3Controller.getMatchHashCount()));
        double similarityPercentage = rkbm4Controller.similarityCalc();
        similarityPercentText.setText(String.valueOf(similarityPercentage) + " %");
        String severity;
        if (similarityPercentage < 1) {
            severity = "Not a plagiarism.";
        } else if (similarityPercentage < 15) {
            severity = "Low possibility of plagiarism.";
        } else if (similarityPercentage < 50) {
            severity = "Medium possibility of plagiarism.";
        } else if (similarityPercentage < 100) {
            severity = "High possibility of plagiarism.";
        } else {
            severity = "Plagiarism detected.";
        }
        plagSeverityText.setText(severity);
    }

    private void checkTextIterator(Map<Integer, Integer> map, Iterator<Entry<Integer, Integer>> itr) {
        List<Integer> keyToRemove = new ArrayList<>();
        int tempStart = 0;
        int tempEnd = 0;
        while (itr.hasNext()) {
            Entry<Integer, Integer> entry = itr.next();
            int start = entry.getKey();
            int end = entry.getValue();
            int size = end - start;
            int tempSize = tempEnd - tempStart;
            if (start < tempEnd) {
                if (end <= tempEnd) {
                    keyToRemove.add(start);
                } else {
                    if (tempSize < size) {
                        keyToRemove.add(tempStart);
                    } else {
                        keyToRemove.add(start);
                    }
                }
            }
            tempStart = start;
            tempEnd = end;
        }
        keyToRemove.forEach((key) -> {
            map.remove(key);
        });
    }

    private void showTextArea() {
        Set<String> srcMatchString = filterController.getFilterSet(false);
        StringBuilder srcSB = new StringBuilder();
        WebEngine srcWebEngine = srcWebview.getEngine();
        String srcText = caseFoldController.getTextArea(true);
        Map<Integer, Integer> srcMap = new HashMap<>();
        Set<String> matchString = new HashSet<>();
        srcMatchString.forEach((match) -> {
            List<Integer> startList = rkbm3Controller.compareString(srcText, match);
            if (startList != null) {
                boolean found = false;
                for (Integer start : startList) {
                    int end = start + match.length();
                    if (!srcMap.containsKey(start) || end > srcMap.get(start)) {
                        srcMap.put(start, end);
                        found = true;
                    }
                }
                if (found) {
                    matchString.add(match);
                }
            }
        });
        Map<Integer, Integer> treeSrcMap = new TreeMap<>(srcMap);
        for (int i = 0; i < NO_OF_ITERATE; i++) {
            checkTextIterator(treeSrcMap, treeSrcMap.entrySet().iterator());
        }
        int pos = 0;
        for (Entry<Integer, Integer> entry : treeSrcMap.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();
            srcSB.append(srcText.substring(pos, start)).append("<mark>").append(srcText.substring(start, end)).append("</mark>");
            pos = end;
        }
        if (pos > 0) {
            srcSB.append(srcText.substring(pos));
        }
        String srcHtml = String.format("<html><body>%s<body></html>", srcSB.toString().replaceAll("\n", "<br>"));
        srcWebEngine.loadContent(srcHtml);
        Set<String> testMatchString = matchString;
        StringBuilder testSB = new StringBuilder();
        WebEngine testWebEngine = testWebview.getEngine();
        String testText = caseFoldController.getTextArea(false);
        Map<Integer, Integer> testMap = new HashMap<>();
        testMatchString.forEach((match) -> {
            List<Integer> startList = rkbm3Controller.compareString(testText, match);
            if (startList != null) {
                startList.forEach((start) -> {
                    int end = start + match.length();
                    if (!testMap.containsKey(start) || end > testMap.get(start)) {
                        testMap.put(start, end);
                    }
                });
            }
        });
        Map<Integer, Integer> treeTestMap = new TreeMap<>(testMap);
        for (int i = 0; i < NO_OF_ITERATE; i++) {
            checkTextIterator(treeTestMap, treeTestMap.entrySet().iterator());
        }
        pos = 0;
        for (Entry<Integer, Integer> entry : treeTestMap.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();
            testSB.append(testText.substring(pos, start)).append("<mark>").append(testText.substring(start, end)).append("</mark>");
            pos = end;
        }
        if (pos > 0) {
            testSB.append(testText.substring(pos));
        }
        String testHtml = String.format("<html><body>%s<body></html>", testSB.toString().replaceAll("\n", "<br>"));
        testWebEngine.loadContent(testHtml);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fillDetails();
        showTextArea();
    }

}
