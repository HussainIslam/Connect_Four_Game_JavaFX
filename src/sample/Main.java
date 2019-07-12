package sample;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.security.auth.callback.LanguageCallback;
import java.util.ArrayList;

public class Main extends Application {
    final private int ROW_NUMBER = 6;
    final private int COLUMN_NUMBER = 7;
    private int moveCounter = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        BorderPane.setAlignment(mainPane, Pos.BOTTOM_CENTER);


        GridPane boardPane = new GridPane();
        boardPane.setAlignment(Pos.BOTTOM_LEFT);

        mainPane.setCenter(boardPane);
        BorderPane.setMargin(boardPane, new Insets(10, 10, 10, 10));
        BorderPane.setAlignment(boardPane, Pos.TOP_RIGHT);

        HBox winnerPane = new HBox();
        winnerPane.setAlignment(Pos.CENTER);
        mainPane.setTop(winnerPane);


        Label textWin = new Label("");
        winnerPane.getChildren().add(textWin);

        VBox player1Side = new VBox();
        player1Side.setAlignment(Pos.BOTTOM_CENTER);
        BorderPane.setMargin(player1Side, new Insets(10, 10, 10, 10));
        mainPane.setLeft(player1Side);

        Circle player1Token = new Circle(20);
        player1Token.setStyle("-fx-fill:red;");
        Label player1 = new Label("Player 1");
        player1Side.getChildren().addAll(player1Token, player1);

        VBox player2Side = new VBox();
        player2Side.setAlignment(Pos.BOTTOM_CENTER);
        BorderPane.setMargin(player2Side, new Insets(10, 10, 10, 10));
        mainPane.setRight(player2Side);

        Circle player2Token = new Circle(20);
        player2Token.setStyle("-fx-fill:green;");
        Label player2 = new Label("Player 2");
        player2Side.getChildren().addAll(player2Token, player2);

        HBox nextTurn = new HBox();
        nextTurn.setAlignment(Pos.CENTER);
        mainPane.setBottom(nextTurn);
        BorderPane.setMargin(nextTurn, new Insets(10, 10, 10, 10));

        Label nextPlayer = new Label("Next Move: Player 1");
        nextTurn.getChildren().add(nextPlayer);


        for (int row = 0; row < ROW_NUMBER; row++){
            for (int column = 0; column < COLUMN_NUMBER; column++){
                StackPane tokenPane = new StackPane();
                Rectangle square = new Rectangle(50, 50, Color.GRAY);
                Circle circleInset = new Circle(20);
                circleInset.setStyle("-fx-fill:white;");
                tokenPane.getChildren().addAll(square, circleInset);
                boardPane.add(tokenPane, column, row);
                tokenPane.setOnMouseClicked(mouseEvent -> {
                    try{
                       int columnNumber = GridPane.getColumnIndex(tokenPane);
                       int rowNumber = this.findEmptyRow(columnNumber, boardPane);
                       if(rowNumber < 0){
                           throw new RowOutOfBoundsException();
                       }
                       System.out.println("Row number: " +rowNumber);
                       Node item = this.getNodeFromGridPane(rowNumber, columnNumber,boardPane);
                       item.setStyle("-fx-fill:"+(moveCounter % 2 == 0 ? "red;" : "green;"));
                       nextPlayer.setText("Next Move: Player " +(moveCounter % 2 == 0 ? "2" : "1"));
                       if(this.checkWinner(rowNumber, columnNumber, boardPane)){
                           String winner;
                           if(moveCounter % 2 == 0){
                               winner = "Player 1 is the winner";
                           }
                           else {
                               winner = "Player 2 is the winner";
                           }
                           textWin.setText(winner);
                       }
                    }
                    catch (RowOutOfBoundsException roobe){
                        roobe.printStackTrace();
                    }
                    catch (NullPointerException npe){
                        npe.printStackTrace();
                    }
                    catch (Exception e){
                        System.out.println("Some other exception");
                    }

                    moveCounter++;
                });
            }
        }

        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(new Scene(mainPane));
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

    public boolean checkRightDownward(int rowIndex, int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        while(rowIndex < ROW_NUMBER - 1 && columnIndex < COLUMN_NUMBER - 1){
            Node temp1 = this.getNodeFromGridPane(rowIndex + 1, columnIndex + 1, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
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
        while(rowIndex > 0  && columnIndex < COLUMN_NUMBER - 1){
            Node temp1 = this.getNodeFromGridPane(rowIndex - 1, columnIndex + 1, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
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
        System.out.println("----------------------------------------------");
        return fullSetMatch;
    }

    public boolean checkLeftDownward(int rowIndex, int columnIndex, GridPane pane){
        int counter = 1;
        boolean fullSetMatch = false;
        while(rowIndex >= 0 && rowIndex < ROW_NUMBER - 1 && columnIndex > 0 && columnIndex < COLUMN_NUMBER){
            Node temp1 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex + 1, columnIndex - 1, pane);
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
        while(rowIndex > 0 && columnIndex > 0){
            Node temp1 = this.getNodeFromGridPane(rowIndex - 1, columnIndex - 1, pane);
            Node temp0 = this.getNodeFromGridPane(rowIndex, columnIndex, pane);
            //System.out.println("Temp1 row: " +rowIndex + " Column: " +columnIndex);
            //System.out.println("Temp0 row: " +(rowIndex - 1) + " Column: " +(columnIndex - 1));
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
        return fullSetMatch;
    }

    public void generateAlert(Alert.AlertType type, String title, String header, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
