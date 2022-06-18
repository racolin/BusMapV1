package github.racolin.busmap.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import github.racolin.busmap.R;

//  Activity chi tiết thông báo
public class NotificationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(R.string.notification_detail_text);

//        Lấy biến content trong intent, nếu có thì sẽ inflate giao diện đúng như yêu cầu
//        Còn nếu không thì sẽ inflate giao diện lỗi
        LinearLayout ll_notification_detail = findViewById(R.id.ll_notification_detail);
        int content = getIntent().getIntExtra("content", -1);
        View view = null;
        FrameLayout fm_content = findViewById(R.id.fm_content);
        if (content != -1) {
            view = getLayoutInflater().inflate(content, ll_notification_detail, false);
        } else {
            view = getLayoutInflater().inflate(R.layout.not_found_layout, ll_notification_detail, false);
        }
        fm_content.addView(view);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}