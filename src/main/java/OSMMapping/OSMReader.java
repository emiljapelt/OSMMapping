package OSMMapping;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.*;

public class OSMReader {
    private ArrayList<Drawable> coastlines;

    private Map<Type, List<Drawable>> enumMap;
    private ArrayList<Address> addresses;

    private SortedArrayList<Node> tempNodes;
    private SortedArrayList<Way> tempWays;
    private SortedArrayList<Relation> tempRelations;
    private HashMap<Node, Way> tempCoastlines;
    private Bound tempBound;

    private Node nodeHolder;
    private Way wayHolder;
    private Relation relationHolder;

    private long currentID;

    Type type = Type.UNKNOWN;

    public ArrayList<Drawable> getCoastlines(){return coastlines;}
    public Bound getTempBound(){return tempBound;}
    public Map<Type, List<Drawable>> getEnumMap(){return enumMap;}
    public ArrayList<Address> getAddresses(){return addresses;}

    public OSMReader(InputStream inputStream){

        tempNodes = new SortedArrayList<>();
        tempWays = new SortedArrayList<>();
        tempRelations = new SortedArrayList<>();
        tempCoastlines = new HashMap<>();

        enumMap = new HashMap<>();
        addresses = new ArrayList<>();
        String[] addressInfo = new String[4];

        coastlines = new ArrayList<>();

        try {
            XMLStreamReader reader = XMLInputFactory
                    .newInstance()
                    .createXMLStreamReader(inputStream);

            while(reader.hasNext()){
                reader.next();
                switch (reader.getEventType()){
                    case START_ELEMENT:
                        String qName = reader.getLocalName();
                        switch (qName){
                            case "bounds":
                                float tempMaxLat = Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                                float tempMinLat = Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                                float tempMinLon = Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                                float tempMaxLon = Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                                tempBound = new Bound(
                                        -tempMaxLat,
                                        -tempMinLat,
                                        (float) Math.cos(tempMinLat*Math.PI/180) * tempMinLon,
                                        (float) Math.cos(tempMaxLat*Math.PI/180) * tempMaxLon
                                );
                                break;
                            case "node":
                                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                                float tempLon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                                float tempLat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                                nodeHolder = new Node(currentID, (float) Math.cos(tempLat*Math.PI/180) * tempLon, -tempLat);
                                tempNodes.add(nodeHolder);
                                addressInfo = new String[4];
                                break;
                            case "way":
                                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                                wayHolder = new Way(currentID);
                                tempWays.add(wayHolder);
                                type = Type.UNKNOWN;
                                break;
                            case "relation":
                                currentID = Long.parseLong(reader.getAttributeValue(null, "id"));
                                relationHolder = new Relation();
                                type = Type.UNKNOWN;
                                break;
                            case "tag":
                                String k = reader.getAttributeValue(null, "k");
                                String v = reader.getAttributeValue(null, "v");

                                switch (k){
                                    case "addr:city":
                                        addressInfo[0] = v;
                                        if (addressInfoIsFull(addressInfo) && wayHolder == null){
                                            addresses.add(new Address(tempNodes.get(currentID), addressInfo));
                                        }
                                        break;
                                    case "addr:postcode":
                                        addressInfo[1] = v;
                                        if (addressInfoIsFull(addressInfo) && wayHolder == null){
                                            addresses.add(new Address(tempNodes.get(currentID), addressInfo));
                                        }
                                        break;
                                    case "addr:street":
                                        addressInfo[2] = v;
                                        if (addressInfoIsFull(addressInfo) && wayHolder == null){
                                            addresses.add(new Address(tempNodes.get(currentID), addressInfo));
                                        }
                                        break;
                                    case "addr:housenumber":
                                        addressInfo[3] = v;
                                        if (addressInfoIsFull(addressInfo) && wayHolder == null){
                                            addresses.add(new Address(tempNodes.get(currentID), addressInfo));
                                        }
                                        break;
                                    case "building":
                                        type = Type.BUILDING;
                                        break;
                                    case "natural":
                                        switch (v){
                                            case "coastline":
                                                type = Type.COASTLINE;
                                                break;
                                            case "water":
                                                type = Type.WATER;
                                                break;
                                            case "beach":
                                                type = Type.BEACH;
                                                break;
                                            case "wood":
                                                type = Type.FOREST;
                                                break;
                                        }
                                        break;
                                    case "waterway":
                                        type = Type.WATERWAY;
                                        break;
                                    case "landuse":
                                        switch (v){
                                            case "meadow":
                                            case "forest":
                                            case "wood":
                                                type = Type.FOREST;
                                                break;
                                            case "farmland":
                                                type = Type.FARMFIELD;
                                                break;
                                        }
                                        break;
                                    case "highway":
                                        type = Type.HIGHWAY;
                                        break;
                                }
                                break;
                            case "nd":
                                long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                                if(wayHolder != null) {
                                    if(tempNodes.get(ref) != null) wayHolder.addNode(tempNodes.get(ref));
                                }
                                break;
                            case "member":
                                switch(reader.getAttributeValue(null, "type")){
                                    case "node":
                                        relationHolder.addNode(tempNodes.get(Long.parseLong(reader.getAttributeValue(null, "ref"))));
                                        break;
                                    case "way":
                                        long memberRef = Long.parseLong(reader.getAttributeValue(null, "ref"));
                                        if (tempWays.get(memberRef) != null)
                                            relationHolder.addWay(tempWays.get(memberRef));
                                        break;
                                    case "relation":
                                        relationHolder.addRefId(Long.parseLong(reader.getAttributeValue(null, "ref")));
                                        break;
                                }
                                break;
                        }
                        break;
                    case END_ELEMENT:
                        qName = reader.getLocalName();
                        switch (qName){
                            case "way":
                                if(type != Type.COASTLINE) {
                                    if(!enumMap.containsKey(type)) enumMap.put(type, new ArrayList<>());
                                    enumMap.get(type).add(new LinePath(wayHolder, type));
                                } else {
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
                                type = Type.UNKNOWN;
                                break;
                            case "relation":
                                if(type != Type.UNKNOWN && type != Type.COASTLINE){
                                    if(!enumMap.containsKey(type)) enumMap.put(type, new ArrayList<>());
                                    enumMap.get(type).add(new PolyLinePath(relationHolder, type));
                                }
                                type = Type.UNKNOWN;
                                break;
                            case "osm":
                                for (Map.Entry<Node, Way> entry : tempCoastlines.entrySet()) {
                                    if (entry.getKey() == entry.getValue().last()) {
                                        coastlines.add(new LinePath(entry.getValue(), Type.COASTLINE));
                                    }
                                }
                                break;
                        }
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public boolean addressInfoIsFull(String[] addressInfo){
        for(String info : addressInfo){
            if (info == null) return false;
        }
        return true;
    }
}
