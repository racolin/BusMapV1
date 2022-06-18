package github.racolin.busmap.component;

//BusDistance gồm busStopraw và khoảng cách
//lớp này được dùng trong thuật toán để lưu thông tin của busstop và khoảng cách từ một vị trí đến nó
public class BusDistance {
    private BusStopRaw bus_stop_raw;
    private int distance;

    public BusStopRaw getBus_stop_raw() {
        return bus_stop_raw;
    }

    public void setBus_stop_raw(BusStopRaw bus_stop_raw) {
        this.bus_stop_raw = bus_stop_raw;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public BusDistance(BusStopRaw bus_stop_raw, int distance) {
        this.bus_stop_raw = bus_stop_raw;
        this.distance = distance;
    }
}
