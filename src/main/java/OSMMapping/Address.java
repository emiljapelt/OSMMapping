package OSMMapping;

public class Address {
    private Node location;
    private String streetname;
    private String house;
    private String postcode;
    private String city;

    public Address(Node node){
        this.location = node;
    }

    @Override
    public String toString(){
        return streetname + " " + house + ", " + postcode + " " + city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public void setHouse(String house) {
        this.house = house;
    }
}
