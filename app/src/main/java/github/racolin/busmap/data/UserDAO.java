package github.racolin.busmap.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import github.racolin.busmap.Support;
import github.racolin.busmap.entities.User;

public class UserDAO {
    public static final int EMAIL = 0;
    public static final int PASSWORD = 1;
    public static final int NAME = 2;
    public static final int PHONE = 3;
    public static final int GENDER = 4;
    public static final int DATE_OF_BIRTH = 5;
    public static final int IMAGE = 6;

//    Hàm get sẽ là hàm lấy data chung, chỉ cần đặt condition vào và lấy data như mong muốn
    private static ArrayList<User> get(Context context, String condition) {
        ArrayList<User> list = new ArrayList<>();

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM user";
        if (condition != null) {
            sql += " WHERE " + condition;
        }
        Cursor cs = db.rawQuery(sql, null);
        cs.moveToFirst();
        while (!cs.isAfterLast())
        {
            String email = cs.getString(EMAIL);
            String name = cs.getString(NAME);
            String phone = cs.getString(PHONE);
            boolean gender = cs.getInt(GENDER) == 0;
            Date day_of_birth = Support.stringToDate(cs.getString(DATE_OF_BIRTH), "dd/MM/yyyy");
            byte[] image  = cs.getBlob(IMAGE);

            User user = new User(email, null, name, phone, gender, day_of_birth, image);
            list.add(user);
            cs.moveToNext();
        }
        cs.close();
        db.close();
        return list;
    }
//    Hàm check xem email có tồn tại hay chưa bằng cách lấy danh sách các user có email đó
//    Sau đó kiểm tra list nếu > 0 thì là đã tồn tại. Nếu = 0 thì không tồn tại
    public static boolean checkExist(Context context, String email) {
        List<User> user = get(context, "email='" + email + "'");
        return user.size() > 0 ? true : false;
    }

//    Hàm getUser lấy user với email và password, email password không khớp thì sẽ trả về null
    public static User getUser(Context context, String email, String password) {
        List<User> user = get(context, "email='" + email + "' AND password='" + password + "'");
        return user.size() > 0 ? user.get(0) : null;
    }

//    Tạo một user từ user object
//    Đầu tiên kiểm tra xem email đã tồn tại chưa, nếu chưa thì tạo mới và return true
//    Nếu đã tạo rồi thì trả về false
    public static boolean createUser(Context context, User user) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean result = false;
        if (!checkExist(context, user.getEmail())) {
            db.insert("user", null, user.getContentValuesForInsert());
            result = true;
        }
        db.close();
        helper.close();
        return result;
    }

//    Thay đổi mật khẩu vơi
    public static boolean changePassword(Context context, String email, String old_password, String new_password) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean result = false;
        if (null != getUser(context, email, old_password)) {
            db.execSQL("UPDATE user SET password='" + new_password + "' " +
                    "WHERE email='" + email + "' AND password='" + old_password + "'");
            result = true;
        }
        db.close();
        helper.close();
        return result;
    }

//    Cập nhật thông tin của user thông qua một user object
    public static boolean updateUser(Context context, User user) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean result = false;
        if (!checkExist(context, user.getEmail())) {
            db.update("user",  user.getContentValuesForUpdate(), "email=?", new String[] {user.getEmail()});
            result = true;
        }
        db.close();
        helper.close();
        return result;
    }

}
