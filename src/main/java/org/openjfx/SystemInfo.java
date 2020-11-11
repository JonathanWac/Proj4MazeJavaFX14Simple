//=====================================================================================================================
// Name        : SystemInfo.java
// Author      : Jonathan Wachholz (JHW190002)
// Course	   : UTDallas CS 3345.002 Fall 2020
// Version     : 1.0
// Copyright   : Nov. 2020
// Description :
//          A simple static class that returns info about the Users System.
//      Specifically, this class will return the current javaVersion in use, the javaFX version, and
//      the Primary screen's dimensions.
//=====================================================================================================================

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

    public static double getScreenWidth(){
        return Screen.getPrimary().getBounds().getWidth();
    }

    public static double getScreenHeight(){
        return Screen.getPrimary().getBounds().getHeight();
    }

    public static Screen getMainScreen(){
        return Screen.getPrimary();
    }


}