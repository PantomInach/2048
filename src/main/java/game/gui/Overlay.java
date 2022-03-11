package game.gui;

//import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;

import game.Coordinate2D;
import game.Direction;
import game.Move;
import game.NumberGame;
import game.Tile;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler; 

public class Overlay extends Application {

    Stage window;
    int lineWidth = 5;
    int elementSize = 100;
    Pane gridPane = new Pane();
    StackPane[][] olTileStorage = new StackPane[4][4]; //Stores the current stackPanes for the tiles
    Label score = new Label("Points: 0"); //score Label
    private NumberGame ng;
    private boolean allowMoving = true;
    Direction dirToMove;
    private boolean gamePaused = false;

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        window.setTitle("2048");

        //Label font size
        score.setFont(new Font(20));

        //GridLines
        ArrayList<Rectangle> lineList = new ArrayList<>();
        Rectangle temp = null;
        for (int i = 1; i < 4; i++) {

            temp = new Rectangle(0 , elementSize*i + lineWidth*(i-1), 4*elementSize+3*lineWidth, lineWidth);
            temp.setFill(Color.GRAY);
            lineList.add(temp);
            temp = new Rectangle(elementSize * i + lineWidth * (i-1), 0, lineWidth, 4*elementSize+3*lineWidth);
            temp.setFill(Color.GRAY);
            lineList.add(temp);
        }

        //Layouts
        //Top Layout
        HBox layout = new HBox();
        layout.getChildren().addAll(score);
            //Top layout background color
        BackgroundFill bgFill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
        layout.setBackground(new Background(bgFill));

        //Grid Layout
        gridPane.getChildren().addAll(lineList);

        //Merge Layouts
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(layout);
        borderPane.setCenter(gridPane);

        //Start Game
        ng = new NumberGame(4,4,3);
        drawField();

        //Create scene
        Scene scene = new Scene(borderPane, 4*elementSize+3*lineWidth, 30+4*elementSize+3*lineWidth);

        //Add KeyListener to scene
        scene.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent keyEvent) {
                if (allowMoving) {
                    allowMoving = false;
                    Direction dir = null;
                    switch (keyEvent.getCode()) {
                        case UP:
                            dir = Direction.UP;
                            break;
                        case DOWN:
                            dir = Direction.DOWN;
                            break;
                        case LEFT:
                            dir = Direction.LEFT;
                            break;
                        case RIGHT:
                            dir = Direction.RIGHT;
                            break;
                        default:
                            dir = null;
                            break;
                    }
                    dirToMove = dir;
                }
            }
        });

        //Show Overlay
        window.setScene(scene);
        window.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (dirToMove != null) {
                    if (ng.canMove(dirToMove)) {
                        List<Move> moveList = ng.move(dirToMove);
                        int points = ng.getPoints();
                        //update(points, moveList);
                        if (!ng.isFull()) ng.addRandomTile();
                        update();
                        //Debug print
                        /*
                        System.out.println("\nScore: "+ng.getPoints());
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                System.out.print(ng.get(j,i)+" ");
                            }
                            System.out.print(" | "+i+"\n");
                        }
                        System.out.println("_________|");
                        System.out.println("0 1 2 3");
                         */
                        allowMoving = true;
                    } else {
                        if (!ng.canMove()) {
                            youLostMessage(ng.getPoints());
                        } else {
                            allowMoving = true;
                        }
                    }
                }
                dirToMove = null;
            }
        };
        timer.start();
    }

    public void update(){
        drawField();
    }

    public void update(int score, List<Move> moves) {
        this.score.setText("Points: "+score);
        for (Move move : moves) {
            removeTileFromOverlay(move.getFrom());
            addTileToOverlay(move.getTo(), move.getNewValue());
        }
    }

    public void addTileToOverlay(Coordinate2D coord, int value){
        int x = coord.getX() * elementSize + coord.getX() * lineWidth + 5;
        int y = coord.getY() * elementSize + coord.getY() * lineWidth + 5;
        //System.out.println(x+", "+y);
        StackPane tileOl = new StackPane();
        Rectangle r = new Rectangle(x, y , elementSize-10, elementSize-10);
        r.setFill(Color.GREEN);
        r.setArcWidth(20);
        r.setArcHeight(20);
        tileOl.getChildren().add(r);
        Label l =new Label(String.valueOf(value));
        l.setFont(new Font(20));
        tileOl.getChildren().add(l);
        olTileStorage[coord.getX()][coord.getY()] = tileOl;
        gridPane.getChildren().add(tileOl);
        tileOl.setTranslateX(x);
        tileOl.setTranslateY(y);
    }

    public void removeTileFromOverlay(int x, int y){
        gridPane.getChildren().remove(olTileStorage[x][y]);
        olTileStorage[x][y] = null;
    }

    public void removeTileFromOverlay(Coordinate2D coord){
        removeTileFromOverlay(coord.getX(), coord.getY());
    }

    public void removeTileFromOverlay(Tile tile){
        removeTileFromOverlay(tile.getCoordinate());
    }

    //Refreshes the field completely
    public void drawField(){
        score.setText("Points: "+ng.getPoints());
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                removeTileFromOverlay(i,j);
                if (ng.get(i,j) != 0) {
                    addTileToOverlay(new Coordinate2D(i, j), ng.get(i,j));
                }
            }
        }
    }

    public void youLostMessage(int points) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        stage.setTitle("You Lost");
        stage.setMinHeight(100);
        stage.setMinWidth(250);
        Label label = new Label();
        label.setText("Your score is: "+points);
        label.setFont(new Font(20));

        //Create Buttons
        javafx.scene.control.Button restart = new javafx.scene.control.Button("Restart");
        javafx.scene.control.Button end = new Button("End");
        restart.setOnAction(x -> {
            allowMoving = true;
            ng = new NumberGame(4,4, 3);
            drawField();
            stage.close();
        });
        end.setOnAction(x -> {
            System.exit(0);
        });

        //Create Layouts
        VBox text = new VBox();
        text.getChildren().addAll(label);
        text.setAlignment(Pos.CENTER);

        Pane options = new Pane();
        options.getChildren().addAll(restart, end);
        options.setMinHeight(40);
        options.setMinWidth(200);
        restart.setLayoutX(120);
        restart.setLayoutY(5);
        end.setLayoutX(180);
        end.setLayoutY(5);

        BorderPane pane = new BorderPane();
        pane.setCenter(text);
        pane.setBottom(options);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }
}
