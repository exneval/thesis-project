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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author exneval
 */
public class SplashController implements Initializable {

    private final Stage thisStage;

    @FXML
    private ProgressBar splashProgress;

    public SplashController() {
        thisStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/plagdetectapp/views/splash.fxml"));
            loader.setController(this);
            thisStage.initStyle(StageStyle.TRANSPARENT);
            thisStage.setScene(new Scene(loader.load()));
            thisStage.getScene().setFill(Color.TRANSPARENT);
            thisStage.getScene().getStylesheets().add("/plagdetectapp/resources/css/plagdetectapp.css");
            new FadeIn(loader.getRoot()).play();
        } catch (IOException ex) {
            Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showStage() {
        thisStage.show();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        thisStage.setX((d.width - thisStage.getWidth()) / 2);
        thisStage.setY((d.height - thisStage.getHeight()) / 2);
    }

    private void openDashboard() {
        DashboardController dashboardController = new DashboardController();
        dashboardController.showStage();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        splashProgress.setProgress(0);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                final int numIterations = 100;
                for (int i = 0; i < numIterations; i++) {
                    updateProgress(i + 1, numIterations);
                    try {
                        Thread.sleep(45);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }
        };
        splashProgress.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(event -> {
            thisStage.hide();
            openDashboard();
        });
        new Thread(task).start();
    }
}
