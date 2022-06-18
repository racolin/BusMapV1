package github.racolin.busmap.entities;

import java.io.Serializable;
import java.util.Objects;

import github.racolin.busmap.Support;

//Address chỉ gồm tên địa chỉ, và tọa độ kinh độ vĩ độ của nó
public class Address implements Serializable {
    private String address;
    private double lat, lng;

    public Address() {

    }

    public Address(String address, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress(int max) {
        return Support.toStringEllipsis(address, max);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return lat == lat && lng == lng && address.equals(address1.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, lat, lng);
    }

    @Override
    public String toString() {
        return "Address{" +
                "address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
