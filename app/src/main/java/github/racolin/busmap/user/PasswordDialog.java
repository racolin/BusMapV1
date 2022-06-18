package github.racolin.busmap.user;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.data.UserDAO;
import github.racolin.busmap.entities.UserAccount;

//Đây là dialog dùng cho việc đổi mật khẩu
//dialog này sẽ xuất hiện hộp thoại yêu cần người dùng nhập password cũ và mới
//Nếu các password hợp lệ thì sẽ đổi mật khẩu
public class PasswordDialog extends Dialog {
    EditText edt_old, edt_new, edt_re_new;
    TextView tv_ok, tv_cancel;
    public PasswordDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_dialog);

        initUI();
        initListener();
    }

//    Ánh xạ các view từ layout
    private void initUI() {
        edt_old = findViewById(R.id.edt_old);
        edt_new = findViewById(R.id.edt_new);
        edt_re_new = findViewById(R.id.edt_re_new);
        tv_ok = findViewById(R.id.tv_ok);
        tv_cancel = findViewById(R.id.tv_cancel);
    }

//    Bắt các sự kiện
    private void initListener() {
//        khi người dùng bấm vào cancel thì dismiss dialog
        tv_cancel.setOnClickListener(v -> {
            dismiss();
        });
//        Khi người dùng bấm vào ok thì sẽ check các mật khẩu
//        và thông báo cho người dùng biết
        tv_ok.setOnClickListener(v -> {
            String old_password = edt_old.getText().toString();
            String re_new_password = edt_re_new.getText().toString();
            String new_password = edt_new.getText().toString();
            if (old_password.equals(new_password)) {
                Toast.makeText(getContext(), R.string.duplicate_password, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Support.checkInvalidPassword(new_password)) {
                Toast.makeText(getContext(), R.string.uninvalid_password, Toast.LENGTH_SHORT).show();
                return;
            }
            if (re_new_password.equals(new_password)) {
                if (UserDAO.changePassword(getContext(), UserAccount.getUser().getEmail(),
                        old_password, new_password)) {
                    Toast.makeText(getContext(), R.string.change_password_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.change_password_fail, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.not_match_password, Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
    }
}
