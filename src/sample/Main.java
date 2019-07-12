package sample;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    final private int ROW_NUMBER = 6;
    final private int COLUMN_NUMBER = 7;
    private int moveCounter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane mainPane = new BorderPane();
        GridPane boardPane = new GridPane();
        boardPane.setAlignment(Pos.BOTTOM_LEFT);
        mainPane.setCenter(boardPane);

        Label textWin = new Label("");
        mainPane.setTop(textWin);

        VBox player1Side = new VBox();
        player1Side.setAlignment(Pos.BOTTOM_CENTER);
        mainPane.setLeft(player1Side);

        Circle player1Token = new Circle(20);
        player1Token.setStyle("-fx-fill:red;");
        player1Side.getChildren().add(player1Token);

        VBox player2Side = new VBox();
        player2Side.setAlignment(Pos.BOTTOM_CENTER);
        mainPane.setRight(player2Side);

        Circle player2Token = new Circle(20);
        player2Token.setStyle("-fx-fill:green;");
        player2Side.getChildren().add(player2Token);

        for (int row = 0; row < ROW_NUMBER; row++){
            for (int column = 0; column < COLUMN_NUMBER; column++){
                StackPane tokenPane = new StackPane();
                Rectangle square = new Rectangle(50, 50, Color.GRAY);
                Circle circleInset = new Circle(20);
                circleInset.setStyle("-fx-fill:white;");
                //circleInset.setFill(Color.WHITE);
                tokenPane.getChildren().addAll(square, circleInset);
                boardPane.add(tokenPane, column, row);
                tokenPane.setOnMouseClicked(mouseEvent -> {
                    try{
                       int columnNumber = GridPane.getColumnIndex(tokenPane);
                       int rowNumber = this.findEmptyRow(columnNumber, boardPane);
                       System.out.println("Column: " +columnNumber + " Row: " +rowNumber);
                       Node item = this.getNodeFromGridPane(rowNumber, columnNumber,boardPane);
                       item.setStyle("-fx-fill:"+(moveCounter % 2 == 0 ? "red;" : "green;"));
                        //System.out.println("We have a winner: " +this.checkWinner(rowNumber, columnNumber, boardPane));
                        System.out.println("Check row: " +this.checkRow(rowNumber, boardPane));
                        System.out.println("check column: " +this.checkColumn(columnNumber, boardPane));
                        System.out.println("check right up: " +this.checkRightUpward(rowNumber, columnNumber, boardPane));
                        System.out.println("check right down: " +this.checkRightDownward(rowNumber, columnNumber, boardPane));
                        System.out.println("check left up: " +this.checkLeftUpward(rowNumber, columnNumber, boardPane));
                        System.out.println("check left down: " +this.checkLeftDownward(rowNumber, columnNumber, boardPane));
                       if(this.checkWinner(rowNumber, columnNumber, boardPane)){
                           String winner;
                           if(moveCounter % 2 == 0){
                               winner = "Player 1 is the winner";
                           }
                           else {
                               winner = "Player 2 is the winner";
                           }
                           //System.out.println(winner);
                           textWin.setText(winner);
                       }
                    }
                    catch (NullPointerException npe){
                        npe.printStackTrace();
                    }
                    catch (Exception e){
                        System.out.println("Some other exception");
                    }
                    //System.out.println(item.toString());

                    moveCounter++;
                });
            }
        }

        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(new Scene(mainPane, 500, 400));
        primaryStage.show();
    }

    public Node getNodeFromGridPane(int row, int column, GridPane gridPane){
        Node temp = null;
        for (Node stack: gridPane.getChildren()){
            if (stack instanceof Pane) {
                if(GridPane.getColumnIndex(stack) == column && GridPane.getRowIndex(stack) == row){
                    temp = this.getNodeFromPane((Pane) stack);
                }
            }
        }
        return temp;
    }

    public Node getNodeFromPane(Pane pane){
        Node temp = null;
        for(Node node: pane.getChildrenUnmodifiable()){
            if(node instanceof Pane){
                getNodeFromPane((Pane)node);
            }
            if (node instanceof Circle){
                temp = node;
            }
        }
        return temp;
    }

    public int findEmptyRow(int columnIndex, GridPane pane){
        int index = -1;
        for(int i = 0; i < ROW_NUMBER; i++){
            Node temp = this.getNodeFromGridPane(i, columnIndex, pane);
            String[] styles = temp.getStyle().split(";");
            for(int position = 0; position < styles.length; position++){
                if(styles[position].equals("-fx-fill:white")){
                    index = i;
                }
            }
        }
        return index;
    }

    public boolean checkWinner(int rowIndex, int columnIndex, GridPane pane){
        return  this.checkColumn(columnIndex, pane) ||
                this.checkRow(rowIndex, pane) ||
                this.checkRightUpward(rowIndex, columnIndex, pane) ||
                this.checkRightDownward(rowIndex, columnIndex, pane) ||
                this.checkLeftDownward(rowIndex, columnIndex, pane) ||
                this.checkLeftUpward(rowIndex, columnIndex, pane);
    }

    public boolean checkRow(int rowIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        for(int columnIndex = 1; columnIndex < COLUMN_NUMBER; columnIndex++){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex, columnIndex - 1, pane);
            if (temp0.getStyle().equals(temp1.getStyle()) && !temp1.getStyle().equals("-fx-fill:white;") && !temp0.getStyle().equals("-fx-fill:white;")) {
                counter++;
                if(counter == 3){
                    fullSetMatch = true;
                }
            } else {
                counter = 0;
            }
        }
        return fullSetMatch;
    }

    public boolean checkColumn(int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        for(int rowIndex = 1; rowIndex < ROW_NUMBER; rowIndex++){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex - 1, columnIndex, pane);
            //System.out.println("temp 1 in check column row number " +rowIndex +": "+temp1.toString());
            //System.out.println("temp 0 in check column row number " +(rowIndex - 1) +": "+temp0.toString());
            //System.out.println(temp0.getStyle().equals(temp1.getStyle()));
            //System.out.println(!temp1.getStyle().equals("-fx-fill:white;"));
            //System.out.println(!temp1.getStyle().equals("-fx-fill:white;"));
            if (temp0.getStyle().equals(temp1.getStyle()) && !temp1.getStyle().equals("-fx-fill:white;") && !temp0.getStyle().equals("-fx-fill:white;")) {
                System.out.println("Inside the if statement");
                counter++;
                if(counter == 3){
                    fullSetMatch = true;
                }
            } else {
                counter = 0;
            }
        }
        return fullSetMatch;
    }

    public boolean checkRightDownward(int rowIndex, int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        //int r = rowIndex;
        //int c = columnIndex;
        rowIndex++; columnIndex++;
        while(rowIndex < ROW_NUMBER && columnIndex < COLUMN_NUMBER){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex - 1, columnIndex - 1, pane);
            //System.out.println("temp 1 in check column row number " +rowIndex +": "+temp1.toString());
            //System.out.println("temp 0 in check column row number " +(rowIndex - 1) +": "+temp0.toString());
            if(temp0.getStyle().equals(temp1.getStyle())  && !temp1.getStyle().equals("-fx-fill:white;") && !temp0.getStyle().equals("-fx-fill:white;")){
                counter++;
                if( counter == 3 ){
                    fullSetMatch = true;
                }
            }
            else{
                counter = 1;
            }
            rowIndex++; columnIndex++;
        }
        return fullSetMatch;
    }

    public boolean checkRightUpward(int rowIndex, int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        rowIndex--; columnIndex++;
        while(rowIndex >= 0  && columnIndex < COLUMN_NUMBER){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex + 1, columnIndex - 1, pane);
            //System.out.println("temp 1 in check column row number " +rowIndex +": "+temp1.toString());
            //System.out.println("temp 0 in check column row number " +(rowIndex - 1) +": "+temp0.toString());
            if(temp0.getStyle().equals(temp1.getStyle()) && !temp1.getStyle().equals("-fx-fill:white;") && !temp0.getStyle().equals("-fx-fill:white;")){
                counter++;
                if( counter == 3 ){
                    fullSetMatch = true;
                }
            }
            else{
                counter = 1;
            }
            rowIndex--; columnIndex++;
        }
        return fullSetMatch;
    }

    public boolean checkLeftDownward(int rowIndex, int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        rowIndex--; columnIndex--;
        while(rowIndex < ROW_NUMBER  && columnIndex >= 0){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex - 1, columnIndex + 1, pane);
            if(temp0.getStyle().equals(temp1.getStyle()) && !temp1.getStyle().equals("-fx-fill:white;") && !temp0.getStyle().equals("-fx-fill:white;")){
                counter++;
                if( counter == 3 ){
                    fullSetMatch = true;
                }
            }
            else{
                counter = 1;
            }
            rowIndex++; columnIndex--;
        }
        return fullSetMatch;
    }

    public boolean checkLeftUpward(int rowIndex, int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        rowIndex--;columnIndex--;
        while(rowIndex >= 0 && columnIndex >= 0){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex + 1, columnIndex + 1, pane);
            if(temp0.getStyle().equals(temp1.getStyle()) && !temp1.getStyle().equals("-fx-fill:white;") && !temp0.getStyle().equals("-fx-fill:white;")){
                counter++;
                if( counter == 3 ){
                    fullSetMatch = true;
                }
            }
            else{
                counter = 1;
            }
            rowIndex--; columnIndex--;
        }
        System.out.println("Check left upward: " +fullSetMatch);
        return fullSetMatch;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
