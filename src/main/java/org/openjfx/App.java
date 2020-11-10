package org.openjfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        SystemInfo.printScreenSizes();

        //Set up a while loop / prompt that asks the user for the initial maze size
        //Create an object here that then returns a scene to the app Driver here
        JavaFXMaze myMaze = new JavaFXMaze(stage);

    }

    public static void main(String[] args) {
        launch();
    }

}