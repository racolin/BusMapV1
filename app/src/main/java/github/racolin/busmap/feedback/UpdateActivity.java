package github.racolin.busmap.feedback;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import github.racolin.busmap.MainActivity;
import github.racolin.busmap.R;
import github.racolin.busmap.Support;
import github.racolin.busmap.data.DatabaseHelper;
import github.racolin.busmap.data.UpdateDAO;
import github.racolin.busmap.entities.Update;

public class UpdateActivity extends AppCompatActivity {
    TextView tv_date, tv_percent;
    Button btn_update;
    AlertDialog.Builder builder;
    ProgressBar pgb_update;
    boolean back = true;
    Update update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.update_data);

        initUI();
        initListener();
    }

//    Ánh xạ view
    public void initUI() {
        update = UpdateDAO.getLastUpdate(this);
        tv_date = findViewById(R.id.tv_date);
        tv_date.setText(Support.dateToString(update.getDate(), "dd/MM/yyyy"));
        btn_update = findViewById(R.id.btn_update);
        builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle(R.string.update_data)
                .setMessage(R.string.update_content)
                .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                update();
            }
        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        tv_percent = findViewById(R.id.tv_percent);
        pgb_update = findViewById(R.id.pgb_update);
    }

//    Bắt sự kiện trên view
    public void initListener() {
//        Khi nhấn vào button update
//        Nếu phiên bản update mới nhất đã upload thì toast là đã cập nhật rồi
//        Nếu chưa thì sẽ hiển thị một dialog, nếu bấm tải thì sẽ tiếp hành nạp data
        btn_update.setOnClickListener(v -> {
            if (update.isUploaded()) {
                Toast.makeText(this, R.string.uploaded_before, Toast.LENGTH_SHORT).show();
            } else {
                builder.create().show();
            }
        });
    }

//    Dữ liệu được lưu vào file data trong raw
//    đọc lần lượt các dòng và thực hiện lệnh sql để insert data
    public void update() {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        back = false;
        new Thread(() -> {
            String sql = null;
            int max = 0, process = 0;
            try {
//                max là tổng số byte của file
                max = is.available();
            } catch (IOException e) {
                e.printStackTrace();
            }

            pgb_update.setMax(max);

            boolean ready = false;
            try {
                ready = reader.ready();
            } catch (IOException e) {
                e.printStackTrace();
            }

            DatabaseHelper helper = new DatabaseHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
//          Đọc lần lượt các dòng và thực hiện câu lệnh sql insert
            while (process < max && ready) {
                try {
                    sql = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                    Sau khi thực hiện thì đếm số byte của sql, +2 là cộng cho ký tự \n
                process += sql.getBytes().length + 2;
                if (!sql.equals("")) {
                    db.execSQL(sql);
                }
                int finalProcess = process;
                int showProcess = 100 * process / max;

                runOnUiThread(() -> {
//                    cập nhật ui trên Main Thread
                    pgb_update.setProgress(finalProcess);
                    tv_percent.setText(String.valueOf(showProcess) + "%");
                });
                ready = false;
                try {
                    ready = reader.ready();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            UpdateDAO.setUpDatedById(UpdateActivity.this, update.getId());
            db.close();
            helper.close();
            runOnUiThread(() -> {
                Toast.makeText(UpdateActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
            });
//            Sau khi cập nhật thành công thì sẽ đợi 2 giây và trờ về main activity
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (back) {
            super.onBackPressed();
        }
    }
}