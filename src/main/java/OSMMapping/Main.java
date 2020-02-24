package OSMMapping;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {

    private Controller controller;
    private OSMModel model;

    private StackPane root;

    private VBox searchField;
    private TextField input;
    private VBox suggestions;
    private ScrollPane suggestionScroll;

    private Canvas mapCanvas;
    private GraphicsContext gc;
    private MapPainter painter;

    private int windowSizeX = 900;
    private int windowSizeY = 700;
    private Affine transform = new Affine();

    private Stage primStage;
    private Bound viewBound;

    @Override
    public void start(Stage primaryStage) throws Exception{
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("faroe-islands-latest.osm");

        mapCanvas = new Canvas(windowSizeX, windowSizeY);
        gc = mapCanvas.getGraphicsContext2D();

        searchField = new VBox();
        input = new TextField();
        input.setMaxWidth(200);
        input.setMinWidth(100);
        input.setMaxHeight(30);
        input.setMinHeight(20);
        suggestions = new VBox();
        suggestionScroll = new ScrollPane();
        suggestionScroll.setContent(suggestions);
        suggestionScroll.setMaxWidth(200);
        suggestionScroll.setMaxHeight(400);
        suggestionScroll.setMinHeight(0);
        suggestionScroll.setStyle("-fx-focus-color: transparent");
        searchField.getChildren().addAll(input, suggestionScroll);

        model = new OSMModel(inputStream);
        model.addObserver(this::paintMap);
        controller = new Controller(this, model);

        painter = new MapPainter(this, model, gc, transform);

        resetView();

        primStage = primaryStage;

        viewBound = new Bound(
                getModelCoordinates(primaryStage.getMaxWidth(), primaryStage.getMaxHeight()),
                getModelCoordinates(primaryStage.getMinWidth(), primaryStage.getMinHeight())
        );

        root = new StackPane();
        root.getChildren().add(mapCanvas);
        searchField.setPickOnBounds(false);
        root.getChildren().add(searchField);
        root.setAlignment(searchField, Pos.TOP_LEFT);

        mapCanvas.widthProperty().bind(primStage.widthProperty());
        mapCanvas.heightProperty().bind(primStage.heightProperty());
        mapCanvas.widthProperty().addListener((a,b,c) -> {
            paintMap();
        });
        mapCanvas.heightProperty().addListener((a,b,c) -> {
            paintMap();
        });

        long time = -System.nanoTime();
        paintMap();
        time += System.nanoTime();
        System.out.println("Paint time: " + ((time)/1000000) + "ms");

        primaryStage.setTitle("OSMMapper");
        primaryStage.setScene(new Scene(root, windowSizeX, windowSizeY));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Canvas getMapCanvas(){return mapCanvas;}
    public Bound getViewBound(){return viewBound;}
    public TextField getInput(){return input;}
    public VBox getSuggestions(){return suggestions;}

    public void paintMap() {
        painter.paintMap();
        System.out.println("*");
    }

    public void zoom(double factor, double x, double y) {
        transform.prependScale(factor, factor, x, y);
        //updateViewBound();
    }

    public void move(double x, double y){
        transform.prependTranslation(x, y);
        //updateViewBound();
    }

    public void zoomToNode(Node node){
        transform.setMxx(1);
        transform.setMyy(1);
        Point2D nodeToScreen = getScreenCoordinates(node.getLon(), node.getLat());
        move(-nodeToScreen.getX() + primStage.getWidth()/2, -nodeToScreen.getY() + primStage.getHeight()/2);
        zoom(80* (mapCanvas.getWidth() / (model.getMapBound().getMaxLat() - model.getMapBound().getMinLat())),primStage.getWidth()/2,primStage.getHeight()/2);
        paintMap();
    }

    public void resetView() {
        move(-model.getMapBound().getMinLon(), -model.getMapBound().getMinLat());
        zoom(mapCanvas.getWidth() / (model.getMapBound().getMaxLat() - model.getMapBound().getMinLat()), 0, 0);
    }

    public void updateViewBound(){
        viewBound.updateBound(
                getModelCoordinates(primStage.getMaxWidth(), primStage.getMaxHeight()),
                getModelCoordinates(primStage.getMinWidth(), primStage.getMinHeight())
        );
    }

    public Point2D getModelCoordinates(double x, double y){
        try{
            return transform.inverseTransform(x,y);
        } catch(NonInvertibleTransformException e){
            e.printStackTrace();
            return null;
        }
    }

    public Point2D getScreenCoordinates(double x, double y){
            return transform.transform(x,y);
    }
}