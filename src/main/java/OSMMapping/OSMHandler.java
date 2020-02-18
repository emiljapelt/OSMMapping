package OSMMapping;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSMHandler extends DefaultHandler {

    private ArrayList<Drawable> buildings;
    private ArrayList<Drawable> coastlines;
    private ArrayList<Drawable> highways;

    private Map<Type, List<Drawable>> enumMap;

    private SortedArrayList<Node> tempNodes;
    private SortedArrayList<Way> tempWays;
    private SortedArrayList<Relation> tempRelations;
    private HashMap<Node, Way> tempCoastlines;
    private Bound tempBound;

    private Way wayHolder;
    private Relation relationHolder;

    private long currentID;

    public OSMHandler(){
        tempNodes = new SortedArrayList<>();
        tempWays = new SortedArrayList<>();
        tempRelations = new SortedArrayList<>();
        tempCoastlines = new HashMap<>();

        enumMap = new HashMap<>();

        buildings = new ArrayList<>();
        coastlines = new ArrayList<>();
        highways = new ArrayList<>();
    }

    public ArrayList<Drawable> getBuildings() {return buildings;}
    public ArrayList<Drawable> getCoastlines(){return coastlines;}
    public ArrayList<Drawable> getHighways(){return highways;}
    public Bound getTempBound(){return tempBound;}

    private boolean building;
    private boolean coastline;
    private boolean highway;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName){
            case "bounds":
                float tempMaxLat = Float.parseFloat(attributes.getValue("maxlat"));
                float tempMinLat = Float.parseFloat(attributes.getValue("minlat"));
                float tempMinLon = Float.parseFloat(attributes.getValue("minlon"));
                float tempMaxLon = Float.parseFloat(attributes.getValue("maxlon"));
                tempBound = new Bound(
                        -tempMaxLat,
                        -tempMinLat,
                        (float) Math.cos(tempMinLat*Math.PI/180) * tempMinLon,
                        (float) Math.cos(tempMaxLat*Math.PI/180) * tempMaxLon
                );
                break;
            case "node":
                currentID = Long.parseLong(attributes.getValue("id"));
                float tempLon = Float.parseFloat(attributes.getValue("lon"));
                float tempLat = Float.parseFloat(attributes.getValue("lat"));
                Node node = new Node(currentID, (float) Math.cos(tempLat*Math.PI/180) * tempLon, -tempLat);
                tempNodes.add(node);
                break;
            case "way":
                building = false;
                coastline = false;
                highway = false;
                currentID = Long.parseLong(attributes.getValue("id"));
                wayHolder = new Way();
                tempWays.add(wayHolder);
                break;
            case "relation":
                building = false;
                coastline = false;
                highway = false;
                currentID = Long.parseLong(attributes.getValue("id"));
                relationHolder = new Relation();
            case "tag":
                String k = attributes.getValue("k");
                String v = attributes.getValue("v");

                if (k != null && k.equals("building")) building = true;
                if (k != null && k.equals("natural") && v.equals("coastline")) coastline = true;
                if (k != null && k.equals("highway")) highway = true;
                break;
            case "nd":
                long ref = Long.parseLong(attributes.getValue("ref"));
                if(wayHolder != null) {
                   if(tempNodes.get(ref) != null) wayHolder.addNode(tempNodes.get(ref));
                }
                break;
            case "member":
                switch(attributes.getValue("type")){
                    case "node":
                        relationHolder.addNode(tempNodes.get(Long.parseLong(attributes.getValue("ref"))));
                        break;
                    case "way":
                        long memberRef = Long.parseLong(attributes.getValue("ref"));
                        if (tempWays.get(memberRef) != null)
                            relationHolder.addWay(tempWays.get(memberRef));
                        break;
                    case "relation":
                        relationHolder.addRefId(Long.parseLong(attributes.getValue("ref")));
                        break;
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName){
            case "way":
                if (wayHolder.getNodes().size() == 0) break;
                if(coastline){
                    Way before = tempCoastlines.remove(wayHolder.first());
                    if (before != null) {
                        tempCoastlines.remove(before.first());
                        tempCoastlines.remove(before.last());
                    }
                    Way after = tempCoastlines.remove(wayHolder.last());
                    if (after != null) {
                        tempCoastlines.remove(after.first());
                        tempCoastlines.remove(after.last());
                    }
                    wayHolder = Way.merge(Way.merge(before, wayHolder), after);
                    tempCoastlines.put(wayHolder.first(), wayHolder);
                    tempCoastlines.put(wayHolder.last(), wayHolder);
                }
                if(building) buildings.add(new Building(wayHolder));
                if(highway) highways.add(new Highway(wayHolder));
                break;
            case "relation":
                tempRelations.add(relationHolder);
                if(building) buildings.add(new Building(relationHolder));
                break;
            case "osm":
                for (Map.Entry<Node, Way> entry : tempCoastlines.entrySet()) {
                    if (entry.getKey() == entry.getValue().last()) {
                        coastlines.add(new Coastline(entry.getValue()));
                    }
                }
                break;
        }
    }
}
