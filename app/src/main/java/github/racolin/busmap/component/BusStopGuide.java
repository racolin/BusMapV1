package github.racolin.busmap.component;

import github.racolin.busmap.Support;
import github.racolin.busmap.config.MoveType;
import github.racolin.busmap.entities.Address;

//Bus stop guide là lớp để chứa thông tin hướng dẫn cách đi giữa các trạm
//route_id để biết nó thuộc route nào
//name cho biết tên của trạm
//movetype cho biết là đến trạm đó người dùng đi bộ hay đi xe buýt,
// chẳng hạn như từ điểm bắt đầu đến trạm đầu tiên thì người dùng đi bộ
//address là địa chỉ của busstop đó
public class BusStopGuide {
    private String route_id = "";
//    Tên của bus_stop với tên của address khác nhau
    private String name = "";
    private MoveType type;
    private Address address;

    public BusStopGuide(String route_id, String name, MoveType type, Address address) {
        this.route_id = route_id;
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getName() {
        return name;
    }

    public String getName(int max) {
        return Support.toStringEllipsis(name, max);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
