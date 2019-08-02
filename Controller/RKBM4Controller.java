/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import animatefx.animation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jfree.fx.FXGraphics2D;
import org.scilab.forge.jlatexmath.Box;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * FXML Controller class
 *
 * @author exneval
 */
public class RKBM4Controller implements Initializable {

    private final SelectdocController selectDocController;
    private final CasefoldController caseFoldController;
    private final TokenController tokenController;
    private final FilterController filterController;
    private final StemController stemController;
    private final RKBMController rkbmController;
    private final RKBM2Controller rkbm2Controller;
    private final RKBM3Controller rkbm3Controller;
    private final Stage thisStage;
    private int KEY_AUTO_SHOW = 1;
    private Popup popup;
    private String SH;
    private String THA;
    private String THB;
    private String RSH;
    private String RTH;
    private String RP;
    private Map<Integer, String> prefs;
    @FXML
    private Button btnPrev;
    @FXML
    private Button btnShowDetails;
    @FXML
    private Text srcHashCountText;
    @FXML
    private Text testHashCountText;
    @FXML
    private Text matchHashCountText;
    @FXML
    private Text similarityValText;
    @FXML
    private StackPane formulaStackPane;

    public RKBM4Controller(SelectdocController selectDocController, CasefoldController caseFoldController, TokenController tokenController, FilterController filterController, StemController stemController, RKBMController rkbmController, RKBM2Controller rkbm2Controller, RKBM3Controller rkbm3Controller) {
        this.selectDocController = selectDocController;
        this.caseFoldController = caseFoldController;
        this.tokenController = tokenController;
        this.filterController = filterController;
        this.stemController = stemController;
        this.rkbmController = rkbmController;
        this.rkbm2Controller = rkbm2Controller;
        this.rkbm3Controller = rkbm3Controller;
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/rkbm4.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            selectDocController.setCenterDashboard(loader.getRoot());
            selectDocController.imageStep6();
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double similarityCalc() {
        double p;
        double sh = rkbm3Controller.getMatchHashCount();
        double tha = rkbm3Controller.getHashCount(true);
        double thb = rkbm3Controller.getHashCount(false);
        p = ((2 * sh) / (tha + thb)) * 100;
        p = round(p, 2);
        return p;
    }

    private Alert createAlertWithOptOut(AlertType type, String title, String headerText,
            String message, String optOutMessage, Consumer<Boolean> optOutAction,
            ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                CheckBox optOut = new CheckBox();
                optOut.setText(optOutMessage);
                optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
                return optOut;
            }
        });
        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setGraphic(graphic);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        return alert;
    }

    private void initPopup() {
        RKBMdetailsController rkbmDetailsController = new RKBMdetailsController(selectDocController, caseFoldController, tokenController, filterController, stemController, rkbmController, rkbm2Controller, rkbm3Controller, this);
    }

    private void showDialog(boolean autoshow) {
        if (autoshow) {
            initPopup();
        } else {
            Alert alert = createAlertWithOptOut(AlertType.INFORMATION, "Show Details", null,
                    "Press ESC button to close the popup window!", "Do not ask again",
                    param -> prefs.put(KEY_AUTO_SHOW, (param ? "Always" : "Never")), ButtonType.OK);
            if (alert.showAndWait().filter(t -> t == ButtonType.OK).isPresent()) {
                initPopup();
            }
        }
    }

    private class MyCanvas extends Canvas {

        private final FXGraphics2D g2;
        private final Box box;
        private final Box box2;
        private final Box box3;
        private final Box box4;

        public MyCanvas() {
            this.g2 = new FXGraphics2D(getGraphicsContext2D());
            this.g2.scale(20, 20);
            TeXFormula formula = new TeXFormula("P=\\frac{2 \\times SH}{THA + THB}\\times 100 \\%");
            TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
            TeXFormula formula2 = new TeXFormula("P=\\frac{2 \\times " + SH + "}{" + THA + " + " + THB + "}\\times 100 \\%");
            TeXIcon icon2 = formula2.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
            TeXFormula formula3 = new TeXFormula("P=\\frac{" + RSH + "}{" + RTH + "}\\times 100 \\%");
            TeXIcon icon3 = formula3.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
            TeXFormula formula4 = new TeXFormula("P=" + RP + "\\%");
            TeXIcon icon4 = formula4.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
            this.box = icon.getBox();
            this.box2 = icon2.getBox();
            this.box3 = icon3.getBox();
            this.box4 = icon4.getBox();
            widthProperty().addListener(evt -> draw());
            heightProperty().addListener(evt -> draw());
        }

        private void draw() {
            double width = getWidth();
            double height = getHeight();
            getGraphicsContext2D().clearRect(0, 0, width, height);
            this.box.draw(g2, 0, 3);
            this.box2.draw(g2, 0, 6);
            this.box3.draw(g2, 0, 9);
            this.box4.draw(g2, 0, 12);
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }

    public void showPopup(Parent root) {
        popup = new Popup();
        popup.setHideOnEscape(true);
        popup.setOnHidden((event) -> {
            btnShowDetails.setVisible(true);
        });
        popup.getContent().setAll(root);
        popup.show(selectDocController.getDasboardStage());
        btnShowDetails.setVisible(false);
        new ZoomIn(root).play();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prefs = new HashMap<>();
        prefs.put(KEY_AUTO_SHOW, "Never");
        SH = String.valueOf(rkbm3Controller.getMatchHashCount());
        THA = String.valueOf(rkbm3Controller.getHashCount(true));
        THB = String.valueOf(rkbm3Controller.getHashCount(false));
        RSH = String.valueOf(rkbm3Controller.getMatchHashCount() * 2);
        RTH = String.valueOf(rkbm3Controller.getHashCount(true) + rkbm3Controller.getHashCount(false));
        RP = String.valueOf(similarityCalc());
        Font.loadFont(RKBM4Controller.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/base/jlm_cmmi10.ttf"), 1);
        Font.loadFont(RKBM4Controller.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/maths/jlm_cmsy10.ttf"), 1);
        Font.loadFont(RKBM4Controller.class.getResourceAsStream("/org/scilab/forge/jlatexmath/fonts/latin/jlm_cmr10.ttf"), 1);
        MyCanvas canvas = new MyCanvas();
        formulaStackPane.getChildren().add(canvas);
        canvas.widthProperty().bind(formulaStackPane.widthProperty());
        canvas.heightProperty().bind(formulaStackPane.heightProperty());
        srcHashCountText.setText(THA);
        testHashCountText.setText(THB);
        matchHashCountText.setText(SH);
        similarityValText.setText(String.valueOf(similarityCalc()) + " %");
        btnPrev.setOnMouseClicked((MouseEvent event) -> {
            RKBM3Controller rb3Controller = new RKBM3Controller(selectDocController, caseFoldController, tokenController, filterController, stemController, rkbmController, rkbm2Controller);
        });
        btnShowDetails.setOnMouseClicked((MouseEvent event) -> {
            showDialog(prefs.get(KEY_AUTO_SHOW).equals("Always"));
        });
    }

}
