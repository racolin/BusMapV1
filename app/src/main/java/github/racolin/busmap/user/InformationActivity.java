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
import github.racolin.busmap.entities.User;
import github.racolin.busmap.entities.UserAccount;

public class InformationActivity extends AppCompatActivity {
//  Các view dùng để ánh xạ
    EditText edt_phone, edt_name, edt_email;
    Button btn_update_info, btn_change_password;
    Spinner spinner_gender;
    TextView tv_dob, tv_name;
    ImageView iv_avatar;
    ImageButton ib_choose_image;
//    image sẽ được sử dụng khi người dùng pick ảnh thì máy
    byte[] image = null;
//    Biến user dùng để lấy user từ UserAccount
    User user;
//    Đây là launcher để lấy ảnh thì máy lên
    public static final int PICK_IMAGE = 10000;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
//                    Khi mà result trả về là OK thì ta sẽ đọc uri trả về và chuyển sang bitmap -> byte array
//                    và setImageUri lên iv_avatar
                    if (result.getResultCode() == RESULT_OK) {
                        InputStream inputStream;
                        try {
                            inputStream = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap bm = BitmapFactory.decodeStream(inputStream);
                            int h = bm.getHeight();
                            int w = bm.getWidth();
                            int p = h > w ? w : h;
                            h = (h - p) / 2;
                            w = (w - p) / 2;
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            Bitmap bitmap = Bitmap.createBitmap(bm, w, h, p, p);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            iv_avatar.setImageBitmap(bitmap);
                            image = stream.toByteArray();
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
        setContentView(R.layout.activity_infomation);
        user = UserAccount.getUser();
//        Ánh xạ view
        initUI();
//        Đặt các thông tin của người dùng lên form thông tin người dùng để người dùng kiểm tra
//        Người dùng có thể thay đổi
        setInformationToForm();
//        Xử lý sự kiện
        initListener();
    }

//    Ánh xạ các view từ layout
    private void initUI() {
        btn_change_password = findViewById(R.id.btn_change_password);

        ib_choose_image = findViewById(R.id.ib_choose_image);

        iv_avatar = findViewById(R.id.iv_avatar);

        tv_name = findViewById(R.id.tv_name);

        edt_phone = findViewById(R.id.edt_phone);

        edt_name = findViewById(R.id.edt_name);

        spinner_gender = findViewById(R.id.spinner_gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.gender_spinner, R.id.tv_gender, getGenders());
        spinner_gender.setAdapter(adapter);

        tv_dob = findViewById(R.id.tv_dob);

        edt_email = findViewById(R.id.edt_email);

        btn_update_info = findViewById(R.id.btn_update_info);

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
//        Khi người dùng bấm vào button update thì sẽ gọi hàm update information
        btn_update_info.setOnClickListener(v -> {
            updateInformation();
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
//        Khi người dùng bấm vào đổi mật khẩu thì show password dialog lên cho người dùng
        btn_change_password.setOnClickListener(v -> {
            PasswordDialog passwordDialog = new PasswordDialog(this);
            passwordDialog.show();
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

//    Khi người dùng bấm vào button cập nhật thông tin thì gọi hàm này
//    Các giá trị người dùng chỉnh sửa sẽ được set vào biến user và cuối cùng sẽ goih hàm update
    public void updateInformation() {
//        Lấy các giá trị người dùng cần cập nhật
        String name = edt_name.getText().toString();
        String phone = edt_phone.getText().toString();
        Date dob = Support.stringToDate(tv_dob.getText().toString(), "dd/MM/yyyy");
        boolean gender = spinner_gender.getSelectedItemPosition() == 0;
//        Nếu người dùng bỏ trống tên và số điện thoại thì toast lên
        if (name.equals("") || phone.equals("")) {
            Toast.makeText(this, R.string.fill, Toast.LENGTH_SHORT);
        } else {
            user.setDate_of_birth(dob);
            user.setGender(gender);
            user.setPhone(phone);
            user.setName(name);
            user.setImage(image);
//            Cập nhật thông qua lớp UserAccount và clear hết focus vào các edittext
            UserAccount.update(this);
            edt_phone.clearFocus();
            edt_name.clearFocus();
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
//            Sau 2 giây thì sẽ chuyển hướng người dùng vào main activity
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }).start();
        }
    }

//    Hàm này có chức đăng là từ user ta xét các giá trị lên trên view để hiển thị
    public void setInformationToForm() {
        byte[] bytes = user.getImage();
        if (bytes == null) {
            iv_avatar.setImageResource(R.drawable.avatar_default);
        } else {
            iv_avatar.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, user.getImage().length));
        }
        tv_name.setText(user.getName());
        edt_name.setText(user.getName());
        edt_email.setText(user.getEmail());
        edt_phone.setText(user.getPhone());
        tv_dob.setText(Support.dateToString(user.getDate_of_birth(), "dd/MM/yyyy"));
        spinner_gender.setSelection(user.isGender() ? 0 : 1);
    }

//    Hàm trả về một list là các genders
    private List<String> getGenders() {
        List<String> genders = new ArrayList<>();
        genders.add("Nam");
        genders.add("Nữ");
        return genders;
    }
}