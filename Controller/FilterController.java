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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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

/**
 * FXML Controller class
 *
 * @author exneval
 */
public class FilterController implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final Stage thisStage;
    private Set<String> srcFilterSet;
    private Set<String> testFilterSet;
    @FXML
    private TableView<String> srcFilterTable;
    @FXML
    private TableColumn<String, String> srcFilterCol;
    @FXML
    private TableView<String> testFilterTable;
    @FXML
    private TableColumn<String, String> testFilterCol;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrev;
    @FXML
    private Text filterText1;
    @FXML
    private Text filtervalText1;
    @FXML
    private Text filterText2;
    @FXML
    private Text filtervalText2;
    @FXML
    private Text timerText;

    public FilterController(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/filter.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            selectDocController.setCenterDashboard(loader.getRoot());
            selectDocController.imageStep3();
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class FilterData {

        private final Set<String> filterSet;

        private FilterData(LinkedHashSet<String> filterSet) {
            this.filterSet = filterSet;
        }

        public Set<String> getFilterSet() {
            return filterSet;
        }
    }

    private void checkTokenIterator(Iterator<String> itr) {
        while (itr.hasNext()) {
            String token = itr.next();
            if (token.length() <= 4) {
                itr.remove();
            }
        }
    }

    private Set<String> parseFiltering(boolean isSource) {
        Set<String> srcTokenSet = new LinkedHashSet<>(tokenController.getTokenSet(true));
        Set<String> testTokenSet = new LinkedHashSet<>(tokenController.getTokenSet(false));
        Set<String> stopWordSet = new HashSet<>();
        InputStream in = getClass().getResourceAsStream("/plagdetectapp/resources/file/StopWords.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String word;
            while ((word = br.readLine()) != null) {
                stopWordSet.add(word);
            }
            srcTokenSet.removeAll(stopWordSet);
            checkTokenIterator(srcTokenSet.iterator());
            testTokenSet.removeAll(stopWordSet);
            checkTokenIterator(testTokenSet.iterator());
        } catch (IOException ex) {
            Logger.getLogger(StemController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (isSource ? srcTokenSet : testTokenSet);
    }

    private void setTableView(boolean isSource) {
        FilterData data = new FilterData(new LinkedHashSet<>(parseFiltering(isSource)));
        if (isSource) {
            srcFilterSet = data.getFilterSet();
            srcFilterTable.getItems().setAll(srcFilterSet);
            srcFilterCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
            srcFilterTable.getColumns().clear();
            srcFilterTable.getColumns().add(srcFilterCol);
            filterText1.setText("String collected" + System.lineSeparator() + "from Source Filtering");
            filtervalText1.setText(": " + srcFilterSet.size());
        } else {
            testFilterSet = data.getFilterSet();
            testFilterTable.getItems().setAll(testFilterSet);
            testFilterCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
            testFilterTable.getColumns().clear();
            testFilterTable.getColumns().add(testFilterCol);
            filterText2.setText("String collected" + System.lineSeparator() + "from Test Filtering");
            filtervalText2.setText(": " + testFilterSet.size());
        }
    }

    public Set<String> getFilterSet(boolean isSource) {
        return (isSource ? srcFilterSet : testFilterSet);
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
            StemController stemController = new StemController(selectDocController, caseFoldController, tokenController, this);
        });
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            TokenController tknController = new TokenController(selectDocController, caseFoldController);
        });
    }
}
