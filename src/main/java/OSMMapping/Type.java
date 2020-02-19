package OSMMapping;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public enum Type {

    UNKNOWN,
    BUILDING,
    HIGHWAY,
    COASTLINE,
    FOREST,
    BEACH,
    FARMFIELD,
    WATERWAY,
    WATER;


    public static Paint getColor(Type type) {
        switch (type) {
            case COASTLINE:
                return Color.WHITE;
            case WATERWAY:
                return Color.BLUE;
            case WATER:
                return Color.AQUA;
            case BUILDING:
                return Color.BROWN;
            case HIGHWAY:
                return Color.DARKGREY;
            case FOREST:
                return Color.GREEN;
            case BEACH:
                return Color.SANDYBROWN;
            case FARMFIELD:
                return Color.YELLOWGREEN;
            default:
                return Color.BLACK;
        }
    }
}
