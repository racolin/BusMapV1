package github.racolin.busmap.component;

import github.racolin.busmap.Support;
import github.racolin.busmap.config.MoveType;

// route guide là hướng dẫn cách di chuyển trên các tuyến xe, tổng quát hơn busstop guide
public class RouteGuide {
    private String title = "";
    private String description = "";
    private MoveType type;
    private int distance;
    private String price;

    public RouteGuide(String title, String description, MoveType type, int distance, String price) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.distance = distance;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public String getTimePass() {
        return Support.distanceToTime(distance, type);
    }

    public int getDistance() {
        return distance;
    }

    public String getDistanceMeter() {
        return Support.toMeterString(distance);
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
