package OSMMapping;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class LinePath implements Drawable{

    float[] coordinates;
    Type type;

    public LinePath(Way way, Type type){
        ArrayList<Node> nodes = way.getNodes();
        coordinates = new float[nodes.size() * 2];
        for(int i = 0; i < nodes.size(); i++){
            coordinates[i*2] = nodes.get(i).getLon();
            coordinates[i*2+1] = nodes.get(i).getLat();
        }
        this.type = type;
    }

    public Type getType(){ return type; }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }

    public void trace(GraphicsContext gc){
        gc.moveTo(coordinates[0], coordinates[1]);
        for (int i = 2 ; i < coordinates.length ; i += 2) {
            gc.lineTo(coordinates[i], coordinates[i+1]);
        }
    }
}
