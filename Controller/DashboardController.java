/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp.Controller;

import animatefx.animation.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author exneval
 */
public class DashboardController implements Initializable {

    private final Stage thisStage;
    private double x, y;

    @FXML
    private Button btnHome;
    @FXML
    private Button btnDetect;
    @FXML
    private Button btnClose;
    @FXML
    private VBox mainVBox;
    @FXML
    private HBox mainHBox;
    @FXML
    private HBox mainHBox2;

    public DashboardController() {
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/dashboard.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            root.setOnMousePressed((MouseEvent event) -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });
            root.setOnMouseDragged((MouseEvent event) -> {
                thisStage.setX(event.getScreenX() - x);
                thisStage.setY(event.getScreenY() - y);
            });
            thisStage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            thisStage.setScene(scene);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
        } catch (IOException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showStage() {
        thisStage.show();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        thisStage.setX(d.width / 2 - (thisStage.getWidth() / 2));
        thisStage.setY(d.height / 2 - (thisStage.getHeight() / 2));
    }

    private void reloadHome() {
        mainVBox.getChildren().setAll(mainHBox, mainHBox2);
        new SlideInDown(mainHBox).play();
        new SlideInUp(mainHBox2).play();
    }

    public void setCenterDashboard(Parent root) {
        mainVBox.getChildren().setAll(root);
        new SlideInRight(root).play();
    }

    public Stage getStage() {
        return thisStage;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnClose.setOnMouseClicked((MouseEvent event) -> {
            thisStage.close();
        });
        btnHome.setOnMouseClicked((MouseEvent event) -> {
            reloadHome();
        });
        btnDetect.setOnMouseClicked((MouseEvent event) -> {
            SelectdocController selectdocController = new SelectdocController(this);
        });
    }
}
