package github.racolin.busmap.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.Support;
import github.racolin.busmap.component.BusStopRaw;
import github.racolin.busmap.entities.Address;
import github.racolin.busmap.entities.BusStop;
import github.racolin.busmap.entities.Station;

public class BusStopDAO {
    public static final int ROUTE_ID = 0;
    public static final int STATION_ID = 1;
    public static final int ORDER = 2;

//    Hàm getRaw sẽ là hàm lấy data chung, chỉ cần đặt condition vào và lấy data như mong muốn
//    hàm chỉ lấy raw nghĩa là các giá trị như trong cơ sở dữ liệu, không load các kháo ngoại
    private static List<BusStopRaw> getRaw(Context context, String condition) {
        ArrayList<BusStopRaw> list = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM busstop";
        if (condition != null) {
            sql += " WHERE " + condition;
        }
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            String route_id = cs.getString(ROUTE_ID);
            int station_id = cs.getInt(STATION_ID);
            int order = cs.getInt(ORDER);
            BusStopRaw bus_stop_raw = new BusStopRaw(route_id, station_id, order);
            list.add(bus_stop_raw);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }
//    Hàm get sẽ là hàm lấy data chung, chỉ cần đặt condition vào và lấy data như mong muốn
//    Khác với getRaw, get sẽ lòa cả khóa ngoại
    private static List<BusStop> get(Context context, String condition) {
        List<BusStopRaw> bus_stop_raws = getRaw(context, condition);
        List<BusStop> bus_stops = new ArrayList<>();
        Address pre_address = null;
        for (BusStopRaw busStopRaw : bus_stop_raws) {
            Station new_station = StationDAO.getStationById(context, busStopRaw.getStation_id());
            BusStop bus_stop = new BusStop(busStopRaw.getRoute_id(),
                    new_station, busStopRaw.getOrder(),
                    Support.calculateDistance(new_station.getAddress(), pre_address));

            pre_address = new_station.getAddress();
            bus_stops.add(bus_stop);
        }
        return bus_stops;
    }

//    Lấy bus stop từ route id và station id của chúng
    public static BusStop getBusStopFromStationIdAndRouteId(Context context, int station_id, String route_id) {
        List<BusStop> busStops = get(context, "route_id='" + route_id + "' AND station_id=" + station_id);
        return busStops.size() > 0 ? busStops.get(0) : null;
    }

//    Lấy bus stop từ route id và thứ tự của chúng
    public static BusStop getBusStopFromRouteIdAndOrder(Context context, String route_id, int order) {
        List<BusStop> busStops = get(context, "route_id='" + route_id + "' AND `order`=" + order);
        return busStops.size() > 0 ? busStops.get(0) : null;
    }

//    hàm lấy các bus stops từ routeid và order start và order end
//    Những bus stops trên tuyến đường từ chặng start đến chặng end sẽ được lấy ra
    public static List<BusStop> getBusStopsFromRouteIdAndOrder(Context context, String route_id, int order_start, int order_end) {
        return get(context, "route_id=" + route_id + " AND `order` BETWEEN " + order_start + " AND " + order_end +  " ORDER BY `order`");
    }

//    Hàm chuyển đổi bus stop raw thành bus stop
//    Load các khóa ngoại
    public static BusStop convertRawToBusStop(Context context, BusStopRaw busStopRaw) {
        return new BusStop(busStopRaw.getRoute_id(),
                StationDAO.getStationById(context, busStopRaw.getStation_id()), busStopRaw.getOrder(),
                -1);
    }

    //    Hàm lấy các bus stops của station id
    public static List<BusStop> getBusStopFromStationId(Context context, int station_id) {
        return get(context, "station_id=" + station_id);
    }

//    Hàm lấy các bus stops raw của station id
    public static List<BusStopRaw> getBusStopRawFromStationId(Context context, int station_id) {
        return getRaw(context, "station_id=" + station_id);
    }

//    Hàm lấy các bus stops của route id
    public static List<BusStop> getBusStopsByRouteId(Context context, String route_id) {
        List<BusStop> busStops = get(context, "route_id='" + route_id + "' ORDER BY `order`");
        return busStops;
    }

//    Hàm lấy tất cả các busstopraw
    public static List<BusStopRaw> getAllBusStopRaw(Context context) {
        return getRaw(context, null);
    }

}
