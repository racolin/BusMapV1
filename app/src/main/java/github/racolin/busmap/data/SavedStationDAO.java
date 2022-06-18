package github.racolin.busmap.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.entities.Station;

public class SavedStationDAO {
    public static final int STATION_ID = 0;
    public static final int USER_EMAIL = 1;

//    Hàm lấy saved sation bằng email
    public static List<Station> getSavedStationsByUserId(Context context , String user_gmail){

        ArrayList<Station> list = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM savedstation WHERE user_gmail='" + user_gmail + "'";
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            int station_id = cs.getInt(STATION_ID);

            Station station = StationDAO.getStationById(context, station_id);
            list.add(station);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }

//    Hàm chèn saved station bằng email và station id
    public static void insertSavedStation(Context context, String user_gmail, int station_id) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_gmail", user_gmail);
        contentValues.put("station_id", station_id);
        db.insert("savedstation", null, contentValues);
        db.close();
    }

    //    Hàm xóa saved station bằng email và station id
    public static void deleteSavedStation(Context context, String user_gmail, int station_id) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM savedstation WHERE user_gmail='" + user_gmail + "' AND station_id='" + station_id + "'");
        db.close();
    }
}
