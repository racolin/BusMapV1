package github.racolin.busmap.entities;

import java.io.Serializable;

// busstop chứa các thông tin về route, station order
// và đặc biệt là distance_previous cho biết khoảng cách giữa nó và trạm kế nó là bao nhiêu mét
public class BusStop implements Serializable {
    private String route_id;
    private Station station;
    private int order;
    private int distance_previous;

    public BusStop(String route_id, Station station, int order, int distance_previous) {
        this.route_id = route_id;
        this.station = station;
        this.order = order;
        this.distance_previous = distance_previous;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getDistance_previous() {
        return distance_previous;
    }

    public void setDistance_previous(int distance_previous) {
        this.distance_previous = distance_previous;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "route_id='" + route_id + '\'' +
                ", station=" + station +
                ", order=" + order +
                '}';
    }
}
