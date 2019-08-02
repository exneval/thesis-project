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
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class TokenController implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final Stage thisStage;
    private Set<String> srcTokenSet;
    private Set<String> testTokenSet;
    @FXML
    private TableView<String> srcTokenTable;
    @FXML
    private TableColumn<String, String> srcTokenCol;
    @FXML
    private TableView<String> testTokenTable;
    @FXML
    private TableColumn<String, String> testTokenCol;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrev;
    @FXML
    private Text tokenText1;
    @FXML
    private Text tokenvalText1;
    @FXML
    private Text tokenText2;
    @FXML
    private Text tokenvalText2;
    @FXML
    private Text timerText;

    public TokenController(SelectdocController selectDocController, CasefoldController caseFoldController) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/token.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            selectDocController.setCenterDashboard(loader.getRoot());
            selectDocController.imageStep2();
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class TokenData {

        private final Set<String> tokenSet;

        private TokenData(LinkedHashSet<String> tokenSet) {
            this.tokenSet = tokenSet;
        }

        public Set<String> getTokenSet() {
            return tokenSet;
        }
    }

    private Set<String> parseTokenizing(boolean isSource) {
        String str = caseFoldController.getTextArea(isSource);
        Set<String> token = new LinkedHashSet<>();
        String regex = "(\\b[a-z]*-[^a-z])|(\\b[a-z-]+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while (m.find()) {
            token.add(m.group(2));
        }
        token.remove(null);
        return token;
    }

    private void setTableView(boolean isSource) {
        TokenData data = new TokenData(new LinkedHashSet<>(parseTokenizing(isSource)));
        if (isSource) {
            srcTokenSet = data.getTokenSet();
            srcTokenTable.getItems().setAll(srcTokenSet);
            srcTokenCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
            srcTokenTable.getColumns().clear();
            srcTokenTable.getColumns().add(srcTokenCol);
            tokenText1.setText("String found" + System.lineSeparator() + "from Source Tokenizing");
            tokenvalText1.setText(": " + srcTokenSet.size());
        } else {
            testTokenSet = data.getTokenSet();
            testTokenTable.getItems().setAll(testTokenSet);
            testTokenCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));
            testTokenTable.getColumns().clear();
            testTokenTable.getColumns().add(testTokenCol);
            tokenText2.setText("String found" + System.lineSeparator() + "from Test Tokenizing");
            tokenvalText2.setText(": " + testTokenSet.size());
        }
    }

    public Set<String> getTokenSet(boolean isSource) {
        return (isSource ? srcTokenSet : testTokenSet);
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
            FilterController filterController = new FilterController(selectDocController, caseFoldController, this);
        });
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            CasefoldController cfController = new CasefoldController(selectDocController);
        });
    }
}
