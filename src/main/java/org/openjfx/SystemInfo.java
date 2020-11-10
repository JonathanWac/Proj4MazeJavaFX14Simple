package org.openjfx;

import javafx.collections.ObservableList;
import javafx.stage.Screen;

public class SystemInfo {

    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }
    public static void printScreenSizes(){
        ObservableList<Screen> screenSizes = Screen.getScreens();
        System.out.println(screenSizes.size());
        screenSizes.forEach(screen -> {
            System.out.println(screen.getBounds());
        });
    }

    public static Screen getMainScreen(){
        return Screen.getPrimary();
    }


}