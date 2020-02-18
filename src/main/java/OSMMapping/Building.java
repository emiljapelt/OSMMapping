package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Building implements Drawable{
    Drawable shape;

    public Building(Way way){
        shape = new LinePath(way);
    }

    public Building(Relation relation){
        shape = new PolyLinePath(relation);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.DARKGREY);
        gc.setStroke(Color.BLACK);
        shape.draw(gc);
        gc.fill();
    }
}