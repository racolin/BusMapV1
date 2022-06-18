package github.racolin.busmap.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import github.racolin.busmap.MainActivity;
import github.racolin.busmap.R;
import github.racolin.busmap.entities.UserAccount;

public class LoginActivity extends AppCompatActivity {
    EditText edt_email, edt_pass;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        initListener();
    }

//    hàm ánh xạ các view từ layout
    private void initUI() {
        edt_email = findViewById(R.id.edt_email);
        edt_pass = findViewById(R.id.edt_pass);
        btn_login = findViewById(R.id.btn_login);
    }

//    Hàm bắt sự kiện
    private void initListener() {
//        Khi click vào button login thì sẽ lấy email và password để kiểm tra
//        Nếu email hoặc password rỗng thì toast lên là người dùng chưa điền đủ thông tín
//        Nếu đầy đủ rồi thì sẽ check xem tài toàn mật khẩu có đúng hay không
//        Nếu đúng thì đăng nhập thành công và chuyển về trang chính
//        Nếu sai thì toast lên và vẫn ở trang login
        btn_login.setOnClickListener(v -> {
            String email = edt_email.getText().toString();
            String pass = edt_pass.getText().toString();
            if (email.equals("") || pass.equals("")) {
                Toast.makeText(this, R.string.fill, Toast.LENGTH_SHORT).show();
            } else {
                if (UserAccount.login(this, email, pass) != null) {
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}