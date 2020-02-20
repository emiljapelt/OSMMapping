package OSMMapping;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OSMModel {

    private ArrayList<Drawable> coastlines;
    private Map<Type, List<Drawable>> enumMap;
    private Bound mapBound;

    private ArrayList<Runnable> observers = new ArrayList<>();

    public OSMModel(InputStream is){
        long time = -System.nanoTime();
        OSMReader reader = new OSMReader(is);
        time += System.nanoTime();
        System.out.println("Read time: " + ((time)/1000000) + "ms");

        coastlines = reader.getCoastlines();
        mapBound = reader.getTempBound();
        enumMap = reader.getEnumMap();
    }

    public ArrayList<Drawable> getCoastlines(){return coastlines;}
    public Bound getMapBound(){return mapBound;}

    public List<Drawable> getDrawablesOfType(Type type) {
        if (enumMap.containsKey(type)) return enumMap.get(type);
        return new ArrayList<>();
    }

    public void addObserver(Runnable observer){
        observers.add(observer);
    }

    public void notifyObservers(){
        for(Runnable observer : observers){
            observer.run();
        }
    }

}

