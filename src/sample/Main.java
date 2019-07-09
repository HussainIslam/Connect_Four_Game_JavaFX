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

public class Main extends Application {
    final int ROW_NUMBER = 6;
    final int COLUMN_NUMBER = 7;
    int moveCounter = 0;

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
                circleInset.setFill(Color.WHITE);
                tokenPane.getChildren().addAll(square, circleInset);
                boardPane.add(tokenPane, column, row);
                tokenPane.setOnMouseClicked(mouseEvent -> {
                    System.out.println("Column" +GridPane.getColumnIndex(tokenPane));
                    System.out.println("Row" +GridPane.getRowIndex(tokenPane));
                    final Node item = this.getNode(GridPane.getColumnIndex(tokenPane), GridPane.getRowIndex(tokenPane), boardPane);
                    System.out.println(item.getStyle());
                    if(moveCounter % 2 ==0){
                        circleInset.setFill(Color.RED);
                    }
                    else{
                        circleInset.setFill(Color.GREEN);
                    }
                    moveCounter++;
                });
            }
        }




        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(new Scene(boardPane, 500, 400));
        primaryStage.show();
    }

    public Node getNode(int row, int column, GridPane gridPane){
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();
        for (Node node: childrens){
            System.out.println(node.toString());
            if(GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row){
                result = node;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
