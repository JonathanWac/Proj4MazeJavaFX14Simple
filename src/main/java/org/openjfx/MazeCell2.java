package org.openjfx;

import javafx.scene.shape.Line;

public class MazeCell2 {
    Line top, bottom, left, right;

    public void setLines(double leftXCord, double rightXCord, double topYCord, double botYCord){
        setTop(leftXCord, topYCord, rightXCord, topYCord);
        setBottom(leftXCord, botYCord, rightXCord, botYCord);
        setLeft(leftXCord, topYCord, leftXCord, botYCord);
        setRight(rightXCord, topYCord, rightXCord, botYCord);
    }

    public void setTop(double x1, double y1, double x2, double y2) {
        this.top = new Line(x1, y1, x2, y2);
    }

    public void setBottom(double x1, double y1, double x2, double y2) {
        this.bottom = new Line(x1, y1, x2, y2);
    }

    public void setLeft(double x1, double y1, double x2, double y2) {
        this.left = new Line(x1, y1, x2, y2);
    }

    public void setRight(double x1, double y1, double x2, double y2) {
        this.right = new Line(x1, y1, x2, y2);
    }
}
