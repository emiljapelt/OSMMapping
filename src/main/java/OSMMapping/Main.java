package OSMMapping;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    private OSMModel model;

    private StackPane root;

    private VBox searchField;
    private TextField input;
    private VBox suggestions;

    private Canvas mapCanvas;
    private GraphicsContext gc;
    private MapPainter painter;

    private int windowSizeX = 900;
    private int windowSizeY = 700;
    private float lineWidth = 1;
    private Affine transform = new Affine();

    private Stage primStage;
    private Bound viewBound;

    @Override
    public void start(Stage primaryStage) throws Exception{
        String mapFileLocation = "src/main/resources/isle-of-man-latest.osm";

        mapCanvas = new Canvas(windowSizeX, windowSizeY);
        gc = mapCanvas.getGraphicsContext2D();

        model = new OSMModel(mapFileLocation);
        model.addObserver(this::paintMap);
        controller = new Controller(this, model);

        painter = new MapPainter(this, model, gc, transform);

        resetView();

        lineWidth = (float) (1 / transform.getMxx());

        primStage = primaryStage;
        mapCanvas.widthProperty().bind(primStage.widthProperty());
        mapCanvas.heightProperty().bind(primStage.heightProperty());
        mapCanvas.widthProperty().addListener((a,b,c) -> {
            paintMap();
        });
        mapCanvas.widthProperty().addListener((a,b,c) -> {
            paintMap();
        });
        viewBound = new Bound(
                getModelCoordinates(primaryStage.getMaxWidth(), primaryStage.getMaxHeight()),
                getModelCoordinates(primaryStage.getMinWidth(), primaryStage.getMinHeight())
        );

        searchField = new VBox();
        input = new TextField();
        input.setMaxWidth(200);
        input.setMinWidth(100);
        input.setMaxHeight(30);
        input.setMinHeight(20);
        suggestions = new VBox();
        searchField.getChildren().addAll(input,suggestions);

        root = new StackPane();
        root.setAlignment(input, Pos.TOP_LEFT);
        root.getChildren().add(mapCanvas);
        root.getChildren().add(input);

        primaryStage.setTitle("OSMMapper");
        primaryStage.setScene(new Scene(root, windowSizeX, windowSizeY));
        primaryStage.show();

        long time = -System.nanoTime();
        painter.paintMap(lineWidth);
        time += System.nanoTime();
        System.out.println("Paint time: " + ((time)/1000000) + "ms");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Canvas getMapCanvas(){return mapCanvas;}
    public Bound getViewBound(){return viewBound;}

    public void paintMap() {
        painter.paintMap(lineWidth);
    }

    public void zoom(double factor, double x, double y) {
        transform.prependScale(factor, factor, x, y);
        lineWidth = (float) (1 / transform.getMxx());
        //updateViewBound();
        paintMap();
    }

    public void move(double x, double y){
        transform.prependTranslation(x, y);
        //updateViewBound();
        paintMap();
    }

    public void resetView() {
        move(-model.getMapBound().getMinLon(), -model.getMapBound().getMinLat());
        zoom(mapCanvas.getWidth() / (model.getMapBound().getMaxLat() - model.getMapBound().getMinLat()), 0, 0);
        paintMap();
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
}

//TODO Optimize, to reduce lag. Detail levels on zoom levels, only render what is nearly in view.
//TODO Rework Reader-Handler system
//TODO Mercatorprojektion https://da.wikipedia.org/wiki/Mercatorprojektion