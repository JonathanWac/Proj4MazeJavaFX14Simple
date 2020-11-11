//=====================================================================================================================
// Name        : App.java
// Author      : Jonathan Wachholz (JHW190002)
// Course	   : UTDallas CS 3345.002 Fall 2020
// Version     : 1.0
// Copyright   : Nov. 2020
// Description :
//         The driver class for all JavaFX applications which will launch the application from main,
//              which then executes start()
//=====================================================================================================================

package org.openjfx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        System.out.printf("This program is running on Java version %s, and JavaFX version %s\n\n", SystemInfo.javaVersion(), SystemInfo.javafxVersion());

        System.out.printf("Welcome! Now executing the JavaFX14 program on a screen size %.0f x %.0f\n", SystemInfo.getScreenWidth(), SystemInfo.getScreenHeight());
        System.out.printf("\tThe maze will be generating a maze of size %d x %d according to the MAZE_DEFAULT values...\n", MAZE_DEFAULTS.cellsAmountWidth, MAZE_DEFAULTS.cellsAmountHeight);
        JavaFXMaze myMaze = new JavaFXMaze(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}