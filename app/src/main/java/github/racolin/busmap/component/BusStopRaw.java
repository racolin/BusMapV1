package github.racolin.busmap.component;

import java.util.Objects;

//Busstop raw chỉ load data từ sqlite lên, không thực hiện chuyển đổi
// station_id hay route_id thành object
public class BusStopRaw {
    private String route_id;
    private int station_id;
    private int order;

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public int getStation_id() {
        return station_id;
    }

    public void setStation_id(int station_id) {
        this.station_id = station_id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public BusStopRaw(String route_id, int station_id, int order) {
        this.route_id = route_id;
        this.station_id = station_id;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusStopRaw that = (BusStopRaw) o;
        return station_id == that.station_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(route_id, station_id, order);
    }
}
