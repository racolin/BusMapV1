package github.racolin.busmap.component;

import java.io.Serializable;
import java.util.List;

import github.racolin.busmap.Support;
import github.racolin.busmap.config.MoveType;
import github.racolin.busmap.config.Speed;

// Result là kết quả tính được ban đầu khi tìm tuyến đường
// từ result thì ta tính được route guide và busstop guide
// Một result có thể phải đi qua nhiều trạm, nên result sẽ chứa một list các result route
public class Result implements Serializable {
    private List<ResultRoute> result_routes;
    private int walk_distance_start, walk_distance_end, bus_distance,
            price, time_pass;

    public Result(List<ResultRoute> result_routes,
                  int walk_distance_start, int walk_distance_end) {
        this.result_routes = result_routes;
        this.walk_distance_start = walk_distance_start;
        this.walk_distance_end = walk_distance_end;

//        timepass và price là giá trị ta phải tính
//        với timepass thì được tính như sau:
//        thời gian ngồi xe + thời gian đợi tuyến + thời gian đi bộ
//        với price thì ta chỉ cần tính tổng giá tiền của các tuyến lại với nhau
        time_pass = 0;
        price = 0;
        for (ResultRoute resultRoute : result_routes) {
            if (price != 0) {
                time_pass += resultRoute.getRoute().getRepeat_time();
            }
            price += resultRoute.getRoute().getPrice();
            bus_distance += resultRoute.getDistance();
        }
        time_pass += Support.calculateTimePass(walk_distance_start + walk_distance_end, Speed.WALK);
        time_pass += Support.calculateTimePass(bus_distance, Speed.BUS);
    }

    public String getTimePass() {
        return time_pass + " phút";
    }

    public String getMoney() {
        return Support.toCurrency(price);
    }

    public int getWalk_distance_start() {
        return walk_distance_start;
    }

    public void setWalk_distance_start(int walk_distance_start) {
        this.walk_distance_start = walk_distance_start;
    }

    public String getWalkStartTimePass() {
        return Support.distanceToTime(walk_distance_start, MoveType.WALK);
    }

    public int getWalk_distance_end() {
        return walk_distance_end;
    }

    public void setWalk_distance_end(int walk_distance_end) {
        this.walk_distance_end = walk_distance_end;
    }

    public String getWalkEndTimePass() {
        return Support.distanceToTime(walk_distance_end, MoveType.WALK);
    }

    public String getWalkDistanceStart() {
        return Support.toMeterString(walk_distance_start);
    }

    public String getWalkDistanceEnd() {
        return Support.toMeterString(walk_distance_end);
    }

    public String getWalkDistance() {
        return Support.toMeterString(walk_distance_start + walk_distance_end);
    }

    public int getBus_distance() {
        return bus_distance;
    }

    public void setBus_distance(int bus_distance) {
        this.bus_distance = bus_distance;
    }

    public String getBusDistance() {
        return Support.toKilometerString(bus_distance);
    }

    public String getBusTimePass() {
        return Support.distanceToTime(bus_distance, MoveType.BUS);
    }

    public List<ResultRoute> getResult_routes() {
        return result_routes;
    }

    public void setResult_routes(List<ResultRoute> result_routes) {
        this.result_routes = result_routes;
    }
}
