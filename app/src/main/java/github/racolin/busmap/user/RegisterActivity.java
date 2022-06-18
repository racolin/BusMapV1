package github.racolin.busmap.user;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import github.racolin.busmap.MainActivity;
import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.data.UserDAO;
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;

public class RegisterActivity extends AppCompatActivity {
    //  Các view dùng để ánh xạ
    ImageView iv_avatar;
    EditText edt_name, edt_email, edt_phone, edt_pass;
    Button btn_register;
    ImageButton ib_choose_image;
    Spinner spinner_gender;
    TextView tv_dob;

    //    image sẽ được sử dụng khi người dùng pick ảnh thì máy
    byte[] image = null;
    //    Đây là launcher để lấy ảnh thì máy lên
    public static final int PICK_IMAGE = 10000;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        InputStream inputStream;
                        try {
                            inputStream = getContentResolver().openInputStream(result.getData().getData());
                            btn_register.setEnabled(false);
                            new Thread(() -> {
                                Bitmap bm = BitmapFactory.decodeStream(inputStream);
                                int h = bm.getHeight();
                                int w = bm.getWidth();
                                int p = h > w ? w : h;
                                h = (h - p) / 2;
                                w = (w - p) / 2;
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                Bitmap bitmap = Bitmap.createBitmap(bm, w, h, p, p);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                image = stream.toByteArray();
                                runOnUiThread(() -> {
                                    btn_register.setEnabled(true);
                                    iv_avatar.setImageBitmap(bitmap);
                                });
                            }).start();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Ánh xạ view
        initUI();
//        Xử lý sự kiện
        initListener();
    }

    //    Ánh xạ các view từ layout
    private void initUI() {
        ib_choose_image = findViewById(R.id.ib_choose_image);
        iv_avatar = findViewById(R.id.iv_avatar);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_pass = findViewById(R.id.edt_pass);
        btn_register = findViewById(R.id.btn_register);
        spinner_gender = findViewById(R.id.spinner_gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.gender_spinner, R.id.tv_gender, getGenders());
        spinner_gender.setAdapter(adapter);
        tv_dob = findViewById(R.id.tv_dob);
    }

    //    Xử lý sự kiện
    private void initListener() {
//        Tạo một ngày mặc định là 11/05/2001
        Calendar dc = Calendar.getInstance();
        dc.set(Calendar.YEAR, 2001);
        dc.set(Calendar.MONTH, 4);
        dc.set(Calendar.DATE, 11);
//        Tạo date picker dialog với ngày mặc định
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DATE, i2);
                tv_dob.setText(Support.dateToString(calendar.getTime(), "dd/MM/yyyy"));
            }
        }, dc.get(Calendar.YEAR), dc.get(Calendar.MONTH), dc.get(Calendar.DATE));
//        Khi người dùng click vào textview dob thì sẽ show lên cho người dùng chọn
//        Sau khi chọn xong thì update ngày được chọn lên textview này
        tv_dob.setOnClickListener(v -> {
            dialog.show();
        });
//        Khi người dùng bấm vào button register thì sẽ gọi hàm register
        btn_register.setOnClickListener(v -> {
            register();
        });
//        Khi người dùng chọn upload ảnh thì sẽ check permission
//        Nếu đã được granted quyền read external storage thì sẽ gpoj hàm getImage
//        Nếu chưa thì request permission
        ib_choose_image.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
                getImage();
            } else {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
            }
        });
    }

    //    Nếu xin cấp quyền thành công thì thực hiện gọi hàm getImage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImage();
        }
    }

    //    Hàm get Image có chức năng tạo một intent với action pick và type là image
    private void getImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        launcher.launch(pickIntent);
    }

//    Hàm register có chức đăng đăng ký tài khoản mà người dùng vừa nhập thông tin xong
    private void register() {
        String email = edt_email.getText().toString();
//        Kiểm tra nếu email không đúng format thì toast lên không hợp lệ
        if (!Support.checkInvalidEmail(email)) {
            Toast.makeText(this, R.string.uninvalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        String pass = edt_pass.getText().toString();
//        Kiểm tra nếu password không đúng format thì toast lên không hợp lệ
        if (!Support.checkInvalidPassword(pass)) {
            Toast.makeText(this, R.string.uninvalid_password, Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = edt_phone.getText().toString();
        String name = edt_name.getText().toString();
        String dob_str = tv_dob.getText().toString();
        Date dob = null;
//        Kiểm tra nếu phone, name, password mà trống thì toast lên
        if (phone.equals("") || name.equals("") || pass.equals("") || email.equals("") || dob_str.equals("")) {
            Toast.makeText(this, R.string.fill, Toast.LENGTH_SHORT).show();
            return;
        }

        dob = Support.stringToDate(dob_str, "dd/MM/yyyy");
        boolean gender = spinner_gender.getSelectedItemPosition() == 0;
//      Kiểm tra xem email đó đã được đăng ký hay chưa
//        Nếu chưa thì tạo user còn không thì báo user đã tồn tại
        boolean exists = UserDAO.checkExist(this, email);
        if (exists) {
            Toast.makeText(this, R.string.account_exists, Toast.LENGTH_SHORT).show();
        } else {
            UserAccount.register(this, new User(email, pass, name, phone, gender, dob, image));
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    //    Hàm trả về một list là các genders
    private List<String> getGenders() {
        List<String> genders = new ArrayList<>();
        genders.add("Nam");
        genders.add("Nữ");
        return genders;
    }
}
