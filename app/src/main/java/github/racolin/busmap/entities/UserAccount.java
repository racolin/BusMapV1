package github.racolin.busmap.entities;

import android.content.Context;

import github.racolin.busmap.data.UserDAO;

//Lớp user account được áp dụng mô hình singleton kết hợp với synchronized để
//chỉ có một instance của user dù có nhiều luồng gọi đến nó
public class UserAccount {
    private static User user;

    private UserAccount(User user) {
        this.user = user;
    }

//    Phương thức login sẽ trả về user hiện tại nếu khác rỗng,
//    Nếu user rỗng thì sẽ sử dụng email password để lấy user về
    public static synchronized User login(Context context, String email, String password) {
        if (user == null) {
            if (UserDAO.checkExist(context, email)) {
                user = UserDAO.getUser(context, email, password);
                return user;
            }
            return null;
        }
        return user;
    }

//    đăng ký tài khoản bằng cách kiểm tra xem tài khoản đã tồn tại hay chưa
//     nếu đã tồn tại thì gán user = null
//    nếu chưa thì tạo và lấy gán vào user
    public static void register(Context context, User _user) {
        if (UserDAO.createUser(context, _user)) {
            user = UserDAO.getUser(context, _user.getEmail(), _user.getPassword());
        } else {
            user = null;
        }
    }

//    Đăng xuất thì chỉ việc xét user = null
    public static void logout() {
        user = null;
    }

//    get user trả về user
    public static User getUser() {
        return user;
    }

//    update user
    public static void update(Context context) {
        UserDAO.updateUser(context, user);
    }
}
