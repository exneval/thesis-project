/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jsastrawi.morphology.DefaultLemmatizer;
import jsastrawi.morphology.Lemmatizer;

/**
 * FXML Controller class
 *
 * @author exneval
 */
public class StemController implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final FilterController filterController;
    private final Stage thisStage;
    private Set<String> srcStemSet;
    private Set<String> testStemSet;
    @FXML
    private TableView<String> srcStemTable;
    @FXML
    private TableColumn<String, String> srcStemCol;
    @FXML
    private TableView<String> testStemTable;
    @FXML
    private TableColumn<String, String> testStemCol;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrev;
    @FXML
    private Text stemText1;
    @FXML
    private Text stemvalText1;
    @FXML
    private Text stemText2;
    @FXML
    private Text stemvalText2;
    @FXML
    private Text timerText;

    public StemController(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController, FilterController filterController) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        this.filterController = filterController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/stem.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            selectDocController.setCenterDashboard(loader.getRoot());
            selectDocController.imageStep4();
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class StemData {

        private final Set<String> stemSet;

        private StemData(LinkedHashSet<String> stemSet) {
            this.stemSet = stemSet;
        }

        public Set<String> getStemSet() {
            return stemSet;
        }
    }

    private List<String> parseStemming(boolean isSource) {
        List<String> srcFilterList = new ArrayList<>(filterController.getFilterSet(true));
        List<String> testFilterList = new ArrayList<>(filterController.getFilterSet(false));
        Set<String> dictionarySet = new HashSet<>();
        InputStream in = Lemmatizer.class.getResourceAsStream("/root-words.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                dictionarySet.add(line);
            }
            ListIterator<String> itr = srcFilterList.listIterator();
            Lemmatizer lemmatizer = new DefaultLemmatizer(dictionarySet);
            while (itr.hasNext()) {
                String filter = itr.next();
                String stem = lemmatizer.lemmatize(filter);
                if (!filter.equals(stem)) {
                    itr.set(stem);
                }
            }
            itr = testFilterList.listIterator();
            while (itr.hasNext()) {
                String filter = itr.next();
                String stem = lemmatizer.lemmatize(filter);
                if (!filter.equals(stem)) {
                    itr.set(stem);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(StemController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (isSource ? srcFilterList : testFilterList);
    }

    private void setTableView(boolean isSource) {
        StemData data = new StemData(new LinkedHashSet<>(parseStemming(isSource)));
        if (isSource) {
            srcStemSet = data.getStemSet();
            srcStemTable.getItems().setAll(srcStemSet);
            srcStemCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
            srcStemTable.getColumns().clear();
            srcStemTable.getColumns().add(srcStemCol);
            stemText1.setText("String collected" + System.lineSeparator() + "from Source Stemming");
            stemvalText1.setText(": " + srcStemSet.size());
        } else {
            testStemSet = data.getStemSet();
            testStemTable.getItems().setAll(testStemSet);
            testStemCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
            testStemTable.getColumns().clear();
            testStemTable.getColumns().add(testStemCol);
            stemText2.setText("String collected" + System.lineSeparator() + "from Test Stemming");
            stemvalText2.setText(": " + testStemSet.size());
        }
    }

    public Set<String> getStemSet(boolean isSource) {
        return (isSource ? srcStemSet : testStemSet);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Instant start = Instant.now();
        setTableView(true);
        setTableView(false);
        Instant end = Instant.now();
        timerText.setText("Generated in " + Duration.between(start, end).toMillis() + "ms.");
        btnNext.setOnMouseClicked((MouseEvent event) -> {
            RKBMController rkbmController = new RKBMController(selectDocController, caseFoldController, tokenController, filterController, this);
        });
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            FilterController flrController = new FilterController(selectDocController, caseFoldController, tokenController);
        });
    }
}
