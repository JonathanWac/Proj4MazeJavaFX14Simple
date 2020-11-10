package org.openjfx;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Vector;

public class JavaFXMaze {
    private Stage mainStage;
    private Scene mainScene;
    private MazeCell[][] cellArr;
    private int cellPixelsWidth, cellPixelsHeight, cellsWidth, cellsHeight;


    public JavaFXMaze(Stage stage) {
        mainStage = stage;
        cellPixelsWidth = cellPixelsHeight = MAZE_DEFAULTS.cellPixelSize;
        cellsWidth = MAZE_DEFAULTS.cellsAmountWidth;
        cellsHeight = MAZE_DEFAULTS.cellsAmountHeight ;
        generateMaze();
        stage.show();
        //mainScene = new Scene(new StackPane(new Label("Test Label")), 640, 480);
    }

    public JavaFXMaze(Stage stage, int cellsWidth, int cellsHeight) {
        mainStage = stage;
        setCellsWidth(cellsWidth);
        setCellsHeight(cellsHeight);
        generateMaze();
        stage.show();
        //mainScene = new Scene(new StackPane(new Label("Test Label")), 640, 480);
    }

    public JavaFXMaze(Stage stage, int cellsWidth, int cellsHeight, int cellPixelSize) {
        mainStage = stage;
        setCellPixelsWidth(cellPixelSize);
        setCellPixelsHeight(cellPixelSize);
        setCellsWidth(cellsWidth);
        setCellsHeight(cellsHeight);
        generateMaze();
        stage.show();
        //mainScene = new Scene(new StackPane(new Label("Test Label")), 640, 480);
    }

    public void generateMaze(){
        Scene maze;
        BorderPane borderPane = new BorderPane();
        Group group = generateMazeGroup();
        VBox topVBox = constructTopBorder();

        borderPane.setTop(topVBox);
        borderPane.setCenterShape(true);
        borderPane.setCenter(group);

        maze = new Scene(borderPane, (double) cellsWidth * cellPixelsWidth * 1.05, (double) cellsHeight * cellPixelsHeight * 1.05 + 100);
        mainScene = maze;
        connectMaze();
        //connectMazeV2();
        mainStage.setTitle(String.format("          %sx%s Maze", cellsWidth, cellsHeight));
        mainStage.setScene(maze);
    }

