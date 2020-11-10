package org.openjfx;

import javafx.scene.shape.Line;

public class MazeCell {
    Line bottom, right;

    public void setLines(double leftXCord, double rightXCord, double topYCord, double botYCord){
        setBottom(leftXCord, botYCord, rightXCord, botYCord);
        setRight(rightXCord, topYCord, rightXCord, botYCord);
    }

    public void setBottom(double x1, double y1, double x2, double y2) {
        this.bottom = new Line(x1, y1, x2, y2);
    }

    public void setRight(double x1, double y1, double x2, double y2) {
        this.right = new Line(x1, y1, x2, y2);
    }
}
