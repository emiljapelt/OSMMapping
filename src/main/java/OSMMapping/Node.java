package OSMMapping;

public class Node{

    private float lon;
    private float lat;

    public Node(float lon, float lat){
        this.lon = lon;
        this.lat = lat;
    }

    public float getLon() { return lon; }
    public float getLat() { return lat; }
}
