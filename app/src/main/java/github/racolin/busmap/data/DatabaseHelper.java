package github.racolin.busmap.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "busmap.sqlite";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

//    Tạo các bảng cần thiết cho ứng dụng
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(
        "CREATE TABLE IF NOT EXISTS user ( " +
            "email TEXT PRIMARY KEY, " +
            "password TEXT, " +
            "name TEXT, " +
            "phone TEXT, " +
            "gender INTEGER, " +
            "date_of_birth TEXT, " +
            "image BLOB" +
            ");");

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS latest_update ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date TEXT, " +
            "updated INTEGER"+
            ");");

//        kiểm tra xem latest_version đã được  cập nhật hay chưa
        boolean latest_version = false;
        String sql = "SELECT * FROM latest_update";
        Cursor cs = sqLiteDatabase.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            latest_version = true;
            break;
        }
        cs.close();
//        Nếu chưa được cập nhật thì add vào một version
        if (!latest_version) {
            sqLiteDatabase.execSQL("INSERT INTO latest_update VALUES(1, '17/06/2022', 0)");
        }

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS address ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_email TEXT , " +
            "name TEXT, " +
            "lat REAL, " +
            "lng REAL, " +
            "FOREIGN KEY (user_email) REFERENCES user(email)" +
            ");");

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS station (" +
            "id INTEGER," +
            "name TEXT," +
            "address TEXT," +
            "lat REAL," +
            "lng REAL," +
            "PRIMARY KEY(id)" +
            ");");

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS route (" +
            "id TEXT," +
            "start_station_id INTEGER," +
            "end_station_id INTEGER," +
            "price INTEGER," +
            "type TEXT," +
            "operation_time TEXT," +
            "cycle_time TEXT," +
            "unit TEXT," +
            "repeat_time INTEGER," +
            "per_day INTEGER," +
            "distance INTEGER," +
            "PRIMARY KEY(id), " +
            "FOREIGN KEY (start_station_id) REFERENCES station(id), " +
            "FOREIGN KEY (end_station_id) REFERENCES station(id)" +
            ");");

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS savedroute ( " +
            "route_id TEXT, " +
            "user_gmail TEXT, " +
            "PRIMARY KEY(route_id, user_gmail), " +
            "FOREIGN KEY (route_id) REFERENCES route(id), " +
            "FOREIGN KEY (user_gmail) REFERENCES user(email)"  +
            ");");

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS savedstation ( " +
            "station_id INTEGER, " +
            "user_gmail TEXT, " +
            "PRIMARY KEY(station_id,user_gmail), " +
            "FOREIGN KEY (station_id) REFERENCES station(id), " +
            "FOREIGN KEY (user_gmail) REFERENCES user(email)"  +
            ");");

        sqLiteDatabase.execSQL(
            "CREATE TABLE IF NOT EXISTS busstop (" +
            "route_id TEXT," +
            "station_id INTEGER," +
            "`order` INTEGER," +
            "PRIMARY KEY(route_id, station_id)," +
            "FOREIGN KEY (route_id) REFERENCES route(id), " +
            "FOREIGN KEY (station_id) REFERENCES station(id)"  +
            ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
