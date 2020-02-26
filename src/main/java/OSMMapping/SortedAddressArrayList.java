package OSMMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class SortedAddressArrayList implements Iterable<Address> {
    private ArrayList<Address> list;
    private boolean isSorted;

    public SortedAddressArrayList(){
        this.list = new ArrayList<Address>();
        isSorted = false;
    }

    public void add(Address address){
        list.add(address);
    }

    public int getAddressCount(){return list.size();}

    public Address getAddressByIndex(int index){
        if (!isSorted){
            Collections.sort(list);
            isSorted = true;
        }
        return list.get(index);
    }

    public Address getAddressByName(String name){
        if (!isSorted){
            Collections.sort(list);
            isSorted = true;
        }
        return BinarySearch(name);
    }

    private Address BinarySearch(String name){
        int low = 0;
        int high = list.size() - 1;
        while(low <= high){
            int mid = (low + high) / 2;
            Address midElement = list.get(mid);
            if(midElement.compareToString(name) < 0){
                low = mid + 1;
            } else if (midElement.compareToString(name) > 0) {
                high = mid - 1;
            } else {
                return midElement;
            }
        }
        return null;
    }

    public int getSuggestions(String name){
        if (!isSorted){
            Collections.sort(list);
            isSorted = true;
        }
        return BinarySuggestor(name);
    }

    private int BinarySuggestor(String name){
        int low = 0;
        int high = list.size() - 1;
        int mid = (low + high) / 2;
        while(low <= high){
            mid = (low + high) / 2;
            Address midElement = list.get(mid);
            if(midElement.compareToString(name) < 0){
                low = mid + 1;
            } else if (midElement.compareToString(name) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return mid;
    }

    @Override
    public Iterator<Address> iterator() {
        return list.iterator();
    }
}
