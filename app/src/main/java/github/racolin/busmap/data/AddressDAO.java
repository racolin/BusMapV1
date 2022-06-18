package github.racolin.busmap.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import github.racolin.busmap.entities.Address;

public class AddressDAO {
    public static final int ID = 0;
    public static final int USER_EMAIL = 1;
    public static final int NAME = 2;
    public static final int LAT = 3;
    public static final int LNG = 4;

//    Hàm get sẽ là hàm lấy data chung, chỉ cần đặt condition vào và lấy data như mong muốn
    private static List<Address> get(Context context, String condition) {
        ArrayList<Address> list = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM address";
        if (condition != null) {
            sql += " WHERE " + condition;
        }
        sql += " ORDER BY id DESC";
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            String name = cs.getString(NAME);
            double lat = cs.getDouble(LAT);
            double lng = cs.getDouble(LNG);
            Address address = new Address(name, lat, lng);
            list.add(address);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }

//    Hàm lấy một list các addresses bằng email
    public static List<Address> getAddressesByUserEmail(Context context, String email) {
        List<Address> addresses = get(context, "user_email='" + email + "'");
        return addresses;
    }

//    Hàm chèn address vào sqlite
    public static void insert(Context context, Address address, String user_email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_email", user_email);
        contentValues.put("name", address.getAddress());
        contentValues.put("lat", address.getLat());
        contentValues.put("lng", address.getLng());
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.insert("address", null, contentValues);
        db.close();
        helper.close();
    }
}