    public VBox constructTopBorder(){
        VBox mainVBox = new VBox();
            mainVBox.setAlignment(Pos.CENTER);
            Label inputPrompt1 = new Label("Enter a new size below to generate a new maze");
            HBox hBox2 = new HBox();
                hBox2.setAlignment(Pos.CENTER);
                TextField widthField = new TextField(String.format("%s", cellsWidth)),
                        heightField = new TextField(String.format("%s", cellsHeight));
                widthField.setAlignment(Pos.BASELINE_RIGHT);
                heightField.setAlignment(Pos.BASELINE_LEFT);
                widthField.setMaxWidth(80);
                heightField.setMaxWidth(80);
                Text stringX = new Text("x");
                stringX.setStyle("-fx-font: 20 arial;");
                hBox2.getChildren().addAll(widthField, stringX, heightField);
            Button generateMaze = new Button("Generate New Maze");
            Text errorMsg = new Text();
            generateMaze.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int newWidth, newHeight;
                    newWidth = Integer.parseInt(widthField.getText());
                    newHeight = Integer.parseInt(heightField.getText());

                    if (newWidth * cellPixelsWidth > SystemInfo.getMainScreen().getBounds().getWidth() * 1.05){
                        if (newHeight * cellPixelsHeight > SystemInfo.getMainScreen().getBounds().getWidth() * 1.05){
                            errorMsg.setText(String.format("Error: Tried to generate a maze larger than your screen..." +
                                            "\nScreen Size %sx%s, New Maze would be %sx%s", SystemInfo.getMainScreen().getBounds().getWidth(),
                                    SystemInfo.getMainScreen().getBounds().getHeight(), newWidth * cellPixelsWidth, newHeight * cellPixelsHeight));
                            errorMsg.setFill(Color.DARKRED);
                        }
                        else{
                            errorMsg.setText(String.format("Error: Tried to generate a maze larger than your screen Width..." +
                                            "\nScreen Size %sx%s, New Maze would be %sx%s", SystemInfo.getMainScreen().getBounds().getWidth(),
                                    SystemInfo.getMainScreen().getBounds().getHeight(), newWidth * cellPixelsWidth, newHeight * cellPixelsHeight));
                            errorMsg.setFill(Color.DARKRED);
                        }
                    }
                    else if (newHeight * cellPixelsHeight > SystemInfo.getMainScreen().getBounds().getWidth() * 1.05){
                        errorMsg.setText(String.format("Error: Tried to generate a maze larger than your screen Height..." +
                                "\nScreen Size %sx%s, New Maze would be %sx%s", SystemInfo.getMainScreen().getBounds().getWidth(),
                                SystemInfo.getMainScreen().getBounds().getHeight(), newWidth * cellPixelsWidth, newHeight * cellPixelsHeight));
                        errorMsg.setFill(Color.DARKRED);
                    }
                    else{
                        setCellsWidth(newWidth);
                        setCellsHeight(newHeight);
                        generateMaze();
                    }
                }
            });
            mainVBox.getChildren().addAll(inputPrompt1, hBox2, generateMaze, errorMsg);

        return mainVBox;
    }

    public void connectMaze(){
        if (cellArr.length > 0 && cellArr[0].length > 0){
            if (cellArr.length == 1 && cellArr[0].length == 1){

                cellArr[0][0].right.setVisible(false);
            }
            else if (cellArr.length == 1){

                cellArr[0][cellArr[0].length-1].right.setVisible(false);
                for (int i = 0; i < cellArr[0].length; i++){
                    cellArr[0][i].bottom.setVisible(false);
                }
            }
            else if (cellArr[0].length == 1){
                cellArr[cellArr.length-1][0].bottom.setVisible(false);
                for (int i = 0; i < cellArr.length; i++){
                    cellArr[i][0].right.setVisible(false);
                }
            }
            else {
                int totalSize = cellArr.length * cellArr[0].length;
                DisjSets sets = new DisjSets(totalSize);
                int startingRoot = randNum(0, totalSize-1), index1D, randDirection, unitedCount = 1;
                while (unitedCount != totalSize){
                    index1D = randNum(0, totalSize-1);
                    // Total Size -> indexes w, h
                    //      w = totalSizeIndex % width
                    //      h = totalSizeIndex / width ROUND Down

                    //      From center 1D node --> you can reach
                    //          TopNode = current1DIndex - cellsWidth
                    //          BotNode = current1DIndex + cellsWidth
                    //          LeftNode = current1DIndex - 1
                    //          RightNode = current1DIndex + 1

                    int wIndex2D = index1D % cellsWidth, hIndex2D = index1D / cellsWidth;
                    randDirection = randNum(1, 4);
                    boolean check1 = false, check2 = false, check3 = false, check4 = false;
                    //Top == 1
                    //Right == 2
                    //Bot == 3
                    //Left == 4

                    //Left Border Cells
                    if (wIndex2D == 0){
                        //Top Left cell
                        if (hIndex2D == 0){
                            randDirection = randNum(2, 3);
                            while (!(check2 && check3)){
                                if (randDirection == 2){
                                    //Perform check against the right cell, if they are in seperate Sets, then combine
                                    int rightCellIndex = index1D + 1;
                                    if (sets.find(index1D) == sets.find(rightCellIndex)){
                                        randDirection = 3;
                                        check2 = true;
                                    }
                                    else {
                                        //sets.union(findMaximalParent(sets, index1D), findMaximalParent(sets, rightCellIndex));
                                        uniteIndexes(sets, index1D, rightCellIndex);
                                        cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                                else if (randDirection == 3){
                                    int botCellIndex = index1D + cellsWidth;
                                    if (sets.find(index1D) == sets.find(botCellIndex)){
                                        randDirection = 2;
                                        check3 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, botCellIndex);
                                        cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                            }
                        }
                        //Bottom Left cell
                        else if (hIndex2D == cellsHeight - 1){
                            randDirection = randNum(1, 2);
                            while (!(check1 && check2)){
                                if (randDirection == 1){
                                    int topCellIndex = index1D - cellsWidth;
                                    if (sets.find(index1D) == sets.find(topCellIndex)){
                                        randDirection = 2;
                                        check1 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, topCellIndex);
                                        cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                                else if (randDirection == 2){
                                    int rightCellIndex = index1D + 1;
                                    if (sets.find(index1D) == sets.find(rightCellIndex)){
                                        randDirection = 1;
                                        check2 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, rightCellIndex);
                                        cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                            }
                        }
                        //Middle Left cell
                        else {
                            randDirection = randNum(1, 3);
                            while (!(check1 && check2 && check3)){
                                if (randDirection == 1){
                                    int topCellIndex = index1D - cellsWidth;
                                    if (sets.find(index1D) == sets.find(topCellIndex)){
                                        randDirection = 2;
                                        check1 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, topCellIndex);
                                        cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                                else if (randDirection == 2){
                                    int rightCellIndex = index1D + 1;
                                    if (sets.find(index1D) == sets.find(rightCellIndex)){
                                        randDirection = 3;
                                        check2 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, rightCellIndex);
                                        cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                                else if (randDirection == 3){
                                    int botCellIndex = index1D + cellsWidth;
                                    if (sets.find(index1D) == sets.find(botCellIndex)){
                                        randDirection = 1;
                                        check3 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, botCellIndex);
                                        cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Right Border Cells
                    else if (wIndex2D == cellsWidth-1){
                        //Top Right cell
                        if (hIndex2D == 0){
                            randDirection = randNum(3, 4);
                        }
                        //Bottom Right cell
                        else if (hIndex2D == cellsHeight - 1){
                            randDirection = randNum(1, 2);
                            if (randDirection == 2)
                                randDirection = 4;
                        }
                        //Middle Right cell
                        else {
                            randDirection = randNum(2, 4);
                            if (randDirection == 2)
                                randDirection = 1;
                            while (!(check1 && check3 && check4)){
                                if (randDirection == 1){
                                    int topCellIndex = index1D - cellsWidth;
                                    if (sets.find(index1D) == sets.find(topCellIndex)){
                                        randDirection = 3;
                                        check1 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, topCellIndex);
                                        cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                                else if (randDirection == 3){
                                    int botCellIndex = index1D + cellsWidth;
                                    if (sets.find(index1D) == sets.find(botCellIndex)){
                                        randDirection = 4;
                                        check3 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, botCellIndex);
                                        cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                                else if (randDirection == 4){
                                    int leftCellIndex = index1D - 1;
                                    if (sets.find(index1D) == sets.find(leftCellIndex)){
                                        randDirection = 1;
                                        check4 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, leftCellIndex);
                                        cellArr[wIndex2D-1][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Top Middle Border Cells
                    else if (hIndex2D == 0){
                        randDirection = randNum(2, 4);
                        while (!(check4 && check2 && check3)){
                            if (randDirection == 2){
                                int rightCellIndex = index1D + 1;
                                if (sets.find(index1D) == sets.find(rightCellIndex)){
                                    randDirection = 3;
                                    check2 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, rightCellIndex);
                                    cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                            else if (randDirection == 3){
                                int botCellIndex = index1D + cellsWidth;
                                if (sets.find(index1D) == sets.find(botCellIndex)){
                                    randDirection = 4;
                                    check3 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, botCellIndex);
                                    cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                            else if (randDirection == 4){
                                int leftCellIndex = index1D - 1;
                                if (sets.find(index1D) == sets.find(leftCellIndex)){
                                    randDirection = 2;
                                    check4 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, leftCellIndex);
                                    cellArr[wIndex2D-1][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                        }
                    }
                    //Bottom Middle Border Cells
                    else if (hIndex2D == cellsHeight - 1){
                        randDirection = randNum(1, 3);
                        if (randDirection == 3)
                            randDirection = 4;
                        while (!(check4 && check2 && check1)) {
                            if (randDirection == 2) {
                                int rightCellIndex = index1D + 1;
                                if (sets.find(index1D) == sets.find(rightCellIndex)) {
                                    randDirection = 4;
                                    check2 = true;
                                } else {
                                    uniteIndexes(sets, index1D, rightCellIndex);
                                    cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            } else if (randDirection == 1){
                                int topCellIndex = index1D - cellsWidth;
                                if (sets.find(index1D) == sets.find(topCellIndex)){
                                    randDirection = 2;
                                    check1 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, topCellIndex);
                                    cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            } else if (randDirection == 4) {
                                int leftCellIndex = index1D - 1;
                                if (sets.find(index1D) == sets.find(leftCellIndex)) {
                                    randDirection = 1;
                                    check4 = true;
                                } else {
                                    uniteIndexes(sets, index1D, leftCellIndex);
                                    cellArr[wIndex2D - 1][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                        }
                    }
                    //Center Cells or Outer Cells w/ direction modified
                    else {
                        randDirection = randNum(1, 4);
                        while (!(check1 && check2 && check3 && check4)){
                            if (randDirection == 1){
                                int topCellIndex = index1D - cellsWidth;
                                if (sets.find(index1D) == sets.find(topCellIndex)){
                                    randDirection = 2;
                                    check1 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, topCellIndex);
                                    cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                            else if (randDirection == 2){
                                int rightCellIndex = index1D + 1;
                                if (sets.find(index1D) == sets.find(rightCellIndex)){
                                    randDirection = 3;
                                    check2 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, rightCellIndex);
                                    cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                            else if (randDirection == 3){
                                int botCellIndex = index1D + cellsWidth;
                                if (sets.find(index1D) == sets.find(botCellIndex)){
                                    randDirection = 4;
                                    check3 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, botCellIndex);
                                    cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }else if (randDirection == 4) {
                                int leftCellIndex = index1D - 1;
                                if (sets.find(index1D) == sets.find(leftCellIndex)) {
                                    randDirection = 1;
                                    check4 = true;
                                } else {
                                    uniteIndexes(sets, index1D, leftCellIndex);
                                    cellArr[wIndex2D - 1][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //Improved efficiency
    public void connectMazeV2(){
        if (cellArr.length > 0 && cellArr[0].length > 0){
            if (cellArr.length == 1 && cellArr[0].length == 1){

                cellArr[0][0].right.setVisible(false);
            }
            else if (cellArr.length == 1){

                cellArr[0][cellArr[0].length-1].right.setVisible(false);
                for (int i = 0; i < cellArr[0].length; i++){
                    cellArr[0][i].bottom.setVisible(false);
                }
            }
            else if (cellArr[0].length == 1){
                cellArr[cellArr.length-1][0].bottom.setVisible(false);
                for (int i = 0; i < cellArr.length; i++){
                    cellArr[i][0].right.setVisible(false);
                }
            }
            else {
                int totalSize = cellArr.length * cellArr[0].length;
                DisjSets sets = new DisjSets(totalSize);

                Vector<Integer> uncheckedCellsVector = new Vector<>(totalSize);
                for (int i = 0; i < totalSize; i++){
                    uncheckedCellsVector.add(i);
                }

                //int startingRoot = randNum(0, totalSize-1), index1D, randDirection, unitedCount = 1;
                int startingRoot = randNum(0, uncheckedCellsVector.size()), index1D, randDirection, unitedCount = 1, randIndex;
                startingRoot = uncheckedCellsVector.get(startingRoot);
                while (unitedCount < totalSize-1){
                    //index1D = randNum(0, totalSize-1);
                    randIndex = randNum(0, uncheckedCellsVector.size()-1);
                    index1D = uncheckedCellsVector.get(randIndex);
                    // Total Size -> indexes w, h
                    //      w = totalSizeIndex % width
                    //      h = totalSizeIndex / width ROUND Down

                    //      From center 1D node --> you can reach
                    //          TopNode = current1DIndex - cellsWidth
                    //          BotNode = current1DIndex + cellsWidth
                    //          LeftNode = current1DIndex - 1
                    //          RightNode = current1DIndex + 1

                    int wIndex2D = index1D % cellsWidth, hIndex2D = index1D / cellsWidth;
                    randDirection = randNum(1, 4);
                    boolean check1 = false, check2 = false, check3 = false, check4 = false;
                    //Top == 1
                    //Right == 2
                    //Bot == 3
                    //Left == 4

                    //Left Border Cells
                    if (wIndex2D == 0){
                        //Top Left cell
                        if (hIndex2D == 0){
                            randDirection = randNum(2, 3);
                            while (!(check2 && check3)){
                                if (randDirection == 2){
                                    //Perform check against the right cell, if they are in seperate Sets, then combine
                                    int rightCellIndex = index1D + 1;
                                    if (sets.find(index1D) == sets.find(rightCellIndex)){
                                        randDirection = 3;
                                        check2 = true;
                                    }
                                    else {
                                        //sets.union(findMaximalParent(sets, index1D), findMaximalParent(sets, rightCellIndex));
                                        uniteIndexes(sets, index1D, rightCellIndex);
                                        cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                                else if (randDirection == 3){
                                    int botCellIndex = index1D + cellsWidth;
                                    if (sets.find(index1D) == sets.find(botCellIndex)){
                                        randDirection = 2;
                                        check3 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, botCellIndex);
                                        cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                            }
                        }
                        //Bottom Left cell
                        else if (hIndex2D == cellsHeight - 1){
                            randDirection = randNum(1, 2);
                            while (!(check1 && check2)){
                                if (randDirection == 1){
                                    int topCellIndex = index1D - cellsWidth;
                                    if (sets.find(index1D) == sets.find(topCellIndex)){
                                        randDirection = 2;
                                        check1 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, topCellIndex);
                                        cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                                else if (randDirection == 2){
                                    int rightCellIndex = index1D + 1;
                                    if (sets.find(index1D) == sets.find(rightCellIndex)){
                                        randDirection = 1;
                                        check2 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, rightCellIndex);
                                        cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                            }
                        }
                        //Middle Left cell
                        else {
                            randDirection = randNum(1, 3);
                            while (!(check1 && check2 && check3)){
                                if (randDirection == 1){
                                    int topCellIndex = index1D - cellsWidth;
                                    if (sets.find(index1D) == sets.find(topCellIndex)){
                                        randDirection = 2;
                                        check1 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, topCellIndex);
                                        cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                                else if (randDirection == 2){
                                    int rightCellIndex = index1D + 1;
                                    if (sets.find(index1D) == sets.find(rightCellIndex)){
                                        randDirection = 3;
                                        check2 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, rightCellIndex);
                                        cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                                else if (randDirection == 3){
                                    int botCellIndex = index1D + cellsWidth;
                                    if (sets.find(index1D) == sets.find(botCellIndex)){
                                        randDirection = 1;
                                        check3 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, botCellIndex);
                                        cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Right Border Cells
                    else if (wIndex2D == cellsWidth-1){
                        //Top Right cell
                        if (hIndex2D == 0){
                            randDirection = randNum(3, 4);
                        }
                        //Bottom Right cell
                        else if (hIndex2D == cellsHeight - 1){
                            randDirection = randNum(1, 2);
                            if (randDirection == 2)
                                randDirection = 4;
                        }
                        //Middle Right cell
                        else {
                            randDirection = randNum(2, 4);
                            if (randDirection == 2)
                                randDirection = 1;
                            while (!(check1 && check3 && check4)){
                                if (randDirection == 1){
                                    int topCellIndex = index1D - cellsWidth;
                                    if (sets.find(index1D) == sets.find(topCellIndex)){
                                        randDirection = 3;
                                        check1 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, topCellIndex);
                                        cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                                else if (randDirection == 3){
                                    int botCellIndex = index1D + cellsWidth;
                                    if (sets.find(index1D) == sets.find(botCellIndex)){
                                        randDirection = 4;
                                        check3 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, botCellIndex);
                                        cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                                else if (randDirection == 4){
                                    int leftCellIndex = index1D - 1;
                                    if (sets.find(index1D) == sets.find(leftCellIndex)){
                                        randDirection = 1;
                                        check4 = true;
                                    }
                                    else {
                                        uniteIndexes(sets, index1D, leftCellIndex);
                                        cellArr[wIndex2D-1][hIndex2D].right.setVisible(false);
                                        unitedCount++;
                                        uncheckedCellsVector.remove(randIndex);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Top Middle Border Cells
                    else if (hIndex2D == 0){
                        randDirection = randNum(2, 4);
                        while (!(check4 && check2 && check3)){
                            if (randDirection == 2){
                                int rightCellIndex = index1D + 1;
                                if (sets.find(index1D) == sets.find(rightCellIndex)){
                                    randDirection = 3;
                                    check2 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, rightCellIndex);
                                    cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                            else if (randDirection == 3){
                                int botCellIndex = index1D + cellsWidth;
                                if (sets.find(index1D) == sets.find(botCellIndex)){
                                    randDirection = 4;
                                    check3 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, botCellIndex);
                                    cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                            else if (randDirection == 4){
                                int leftCellIndex = index1D - 1;
                                if (sets.find(index1D) == sets.find(leftCellIndex)){
                                    randDirection = 2;
                                    check4 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, leftCellIndex);
                                    cellArr[wIndex2D-1][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                        }
                    }
                    //Bottom Middle Border Cells
                    else if (hIndex2D == cellsHeight - 1){
                        randDirection = randNum(1, 3);
                        if (randDirection == 3)
                            randDirection = 4;
                        while (!(check4 && check2 && check1)) {
                            if (randDirection == 2) {
                                int rightCellIndex = index1D + 1;
                                if (sets.find(index1D) == sets.find(rightCellIndex)) {
                                    randDirection = 4;
                                    check2 = true;
                                } else {
                                    uniteIndexes(sets, index1D, rightCellIndex);
                                    cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            } else if (randDirection == 1){
                                int topCellIndex = index1D - cellsWidth;
                                if (sets.find(index1D) == sets.find(topCellIndex)){
                                    randDirection = 2;
                                    check1 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, topCellIndex);
                                    cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            } else if (randDirection == 4) {
                                int leftCellIndex = index1D - 1;
                                if (sets.find(index1D) == sets.find(leftCellIndex)) {
                                    randDirection = 1;
                                    check4 = true;
                                } else {
                                    uniteIndexes(sets, index1D, leftCellIndex);
                                    cellArr[wIndex2D - 1][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                        }
                    }
                    //Center Cells or Outer Cells w/ direction modified
                    else {
                        randDirection = randNum(1, 4);
                        while (!(check1 && check2 && check3 && check4)){
                            if (randDirection == 1){
                                int topCellIndex = index1D - cellsWidth;
                                if (sets.find(index1D) == sets.find(topCellIndex)){
                                    randDirection = 2;
                                    check1 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, topCellIndex);
                                    cellArr[wIndex2D][hIndex2D-1].bottom.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                            else if (randDirection == 2){
                                int rightCellIndex = index1D + 1;
                                if (sets.find(index1D) == sets.find(rightCellIndex)){
                                    randDirection = 3;
                                    check2 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, rightCellIndex);
                                    cellArr[wIndex2D][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                            else if (randDirection == 3){
                                int botCellIndex = index1D + cellsWidth;
                                if (sets.find(index1D) == sets.find(botCellIndex)){
                                    randDirection = 4;
                                    check3 = true;
                                }
                                else {
                                    uniteIndexes(sets, index1D, botCellIndex);
                                    cellArr[wIndex2D][hIndex2D].bottom.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }else if (randDirection == 4) {
                                int leftCellIndex = index1D - 1;
                                if (sets.find(index1D) == sets.find(leftCellIndex)) {
                                    randDirection = 1;
                                    check4 = true;
                                } else {
                                    uniteIndexes(sets, index1D, leftCellIndex);
                                    cellArr[wIndex2D - 1][hIndex2D].right.setVisible(false);
                                    unitedCount++;
                                    uncheckedCellsVector.remove(randIndex);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void uniteIndexes(DisjSets sets, int index1, int index2){
        sets.union(sets.find(index1), sets.find(index2));
    }

    public int findMaximalParent(DisjSets sets, int childIndex){
        /*int maxParent = childIndex, tempNum = sets.find(childIndex);
        while (sets.find(maxParent) != maxParent){
            maxParent = sets.find(maxParent);
        }*/
        int maxParent = childIndex, tempNum;
        while (true){
            tempNum = sets.find(maxParent);
            if (tempNum == maxParent)
                break;
            maxParent = tempNum;
        }
        System.out.printf("The child index %s's maximum root is index %s", childIndex, maxParent);
        return maxParent;
    }

    public int randNum(int min, int max){
        if (min > max){
            int temp = max;
            max = min;
            min = temp;
        }
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }

    public Group generateMazeGroup(){
        Group group = new Group();
        MazeCell[][] cellArr = new MazeCell[cellsWidth][cellsHeight];
        var groupChildren = group.getChildren();

        //Fills the cellArr with full Border cells
        for (int w = 0; w < cellsWidth; w++){
            for (int h = 0; h < cellsHeight; h++){
                MazeCell tempCell = new MazeCell();
                tempCell.setLines(w*cellPixelsWidth, w*cellPixelsWidth + cellPixelsWidth,
                        h*cellPixelsHeight, h*cellPixelsHeight +cellPixelsHeight);
                // w = 0 is a Left Border cell, w = cellWidth - 1 is a Right border cell
                // h = 0 is a Top Border cell, h = cellHeight - 1 is a Bottom border cell

                //Top left
                if (w == 0 && h == 0){
                    ;//groupChildren.add(new Line(w*cellPixelsWidth, h*cellPixelsHeight, w*cellPixelsWidth + cellPixelsWidth, h*cellPixelsHeight));
                }
                //Left
                else if (w == 0)
                    //Adds a left line
                    groupChildren.add(new Line(w*cellPixelsWidth, h*cellPixelsHeight, w*cellPixelsWidth, h*cellPixelsHeight +cellPixelsHeight));
                //Top
                else if (h == 0)
                    //Adds Top line
                    groupChildren.add(new Line(w*cellPixelsWidth, h*cellPixelsHeight, w*cellPixelsWidth + cellPixelsWidth, h*cellPixelsHeight));
                groupChildren.add(tempCell.bottom);
                groupChildren.add(tempCell.right);
                cellArr[w][h] = tempCell;
            }
        }
        cellArr[cellsWidth-1][cellsHeight-1].right.setVisible(false);
        this.cellArr = cellArr;
        return group;
    }

    public MazeCell[][] generateMazeCellArr(){
        MazeCell[][] cellArr = new MazeCell[cellsWidth][cellsHeight];
        for (int w = 0; w < cellsWidth; w++){
            for (int h = 0; h < cellsHeight; h++){
                MazeCell tempCell = new MazeCell();
                tempCell.setLines(w*cellPixelsWidth, w*cellPixelsWidth + cellPixelsWidth,
                        h*cellPixelsHeight, h*cellPixelsHeight +cellPixelsHeight);
                cellArr[w][h] = tempCell;
            }
        }
        return cellArr;
    }

    public Scene getMainScene(){
        return mainScene;
    }

    public void setCellPixelsWidth(int cellPixelsWidth) {
        if (cellPixelsWidth > 0)
            this.cellPixelsWidth = cellPixelsWidth;
        else{
            this.cellPixelsWidth = MAZE_DEFAULTS.cellPixelSize;
            System.err.printf("Tried to set the cellPixelsHorizontal value to '%s' something less than 0..." +
                    "\n\tNow setting cellPixelsHorizontal to its default value of: %s ", cellPixelsWidth, this.cellPixelsHeight);
        }
    }
    public void setCellPixelsHeight(int cellPixelsHeight) {
        if (cellPixelsHeight > 0) {
            this.cellPixelsHeight = cellPixelsHeight;
        }
        else{
            this.cellPixelsWidth = MAZE_DEFAULTS.cellPixelSize;
            System.err.printf("Tried to set the cellPixelsVertical value to '%s' something less than 0..." +
                    "\n\tNow setting cellPixelsVertical to its default value of: %s ", cellPixelsHeight, this.cellPixelsHeight);
        }
    }

    public void setCellsWidth(int cellsWidth) {
        if (cellsWidth > 0) {
            this.cellsWidth = cellsWidth;
        }
        else{
            this.cellsWidth = MAZE_DEFAULTS.cellsAmountWidth;
            System.err.printf("Tried to set the cellsHorizontal value to '%s' something less than 0..." +
                    "\n\tNow setting cellsHorizontal to its default value of: %s ", cellsWidth, this.cellsWidth);
        }
    }
    public void setCellsHeight(int cellsHeight) {
        if (cellsHeight > 0) {
            this.cellsHeight = cellsHeight;
        }
        else{
            this.cellsHeight = MAZE_DEFAULTS.cellsAmountHeight;
            System.err.printf("Tried to set the cellsVertical value to '%s' something less than 0..." +
                    "\n\tNow setting cellsVertical to its default value of: %s ", cellsHeight, this.cellsHeight);
        }
    }
}
