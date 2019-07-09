package sample;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

        GridPane boardPane = new GridPane();
        boardPane.setAlignment(Pos.BOTTOM_LEFT);
        boardPane.setGridLinesVisible(true);
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
                    System.out.println("Column" +GridPane.getColumnIndex(tokenPane));
                    System.out.println("Row" +GridPane.getRowIndex(tokenPane));
                    try{
                       int rowNumber = this.findEmptyRow(GridPane.getColumnIndex(tokenPane), boardPane);
                       Node item = this.getNodeFromGridPane(rowNumber, GridPane.getColumnIndex(tokenPane),boardPane);
                       item.setStyle("-fx-stroke:"+(moveCounter % 2 == 0 ? "red;" : "green;")  +"-fx-fill:yellow;");

                       System.out.println(item.getStyle());
                    }
                    catch (NullPointerException npe){
                        System.out.println("Null pointer exception");
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
        primaryStage.setScene(new Scene(boardPane, 500, 400));
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

    public static void main(String[] args) {
        launch(args);
    }
}
