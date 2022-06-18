package github.racolin.busmap.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.entities.Address;
import github.racolin.busmap.entities.Station;

public class StationDAO {
    public static final int ID = 0;
    public static final int NAME = 1;
    public static final int ADDRESS = 2;
    public static final int LAT = 3;
    public static final int LNG = 4;

//    Hàm get sẽ là hàm lấy data chung, chỉ cần đặt condition vào và lấy data như mong muốn
    private static List<Station> get(Context context, String condition) {
        ArrayList<Station> list = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM station";
        if (condition != null) {
            sql += " WHERE " + condition;
        }
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            int id = cs.getInt(ID);
            String name = cs.getString(NAME);
            String address = cs.getString(ADDRESS);
            double lat = cs.getDouble(LAT);
            double lng = cs.getDouble(LNG);

            Station station = new Station(id, name, new Address(address, lat, lng));
            list.add(station);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }

//    Lấy một list các lat lng của stations
    public static List<LatLng> getLatLngOfStations(Context context) {

        ArrayList<LatLng> list = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM station";
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            double lat = cs.getDouble(LAT);
            double lng = cs.getDouble(LNG);

            LatLng latLng = new LatLng(lat, lng);
            list.add(latLng);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }

//    Lấy tất cả stations
    public static List<Station> getAllStations(Context context) {
        return get(context, null);
    }

//    Lấy tất cả các station xung quanh vị trí đã cho, với mức độ mở rộng là extend
    public static List<Station> getStationsAround(Context context, double lat, double lng, double extend) {
        String condition = String.format("lat BETWEEN %s AND %s AND lng BETWEEN %s AND %s",
                lat - extend, lat + extend, lng - extend, lng + extend);
        return get(context, condition);
    }

//    Lấy station bằng station id
    public static Station getStationById(Context context, int id) {
        List<Station> stations = get(context, "id=" + id);
        return stations.size() > 0 ? stations.get(0) : null;
    }

}
