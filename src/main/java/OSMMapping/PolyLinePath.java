package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class PolyLinePath implements Drawable{
    private ArrayList<LinePath> ways;
    Type type;

    public PolyLinePath(Relation relation, Type type){
        ways = new ArrayList<>();
        this.type = type;
        for(Way way : relation.getWays()){
            ways.add(new LinePath(way, type));
        }
    }

    public Type getType(){return type;}

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        for(LinePath linePath : ways){
            linePath.trace(gc);
        }
        gc.stroke();
    }
}
