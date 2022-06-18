package github.racolin.busmap.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import github.racolin.busmap.Support;
import github.racolin.busmap.entities.Update;

public class UpdateDAO {
    public static final int ID = 0;
    public static final int DATE = 1;
    public static final int UPDATED = 2;

//    Hàm get sẽ là hàm lấy data chung, chỉ cần đặt condition vào và lấy data như mong muốn
    private static List<Update> get(Context context, String condition) {
        ArrayList<Update> list = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM latest_update";
        if (condition != null) {
            sql += " WHERE " + condition;
        }
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            int id = cs.getInt(ID);
            Date name = Support.stringToDate(cs.getString(DATE), "dd/MM/yyyy");
            boolean updated = cs.getInt(UPDATED) == 1;

            Update update = new Update(id, name, updated);
            list.add(update);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }

//    Hàm sẽ lấy hàng update ở vị trí cuối cùng
    public static Update getLastUpdate(Context context) {

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Update update = null;
        String sql = "SELECT * FROM latest_update";
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            int id = cs.getInt(ID);
            Date name = Support.stringToDate(cs.getString(DATE), "dd/MM/yyyy");
            boolean updated = cs.getInt(UPDATED) == 1;

            update = new Update(id, name, updated);
            break;
        }
        cs.close();
        db.close();
        return update;
    }

//    Khi đã update xong thì hàm setUpdated được gọi để chuyển giá trụ updated thành true
    public static void setUpDatedById(Context context, int id) {

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "UPDATE latest_update SET updated = 1 WHERE id=" + id;
        db.execSQL(sql);
        db.close();
        helper.close();
    }
}
