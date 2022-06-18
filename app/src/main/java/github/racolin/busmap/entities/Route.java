package github.racolin.busmap.entities;

import java.io.Serializable;

import github.racolin.busmap.Support;

//Route được load từ sqlite lên, với những id station thì sẽ được chuyển đổi thành object
public class Route implements Serializable {
    private String id;
    private Station start_station;
    private Station end_station;
    private int price;
    private String type;
    private String operation_time;
    private String cycle_time;
    private String unit;
    private int repeat_time;
    private int per_day;
    private int distance;

    public Route(String id, Station start_station, Station end_station, int price, String type,
                 String operation_time, String cycle_time, String unit, int repeat_time, int per_day, int distance) {
        this.id = id;
        this.start_station = start_station;
        this.end_station = end_station;
        this.price = price;
        this.type = type;
        this.operation_time = operation_time;
        this.cycle_time = cycle_time;
        this.unit = unit;
        this.repeat_time = repeat_time;
        this.per_day = per_day;
        this.distance = distance;
    }

    public String getName(int max) {
        String name_1 = start_station.getName();
        String name_2 = end_station.getName();
        String name = name_1 + " - " + name_2;
        if (name.length() > max) {
            name = Support.toStringEllipsis(name_1, (max - 3) / 2)
            + " - " + Support.toStringEllipsis(name_2, max - (max - 3) / 2);
        }
        return name;
    }

    public String getName() {
        return start_station.getName() + " - " + end_station.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Station getStart_station() {
        return start_station;
    }

    public void setStart_station(Station start_station) {
        this.start_station = start_station;
    }

    public Station getEnd_station() {
        return end_station;
    }

    public void setEnd_station(Station end_station) {
        this.end_station = end_station;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation_time() {
        return operation_time;
    }

    public void setOperation_time(String operation_time) {
        this.operation_time = operation_time;
    }

    public String getCycle_time() {
        return cycle_time + " phút";
    }

    public void setCycle_time(String cycle_time) {
        this.cycle_time = cycle_time;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getRepeat_time() {
        return repeat_time;
    }

    public String getRepeatTime() {
        return repeat_time + " phút";
    }

    public void setRepeat_time(int repeat_time) {
        this.repeat_time = repeat_time;
    }

    public int getPer_day() {
        return per_day;
    }

    public String getPerDay() {
        return per_day + " chuyến/ngày";
    }

    public void setPer_day(int per_day) {
        this.per_day = per_day;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getMoney() {
        return Support.toCurrency(price);
    }

    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", start_station=" + start_station +
                ", end_station=" + end_station +
                ", price=" + price +
                ", type='" + type + '\'' +
                ", operation_time='" + operation_time + '\'' +
                ", cycle_time='" + cycle_time + '\'' +
                ", unit='" + unit + '\'' +
                ", repeat_time=" + repeat_time +
                ", per_day=" + per_day +
                ", distance=" + distance +
                '}';
    }
}
