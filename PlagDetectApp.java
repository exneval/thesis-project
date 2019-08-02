/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plagdetectapp;

import plagdetectapp.Controller.SplashController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author exneval
 */
public class PlagDetectApp extends Application {

    @Override
    public void start(Stage stage) {
        SplashController splashController = new SplashController();
        splashController.showStage();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
